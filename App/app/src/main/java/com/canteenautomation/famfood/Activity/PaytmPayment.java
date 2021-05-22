package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.PaytmAPIEntity;
import com.canteenautomation.famfood.Entities.TransactionEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.JSONParser;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaytmPayment extends AppCompatActivity {

    private UserEntity userEntity;
    private OrderEntity orderEntity;
    private CanteenEntity canteenEntity;
    private SharedData sharedData;
    private Helper helper;
    private PaytmPGService Service;
    private Intent intent;
    private OrderUtility orderUtility;
    private MaterialDialog materialDialog;
    private TransactionEntity transactionEntity;
    private String MID, merchantKey, customerId, orderId, channelId, transactionAmt, website, industryType, callBackUrl, genChksumUrl, checkSumHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initialize();

        customerId = userEntity.getFirebaseID();
        orderId = orderEntity.getOrder_id();
        transactionAmt = orderEntity.getAmount();
        callBackUrl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;
        PaytmAPIEntity paytmAPIEntity = canteenEntity.getPaytmAPIEntity();
        merchantKey = paytmAPIEntity.getMerchantKey();
        MID = paytmAPIEntity.getMerchantId();
        channelId = paytmAPIEntity.getChannelId();
        website = paytmAPIEntity.getWebsite();
        industryType = paytmAPIEntity.getIndustryType();
        genChksumUrl = paytmAPIEntity.getGenerateChecksumUrl();

        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void initialize() {
        helper = new Helper(getApplicationContext());
        sharedData = new SharedData(getApplicationContext());
        userEntity = sharedData.getUserEntity();
        Service = PaytmPGService.getProductionService();
        orderEntity = sharedData.getOrderEntity();
        canteenEntity = sharedData.getCanteenEntity();
        transactionEntity = new TransactionEntity();
        if (sharedData.getUserEntity() != null)
            orderUtility = new OrderUtility(sharedData.getUserEntity().getFirebaseID());
    }


    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        String CHECKSUMHASH = "";

        @Override
        protected void onPreExecute() {
            onProgressStart();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            onProgressStop();
            JSONParser jsonParser = new JSONParser(PaytmPayment.this);
            String param =
                    "MID=" + MID +
                            "&ORDER_ID=" + orderId +
                            "&CUST_ID=" + customerId +
                            "&CHANNEL_ID=" + channelId + "&TXN_AMOUNT=" + transactionAmt + "&WEBSITE=" + website +
                            "&CALLBACK_URL=" + callBackUrl + "&INDUSTRY_TYPE_ID=" + industryType + "&PAYTM_MERCHANT_KEY=" + helper.encodeStringToURLSafe(merchantKey);

            JSONObject jsonObject = jsonParser.makeHttpRequest(genChksumUrl, "POST", param);
            if (jsonObject != null) {
                try {
                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {

            onProgressStart();
            Service = PaytmPGService.getProductionService();

            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("MID", MID);
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", customerId);
            paramMap.put("CHANNEL_ID", channelId);
            paramMap.put("TXN_AMOUNT", transactionAmt);
            paramMap.put("WEBSITE", website);
            paramMap.put("CALLBACK_URL", callBackUrl);
            paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", industryType);

            checkSumHash = CHECKSUMHASH;

            PaytmOrder Order = new PaytmOrder(paramMap);
            Service.initialize(Order, null);

            Service.startPaymentTransaction(PaytmPayment.this, true, true, new PaytmPaymentTransactionCallback() {
                public void someUIErrorOccurred(String inErrorMessage) {
                    orderEntity.setTransactionStatus("TXN_FAILURE");
                    transactionEntity.setStatus("TXN_FAILURE");
                    orderEntity.setTransactionEntity(transactionEntity);
                    failDialog("Error Occured", "UI Error", orderEntity);
                }

                public void onTransactionResponse(Bundle inResponse) {
                    //TODO: Verify using transactionAPi
                    System.out.println("inResponse: " + inResponse.toString());
                    String verifyChksumHash = inResponse.getString("CHECKSUMHASH");
                    String verifyOrderId = inResponse.getString("ORDERID");
                    String verifyAmt = inResponse.getString("TXNAMOUNT");
                    String verifyMID = inResponse.getString("MID");
                    String bankTxnId = inResponse.getString("BANKTXNID");
                    String status = inResponse.getString("STATUS"); //TXN_SUCCESS, TXN_FAILURE & PENDING
                    String respCode = inResponse.getString("RESPCODE");
                    String respMessage = inResponse.getString("RESPMSG");
                    transactionEntity.setRespCode(respCode);
                    transactionEntity.setRespMessage(respMessage);
                    transactionEntity.setStatus(status);
                    transactionEntity.setBankTxnId(bankTxnId);
                    orderEntity.setTransactionStatus(status);
                    if (status.equals("TXN_SUCCESS")) {
                        String txnId = inResponse.getString("TXNID");
                        String gatewayName = inResponse.getString("GATEWAYNAME");
                        String bankName = inResponse.getString("BANKNAME");
                        String paymentMode = inResponse.getString("PAYMENTMODE");
                        transactionEntity.setBankName(bankName);
                        transactionEntity.setPaymentMode(paymentMode);
                        transactionEntity.setGatewayName(gatewayName);
                        transactionEntity.setTxnId(txnId);
                        orderEntity.setTransactionEntity(transactionEntity);
                        successDialog(orderEntity);
                    } else if (status.equals("TXN_FAILURE")) {
                        orderEntity.setTransactionEntity(transactionEntity);
                        failDialog("Error Occured", "Transaction Failed", orderEntity);
                    } else {
                        orderEntity.setTransactionEntity(transactionEntity);
                        failDialog("Pending", "Transaction Pending", orderEntity);
                    }

                }

                public void networkNotAvailable() {
                    orderEntity.setTransactionStatus("TXN_FAILURE");
                    transactionEntity.setStatus("TXN_FAILURE");
                    orderEntity.setTransactionEntity(transactionEntity);
                    failDialog("Error Occured", "Network connection error: Check your internet connectivity", orderEntity);
                }

                public void clientAuthenticationFailed(String inErrorMessage) {
                    orderEntity.setTransactionStatus("TXN_FAILURE");
                    transactionEntity.setStatus("TXN_FAILURE");
                    orderEntity.setTransactionEntity(transactionEntity);
                    failDialog("Error Occured", "Authentication failed: Server error", orderEntity);
                }

                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                    orderEntity.setTransactionStatus("TXN_FAILURE");
                    transactionEntity.setStatus("TXN_FAILURE");
                    orderEntity.setTransactionEntity(transactionEntity);
                    failDialog("Error Occured", "Unable to load webpage ", orderEntity);
                }

                public void onBackPressedCancelTransaction() {
                    orderEntity.setTransactionStatus("TXN_FAILURE");
                    transactionEntity.setStatus("TXN_FAILURE");
                    orderEntity.setTransactionEntity(transactionEntity);
                    failDialog("Error Occured", "Transaction cancelled", orderEntity);
                }

                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                    orderEntity.setTransactionStatus("TXN_FAILURE");
                    transactionEntity.setStatus("TXN_FAILURE");
                    orderEntity.setTransactionEntity(transactionEntity);
                    failDialog("Error Occured", "Transaction cancelled " + inResponse.toString(), orderEntity);
                }
            });


        }

    }

    public void onProgressStart() {
        materialDialog = new MaterialDialog.Builder(PaytmPayment.this)
                .title("Getting Payment Status")
                .content("Please Wait")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStop() {
        materialDialog.dismiss();
    }

    /*public void failDialog(String title_name, String content_text, final OrderEntity orderEntity){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title_name)
                .setContentText(content_text)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        intent = new Intent(getApplicationContext(), ItemActivity.class);
                        startActivity(intent);
                    }
                });
        sweetAlertDialog.setCancelable(false);
        HashMap<String, Integer> cart = new HashMap<>();
        sharedData.setCart(cart);
        List<String> item = new ArrayList<>();
        sharedData.setJainItems(item);
        orderUtility.addOrder(orderEntity);
        onProgressStop();
        helper.printToast("Operation Failed",1);
        sweetAlertDialog.show();
    }*/

    public void successDialog(final OrderEntity orderEntity){
        HashMap<String, Integer> cart = new HashMap<>();
        sharedData.setCart(cart);
        List<String> item = new ArrayList<>();
        sharedData.setJainItems(item);
        sharedData.setOrderEntity(orderEntity);
        orderUtility.addOrder(orderEntity);
        intent = new Intent(getApplicationContext(), TrackOrder.class);
        intent.putExtra("orderplaced", true);
        onProgressStop();
        startActivity(intent);
    }

    public void failDialog(String title_name, String content_text, final OrderEntity orderEntity){
        HashMap<String, Integer> cart = new HashMap<>();
        sharedData.setCart(cart);
        List<String> item = new ArrayList<>();
        sharedData.setJainItems(item);
        orderUtility.addOrder(orderEntity);
        onProgressStop();
        new MaterialDialog.Builder(PaytmPayment.this)
                .title(title_name)
                .content(content_text)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        HashMap<String, Integer> cart = new HashMap<>();
                        sharedData.setCart(cart);
                        List<String> item = new ArrayList<>();
                        sharedData.setJainItems(item);
                        orderUtility.addOrder(orderEntity);
                        intent = new Intent(getApplicationContext(), ItemActivity.class);
                        startActivity(intent);
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(PaytmPayment.this)
                .title("Confirm")
                .content("Are you sure you want to cancel transaction?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        onProgressStop();
                        orderEntity.setTransactionStatus("TXN_FAILURE");
                        transactionEntity.setStatus("TXN_FAILURE");
                        orderEntity.setTransactionEntity(transactionEntity);
                        HashMap<String, Integer> cart = new HashMap<>();
                        sharedData.setCart(cart);
                        List<String> item = new ArrayList<>();
                        sharedData.setJainItems(item);
                        orderUtility.addOrder(orderEntity);
                        intent = new Intent(getApplicationContext(), ItemActivity.class);
                        startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }


}

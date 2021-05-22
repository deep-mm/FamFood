package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.canteenautomation.famfood.Entities.TransactionEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.OnTransactionChangeListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.TransactionUtility;
import com.canteenautomation.famfood.Utilities.VolleyUtility;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PaymentActivity extends AppCompatActivity {

    private static final int TEZ_REQUEST_CODE = 123;
    private UserEntity userEntity;
    private SharedData sharedData;
    private TransactionUtility transactionUtility;
    private VolleyUtility volleyUtility;
    private String MID,merchantKey,customerId,orderId,mobile,email,channelId,transactionAmt,website,industryType,callBackUrl;

    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final PaytmPGService Service = PaytmPGService.getProductionService();

        sharedData = new SharedData(getApplicationContext());
        userEntity = sharedData.getUserEntity();

        /*payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                merchantKey = "UYcNhq5&pFn2%5P1";
                MID = "VWgiWN01208844400057";
                customerId = userEntity.getFirebaseID();
                orderId = "order1"; // TODO: Replace with actual orderId
                mobile = userEntity.getUserMobile();
                if(userEntity.getUserEmail()!=null)
                    email = userEntity.getUserEmail();
                else
                    email = "";

                channelId = "WAP";
                transactionAmt = "1.00";
                website = "DEFAULT";
                industryType = "Retail";
                callBackUrl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=order1";

                TransactionEntity transactionEntity = new TransactionEntity(merchantKey,MID,orderId,customerId,mobile,email,channelId,transactionAmt,website,industryType);
                transactionUtility.addTransaction(transactionEntity);
                String url = "https://dhaval-d1197.firebaseapp.com/paytm/generate_checksum?uid="+encodeStringToURLSafe(userEntity.getFirebaseID());
                volleyUtility = new VolleyUtility(getApplicationContext(),url);
            }
        });*/

        /*transactionUtility = new TransactionUtility(userEntity.getFirebaseID(), new OnTransactionChangeListener() {
            @Override
            public void onDataChanged(TransactionEntity transactionEntity) {
                if (transactionEntity != null) {
                    if (transactionEntity.getChecksum() != null) {
                        HashMap<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("MID", MID);
// Key in your staging and production MID available in your dashboard
                        paramMap.put("ORDER_ID", orderId);
                        paramMap.put("CUST_ID", customerId);
                        paramMap.put("MOBILE_NO", "9867236820");
                        paramMap.put("EMAIL", email);
                        paramMap.put("CHANNEL_ID", channelId);
                        paramMap.put("TXN_AMOUNT", transactionAmt);
                        paramMap.put("WEBSITE", website);
// This is the staging value. Production value is available in your dashboard
                        paramMap.put("INDUSTRY_TYPE_ID", industryType);
// This is the staging value. Production value is available in your dashboard
                        paramMap.put("CALLBACK_URL", callBackUrl);
                        paramMap.put("CHECKSUMHASH", transactionEntity.getChecksum());
                        PaytmOrder Order = new PaytmOrder(paramMap);

                        Service.initialize(Order, null);

                        Service.startPaymentTransaction(PaymentActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            *//*Call Backs*//*
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
                            }

                            public void onTransactionResponse(Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                            }

                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                            }

                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();
                            }

                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();
                            }

                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
                            }

                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
            @Override
            public void onErrorOccured() {

            }
        });*/
    }

    public String encodeStringToURLSafe(String pathParam) {
        try {
            return URLEncoder.encode(pathParam, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return pathParam;
        }
    }



}

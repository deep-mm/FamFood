package com.canteenautomation.famfood.Entities;

public class TransactionEntity {

    private String bankTxnId;
    private String status;
    private String txnId;
    private String respCode;
    private String respMessage;
    private String gatewayName;
    private String bankName;
    private String paymentMode;


    public TransactionEntity(String bankTxnId, String status, String txnId, String respCode, String respMessage, String gatewayName, String bankName, String paymentMode) {
        this.bankTxnId = bankTxnId;
        this.status = status;
        this.txnId = txnId;
        this.respCode = respCode;
        this.respMessage = respMessage;
        this.gatewayName = gatewayName;
        this.bankName = bankName;
        this.paymentMode = paymentMode;
    }

    public TransactionEntity() {

    }

    public String getBankTxnId() {
        return bankTxnId;
    }

    public void setBankTxnId(String bankTxnId) {
        this.bankTxnId = bankTxnId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}

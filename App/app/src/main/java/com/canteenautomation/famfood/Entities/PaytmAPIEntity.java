package com.canteenautomation.famfood.Entities;

public class PaytmAPIEntity {
    private String merchantId;
    private String merchantKey;
    private String channelId;
    private String website;
    private String industryType;
    private String generateChecksumUrl;

    public PaytmAPIEntity(String merchantId, String merchantKey, String channelId, String website, String industryType, String generateChecksumUrl) {
        this.merchantId = merchantId;
        this.merchantKey = merchantKey;
        this.channelId = channelId;
        this.website = website;
        this.industryType = industryType;
        this.generateChecksumUrl = generateChecksumUrl;
    }

    public PaytmAPIEntity() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public void setMerchantKey(String merchantKey) {
        this.merchantKey = merchantKey;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getGenerateChecksumUrl() {
        return generateChecksumUrl;
    }

    public void setGenerateChecksumUrl(String generateChecksumUrl) {
        this.generateChecksumUrl = generateChecksumUrl;
    }
}

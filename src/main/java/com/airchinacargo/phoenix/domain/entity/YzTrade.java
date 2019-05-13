package com.airchinacargo.phoenix.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 有赞订单
 *
 * @author ChenYu 2018 03 14
 */
public class YzTrade {
    @SerializedName("num")
    private int totalNum;
    private String tid;
    private List<YzOrder> orders;
    @SerializedName("receiver_city")
    private String receiverCity;
    @SerializedName("receiver_state")
    private String receiverState;
    @SerializedName("receiver_district")
    private String receiverDistrict;
    @SerializedName("receiver_address")
    private String receiverAddress;
    @SerializedName("receiver_mobile")
    private String receiverMobile;
    @SerializedName("receiver_name")
    private String receiverName;
    @SerializedName("coupon_details")
    private List<Coupon> coupons;
    @SerializedName("buyer_message")
    private String buyerMessage;
    private double payment;

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public List<YzOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<YzOrder> orders) {
        this.orders = orders;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverState() {
        return receiverState;
    }

    public void setReceiverState(String receiverState) {
        this.receiverState = receiverState;
    }

    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    @Override
    public String toString() {
        return "YzTrade{" +
                "totalNum=" + totalNum +
                ", tid='" + tid + '\'' +
                ", orders=" + orders +
                ", receiverCity='" + receiverCity + '\'' +
                ", receiverState='" + receiverState + '\'' +
                ", receiverDistrict='" + receiverDistrict + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", coupons=" + coupons +
                ", buyerMessage='" + buyerMessage + '\'' +
                ", payment=" + payment +
                '}';
    }
}

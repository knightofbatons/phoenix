package com.airchinacargo.phoenix.domain.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 有赞优惠券信息
 *
 * @author ChenYu
 */
public class Coupon {
    @SerializedName("coupon_name")
    private String couponName;
    @SerializedName("coupon_description")
    private String couponDescription;
    @SerializedName("coupon_content")
    private String couponContent;

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public void setCouponDescription(String couponDescription) {
        this.couponDescription = couponDescription;
    }

    public String getCouponContent() {
        return couponContent;
    }

    public void setCouponContent(String couponContent) {
        this.couponContent = couponContent;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "couponName='" + couponName + '\'' +
                ", couponDescription='" + couponDescription + '\'' +
                ", couponContent='" + couponContent + '\'' +
                '}';
    }
}

package com.airchinacargo.phoenix.domain.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 有赞订单分单
 *
 * @author ChenYu 2018 03 14
 */
public class YzOrder {
    @SerializedName("num")
    private int orderNum;
    private String oid;
    private String title;
    @SerializedName("outer_item_id")
    private String itemId;
    @SerializedName("sku_id")
    private String skuId;

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    @Override
    public String toString() {
        return "YzOrder{" +
                "orderNum=" + orderNum +
                ", oid='" + oid + '\'' +
                ", title='" + title + '\'' +
                ", itemId='" + itemId + '\'' +
                ", skuId='" + skuId + '\'' +
                '}';
    }

    public YzOrder(int orderNum, String oid, String title, String itemId, String skuId) {
        this.orderNum = orderNum;
        this.oid = oid;
        this.title = title;
        this.itemId = itemId;
        this.skuId = skuId;
    }
}

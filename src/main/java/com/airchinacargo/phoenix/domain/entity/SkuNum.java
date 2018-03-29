package com.airchinacargo.phoenix.domain.entity;

/**
 * 计划在京东购买
 *
 * @author ChenYu 2018 03 27
 */
public class SkuNum {
    private String skuId;
    private int num;


    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public SkuNum(String skuId, int num) {
        this.skuId = skuId;
        this.num = num;
    }

    @Override
    public String toString() {
        return "{" +
                "skuId:'" + skuId + '\'' +
                ", num:" + num +
                '}';
    }

}

package com.airchinacargo.phoenix.domain.entity;

/**
 * 京东商品
 *
 * @author ChenYu 2018 04 11
 */
public class JdSku {
    private Long skuId;
    private int num;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "JdSku{" +
                "skuId=" + skuId +
                ", num=" + num +
                '}';
    }
}

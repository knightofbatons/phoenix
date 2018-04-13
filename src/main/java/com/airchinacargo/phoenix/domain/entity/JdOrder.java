package com.airchinacargo.phoenix.domain.entity;

import java.util.List;

/**
 * 京东子单
 *
 * @author ChenYu 2018 04 11
 */
public class JdOrder {
    private Long jdOrderId;
    private List<JdSku> sku;

    public Long getJdOrderId() {
        return jdOrderId;
    }

    public void setJdOrderId(Long jdOrderId) {
        this.jdOrderId = jdOrderId;
    }

    public List<JdSku> getSku() {
        return sku;
    }

    public void setSku(List<JdSku> sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return "JdOrder{" +
                "jdOrderId=" + jdOrderId +
                ", sku=" + sku +
                '}';
    }
}

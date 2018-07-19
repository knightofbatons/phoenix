package com.airchinacargo.phoenix.domain.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 京东子单
 *
 * @author ChenYu 2018 04 11
 */
public class JdOrder {
    private int state;
    private BigDecimal freight;
    private BigDecimal orderPrice;
    private Long jdOrderId;
    private List<JdSku> sku;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

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

}

package com.airchinacargo.phoenix.domain.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ChenYu 2018 07 19
 * <p>
 * 记录申请开票信息
 */
@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "settlement_id")
    private String settlementId;
    @Column(name = "begin_id")
    private int beginId;
    @Column(name = "end_id")
    private int endId;
    @Column(name = "timestamp")
    private Date timestamp;
    @Column(name = "total_batch")
    private int totalBatch;
    @Column(name = "error_orders")
    private String errorOrders;
    @Column(name = "freight")
    private BigDecimal freight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }

    public int getBeginId() {
        return beginId;
    }

    public void setBeginId(int beginId) {
        this.beginId = beginId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalBatch() {
        return totalBatch;
    }

    public void setTotalBatch(int totalBatch) {
        this.totalBatch = totalBatch;
    }

    public String getErrorOrders() {
        return errorOrders;
    }

    public void setErrorOrders(String errorOrders) {
        this.errorOrders = errorOrders;
    }

    public Invoice(String settlementId, int beginId, int endId, Date timestamp, int totalBatch, String errorOrders, BigDecimal freight) {
        this.settlementId = settlementId;
        this.beginId = beginId;
        this.endId = endId;
        this.timestamp = timestamp;
        this.totalBatch = totalBatch;
        this.errorOrders = errorOrders;
        this.freight = freight;
    }
}

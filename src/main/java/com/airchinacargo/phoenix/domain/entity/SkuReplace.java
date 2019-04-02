package com.airchinacargo.phoenix.domain.entity;

import javax.persistence.*;

/**
 * 缺货替代关系
 *
 * @author ChenYu 2018 03 26
 */
@Entity
@Table(name = "sku_replace")
public class SkuReplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "before_num")
    private int beforeNum;
    @Column(name = "after_num")
    private int afterNum;
    @Column(name = "before_sku")
    private String beforeSku;
    @Column(name = "after_sku")
    private String afterSku;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBeforeNum() {
        return beforeNum;
    }

    public void setBeforeNum(int beforeNum) {
        this.beforeNum = beforeNum;
    }

    public int getAfterNum() {
        return afterNum;
    }

    public void setAfterNum(int afterNum) {
        this.afterNum = afterNum;
    }

    public String getBeforeSku() {
        return beforeSku;
    }

    public void setBeforeSku(String beforeSku) {
        this.beforeSku = beforeSku;
    }

    public String getAfterSku() {
        return afterSku;
    }

    public void setAfterSku(String afterSku) {
        this.afterSku = afterSku;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"beforeNum\":")
                .append(beforeNum);
        sb.append(",\"afterNum\":")
                .append(afterNum);
        sb.append(",\"beforeSku\":\"")
                .append(beforeSku).append('\"');
        sb.append(",\"afterSku\":\"")
                .append(afterSku).append('\"');
        sb.append('}');
        return sb.toString();
    }
}

package com.airchinacargo.phoenix.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 有赞和京东商品对应关系
 *
 * @author ChenYu 2018 03 26
 */
@Entity
@Table(name = "yz_to_jd")
public class YzToJd {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "item_id")
    private String itemId;
    @Column(name = "sku_id")
    private String skuId;
    @Column(name = "num")
    private int num;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public YzToJd() {
    }

    public YzToJd(String itemId, String skuId, int num) {
        this.itemId = itemId;
        this.skuId = skuId;
        this.num = num;
    }
}

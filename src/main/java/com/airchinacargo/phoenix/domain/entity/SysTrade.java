package com.airchinacargo.phoenix.domain.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统处理过的交易记录
 *
 * @author ChenYu 2018 04 08
 */

@Entity
@Table(name = "sys_trade")
public class SysTrade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "tid")
    private String tid;
    @Column(name = "jd_order_id")
    private String jdOrderId;
    @Column(name = "due_date")
    private Date dueDate;
    @Column(name = "result_message")
    private String resultMessage;
    @Column(name = "price")
    private Double price;
    @Column(name = "is_success")
    private Boolean isSuccess;

    public SysTrade(String tid, String jdOrderId, Date dueDate, String resultMessage, double price, Boolean isSuccess) {
        this.tid = tid;
        this.jdOrderId = jdOrderId;
        this.dueDate = dueDate;
        this.resultMessage = resultMessage;
        this.price = price;
        this.isSuccess = isSuccess;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getJdOrderId() {
        return jdOrderId;
    }

    public void setJdOrderId(String jdOrderId) {
        this.jdOrderId = jdOrderId;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "SysTrade{" +
                "id=" + id +
                ", tid='" + tid + '\'' +
                ", jdOrderId='" + jdOrderId + '\'' +
                ", dueDate=" + dueDate +
                ", resultMessage='" + resultMessage + '\'' +
                ", price=" + price +
                ", isSuccess=" + isSuccess +
                '}';
    }
}

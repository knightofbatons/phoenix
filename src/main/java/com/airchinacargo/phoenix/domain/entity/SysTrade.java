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
    private double price;
    @Column(name = "success")
    private boolean success;
    @Column(name = "confirm")
    private boolean confirm;
    @Column(name = "receiver_name")
    private String receiverName;
    @Column(name = "receiver_mobile")
    private String receiverMobile;
    @Column(name = "receiver_address")
    private String receiverAddress;


    /**
     * Hibernate 需要一个无参构造函数 避免错误 No default constructor for entity
     */
    public SysTrade() {
    }

    public SysTrade(String tid, String jdOrderId, Date dueDate, String resultMessage, double price, boolean success, boolean confirm, String receiverName, String receiverMobile, String receiverAddress) {
        this.tid = tid;
        this.jdOrderId = jdOrderId;
        this.dueDate = dueDate;
        this.resultMessage = resultMessage;
        this.price = price;
        this.success = success;
        this.confirm = confirm;
        this.receiverName = receiverName;
        this.receiverMobile = receiverMobile;
        this.receiverAddress = receiverAddress;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
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
                ", success=" + success +
                ", confirm=" + confirm +
                ", receiverName='" + receiverName + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                '}';
    }
}

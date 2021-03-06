package com.airchinacargo.phoenix.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    @Column(name = "coupon")
    private String coupon;
    @Column(name = "invoice_type")
    private int invoiceType;


    /**
     * Hibernate 需要一个无参构造函数 避免错误 No default constructor for entity
     */
    public SysTrade() {
    }

    public SysTrade(String tid, String jdOrderId, Date dueDate, String resultMessage, double price, boolean success, boolean confirm, String receiverName, String receiverMobile, String receiverAddress, String coupon, int invoiceType) {
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
        this.coupon = coupon;
        this.invoiceType = invoiceType;
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

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"tid\":\"")
                .append(tid).append('\"');
        sb.append(",\"jdOrderId\":\"")
                .append(jdOrderId).append('\"');
        sb.append(",\"dueDate\":\"")
                .append(dueDate).append('\"');
        sb.append(",\"resultMessage\":\"")
                .append(resultMessage).append('\"');
        sb.append(",\"price\":")
                .append(price);
        sb.append(",\"success\":")
                .append(success);
        sb.append(",\"confirm\":")
                .append(confirm);
        sb.append(",\"receiverName\":\"")
                .append(receiverName).append('\"');
        sb.append(",\"receiverMobile\":\"")
                .append(receiverMobile).append('\"');
        sb.append(",\"receiverAddress\":\"")
                .append(receiverAddress).append('\"');
        sb.append(",\"coupon\":\"")
                .append(coupon).append('\"');
        sb.append(",\"invoiceType\":")
                .append(invoiceType);
        sb.append('}');
        return sb.toString();
    }
}

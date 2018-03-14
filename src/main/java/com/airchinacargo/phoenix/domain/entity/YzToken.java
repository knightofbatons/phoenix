package com.airchinacargo.phoenix.domain.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ChenYu 2018 03 13
 */
@Entity
@Table(name = "yz_token")
public class YzToken {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "token")
    private String token;
    @Column(name = "date")
    private Date date;

    public YzToken(int id, String token, Date date) {
        this.id = id;
        this.token = token;
        this.date = date;
    }

    public YzToken() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

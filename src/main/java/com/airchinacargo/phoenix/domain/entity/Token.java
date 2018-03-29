package com.airchinacargo.phoenix.domain.entity;

import com.google.gson.annotations.SerializedName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 授权信息
 *
 * @author ChenYu 2018 03 15
 */
@Entity
@Table(name = "token")
public class Token {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "access_token")
    @SerializedName("access_token")
    private String accessToken;
    @Column(name = "refresh_token")
    @SerializedName("refresh_token")
    private String refreshToken;
    @Column(name = "date")
    private Date date;

    public Token(int id, String accessToken, String refreshToken, Date date) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.date = date;
    }

    public Token() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

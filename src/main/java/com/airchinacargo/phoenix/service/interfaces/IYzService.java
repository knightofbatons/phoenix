package com.airchinacargo.phoenix.service.interfaces;

import com.airchinacargo.phoenix.domain.entity.YzToken;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author ChenYu 2018 03 13
 */
public interface IYzService {

    /**
     * 获取有赞授权并更新 Token 到数据库
     *
     * @return YzToken 用于有赞免签名请求有效期 7 天
     * @throws UnirestException 请求异常
     */
    YzToken getYzToken() throws UnirestException;

    /**
     * 从数据库读取储存的 Token 如果不过时直接返回 过时更新数据库并返回
     *
     * @return Token
     */
    String readYzToken() throws UnirestException;

    /**
     * 获取付款未发货订单
     *
     * @param accessToken
     * @throws UnirestException
     */
    void getYzTradesSold (String accessToken) throws UnirestException;
}

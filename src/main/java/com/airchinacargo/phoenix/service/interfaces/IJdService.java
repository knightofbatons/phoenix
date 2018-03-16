package com.airchinacargo.phoenix.service.interfaces;

import com.airchinacargo.phoenix.domain.entity.Token;

/**
 * @author ChenYu 2018 03 15
 */
public interface IJdService {

    /**
     * 获取京东授权并更新 token 到数据库
     *
     * @return Token
     */
    Token getJdToken();

    /**
     * 从数据库读取储存的 Token 如果不是当天的 就刷新 并更新数据库 然后返回
     *
     * @return Token 京东的 access token 等
     */
    Token readJdToken();

    /**
     * 用 refresh token 刷新 access token 计划添加在定时任务中每天刷新 刷新失败就重新请求
     *
     * @param refreshToken 刷新 access token 用的 token
     * @return Token 京东的 access token 等
     */
    Token refreshJdToken(String refreshToken);

    /**
     * 通过详细地址获取京东地址
     *
     * @param accessToken 接入授权 token
     * @param address     详细地址
     * @return String 京东四级编码和名称
     */
    String getJdAddressFromAddress(String address, String accessToken);
}

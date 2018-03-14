package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.YzToken;
import com.airchinacargo.phoenix.domain.repository.IYzTokenRepository;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author ChenYu 2018 03 13
 */

@Service
public class YzServiceImpl implements IYzService {

    @Autowired
    IYzTokenRepository yzTokenRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取有赞 Token 需要的参数 从配置文件读取
     * <p>
     * ClientId 有赞 ID
     * ClientSecret 有赞密码
     * KdtId 有赞店铺 ID
     */
    @Value("${Yz.CLIENT_ID}")
    private String clientId;
    @Value("${Yz.CLIENT_SECRET}")
    private String clientSecret;
    @Value("${Yz.KDT_ID}")
    private String kdtId;

    /**
     * 获取有赞授权并更新 Token 到数据库
     *
     * @return YzToken 用于有赞免签名请求有效期 7 天
     */
    @Override
    public YzToken getYzToken() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://open.youzan.com/oauth/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .queryString("client_id", clientId)
                    .queryString("client_secret", clientSecret)
                    .queryString("grant_type", "silent")
                    .queryString("kdt_id", kdtId)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        String token = response.getBody().getObject().get("access_token").toString();
        logger.info("[ getYzToken ] --> " + token);
        YzToken yzToken = new YzToken(0, token, new Date());
        try {
            yzTokenRepository.deleteById(0);
        } catch (Exception e) {
            logger.info("[ getYzToken ] --> never have Yz token before");
        }
        yzTokenRepository.save(yzToken);
        return yzToken;
    }


    /**
     * 计算 Token 是否过期使用的参数
     * <p>
     * millisOneDay 一天有多少毫秒
     * days 预计多少天更新
     */
    private final int MILLIS_ONE_DAY = 1000 * 3600 * 24;
    private final int DAYS = 6;

    /**
     * 从数据库读取储存的 Token 没有直接请求一个新的 有的话判断如果不过时直接返回 过时重新请求后更新数据库并返回 计划 6 天更新一次
     *
     * @return Token
     */
    @Override
    public String readYzToken() {
        // 存在直接赋值不存在就去请求
        YzToken yzToken = yzTokenRepository.findById(0).orElseGet(() -> getYzToken());
        // 判断如果没超过时限就继续用
        if ((System.currentTimeMillis() - yzToken.getDate().getTime()) / MILLIS_ONE_DAY < DAYS) {
            return yzToken.getToken();
        }
        return getYzToken().getToken();
    }

    /**
     * 获取订单信息需要的参数
     * <p>
     * FIELDS 需要的订单信息字段
     */
    //private final String FIELDS = "tid,title,receiver_city,receiver_address,update_time,receiver_mobile,receiver_name";

    /**
     * 获取有赞付款未发货订单
     *
     * @param accessToken
     */
    @Override
    public void getYzTradesSold(String accessToken) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://open.youzan.com/api/oauthentry/youzan.trades.sold/3.0.0/get")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .queryString("status", "WAIT_SELLER_SEND_GOODS")
                    //.queryString("fields", FIELDS)
                    .queryString("access_token", accessToken)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info("[ getYzTradesSold ] --> " + response.getBody());
        //TODO 根据新的有赞订单组织形式解析出需要的订单字段 E20180313084627051900006
    }

}

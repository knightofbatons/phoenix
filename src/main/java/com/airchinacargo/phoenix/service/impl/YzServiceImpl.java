package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.Token;
import com.airchinacargo.phoenix.domain.entity.YzTrade;
import com.airchinacargo.phoenix.domain.repository.ITokenRepository;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;

/**
 * @author ChenYu 2018 03 13
 */

@Service
public class YzServiceImpl implements IYzService {

    @Autowired
    ITokenRepository tokenRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取有赞 Token 需要的参数 前三个是从配置文件读取的
     * <p>
     * clientId 有赞 ID
     * clientSecret 有赞密码
     * kdtId 有赞店铺 ID
     * YZ 数据库里有赞的标记
     */
    @Value("${Yz.CLIENT_ID}")
    private String clientId;
    @Value("${Yz.CLIENT_SECRET}")
    private String clientSecret;
    @Value("${Yz.KDT_ID}")
    private String kdtId;
    private final int YZ = 0;

    /**
     * 获取有赞授权并更新 token 到数据库
     *
     * @return Token
     */
    @Override
    public Token getYzToken() {
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
        String accessToken = response.getBody().getObject().get("access_token").toString();
        Token yzToken = new Token(YZ, accessToken, "NO_REFRESH_TOKEN", new Date());
        try {
            tokenRepository.deleteById(YZ);
        } catch (Exception e) {
            logger.info("[ getYzToken ] --> never have Yz token before");
        }
        tokenRepository.save(yzToken);
        logger.info("[ getYzToken ] --> " + yzToken.getDate());
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
     * 从数据库读取储存的 token 如果不过时直接返回 过时更新数据库并返回
     *
     * @return String 有赞的 access token
     */
    @Override
    public String readYzToken() {
        // 存在直接赋值不存在就去请求
        Token yzToken = tokenRepository.findById(YZ).orElseGet(() -> getYzToken());
        // 判断如果没超过时限就继续用
        if ((System.currentTimeMillis() - yzToken.getDate().getTime()) / MILLIS_ONE_DAY < DAYS) {
            return yzToken.getAccessToken();
        }
        return getYzToken().getAccessToken();
    }

    /**
     * 获取订单信息需要的参数
     * <p>
     * FIELDS 需要的订单信息字段
     */
    private final String FIELDS = "num,tid,orders,receiver_city,receiver_state,receiver_district,receiver_address,receiver_mobile,receiver_name";

    /**
     * 获取付款未发货订单
     *
     * @param accessToken 读到的有赞授权 token
     * @return List<YzTrade> 返回包含所有 tread 的 List tread 里 包含 order 这个 order 在新的订单设置下和以往不同会有多个
     */
    @Override
    public List<YzTrade> getYzTradesSold(String accessToken) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://open.youzan.com/api/oauthentry/youzan.trades.sold/3.0.0/get")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .queryString("status", "WAIT_SELLER_SEND_GOODS")
                    .queryString("fields", FIELDS)
                    .queryString("access_token", accessToken)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        String tradesArray = response.getBody().getObject().getJSONObject("response").getJSONArray("trades").toString();
        Gson gson = new Gson();
        List<YzTrade> yzTrades = gson.fromJson(tradesArray, new TypeToken<List<YzTrade>>() {
        }.getType());
        logger.info("[ getYzTradesSold ] --> " + yzTrades);
        return yzTrades;
    }

}

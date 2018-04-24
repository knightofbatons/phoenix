package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.*;
import com.airchinacargo.phoenix.domain.repository.ITokenRepository;
import com.airchinacargo.phoenix.domain.repository.IYzToJdRepository;
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

import static java.util.stream.Collectors.toList;

/**
 * @author ChenYu 2018 03 13
 */

@Service
public class YzServiceImpl implements IYzService {

    @Autowired
    ITokenRepository tokenRepository;
    @Autowired
    IYzToJdRepository yzToJdRepository;

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
            logger.info("[ getYzToken ] --> never have YZ token before");
        }
        tokenRepository.save(yzToken);
        logger.info("[ getYzToken ] --> get new YZ token");
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
        // 超过时限重新请求
        return getYzToken().getAccessToken();
    }

    /**
     * 获取订单信息需要的参数
     * <p>
     * FIELDS 需要的订单信息字段
     */
    private final String FIELDS = "coupon_details,num,tid,payment,orders,receiver_city,receiver_state,receiver_district,receiver_address,receiver_mobile,receiver_name";

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
        // 将获取的 json 转化后返回
        Gson gson = new Gson();
        return gson.fromJson(tradesArray, new TypeToken<List<YzTrade>>() {
        }.getType());
    }

    /**
     * 根据订单号查询订单详细
     *
     * @param accessToken 读到的有赞授权 token
     * @param tid         有赞订单号
     * @return YzTrade  有赞订单信息
     */
    @Override
    public YzTrade getYzTreadByTid(String accessToken, String tid) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://open.youzan.com/api/oauthentry/youzan.trade/3.0.0/get")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .queryString("fields", FIELDS)
                    .queryString("tid", tid)
                    .queryString("access_token", accessToken)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        try {
            String tread = response.getBody().getObject().getJSONObject("response").getJSONObject("trade").toString();
            Gson gson = new Gson();
            return gson.fromJson(tread, new TypeToken<YzTrade>() {
            }.getType());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过有赞订单获取计划在京东购买的商品和数量
     *
     * @param yzTrade 单个有赞订单
     * @return List<SkuNum> 返回期望在京东购买的 skuId 和 num 列表
     */
    @Override
    public List<SkuNum> getSkuIdAndNum(YzTrade yzTrade) {
        List<YzOrder> yzOrderList = yzTrade.getOrders();
        return yzOrderList
                .stream()
                .map(p -> yzToJdRepository.findByItemId(p.getItemId()).orElseGet(() -> new YzToJd()))
                .filter(p -> p.getSkuId() != null)
                .map(p -> new SkuNum(p.getSkuId(), p.getNum()))
                .collect(toList());
    }

    /**
     * 有赞系统发货需要的参数
     * <p>
     * JD_EXPRESS 有赞系统内代表京东快递的编号
     */
    private final String JD_EXPRESS = "138";

    /**
     * 拆单发货
     *
     * @param accessToken 读到的有赞授权 token
     * @param tid         有赞订单 id
     * @param jdOrderId   京东订单 id
     * @param oidList     这个京东订单下的有赞子订单 用于分单发货
     */
    @Override
    public void confirm(String accessToken, String tid, String jdOrderId, String oidList) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://open.youzan.com/api/oauthentry/youzan.logistics.online/3.0.0/confirm")
                    .queryString("oids", oidList)
                    .queryString("out_sid", jdOrderId)
                    .queryString("out_stype", JD_EXPRESS)
                    .queryString("outer_tid", jdOrderId)
                    .queryString("tid", tid)
                    .queryString("access_token", accessToken)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info("[ confirm ] --> " + response.getBody().toString());
    }

    /**
     * 不拆单发货
     *
     * @param accessToken 读到的有赞授权 token
     * @param tid         有赞订单 id
     * @param jdOrderId   京东订单 id
     */
    @Override
    public void confirmNoSplit(String accessToken, String tid, String jdOrderId) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://open.youzan.com/api/oauthentry/youzan.logistics.online/3.0.0/confirm")
                    .queryString("out_sid", jdOrderId)
                    .queryString("out_stype", JD_EXPRESS)
                    .queryString("outer_tid", jdOrderId)
                    .queryString("tid", tid)
                    .queryString("access_token", accessToken)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info("[ confirmNoSplit ] --> " + response.getBody().toString());
    }

    /**
     * 无物流发货
     *
     * @param accessToken 读到的有赞授权 token
     * @param tid         有赞订单 id
     * @param jdOrderId   京东订单 id
     */
    @Override
    public void confirmNoExpress(String accessToken, String tid, String jdOrderId) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://open.youzan.com/api/oauthentry/youzan.logistics.online/3.0.0/confirm")
                    .queryString("is_no_express", 1)
                    .queryString("outer_tid", jdOrderId)
                    .queryString("tid", tid)
                    .queryString("access_token", accessToken)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info("[ confirmNoExpress ] --> " + response.getBody().toString());
    }


}

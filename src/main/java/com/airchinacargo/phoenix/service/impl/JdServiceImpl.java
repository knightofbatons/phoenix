package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.SkuNum;
import com.airchinacargo.phoenix.domain.entity.SkuReplace;
import com.airchinacargo.phoenix.domain.entity.Token;
import com.airchinacargo.phoenix.domain.entity.YzTrade;
import com.airchinacargo.phoenix.domain.repository.ISkuReplaceRepository;
import com.airchinacargo.phoenix.domain.repository.ITokenRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;


/**
 * @author ChenYu 2018 03 15
 */
@Service
public class JdServiceImpl implements IJdService {

    @Autowired
    ITokenRepository tokenRepository;
    @Autowired
    ISkuReplaceRepository skuReplaceRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取京东 Token 需要的参数 前四个是从配置文件读取的
     * <p>
     * clientId 京东提供的对接账号
     * clientSecret 京东提供的对接账号的密码
     * username 京东的用户名
     * password 京东的用户密码
     */
    @Value("${Jd.CLIENT_ID}")
    private String clientId;
    @Value("${Jd.CLIENT_SECRET}")
    private String clientSecret;
    @Value("${Jd.USERNAME}")
    private String rowUsername;
    @Value("${Jd.PASSWORD}")
    private String password;
    private final int JD = 1;

    /**
     * 获取京东授权并更新 token 到数据库
     *
     * @return Token 京东的相关 token
     */
    @Override
    public Token getJdToken() {

        // 京东需要的时间格式
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String timestamp = mediumDateFormat.format(new Date());

        // SpringBoot 2.0 暂且存在从配置文件中读取中文乱码的问题 SpringBoot 1.x 的解决办法无效 所以在这里转码
        String username = null;
        try {
            username = new String(rowUsername.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String grantType = "access_token";
        String md5Password = md5Hex(password);
        String originalSign = clientSecret + timestamp + clientId + username + md5Password + grantType + clientSecret;
        String sign = md5Hex(originalSign).toUpperCase();

        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/oauth2/accessToken")
                    .queryString("grant_type", grantType)
                    .queryString("client_id", clientId)
                    .queryString("client_secret", clientSecret)
                    .queryString("timestamp", timestamp)
                    .queryString("username", username)
                    .queryString("password", md5Password)
                    .queryString("sign", sign)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        String token = response.getBody().getObject().getJSONObject("result").toString();
        Gson gson = new Gson();
        Token jdToken = gson.fromJson(token, new TypeToken<Token>() {
        }.getType());
        jdToken.setId(JD);
        jdToken.setDate(new Date());
        // 删除老 token 存入新的 但是防止数据库中不存在 token
        try {
            tokenRepository.deleteById(JD);
        } catch (Exception e) {
            logger.info("[ getJdToken ] --> never have Jd token before");
        }
        tokenRepository.save(jdToken);
        logger.info("[ getJdToken ] --> save new Jd token");
        return jdToken;
    }

    /**
     * 从数据库读取储存的 Token 如果不是当天的 就刷新 并更新数据库 然后返回
     *
     * @return Token 京东的相关 token
     */
    @Override
    public Token readJdToken() {
        // 从数据库读取 存在赋值不存在就去请求
        Token yzToken = tokenRepository.findById(JD).orElseGet(this::getJdToken);
        // 是当天的直接返回
        if (isToday(yzToken.getDate())) {
            logger.info("[ readJdToken ] --> return today token");
            return yzToken;
        }
        // 不是当天的调用刷新函数
        logger.info("[ readJdToken ] --> refresh token");
        return refreshJdToken(yzToken.getRefreshToken());
    }

    /**
     * 判断 token 是不是今天的 用来防止定时任务出错后的停摆 或者作为备用方案完全替代定时任务来刷新 token
     *
     * @param date 待测日期
     * @return boolean 是否是同一天
     */
    private boolean isToday(Date date) {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(now);
        String anotherDay = simpleDateFormat.format(date);
        return today.equals(anotherDay);
    }

    /**
     * 错误时返回的字符串
     */
    private final String FALSE = "false";

    /**
     * 用 refresh token 刷新 access token 计划添加在定时任务中每天刷新 刷新失败就重新请求
     *
     * @param refreshToken 授权时获取的 refresh token
     * @return Token 京东的相关 token
     */
    @Override
    public Token refreshJdToken(String refreshToken) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/oauth2/refreshToken")
                    .queryString("refresh_token", refreshToken)
                    .queryString("client_id", clientId)
                    .queryString("client_secret", clientSecret)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        // 检查刷新是否成功 未成功就调用请求函数
        String isSuccess = response.getBody().getObject().get("success").toString();
        if (FALSE.equals(isSuccess)) {
            logger.info("[ refreshJdToken ] --> refresh failed run getJdToken to get a new one");
            return getJdToken();
        }
        // 成功的话正常获取刷新得到的新 token
        String token = response.getBody().getObject().getJSONObject("result").toString();
        Gson gson = new Gson();
        Token jdToken = gson.fromJson(token, new TypeToken<Token>() {
        }.getType());
        jdToken.setId(JD);
        jdToken.setDate(new Date());
        tokenRepository.save(jdToken);
        logger.info("[ refreshJdToken ] --> refresh success");
        return jdToken;
    }

    /**
     * 通过详细地址获取京东地址
     *
     * @param accessToken 授权时获取的 access token
     * @param address     详细地址 例如 四川省成都市武侯区武科西五路 360 号
     * @return Map 京东四级地址的编码
     */
    @Override
    public Map<String, Integer> getJdAddressFromAddress(String address, String accessToken) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/area/getJDAddressFromAddress")
                    .queryString("token", accessToken)
                    .queryString("address", address)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        // 判断获取地址是否成功
        String isSuccess = response.getBody().getObject().get("success").toString();
        if (FALSE.equals(isSuccess)) {
            //TODO 获取地址不成功的情况之后需要考虑
            return null;
        }
        // 成功的情况正常获取
        Map<String, Integer> addressMap = new HashMap<>(4);
        JSONObject addressObject = response.getBody().getObject().getJSONObject("result");
        addressMap.put("province", addressObject.getInt("provinceId"));
        addressMap.put("city", addressObject.getInt("cityId"));
        addressMap.put("county", addressObject.getInt("countyId"));
        // 京东地址有可能不存在第四级地址
        addressMap.put("town", addressObject.getString("townId").isEmpty() ? 0 : addressObject.getInt("townId"));
        return addressMap;
    }


    /**
     * 京东货物状态代码
     * <p>
     * 34 无货
     * 36 预定
     * <p>
     * 33 有货 现货-下单立即发货
     * 39 有货 在途-正在内部配货，预计 2~6 天到达本仓库
     * 40 有货 可配货-下单后从有货仓库配货
     */
    private final static int OUT_OF_STOCK = 34;
    private final static int RESERVE = 36;


    /**
     * 用于下单时先行检查区域库存
     *
     * @param accessToken         授权时获取的 access token
     * @param skuNum              商品和数量  例如 [{skuId:569172,num:101}]
     * @param area                查询区域 由京东前三级地址编码组成 形如 1_0_0 分别代表 1、2、3 级地址
     * @param searchForOutOfStock 是否是查询缺货，不是的话就是查询有货的 逻辑为 34 || 36 缺货 !34 && !36 有货
     * @return List<String> 根据参数选择返回缺货列表或有货列表
     */
    @Override
    public List<String> getNewStockBySkuIdAndArea(String accessToken, List<SkuNum> skuNum, String area, Boolean searchForOutOfStock) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/ api/stock/getNewStockById")
                    .queryString("token", accessToken)
                    .queryString("skuNums", skuNum.toString())
                    .queryString("area", area)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JSONArray resultArray = new JSONArray(response.getBody().getObject().getString("result"));
        List<String> goodList = new ArrayList<>();
        if (searchForOutOfStock) {
            for (int i = 0; i < resultArray.length(); i++) {
                int stockStateId = resultArray.getJSONObject(i).getInt("stockStateId");
                if (OUT_OF_STOCK == stockStateId || RESERVE == stockStateId) {
                    goodList.add(String.valueOf(resultArray.getJSONObject(i).getInt("skuId")));
                }
            }
            return goodList;
        }
        for (int i = 0; i < resultArray.length(); i++) {
            int stockStateId = resultArray.getJSONObject(i).getInt("stockStateId");
            if (OUT_OF_STOCK != stockStateId && RESERVE != stockStateId) {
                goodList.add(String.valueOf(resultArray.getJSONObject(i).getInt("skuId")));
            }
        }
        return goodList;
    }

    /**
     * 替换缺货商品
     *
     * @param accessToken 授权时获取的 access token
     * @param skuNum      计划购买商品和数量
     * @param area        查询区域 由京东前三级地址编码组成 形如 1_0_0 分别代表 1、2、3 级地址
     * @return List<SkuNum> 返回真正购买的货物和数量
     */
    @Override
    public List<SkuNum> getNeedToBuy(String accessToken, List<SkuNum> skuNum, String area) {
        // 先查出计划购买中缺货的
        List<String> needToReplaceSkuIdList = getNewStockBySkuIdAndArea(accessToken, skuNum, area, true);
        logger.info("[ getNeedToBuy ] --> 缺货的 " + needToReplaceSkuIdList.toString());
        for (SkuNum s : skuNum) {
            // 如果缺货
            if (needToReplaceSkuIdList.contains(s.getSkuId())) {
                // 查出缺货的替代列表
                List<SkuReplace> skuReplaceList = skuReplaceRepository.findByBeforeSkuAndBeforeNum(s.getSkuId(), s.getNum());
                // 查出替代货品有货列表
                List<SkuNum> skuNumList = skuReplaceList.stream()
                        .map(p -> new SkuNum(p.getAfterSku(), p.getAfterNum()))
                        .collect(Collectors.toList());
                List<String> couldBeBuySkuIdList = getNewStockBySkuIdAndArea(accessToken, skuNumList, area, false);
                // 用替代列表里有货的把之前缺货的换了 有多个有货 第一个是谁算谁
                for (SkuReplace sr : skuReplaceList) {
                    if (couldBeBuySkuIdList.contains(sr.getAfterSku())) {
                        s.setSkuId(sr.getAfterSku());
                        s.setNum(sr.getAfterNum());
                        break;
                    }
                }
            }

        }
        return skuNum;
    }


    /**
     * 下单需要的参数
     * <p>
     * email 准备接收物流信息反馈的邮箱
     */
    @Value("${Fh.EMAIL}")
    private String email;

    /**
     * 在京东下单
     *
     * @param accessToken 授权时获取的 access token
     * @param yzTrade     有赞订单
     * @param skuNum      商品和数量等 [{"skuId": 商 品 编 号 , "num": 商 品 数 量 ,"bNeedAnnex":true,"bNeedGift":true, "price":100, "yanbao":[{"skuId": 商品编号}]}]
     * @param area        京东四级地址的编码
     */
    @Override
    public void submitOrder(String accessToken, YzTrade yzTrade, List<SkuNum> skuNum, Map<String, Integer> area) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/order/submitOrder")
                    .queryString("token", accessToken)
                    .queryString("thirdOrder", yzTrade.getTid())
                    .queryString("sku", skuNum.toString())
                    .queryString("name", yzTrade.getReceiverName())
                    .queryString("province", area.get("province"))
                    .queryString("city", area.get("city"))
                    .queryString("county", area.get("county"))
                    .queryString("town", area.get("town"))
                    .queryString("address", yzTrade.getReceiverAddress())
                    .queryString("mobile", yzTrade.getReceiverMobile())
                    .queryString("email", email)
                    // 订单开票方式 集中开票
                    .queryString("invoiceState", 2)
                    //发票类型 增值税发票
                    .queryString("invoiceType", 2)
                    // 发票类型 单位
                    .queryString("selectedInvoiceTitle", 5)
                    // 发票抬头 国航投资控股有限公司
                    .queryString("companyName", "国航投资控股有限公司")
                    // 增值税发票只能选择 明细
                    .queryString("invoiceContent", 1)
                    // 支付方式 余额支付
                    .queryString("paymentType", 4)
                    // 余额支付方式 固定选择使用余额
                    .queryString("isUseBalance", 1)
                    // 不预占库存
                    .queryString("submitState", 1)
                    // 不做价格对比
                    .queryString("doOrderPriceMode", 0)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().toString());
    }

}

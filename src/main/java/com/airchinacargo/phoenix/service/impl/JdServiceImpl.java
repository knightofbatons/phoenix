package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.*;
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
            return yzToken;
        }
        // 不是当天的调用刷新函数
        logger.info("[ readJdToken ] --> refresh JD token");
        return refreshJdToken(yzToken.getRefreshToken());
    }

    /**
     * 判断 token 是不是今天的
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
        boolean isSuccess = response.getBody().getObject().getBoolean("success");
        if (!isSuccess) {
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
        logger.info("[ getJdAddressFromAddress ] --> INPUT: address: " + address);
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
        boolean isSuccess = response.getBody().getObject().getBoolean("success");
        if (!isSuccess) {
            // 不行就百度根据详细获取经纬度 再京东根据经纬度获取地址编码 百度也拿不到就不处理了
            Map<String, Double> addressMap = getLatLngFromAddress(address);
            if (null == addressMap) {
                return null;
            }
            response = getJDAddressFromLatLng(accessToken, addressMap);
        }
        logger.info("[ getJdAddressFromAddress ] --> RESPONSE: " + response.getBody());
        // 成功的情况正常获取
        Map<String, Integer> addressMap = new HashMap<>(4);
        JSONObject addressObject = response.getBody().getObject().getJSONObject("result");
        addressMap.put("province", addressObject.getInt("provinceId"));
        addressMap.put("city", addressObject.getInt("cityId"));
        // 京东地址有可能不存在第三四级地址
        addressMap.put("county", addressObject.isNull("countyId") || addressObject.getString("countyId").isEmpty() || "null".equals(addressObject.getString("countyId")) ? 0 : addressObject.getInt("countyId"));
        addressMap.put("town", addressObject.isNull("townId") || addressObject.getString("townId").isEmpty() || "null".equals(addressObject.getString("townId")) ? 0 : addressObject.getInt("townId"));
        logger.info("[ getJdAddressFromAddress ] --> RETURN: addressMap: " + addressMap);
        return addressMap;
    }

    /**
     * 根据经纬度查询京东地址编码
     *
     * @param accessToken 授权时获取的 access token
     * @param latLngMap   经纬度
     * @return HttpResponse<JsonNode> HTTP 请求返回的结果
     */
    @Override
    public HttpResponse<JsonNode> getJDAddressFromLatLng(String accessToken, Map<String, Double> latLngMap) {
        logger.info("[ getJDAddressFromLatLng ] --> INPUT: latLngMap: " + latLngMap);
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/area/getJDAddressFromLatLng")
                    .queryString("token", accessToken)
                    .queryString("lng", latLngMap.get("lng"))
                    .queryString("lat", latLngMap.get("lat"))
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info("[ getJDAddressFromLatLng ] --> RESPONSE: " + response.getBody());
        return response;
    }

    /**
     * 使用百度地图开放平台 API 需要的参数 从配置文件读取
     * <p>
     * bdKey 百度密钥
     */
    @Value("${Bd.KEY}")
    private String bdKey;

    /**
     * 根据详细地址获取经纬度 使用百度 api
     *
     * @param address 详细地址
     * @return Map 目标地址经纬度
     */
    @Override
    public Map<String, Double> getLatLngFromAddress(String address) {
        logger.info("[ getLatLngFromAddress ] --> INPUT: address: " + address);
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get("http://api.map.baidu.com/geocoder/v2/")
                    .queryString("ak", bdKey)
                    .queryString("output", "json")
                    .queryString("address", address)
                    .asJson();
        } catch (UnirestException e) {
            logger.info("[ getLatLngFromAddress ] --> BAI_DU_API_ERROR");
            return null;
        }
        try {
            JSONObject latLngObject = response.getBody().getObject().getJSONObject("result").getJSONObject("location");
            Map<String, Double> latLngMap = new HashMap<>(2);
            latLngMap.put("lat", latLngObject.getDouble("lat"));
            latLngMap.put("lng", latLngObject.getDouble("lng"));
            logger.info("[ getLatLngFromAddress ] --> RETURN: latLngMap: " + latLngMap);
            return latLngMap;
        } catch (Exception e) {
            logger.info("[ getLatLngFromAddress ] --> RETURN: null: ");
            return null;
        }
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
     * @param searchForOutOfStock 是否是查询缺货，不是的话就是查询有货的 逻辑为 34 || 36 缺货 !34 && !36 有货 / 暂行逻辑为 34 缺货 !34 有货
     * @return List<String> 根据参数选择返回缺货列表或有货列表
     */
    @Override
    public List<String> getNewStockBySkuIdAndArea(String accessToken, List<SkuNum> skuNum, String area, boolean searchForOutOfStock) {
        logger.info("[ getNewStockBySkuIdAndArea ] --> INPUT: skuNum: " + skuNum.toString() + " area: " + area + " searchForOutOfStock " + searchForOutOfStock);
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/stock/getNewStockById")
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
                if (OUT_OF_STOCK == stockStateId) {
                    goodList.add(String.valueOf(resultArray.getJSONObject(i).getInt("skuId")));
                }
            }
            logger.info("[ getNewStockBySkuIdAndArea ] --> RETURN: goodList: " + goodList);
            return goodList;
        }
        for (int i = 0; i < resultArray.length(); i++) {
            int stockStateId = resultArray.getJSONObject(i).getInt("stockStateId");
            if (OUT_OF_STOCK != stockStateId) {
                goodList.add(String.valueOf(resultArray.getJSONObject(i).getInt("skuId")));
            }
        }
        logger.info("[ getNewStockBySkuIdAndArea ] --> RETURN: goodList: " + goodList);
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
        logger.info("[ getNeedToBuy ] --> INPUT: skuNum: " + skuNum.toString() + " area: " + area);
        // 先查出计划购买中缺货的
        List<String> needToReplaceSkuIdList = getNewStockBySkuIdAndArea(accessToken, skuNum, area, true);
        for (SkuNum s : skuNum) {
            // 如果当前商品缺货
            if (needToReplaceSkuIdList.contains(s.getSkuId())) {
                // 查出当前缺货商品的可替代列表 结果是按照 id 排序的 id 的意义是替换的优先度
                List<SkuReplace> skuReplaceList = skuReplaceRepository.findByBeforeSkuAndBeforeNumOrderById(s.getSkuId(), s.getNum());
                // 查出可替代货品的有货列表
                List<SkuNum> skuNumList = skuReplaceList.stream()
                        .map(p -> new SkuNum(p.getAfterSku(), p.getAfterNum()))
                        .collect(Collectors.toList());
                List<String> couldBeBuySkuIdList = getNewStockBySkuIdAndArea(accessToken, skuNumList, area, false);
                // 如果这个缺货商品的可替代货品全部缺货 那么停止并返回 null
                if (0 == couldBeBuySkuIdList.size()) {
                    return null;
                }
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
        logger.info("[ getNeedToBuy ] --> RETURN: skuNum: " + skuNum.toString());
        return skuNum;
    }


    /**
     * 下单需要的参数
     * <p>
     * email 准备接收物流信息反馈的邮箱
     * PAYMENT_MAX 最多付款为 20 块邮费
     */
    @Value("${Fh.EMAIL}")
    private String email;
    private final Double PAYMENT_MAX = 20.0;

    /**
     * 在京东下单
     *
     * @param accessToken 授权时获取的 access token
     * @param yzTrade     有赞订单
     * @param skuNum      商品和数量等 [{"skuId": 商 品 编 号 , "num": 商 品 数 量 ,"bNeedAnnex":true,"bNeedGift":true, "price":100, "yanbao":[{"skuId": 商品编号}]}]
     * @param area        京东四级地址的编码
     * @return SysTrade 需要被记录的已处理订单信息
     */
    @Override
    public SysTrade submitOrder(String accessToken, YzTrade yzTrade, List<SkuNum> skuNum, Map<String, Integer> area) {
        String address = yzTrade.getReceiverState() + yzTrade.getReceiverCity() + yzTrade.getReceiverDistrict() + yzTrade.getReceiverAddress();
        // 判断是不是有缺货商品的所有可替代都缺货
        if (null == skuNum) {
            return new SysTrade(yzTrade.getTid(), "NO_JD_ORDER_ID", new Date(), "存在商品缺货且不能替换", 0.00, false, false, yzTrade.getReceiverName(), yzTrade.getReceiverMobile(), address, yzTrade.getCoupons().get(0).getCouponName());
        }
        // 判断是不是实际花钱购买的 最多付款
        if (PAYMENT_MAX < yzTrade.getPayment()) {
            return new SysTrade(yzTrade.getTid(), "NO_JD_ORDER_ID", new Date(), "此订单是实际付款订单", 0.00, false, false, yzTrade.getReceiverName(), yzTrade.getReceiverMobile(), address, "NO_COUPONS_USED");
        }
        HttpResponse<JsonNode> response = null;
        try {
            logger.info("[ submitOrder ] --> API_INPUT: thirdOrder: " + yzTrade.getTid() + " sku: " + skuNum.toString() + " name: " + yzTrade.getReceiverName() + " province: " + area.get("province") + " city: " + area.get("city") + " county: " + area.get("county") + " town: " + area.get("town") + " address: " + yzTrade.getReceiverAddress() + " mobile: " + yzTrade.getReceiverMobile());
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
        logger.info("[ submitOrder ] --> API_RESPONSE: " + response.getBody());
        // 检查下单是否成功
        JSONObject reJsonObject = response.getBody().getObject();
        boolean isSuccess = reJsonObject.getBoolean("success");
        String resultMessage = reJsonObject.getString("resultMessage");
        // 如果下单失败
        if (!isSuccess) {
            return new SysTrade(yzTrade.getTid(), "NO_JD_ORDER_ID", new Date(), resultMessage, 0.00, false, false, yzTrade.getReceiverName(), yzTrade.getReceiverMobile(), address, yzTrade.getCoupons().get(0).getCouponName());
        }
        // 下单成功
        JSONObject result = reJsonObject.getJSONObject("result");
        return new SysTrade(yzTrade.getTid(), String.valueOf(result.getLong("jdOrderId")), new Date(), resultMessage, result.getDouble("orderPrice"), true, false, yzTrade.getReceiverName(), yzTrade.getReceiverMobile(), address, yzTrade.getCoupons().get(0).getCouponName());
    }

    /**
     * 根据第三方订单号进行订单反查
     *
     * @param accessToken 授权时获取的 access token
     * @param thirdOrder  客户系统订单号 这里是有赞 tid
     */
    @Override
    public void selectJdOrderIdByThirdOrder(String accessToken, String thirdOrder) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/order/selectJdOrderIdByThirdOrder")
                    .queryString("token", accessToken)
                    .queryString("thirdOrder", thirdOrder)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().toString());
    }

    /**
     * 查询京东订单信息
     *
     * @param accessToken 授权时获取的 access token
     * @param jdOrderId   京东订单号
     * @return JSONObject HTTP 请求返回的结果
     */
    @Override
    public JSONObject selectJdOrder(String accessToken, String jdOrderId) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/order/selectJdOrder")
                    .queryString("token", accessToken)
                    .queryString("jdOrderId", jdOrderId)
                    .queryString("queryExts", "jdOrderState")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response.getBody().getObject();
    }

    /**
     * 查询子订单配送信息
     *
     * @param accessToken 授权时获取的 access token
     * @param jdOrderId   京东订单号
     * @return JSONObject HTTP 请求返回的结果
     */
    @Override
    public JSONObject orderTrack(String accessToken, String jdOrderId) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/order/orderTrack")
                    .queryString("token", accessToken)
                    .queryString("jdOrderId", jdOrderId)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response.getBody().getObject();
    }

    /**
     * 统一余额查询
     *
     * @param accessToken 授权时获取的 access token
     * @return String 余额
     */
    @Override
    public String getBalance(String accessToken) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/price/getBalance")
                    .queryString("token", accessToken)
                    .queryString("payType", 4)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response.getBody().getObject().getString("result");
    }

    /**
     * 批量查询商品价格
     *
     * @param accessToken 授权时获取的 access token
     * @param skuList     商品编号，请以，(英文逗号)分割(最高支持 100 个商品)。例如：129408,129409
     */
    @Override
    public void getSellPrice(String accessToken, String skuList) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/price/getSellPrice")
                    .queryString("token", accessToken)
                    .queryString("sku", skuList)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().toString());
    }

    /**
     * 取消订单
     *
     * @param accessToken 授权时获取的 access token
     * @param jdOrderId   京东的订单单号（父订单号）
     */
    @Override
    public void cancel(String accessToken, String jdOrderId) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/order/cancel\n")
                    .queryString("token", accessToken)
                    .queryString("jdOrderId", jdOrderId)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().toString());
    }

    /**
     * 获取京东信息推送池中的信息
     *
     * @param accessToken 授权时获取的 access token
     * @param type        推送类型 支持多个 例如 1,2,3
     * @return JSONObject HTTP 请求返回的结果
     */
    @Override
    public JSONObject messageGet(String accessToken, String type) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/message/get")
                    .queryString("token", accessToken)
                    .queryString("type", type)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response.getBody().getObject();
    }

    /**
     * 删除消息池中信息
     *
     * @param accessToken 授权时获取的 access token
     * @param id          推送信息 id 支持批量删除，英文逗号间隔，最大 100 个
     */
    @Override
    public void messageDel(String accessToken, String id) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/message/del")
                    .queryString("token", accessToken)
                    .queryString("id", id)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().getObject().toString());
    }
}

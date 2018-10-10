package com.airchinacargo.phoenix.service.interfaces;

import com.airchinacargo.phoenix.domain.entity.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author ChenYu 2018 03 15
 */
public interface IJdService {

    /**
     * 获取京东授权并更新 token 到数据库
     *
     * @return Token 京东的相关 token
     */
    Token getJdToken();

    /**
     * 从数据库读取储存的 Token 如果不是当天的 就刷新 并更新数据库 然后返回
     *
     * @return Token 京东的相关 token
     */
    Token readJdToken();

    /**
     * 用 refresh token 刷新 access token 计划添加在定时任务中每天刷新 刷新失败就重新请求
     *
     * @param refreshToken 授权时获取的 refresh token
     * @return Token 京东的相关 token
     */
    Token refreshJdToken(String refreshToken);

    /**
     * 通过详细地址获取京东地址
     *
     * @param accessToken 授权时获取的 access token
     * @param address     详细地址 例如 四川省成都市武侯区武科西五路 360 号
     * @return Map 京东四级地址的编码
     */
    Map<String, Integer> getJdAddressFromAddress(String address, String accessToken);

    /**
     * 根据经纬度查询京东地址编码
     *
     * @param accessToken 授权时获取的 access token
     * @param latLngMap   经纬度
     * @return HttpResponse<JsonNode> HTTP 请求返回的结果
     */
    HttpResponse<JsonNode> getJDAddressFromLatLng(String accessToken, Map<String, Double> latLngMap);

    /**
     * 根据详细地址获取经纬度 使用百度 api
     *
     * @param address 详细地址
     * @return Map 目标地址经纬度
     */
    Map<String, Double> getLatLngFromAddress(String address);

    /**
     * 用于下单时先行检查区域库存
     *
     * @param accessToken         授权时获取的 access token
     * @param skuNum              商品和数量  例如 [{skuId:569172,num:101}]
     * @param area                查询区域 由京东前三级地址编码组成 形如 1_0_0 分别代表 1、2、3 级地址
     * @param searchForOutOfStock 是否是查询缺货，不是的话就是查询有货的
     * @return List<String> 返回缺货列表
     */
    List<String> getNewStockBySkuIdAndArea(String accessToken, List<SkuNum> skuNum, String area, boolean searchForOutOfStock);

    /**
     * 替换缺货商品
     *
     * @param accessToken 授权时获取的 access token
     * @param skuNum      计划购买商品和数量
     * @param area        查询区域 由京东前三级地址编码组成 形如 1_0_0 分别代表 1、2、3 级地址
     * @return List<SkuNum> 返回真正购买的货物和数量
     */
    List<SkuNum> getNeedToBuy(String accessToken, List<SkuNum> skuNum, String area);

    /**
     * 在京东下单
     *
     * @param accessToken 授权时获取的 access token
     * @param yzTrade     有赞订单
     * @param skuNum      商品和数量等 [{"skuId": 商 品 编 号 , "num": 商 品 数 量 ,"bNeedAnnex":true,"bNeedGift":true, "price":100, "yanbao":[{"skuId": 商品编号}]}]
     * @param area        京东四级地址的编码
     * @return SysTrade 需要被记录的已处理订单信息
     */
    SysTrade submitOrder(String accessToken, YzTrade yzTrade, List<SkuNum> skuNum, Map<String, Integer> area);

    /**
     * 根据第三方订单号进行订单反查
     *
     * @param accessToken 授权时获取的 access token
     * @param thirdOrder  客户系统订单号 这里是有赞 tid
     */
    void selectJdOrderIdByThirdOrder(String accessToken, String thirdOrder);

    /**
     * 查询京东订单信息
     *
     * @param accessToken 授权时获取的 access token
     * @param jdOrderId   京东订单号
     * @return JSONObject HTTP 请求返回的结果
     */
    JSONObject selectJdOrder(String accessToken, String jdOrderId);

    /**
     * 查询子订单配送信息
     *
     * @param accessToken 授权时获取的 access token
     * @param jdOrderId   京东订单号
     * @return JSONObject HTTP 请求返回的结果
     */
    JSONObject orderTrack(String accessToken, String jdOrderId);

    /**
     * 统一余额查询
     *
     * @param accessToken 授权时获取的 access token
     * @return String 余额
     */
    String getBalance(String accessToken);

    /**
     * 批量查询商品价格
     *
     * @param accessToken 授权时获取的 access token
     * @param skuList     商品编号，请以，(英文逗号)分割(最高支持 100 个商品)。例如：129408,129409
     * @return JSONObject
     */
    JSONObject getSellPrice(String accessToken, String skuList);

    /**
     * 取消订单
     *
     * @param accessToken 授权时获取的 access token
     * @param jdOrderId   京东的订单单号（父订单号）
     */
    void cancel(String accessToken, String jdOrderId);

    /**
     * 获取京东信息推送池中的信息
     *
     * @param accessToken 授权时获取的 access token
     * @param type        推送类型 支持多个 例如 1,2,3
     * @return JSONObject HTTP 请求返回的结果
     */
    JSONObject messageGet(String accessToken, String type);

    /**
     * 删除消息池中信息
     *
     * @param accessToken 授权时获取的 access token
     * @param id          推送信息 id 支持批量删除，英文逗号间隔，最大 100 个
     */
    void messageDel(String accessToken, String id);

    /**
     * 获取所有在售卖的商品列表 包括可替换到的
     *
     * @return List<String>
     */
    List<String> getAllSellSku();

    /**
     * 发票提报
     *
     * @param accessToken             授权时获取的 access token
     * @param supplierOrder           需要开发票的 子订单号，批量以，分割
     * @param markId                  第三方申请单号：申请发票的唯一id标识 (该标记下可以对应多张发票信息)
     * @param settlementId            结算单号（一个结算单号可对对应多个第三方申请单号）
     * @param area                    京东四级地址的编码
     * @param invoiceDate             期望开票时间 2013-11-8
     * @param invoiceNum              当前批次子订单总数
     * @param invoicePrice            当前批次含税总金额
     * @param currentBatch            当前批次号
     * @param totalBatch              总批次数
     * @param totalBatchInvoiceAmount 总批次开发票价税合计
     */
    void invoiceSubmit(String accessToken, String supplierOrder, String markId, String settlementId, Map<String, Integer> area, String invoiceDate, int invoiceNum, BigDecimal invoicePrice, int currentBatch, int totalBatch, BigDecimal totalBatchInvoiceAmount);

    /**
     * 查询发票信息
     *
     * @param accessToken 授权时获取的 access token
     * @param markId      第三方申请单号：申请发票的唯一id标识 (该标记下可以对应多张发票信息)
     */
    void invoiceSelect(String accessToken, String markId);

    /**
     * 根据选择开票的系统订单编号 得到详细的京东子单列表
     *
     * @param accessToken 授权时获取的 access token
     * @param beginId     开始系统订单编码
     * @param endId       结束系统订单编码
     * @return needToInvoiceJdOrder 想要开票的京东子单列表
     */
    List<JdOrder> getNeedToInvoiceJdOrderList(String accessToken, int beginId, int endId);

    /**
     * 开取发票
     *
     * @param accessToken 授权时获取的 access token
     * @param beginId     开始系统订单编码
     * @param endId       结束系统订单编码
     */
    void invoice(String accessToken, int beginId, int endId);

    /**
     * 测试开取发票准备入参 显示信息 但是不开发票
     *
     * @param accessToken 授权时获取的 access token
     * @param beginId     开始系统订单编码
     * @param endId       结束系统订单编码
     */
    void invoiceTest(String accessToken, int beginId, int endId);
}

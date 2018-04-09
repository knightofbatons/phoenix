package com.airchinacargo.phoenix.service.interfaces;

import com.airchinacargo.phoenix.domain.entity.SkuNum;
import com.airchinacargo.phoenix.domain.entity.Token;
import com.airchinacargo.phoenix.domain.entity.YzTrade;

import java.util.List;

/**
 * @author ChenYu 2018 03 13
 */
public interface IYzService {

    /**
     * 获取有赞授权并更新 token 到数据库
     *
     * @return Token
     */
    Token getYzToken();

    /**
     * 从数据库读取储存的 token 如果不过时直接返回 过时更新数据库并返回
     *
     * @return String 有赞的 access token
     */
    String readYzToken();

    /**
     * 获取付款未发货订单
     *
     * @param accessToken 读到的有赞授权 token
     * @return List<YzTrade> 返回包含所有 tread 的 List tread 里 包含 order 这个 order 在新的订单设置下和以往不同会有多个
     */
    List<YzTrade> getYzTradesSold(String accessToken);


    /**
     * 根据订单号查询订单详细
     *
     * @param accessToken 读到的有赞授权 token
     * @param tid         有赞订单号
     * @return YzTrade  有赞订单信息
     */
    YzTrade getYzTreadByTid(String accessToken, String tid);

    /**
     * 通过有赞订单获取计划在京东购买的商品和数量
     *
     * @param yzTrade 单个有赞订单
     * @return List<SkuNum> 返回期望在京东购买的 skuId 和 num 列表
     */
    List<SkuNum> getSkuIdAndNum(YzTrade yzTrade);


    /**
     * 发货
     *
     * @param accessToken 读到的有赞授权 token
     * @param tid         有赞订单 id
     * @param jdOrderId   京东订单 id
     * @param oidList     这个京东订单下的有赞子订单 用于分单发货
     */
    void confirm(String accessToken, String tid, String jdOrderId, String oidList);

    /**
     * 无物流发货
     *
     * @param accessToken 读到的有赞授权 token
     * @param tid         有赞订单 id
     * @param jdOrderId   京东订单 id
     */
    void confirmNoExpress(String accessToken, String tid, String jdOrderId);
}

package com.airchinacargo.phoenix.service.interfaces;

import com.airchinacargo.phoenix.domain.entity.YzToken;
import com.airchinacargo.phoenix.domain.entity.YzTrade;

import java.util.List;

/**
 * @author ChenYu 2018 03 13
 */
public interface IYzService {

    /**
     * 获取有赞授权并更新 Token 到数据库
     *
     * @return YzToken 用于有赞免签名请求有效期 7 天
     */
    YzToken getYzToken();

    /**
     * 从数据库读取储存的 Token 如果不过时直接返回 过时更新数据库并返回
     *
     * @return Token
     */
    String readYzToken();

    /**
     * 获取付款未发货订单
     *
     * @param accessToken 读到的有赞授权 token
     * @return List<YzTrade> yzTrades 返回包含所有 tread 的 List tread 里 包含 order 这个 order 在新的订单设置下和以往不同会有多个
     */
    List<YzTrade> getYzTradesSold(String accessToken);
}

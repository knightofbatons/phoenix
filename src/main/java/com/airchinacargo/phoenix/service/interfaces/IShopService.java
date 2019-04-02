package com.airchinacargo.phoenix.service.interfaces;

/**
 * @author ChenYu 2018 12 13
 */
public interface IShopService {
    /**
     * 上一批次处理的系统订单 在京东查询物流信息 回填到有赞
     *
     * @param jdToken 授权
     * @param yzToken 授权
     */
    void shopConfirm(String jdToken, String yzToken);

    /**
     * 遍历读取所有有赞付款未发货订单 完成在京东的购买
     *
     * @param jdToken 授权
     * @param yzToken 授权
     */
    void shopBuy(String jdToken, String yzToken);
}

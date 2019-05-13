package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.*;
import com.airchinacargo.phoenix.domain.repository.ISkuReplaceRepository;
import com.airchinacargo.phoenix.domain.repository.ISysTradeRepository;
import com.airchinacargo.phoenix.domain.repository.IYzToJdRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.airchinacargo.phoenix.service.interfaces.IShopService;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ChenYu 2018 12 13
 */
@Service
public class ShopServiceImpl implements IShopService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IYzService yzService;
    @Autowired
    IJdService jdService;
    @Autowired
    ISysTradeRepository sysTradeRepository;
    @Autowired
    IYzToJdRepository yzToJdRepository;
    @Autowired
    ISkuReplaceRepository skuReplaceRepository;

    /**
     * 上一批次处理的系统订单 在京东查询物流信息 回填到有赞
     *
     * @param jdToken 授权
     * @param yzToken 授权
     */
    @Override
    public void shopConfirm(String jdToken, String yzToken) {
        // 获取 sys_Trade 表中记录的提交京东成功但未发货订单
        List<SysTrade> needToConfirmSysTradeList = sysTradeRepository.findBySuccessAndConfirm(true, false).orElse(null);
        // 如果存在此类订单 给这些订单在有赞平台发货
        if (null != needToConfirmSysTradeList) {
            // 拆单发货
            for (SysTrade sysTrade : needToConfirmSysTradeList) {
                // 查询京东订单详细信息
                JSONObject responseObject = jdService.selectJdOrder(jdToken, sysTrade.getJdOrderId());
                // 如果京东拆单了 （这里判断的依据是如果没拆单那么父单号也就是子单号 pOrder 字段不会是是 JSONObject 类型而会是 0）
                if (responseObject.getJSONObject("result").get("pOrder") instanceof JSONObject) {
                    // 查询有赞订单详细信息
                    List<YzOrder> yzOrderList = yzService.getYzTreadByTid(yzToken, sysTrade.getTid()).getOrders();
                    // 查询 有赞京东商品对应关系 yzToJd 和 替换列表 skuReplace 找到 jdOrderId 下对应的 oid
                    // 获取京东子单列表
                    Gson gson = new Gson();
                    List<JdOrder> jdOrderList = gson.fromJson(responseObject.getJSONObject("result").get("cOrder").toString(), new TypeToken<List<JdOrder>>() {
                    }.getType());
//                    // 遍历子单
////                    for (JdOrder jdOrder : jdOrderList) {
////                        List<String> oidList = new ArrayList<>();
////                        // 遍历此京东子单下的商品
////                        for (JdSku jdSku : jdOrder.getSku()) {
////                            // 根据 skuId 和 num 找到 itemId 先去有赞京东商品对应表里找 找不到就是已经缺货替换过了 去京东商品替换里找到替换之前的 再去有赞京东商品对应表里找
////                            String itemId = yzToJdRepository.findBySkuIdAndNum(String.valueOf(jdSku.getSkuId()), jdSku.getNum())
////                                    .orElseGet(() -> yzToJdRepository.findBySkuIdAndNum(
////                                            skuReplaceRepository.findByAfterSkuAndAfterNum(String.valueOf(jdSku.getSkuId()), jdSku.getNum()).getBeforeSku(),
////                                            skuReplaceRepository.findByAfterSkuAndAfterNum(String.valueOf(jdSku.getSkuId()), jdSku.getNum()).getBeforeNum()
////                                    ).orElse(null)).getItemId();
////                            // 找到这些商品对应的有赞订单 oid 可能会有多个
////                            oidList.addAll(
////                                    yzOrderList.stream()
////                                            .filter(p -> p.getItemId().equals(itemId))
////                                            .map(p -> p.getOid())
////                                            .collect(toList()));
////                        }
////                        // 在有赞物流拆单发货
////                        logger.info("[ 分单发货 ] --> " + jdOrder.getJdOrderId() + " : " + oidList.toString());
////                        yzService.confirm(yzToken, sysTrade.getTid(), String.valueOf(jdOrder.getJdOrderId()), StringUtils.stripFrontBack(oidList.toString(), "[", "]").replaceAll(" ", ""));
////                    }
                    logger.info("[ 分单单物流信息发货 ] --> ");
                    yzService.confirmNoSplit(yzToken, sysTrade.getTid(), String.valueOf(jdOrderList.get(0).getJdOrderId()));
                    sysTradeRepository.updateIsConfirm(sysTrade.getTid());
                } else {
                    // 否则就是京东没有拆单 在有赞直接发货
                    logger.info("[ 一起发货 ] --> " + sysTrade.getJdOrderId());
                    yzService.confirmNoSplit(yzToken, sysTrade.getTid(), sysTrade.getJdOrderId());
                    sysTradeRepository.updateIsConfirm(sysTrade.getTid());
                }
            }
        }
    }

    /**
     * 遍历读取所有有赞付款未发货订单 完成在京东的购买
     *
     * @param jdToken 授权
     * @param yzToken 授权
     */
    @Override
    public void shopBuy(String jdToken, String yzToken) {
        // 获取有赞所有付款未发货订单
        List<YzTrade> yzTradeList = yzService.getYzTradesSold(yzToken);
        // 如果不为空遍历这些订单
        if (null != yzTradeList) {
            for (YzTrade yzTrade : yzTradeList) {
                // 如果没有之前处理过的记录
                if (!sysTradeRepository.findByTid(yzTrade.getTid()).isPresent()) {
                    // 根据有赞京东商品对应关系获取计划购买商品数量列表
                    List<SkuNum> planSkuNum = yzService.getSkuIdAndNum(yzTrade);
                    // 判断这个订单是否需要处理 （订单内至少包含一个对应的商品，且少于 10 个）
                    if (planSkuNum.size() > 0 && planSkuNum.size() < 10) {
                        // 准备下单需要的参数
                        String address = yzTrade.getReceiverState() + yzTrade.getReceiverCity() + yzTrade.getReceiverDistrict() + yzTrade.getReceiverAddress();
                        Map<String, Integer> addressMap = jdService.getJdAddressFromAddress(address, jdToken);
                        // 如果获取地址正常
                        if (null != addressMap) {
                            String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("county");
                            List<SkuNum> realSkuNum = jdService.getNeedToBuy(jdToken, planSkuNum, area);
                            // 在京东下单并获得下单结果
                            SysTrade sysTrade = jdService.submitOrder(jdToken, yzTrade, realSkuNum, addressMap);
                            // 无论下单成功与否保存处理记录到数据库
                            logger.info("[ submitOrder ] --> RETURN: " + sysTrade.toString());
                            sysTradeRepository.save(sysTrade);
                        } else {
                            // 处理地址不正常订单记录到数据库
                            sysTradeRepository.save(new SysTrade(yzTrade.getTid(), "NO_JD_ORDER_ID", new Date(), "地址无法解析", 0.00, false, false, yzTrade.getReceiverName(), yzTrade.getReceiverMobile(), address, yzTrade.getCoupons().get(0).getCouponName(), 0));
                        }
                    }
                }
            }
        }
    }
}

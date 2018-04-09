package com.airchinacargo.phoenix.controller;

import com.airchinacargo.phoenix.domain.entity.SkuNum;
import com.airchinacargo.phoenix.domain.entity.SysTrade;
import com.airchinacargo.phoenix.domain.entity.YzTrade;
import com.airchinacargo.phoenix.domain.repository.ISysTradeRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定时任务每个小时的 54 分00 秒 执行
 *
 * @author ChenYu 2018 04 08
 */
@Component
public class ScheduledTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IYzService yzService;
    @Autowired
    IJdService jdService;
    @Autowired
    ISysTradeRepository sysTradeRepository;

    @Scheduled(cron = "0 54 * * * *")
    public void test() {
        logger.info("[ 定时任务运行 ] --> ");
        // 获取有赞所有付款未发货订单 （取最新至多20条）
        String yzToken = yzService.readYzToken();
        List<YzTrade> yzTradeList = yzService.getYzTradesSold(yzToken);
        String accessToken = jdService.readJdToken().getAccessToken();
        // 遍历这些订单
        for (YzTrade yzTrade : yzTradeList) {
            // 如果没有之前处理过的记录
            if (!sysTradeRepository.findByTid(yzTrade.getTid()).isPresent()) {

                // 根据有赞京东商品对应关系获取计划购买商品数量列别
                List<SkuNum> planSkuNum = yzService.getSkuIdAndNum(yzTrade);
                // 判断这个订单是否需要处理 （订单内至少包含两个对应的商品）
                if (planSkuNum.size() > 1) {
                    // 准备下单需要的参数
                    String address = yzTrade.getReceiverState() + yzTrade.getReceiverCity() + yzTrade.getReceiverDistrict() + yzTrade.getReceiverAddress();
                    Map<String, Integer> addressMap = jdService.getJdAddressFromAddress(address, accessToken);
                    String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("city");
                    List<SkuNum> realSkuNum = jdService.getNeedToBuy(accessToken, planSkuNum, area);
                    // 在京东下单并获得下单结果
                    SysTrade sysTrade = jdService.submitOrder(accessToken, yzTrade, realSkuNum, addressMap);
                    // 判断下单结果如果成功就在有赞发货
                    if (sysTrade.getSuccess()) {
                        //TODO 目前不拆单反馈物流信息仅做无物流发货是循环得以继续 之后需要拆单发货
                        yzService.confirmNoExpress(yzToken, sysTrade.getTid(), sysTrade.getJdOrderId());
                    }
                    // 无论成功与否保存处理记录到数据库
                    sysTradeRepository.save(sysTrade);
                    logger.info("[ 此次定时任务处理订单结果 ] --> " + sysTrade.toString());
                }

            }
        }
    }
}

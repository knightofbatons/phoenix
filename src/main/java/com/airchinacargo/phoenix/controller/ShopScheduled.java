package com.airchinacargo.phoenix.controller;

import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.airchinacargo.phoenix.service.interfaces.IShopService;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务每个小时的 ?? 分 ?? 秒 执行
 *
 * @author ChenYu 2018 04 08
 */
@Component
public class ShopScheduled {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IYzService yzService;
    @Autowired
    IJdService jdService;
    @Autowired
    IShopService shopService;

    /**
     * 每小时的 02 分运行一次定时任务 进行系统的基本 购买 和 发货 操作
     */
    @Scheduled(cron = "0 2 * * * *")
    public void shop() {
        logger.info("[ 定时任务运行 ] --> ");
        // 获取两种授权
        String jdToken = jdService.readJdToken().getAccessToken();
        String yzToken = yzService.readYzToken();
        logger.info("[ 开始物流信息回填 ] --> ");
        shopService.shopConfirm(jdToken, yzToken);
        logger.info("[ 开始购买 ] --> ");
        shopService.shopBuy(jdToken, yzToken);
        logger.info(" <-- [ 定时任务结束 ]");
    }
}

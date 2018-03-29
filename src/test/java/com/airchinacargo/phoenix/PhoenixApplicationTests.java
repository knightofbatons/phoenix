package com.airchinacargo.phoenix;

import com.airchinacargo.phoenix.domain.entity.SkuNum;
import com.airchinacargo.phoenix.domain.entity.YzOrder;
import com.airchinacargo.phoenix.domain.entity.YzTrade;
import com.airchinacargo.phoenix.domain.repository.IYzToJdRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PhoenixApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void contextLoads() {
    }

    @Autowired
    IYzService yzService;
    @Autowired
    IYzToJdRepository yzToJdRepository;

    /**
     * 测试读取有赞 access token
     */
    @Test
    public void readYzToken() {
        logger.info(yzService.readYzToken());
    }

    /**
     * 测试获取有赞订单
     */
    @Test
    public void getYzTradesSoldTest() {
        logger.info(yzService.getYzTradesSold(yzService.readYzToken()).toString());
    }

    /**
     * 测试从有赞订单获取期望在京东购买的商品以及数量
     */
    @Test
    public void getSkuIdAndNumTest() {
        YzTrade yzTrade = new YzTrade();

        List<YzOrder> yzOrderList = Arrays.asList(
                new YzOrder(1, "1", "1", "410875958", "1"),
                new YzOrder(1, "2", "2", "410875960", "2"),
                new YzOrder(10, "3", "3", "410876553", "3"),
                new YzOrder(10, "3", "3", "410876551", "3")
        );

        yzTrade.setOrders(yzOrderList);

        logger.info(yzService.getSkuIdAndNum(yzTrade).toString());
    }

    @Autowired
    IJdService jdService;

    /**
     * 测试读取京东 token
     */
    @Test
    public void readJdTokenTest() {
        logger.info(jdService.readJdToken().getDate().toString());
    }

    /**
     * 测试刷新京东 token
     * 数据库没有应该可以从新获得
     */
    @Test
    public void refreshJdTokenTest() {
        jdService.refreshJdToken(jdService.readJdToken().getRefreshToken());
    }

    /**
     * 测试京东获取地址
     */
    @Test
    public void getJdAddressFromAddressTest() {
        logger.info(String.valueOf(jdService.getJdAddressFromAddress("北京市顺义区天竺空港经济开发区天柱路29号", jdService.readJdToken().getAccessToken())));
    }

    /**
     * 测试京东查询区域库存
     */
    @Test
    public void getNewStockBySkuIdAndAreaTest() {
        YzTrade yzTrade = new YzTrade();

        List<YzOrder> yzOrderList = Arrays.asList(
                new YzOrder(1, "1", "1", "410875958", "1"),
                new YzOrder(1, "2", "2", "410875960", "2"),
                new YzOrder(10, "3", "3", "410876553", "3")
        );

        yzTrade.setOrders(yzOrderList);


        String accessToken = jdService.readJdToken().getAccessToken();
        Map<String, Integer> addressMap = jdService.getJdAddressFromAddress("北京市顺义区天竺空港经济开发区天柱路29号", accessToken);
        String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("city");
        logger.info(jdService.getNewStockBySkuIdAndArea(accessToken, yzService.getSkuIdAndNum(yzTrade), area, false).toString());
        logger.info(jdService.getNewStockBySkuIdAndArea(accessToken, yzService.getSkuIdAndNum(yzTrade), area, true).toString());
    }

    /**
     * 测试获取真正要买的商品
     */
    @Test
    public void getNeedToBuyTest() {
        YzTrade yzTrade = new YzTrade();
        List<YzOrder> yzOrderList = Arrays.asList(
                new YzOrder(1, "1", "1", "410875958", "1"),
                new YzOrder(1, "2", "2", "410875960", "2"),
                new YzOrder(10, "3", "3", "410876553", "3")
        );
        yzTrade.setOrders(yzOrderList);

        String accessToken = jdService.readJdToken().getAccessToken();
        Map<String, Integer> addressMap = jdService.getJdAddressFromAddress("北京市顺义区天竺空港经济开发区天柱路29号", accessToken);
        String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("city");
        List<SkuNum> planSkuNum = yzService.getSkuIdAndNum(yzTrade);
        List<SkuNum> realSkuNum = jdService.getNeedToBuy(accessToken, planSkuNum, area);
        logger.info(realSkuNum.toString());
    }

    /**
     * 测试下单
     */
    @Test
    public void submitOrderTest() {
        List<YzTrade> yzTradeList = yzService.getYzTradesSold(yzService.readYzToken());
        String accessToken = jdService.readJdToken().getAccessToken();
        for (YzTrade yzTrade : yzTradeList) {
            List<SkuNum> planSkuNum = yzService.getSkuIdAndNum(yzTrade);
            // TODO 这个地方需要讨论 可能不能这么简单的判断 这个订单是否需要处理
            if (planSkuNum.size() != 0) {

                logger.info(yzTrade.toString());

                String address = yzTrade.getReceiverState() + yzTrade.getReceiverCity() + yzTrade.getReceiverDistrict() + yzTrade.getReceiverAddress();
                Map<String, Integer> addressMap = jdService.getJdAddressFromAddress(address, accessToken);
                String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("city");

                List<SkuNum> realSkuNum = jdService.getNeedToBuy(accessToken, planSkuNum, area);
                logger.info(realSkuNum.toString());
                //jdService.submitOrder(accessToken,yzTrade,realSkuNum,addressMap);
            }
        }
    }

}

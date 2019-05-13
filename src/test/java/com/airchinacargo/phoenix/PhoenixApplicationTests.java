package com.airchinacargo.phoenix;

import com.airchinacargo.phoenix.domain.entity.SkuNum;
import com.airchinacargo.phoenix.domain.entity.SysTrade;
import com.airchinacargo.phoenix.domain.entity.YzTrade;
import com.airchinacargo.phoenix.domain.repository.ISysTradeRepository;
import com.airchinacargo.phoenix.domain.repository.IYzToJdRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
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
    IJdService jdService;
    @Autowired
    IYzToJdRepository yzToJdRepository;
    @Autowired
    ISysTradeRepository sysTradeRepository;

    @Test
    public void testSubmitOrder() {

        String jdToken = jdService.readJdToken().getAccessToken();
        String tidString = "E20190510155828004500038";
        String[] splitTid = tidString.split(",");
        List<String> tidList = Lists.newArrayList(splitTid);
        for (String tid : tidList) {
            logger.info("单号： " + tid);
            YzTrade yzTrade = yzService.getYzTreadByTid(yzService.readYzToken(), tid);
            //yzTrade.setTid(yzTrade.getTid() + "BF");
            //yzTrade.setReceiverDistrict("滦平县");
            yzTrade.setReceiverAddress("丈八沟街道枫韵蓝湾社区2号楼");
            List<SkuNum> planSkuNum = yzService.getSkuIdAndNum(yzTrade);
            //List<SkuNum> planSkuNum = new ArrayList<>();
            //planSkuNum.add(new SkuNum("3088504", 1));
            if (planSkuNum.size() > 0) {
                // 准备下单需要的参数
                String address = yzTrade.getReceiverState() + yzTrade.getReceiverCity() + yzTrade.getReceiverDistrict() + yzTrade.getReceiverAddress();
                Map<String, Integer> addressMap = jdService.getJdAddressFromAddress(address, jdToken);

                addressMap.replace("town", 53951);

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

    @Test
    public void testGetTown() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/area/getTown")
                    .queryString("token", jdService.readJdToken().getAccessToken())
                    .queryString("id", "4343")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().toString());
    }

    @Test
    public void testTid() {
        logger.info(yzService.getYzTreadByTid(yzService.readYzToken(), "E20190510100536098400060").toString());
    }

    @Test
    public void testCancel() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/order/cancel")
                    .queryString("token", jdService.readJdToken().getAccessToken())
                    .queryString("jdOrderId", "86264014681")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().toString());
    }

    @Test
    public void testDoPay() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post("https://bizapi.jd.com/api/order/doPay")
                    .queryString("token", jdService.readJdToken().getAccessToken())
                    .queryString("jdOrderId", "90838492256")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        logger.info(response.getBody().toString());
    }


    @Test
    public void testConfirmNoSplit() {
        yzService.confirmNoSplit(yzService.readYzToken(), "E20190128182621071000002", "86284308199");
    }

    @Test
    public void testGetYzTrade() {
        YzTrade yzTrade = yzService.getYzTreadByTid(yzService.readYzToken(), "E20190218175641082800001");
        logger.info(yzTrade.toString());
    }

    @Test
    public void testMsgGet() {
        logger.info(jdService.messageGet(jdService.readJdToken().getAccessToken(), "14").toString());
    }


    @Test
    public void testShop() {
        String yzToken = yzService.readYzToken();
        String jdToken = jdService.readJdToken().getAccessToken();
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
                            //SysTrade sysTrade = jdService.submitOrder(jdToken, yzTrade, realSkuNum, addressMap);
                            // 无论下单成功与否保存处理记录到数据库
                            //logger.info("[ submitOrder ] --> RETURN: " + sysTrade.toString());
                            //sysTradeRepository.save(sysTrade);
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


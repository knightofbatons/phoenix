package com.airchinacargo.phoenix;

import com.airchinacargo.phoenix.domain.entity.SkuNum;
import com.airchinacargo.phoenix.domain.entity.SysTrade;
import com.airchinacargo.phoenix.domain.entity.YzOrder;
import com.airchinacargo.phoenix.domain.entity.YzTrade;
import com.airchinacargo.phoenix.domain.repository.ISysTradeRepository;
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

import java.util.*;

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
    public void readYzTokenTest() {
        logger.info(yzService.readYzToken());
    }

    /**
     * 测试获取有赞订单
     */
//    @Test
//    public void getYzTradesSoldTest() {
//
//        for (YzTrade yzTrade : yzService.getYzTradesSold(yzService.readYzToken())){
//            logger.info(yzTrade.toString());
//        }
//    }

    /**
     * 测试获取有赞订单详细信息
     */
    @Test
    public void getYzTreadByTidTest() {
        logger.info(yzService.getYzTreadByTid(yzService.readYzToken(), "E20180914103219028700003").toString());
    }

    /**
     * 测试从有赞订单获取期望在京东购买的商品以及数量
     */
    @Test
    public void getSkuIdAndNumTest() {
        YzTrade yzTrade = new YzTrade();
        List<YzOrder> yzOrderList = Arrays.asList(
                new YzOrder(1, "1", "1", "3503652", "1"),
                new YzOrder(1, "2", "2", "302010", "2")
        );
        yzTrade.setOrders(yzOrderList);
        logger.info(yzService.getSkuIdAndNum(yzTrade).toString());
    }

    /**
     * 测试有赞有物流发货
     */
    @Test
    public void confirmTest() {
        String oidList = "18070869";
        yzService.confirm(yzService.readYzToken(), "E20180408150125051900008", "72832193682", oidList);
        oidList = "18070870";
        yzService.confirm(yzService.readYzToken(), "E20180408150125051900008", "72832306454", oidList);
        oidList = "18070871";
        yzService.confirm(yzService.readYzToken(), "E20180408150125051900008", "72825844185", oidList);
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
     * 测试京东根据详细地址获取地址编码
     */
    @Test
    public void getJdAddressFromAddressTest() {
        jdService.getJdAddressFromAddress("北京市北京市通州区通州兴贸二街5号院，首开国风美仑3-2-1101", jdService.readJdToken().getAccessToken());
    }

    /**
     * 测试百度根据详细地址获取经纬度
     */
    @Test
    public void getLatLngFromAddressTest() {
        jdService.getLatLngFromAddress("天津市天津市蓟州区人民东路 步行街正对的张庄胡同内100米 鱼里女装");
    }

    /**
     * 测试京东根据经纬度获取地址编码
     */
    @Test
    public void getJDAddressFromLatLngTest() {
        Map<String, Double> map = new HashMap<>(2);
        map.put("lng", 114.54258178082);
        map.put("lat", 38.054369105989);
        jdService.getJDAddressFromLatLng(jdService.readJdToken().getAccessToken(), map);
    }

    /**
     * 测试下单
     */
    //   @Test
//    public void submitOrderTest() {
//        List<YzTrade> yzTradeList = yzService.getYzTradesSold(yzService.readYzToken());
//        String accessToken = jdService.readJdToken().getAccessToken();
//        int i = 0;
//        for (YzTrade yzTrade : yzTradeList) {
//            List<SkuNum> planSkuNum = yzService.getSkuIdAndNum(yzTrade);
//            // 这个订单是否需要处理
//
//            if (planSkuNum.size() > 0 && planSkuNum.size() < 6) {
//                //logger.info(yzTrade.toString());
//                String address = yzTrade.getReceiverState() + yzTrade.getReceiverCity() + yzTrade.getReceiverDistrict() + yzTrade.getReceiverAddress();
//                Map<String, Integer> addressMap = jdService.getJdAddressFromAddress(address, accessToken);
//                if (null != addressMap) {
//                    String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("city");
//                    List<SkuNum> realSkuNum = jdService.getNeedToBuy(accessToken, planSkuNum, area);
//                    i++;
//                    if (null != realSkuNum) {
//                        logger.info(i + " " + realSkuNum.toString());
//                    }
//                    //jdService.submitOrder(accessToken,yzTrade,realSkuNum,addressMap);
//                    //TODO 京东没有测试环境所有测试均进入生产系统 如非必须不要测试下单
//                }
//            }
//        }
//    }

    /**
     * 测试京东订单反查
     */
    @Test
    public void selectJdOrderIdByThirdOrderTest() {
        jdService.selectJdOrderIdByThirdOrder(jdService.readJdToken().getAccessToken(), "E20180408150125051900008");
    }

    /**
     * 测试京东订单查询
     */
    @Test
    public void selectJdOrderTest() {
        logger.info(jdService.selectJdOrder(jdService.readJdToken().getAccessToken(), "77216164338").toString());
    }

    /**
     * 测试京东订单配送信息查询
     */
    @Test
    public void orderTrackTest() {
        logger.info(jdService.orderTrack(jdService.readJdToken().getAccessToken(), "76680368194").toString());
    }

    /**
     * 测试京东查询余额
     */
    @Test
    public void getBalanceTest() {
        logger.info(jdService.getBalance(jdService.readJdToken().getAccessToken()));
    }

    /**
     * 测试京东批量查询商品价格
     */
    @Test
    public void getSellPriceTest() {
        jdService.getSellPrice(jdService.readJdToken().getAccessToken(), "2950891");
    }


    @Autowired
    ISysTradeRepository sysTradeRepository;

    @Test
    public void testSubmitOrder() {

        String jdToken = jdService.readJdToken().getAccessToken();
        YzTrade yzTrade = yzService.getYzTreadByTid(yzService.readYzToken(), "E20180929173716058800009");
        yzTrade.setReceiverAddress("中台镇新河小区2#楼1002室");
        List<SkuNum> planSkuNum = yzService.getSkuIdAndNum(yzTrade);

        if (planSkuNum.size() > 0 && planSkuNum.size() < 6) {
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

    //  @Test
//    public void testBf() {
//        HttpResponse<JsonNode> response = null;
//        try {
//            response = Unirest.get("http://10.10.251.42/api/search/sysTrade/10800/11700/true/true")
//                    .header("accept", "application/json")
//                    .header("Content-Type", "application/json")
//                    .asJson();
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
//        Gson gson = new Gson();
//        List<SysTrade> sysTradeList = gson.fromJson(response.getBody().toString(), new TypeToken<List<SysTrade>>() {
//        }.getType());
//
//        double price1 = 277.3;
//        double price2 = 298;
//        double price3 = 302.6;
//        String jdToken = jdService.readJdToken().getAccessToken();
//        List<SkuNum> planSkuNum = new ArrayList<>();
//        SkuNum skuNum = new SkuNum("862555", 1);
//        planSkuNum.add(skuNum);
//
//        List<String> tidList = sysTradeList.stream()
//                .filter(p -> price1 == p.getPrice() || price2 == p.getPrice() || price3 == p.getPrice())
//                .map(p -> p.getTid())
//                .collect(Collectors.toList());
//
//        for (int i =0;i<tidList.size();i++){
//            logger.info("[ testBf ] --> " + i);
//            YzTrade yzTrade = yzService.getYzTreadByTid(yzService.readYzToken(), tidList.get(i));
//            yzTrade.setTid(yzTrade.getTid()+"BF");
//            logger.info(yzTrade.toString());
//            // 准备下单需要的参数
//            String address = yzTrade.getReceiverState() + yzTrade.getReceiverCity() + yzTrade.getReceiverDistrict() + yzTrade.getReceiverAddress();
//            Map<String, Integer> addressMap = jdService.getJdAddressFromAddress(address, jdToken);
//            // 如果获取地址正常
//            if (null != addressMap) {
//                String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("county");
//                List<SkuNum> realSkuNum = jdService.getNeedToBuy(jdToken, planSkuNum, area);
//                // 在京东下单并获得下单结果
//                SysTrade sysTrade = jdService.submitOrder(jdToken, yzTrade, realSkuNum, addressMap);
//                // 无论下单成功与否保存处理记录到数据库
//                logger.info("[ submitOrder ] --> RETURN: " + sysTrade.toString());
//                sysTradeRepository.save(sysTrade);
//            } else {
//                // 处理地址不正常订单记录到数据库
//                sysTradeRepository.save(new SysTrade(yzTrade.getTid(), "NO_JD_ORDER_ID", new Date(), "地址无法解析", 0.00, false, false, yzTrade.getReceiverName(), yzTrade.getReceiverMobile(), address, yzTrade.getCoupons().get(0).getCouponName(), 0));
//            }
//        }
//    }
}

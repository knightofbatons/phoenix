package com.airchinacargo.phoenix;

import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PhoenixApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void contextLoads() {
    }

    @Autowired
    IYzService yzService;

    /**
     * 测试获取有赞 token
     */
    @Test
    public void getYzTokenTest() {
        yzService.getYzToken();
    }

    /**
     * 测试获取有赞订单
     */
    @Test
    public void getYzTradesSoldTest() {
        yzService.getYzTradesSold(yzService.readYzToken());
    }

    @Autowired
    IJdService jdService;


    /**
     * 测试获取京东 token
     */
    @Test
    public void getJdTokenTest() {
        jdService.getJdToken();
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
     * 测试转换时间格式
     */
    @Test
    public void dateFormatTest() {
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance
                (DateFormat.MEDIUM, DateFormat.MEDIUM);
        logger.info(mediumDateFormat.format(new Date()));
    }
}

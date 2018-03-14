package com.airchinacargo.phoenix;

import com.airchinacargo.phoenix.service.interfaces.IYzService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
     * 测试获取 Token
     * @throws UnirestException
     */
    @Test
    public void getYzTokenTest() throws UnirestException {
        yzService.getYzToken();
    }

    /**
     * 测试获取订单
     * @throws UnirestException
     */
    @Test
    public void getYzTradesSoldTest() throws UnirestException {
        yzService.getYzTradesSold(yzService.readYzToken());
    }

}

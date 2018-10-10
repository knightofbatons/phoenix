package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.service.interfaces.IYzService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YzServiceImplTest {
    @Autowired
    IYzService yzService;

    @Test
    public void getYzToken() {
        assertNotNull(yzService.getYzToken());
    }

    @Test
    public void readYzToken() {
        assertNotNull(yzService.readYzToken());
    }

    @Test
    public void getYzTradesSold() {
        assertNotNull(yzService.getYzTradesSold(yzService.readYzToken()));
    }

    @Test
    public void getYzTreadByTid() {
        assertNotNull(yzService.getYzTreadByTid(yzService.readYzToken(), "E20180412141405023500002"));
        assertNull(yzService.getYzTreadByTid(yzService.readYzToken(), "E20180111111111111111111"));
    }

    @Test
    public void getSkuIdAndNum() {
    }

    @Test
    public void confirm() {
    }

    @Test
    public void confirmNoSplit() {
    }

    @Test
    public void confirmNoExpress() {
    }

}
package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.service.interfaces.IJdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdServiceImplTest {
    @Autowired
    IJdService jdService;

    @Test
    public void getJdToken() {
        assertNotNull(jdService.readJdToken());
    }

    @Test
    public void readJdToken() {
        assertNotNull(jdService.readJdToken());
    }

    @Test
    public void refreshJdToken() {
        assertNotNull(jdService.refreshJdToken("XXXXXX"));
    }

    @Test
    public void getJdAddressFromAddress() {
        assertNotNull(jdService.getJdAddressFromAddress("河北省石家庄市桥东区跃进路12号河北医科大学宿舍", jdService.readJdToken().getAccessToken()));
    }

    @Test
    public void getJDAddressFromLatLng() {
    }

    @Test
    public void getLatLngFromAddress() {
    }

    @Test
    public void getNewStockBySkuIdAndArea() {
    }

    @Test
    public void getNeedToBuy() {
    }

    @Test
    public void submitOrder() {
    }

    @Test
    public void selectJdOrderIdByThirdOrder() {
    }

    @Test
    public void selectJdOrder() {
    }

    @Test
    public void orderTrack() {
    }

    @Test
    public void getBalance() {
    }

    @Test
    public void getSellPrice() {
    }

    @Test
    public void cancel() {
    }

}
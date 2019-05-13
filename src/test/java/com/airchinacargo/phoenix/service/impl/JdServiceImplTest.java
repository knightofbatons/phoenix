package com.airchinacargo.phoenix.service.impl;

import com.airchinacargo.phoenix.domain.entity.SkuNum;
import com.airchinacargo.phoenix.domain.repository.ITokenRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IJdService jdService;
    @Autowired
    ITokenRepository tokenRepository;
    private final int JD = 1;

    @Test
    public void getJdToken() {
        assertNotNull(jdService.getJdToken().getAccessToken());
    }

    @Test
    public void readJdToken() {
        tokenRepository.deleteById(JD);
        assertNotNull(jdService.readJdToken().getAccessToken());
    }

    @Test
    public void refreshJdToken() {
        assertNotNull(jdService.refreshJdToken(jdService.readJdToken().getRefreshToken()).getAccessToken());
    }

    @Test
    public void getJdAddressFromAddress() {
        assertNotNull(jdService.getJdAddressFromAddress("陕西省西安市雁塔区丈八沟街道高新路25号高新商务楼", jdService.readJdToken().getAccessToken()));
    }

    @Test
    public void getNeedToBuy() {
        String jdToken = jdService.readJdToken().getAccessToken();
        List<SkuNum> planSkuNum = new ArrayList<>();
        planSkuNum.add(new SkuNum("285480", 10));
        Map<String, Integer> addressMap = jdService.getJdAddressFromAddress("北京市通州区玉桥北里25号楼442", jdService.readJdToken().getAccessToken());
        String area = addressMap.get("province") + "_" + addressMap.get("city") + "_" + addressMap.get("county");
        assertNotNull(jdService.getNeedToBuy(jdToken, planSkuNum, area));
    }

    @Test
    public void submitOrder() {
    }

    @Test
    public void selectJdOrderIdByThirdOrder() {
        jdService.selectJdOrderIdByThirdOrder(jdService.readJdToken().getAccessToken(), "E20190505104320042600027BF");
    }

    @Test
    public void selectJdOrder() {
    }

    @Test
    public void orderTrack() {
        logger.info(jdService.orderTrack(jdService.readJdToken().getAccessToken(), "85777111290") + "");
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

    @Test
    public void messageGet() {
    }

    @Test
    public void messageDel() {
    }

    @Test
    public void getAllSellSku() {
    }

    @Test
    public void invoiceSubmit() {
    }

    @Test
    public void invoiceSelect() {
    }

    @Test
    public void getNeedToInvoiceJdOrderList() {
    }

    @Test
    public void invoice() {
    }
}
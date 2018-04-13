package com.airchinacargo.phoenix.controller;

import com.airchinacargo.phoenix.domain.entity.SysTrade;
import com.airchinacargo.phoenix.domain.repository.ISysTradeRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ChenYu
 */
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/search")
public class TestController {
    @Autowired
    IJdService jdService;
    @Autowired
    ISysTradeRepository sysTradeRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "/selectJdOrder/{jdOrderId}", method = RequestMethod.GET)
    public String selectJdOrder(@PathVariable("jdOrderId") String jdOrderId) {
        return jdService.selectJdOrder(jdService.readJdToken().getAccessToken(), jdOrderId).toString();
    }

    @RequestMapping(value = "/orderTrack/{jdOrderId}", method = RequestMethod.GET)
    public String orderTrack(@PathVariable("jdOrderId") String jdOrderId) {
        return jdService.orderTrack(jdService.readJdToken().getAccessToken(), jdOrderId).toString();
    }

    @RequestMapping(value = "/sysTradeList", method = RequestMethod.GET)
    public List<SysTrade> searchSysTradeList() {
        return sysTradeRepository.findAll();
    }

    @RequestMapping(value = "/searchSysTradeListBy/receiverName/{receiverName}", method = RequestMethod.GET)
    public List<SysTrade> searchSysTradeListByReceiverName(@PathVariable("receiverName") String receiverName) {
        return sysTradeRepository.findByReceiverName(receiverName).orElse(null);
    }

    @RequestMapping(value = "/searchSysTradeListBy/receiverMobile/{receiverMobile}", method = RequestMethod.GET)
    public List<SysTrade> searchSysTradeListByReceiverMobile(@PathVariable("receiverMobile") String receiverMobile) {
        return sysTradeRepository.findByReceiverMobile(receiverMobile).orElse(null);
    }
}

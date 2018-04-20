package com.airchinacargo.phoenix.controller;

import com.airchinacargo.phoenix.domain.entity.SysTrade;
import com.airchinacargo.phoenix.domain.repository.ISysTradeRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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
    public Page<SysTrade> searchSysTradeList(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "20") int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return sysTradeRepository.findAll(PageRequest.of(page, size, sort));
    }

    @RequestMapping(value = "/searchSysTradeListBy/receiverName/{receiverName}", method = RequestMethod.GET)
    public List<SysTrade> searchSysTradeListByReceiverName(@PathVariable("receiverName") String receiverName) {
        return sysTradeRepository.findByReceiverName(receiverName).orElse(null);
    }

    @RequestMapping(value = "/searchSysTradeListBy/{receiver}", method = RequestMethod.GET)
    public List<SysTrade> searchSysTradeListByReceiver(@PathVariable("receiver") String receiver) {
        return sysTradeRepository.findByReceiverMobileOrReceiverName(receiver, receiver).orElse(null);
    }

    @RequestMapping(value = "/searchSysTradeListBy/receiverMobile/{receiverMobile}", method = RequestMethod.GET)
    public List<SysTrade> searchSysTradeListByReceiverMobile(@PathVariable("receiverMobile") String receiverMobile) {
        return sysTradeRepository.findByReceiverMobile(receiverMobile).orElse(null);
    }

    @RequestMapping(value = "/getFailedSysTradeList", method = RequestMethod.GET)
    public List<SysTrade> getFailed() {
        return sysTradeRepository.findBySuccessAndConfirm(false, false).orElse(null);
    }

    @RequestMapping(value = "/getBalance", method = RequestMethod.GET)
    public String getBalance() {
        return jdService.getBalance(jdService.readJdToken().getAccessToken());
    }
}

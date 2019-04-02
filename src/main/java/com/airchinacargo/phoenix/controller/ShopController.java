package com.airchinacargo.phoenix.controller;

import antlr.StringUtils;
import com.airchinacargo.phoenix.domain.entity.SkuReplace;
import com.airchinacargo.phoenix.domain.entity.SysTrade;
import com.airchinacargo.phoenix.domain.entity.YzToJd;
import com.airchinacargo.phoenix.domain.repository.ISkuReplaceRepository;
import com.airchinacargo.phoenix.domain.repository.ISysTradeRepository;
import com.airchinacargo.phoenix.domain.repository.IYzToJdRepository;
import com.airchinacargo.phoenix.service.interfaces.IJdService;
import com.airchinacargo.phoenix.service.interfaces.IShopService;
import com.airchinacargo.phoenix.service.interfaces.IYzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ShopController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IJdService jdService;
    @Autowired
    IYzService yzService;
    @Autowired
    IShopService shopService;
    @Autowired
    ISysTradeRepository sysTradeRepository;
    @Autowired
    ISkuReplaceRepository skuReplaceRepository;
    @Autowired
    IYzToJdRepository yzToJdRepository;

    /**
     * 查询京东订单信息
     *
     * @param jdOrderId 京东订单号
     * @return JSONObject HTTP 请求返回的结果
     */
    @RequestMapping(value = "/selectJdOrder/{jdOrderId}", method = RequestMethod.GET)
    public String selectJdOrder(@PathVariable("jdOrderId") String jdOrderId) {
        return jdService.selectJdOrder(jdService.readJdToken().getAccessToken(), jdOrderId).toString();
    }

    /**
     * 查询子订单配送信息
     *
     * @param jdOrderId 京东订单号
     * @return JSONObject HTTP 请求返回的结果
     */
    @RequestMapping(value = "/orderTrack/{jdOrderId}", method = RequestMethod.GET)
    public String orderTrack(@PathVariable("jdOrderId") String jdOrderId) {
        return jdService.orderTrack(jdService.readJdToken().getAccessToken(), jdOrderId).toString();
    }

    /**
     * 分页查询系统订单 含默认设置
     *
     * @param page 分页所在页
     * @param size 分页每页条数
     * @return 系统订单列表
     */
    @RequestMapping(value = "/sysTradeList", method = RequestMethod.GET)
    public Page<SysTrade> searchSysTradeList(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "20") int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return sysTradeRepository.findAll(PageRequest.of(page, size, sort));
    }

    /**
     * 根据收货人电话或姓名或京东订单编号查询系统订单
     *
     * @param receiver 收货人电话或姓名或京东订单编号
     * @return Optional 查询结果列表
     */
    @RequestMapping(value = "/searchSysTradeListBy/{receiver}", method = RequestMethod.GET)
    public List<SysTrade> searchSysTradeListByReceiver(@PathVariable("receiver") String receiver) {
        return sysTradeRepository.findByReceiverMobileOrReceiverNameOrJdOrderId(receiver, receiver, receiver).orElse(null);
    }

    /**
     * 分页查询未成功提交京东且未处理过发货订单 含默认设置
     *
     * @param page 分页所在页
     * @param size 分页每页条数
     * @return Optional 失败订单列表
     */
    @RequestMapping(value = "/getFailedSysTradeList", method = RequestMethod.GET)
    public Page<List<SysTrade>> getFailed(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "5") int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return sysTradeRepository.findBySuccessAndConfirm(false, false, PageRequest.of(page, size, sort));
    }

    /**
     * 统一余额查询
     *
     * @return String 余额
     */
    @RequestMapping(value = "/getBalance", method = RequestMethod.GET)
    public String getBalance() {
        return jdService.getBalance(jdService.readJdToken().getAccessToken());
    }

    /**
     * 获取京东信息推送池中的信息 含默认设置 14.支付失败消息 11.发票信息
     *
     * @param type 推送类型 支持多个 例如 1,2,3
     * @return JSONObject HTTP 请求返回的结果
     */
    @RequestMapping(value = "/getMessage", method = RequestMethod.GET)
    public String getMessage(@RequestParam(value = "type", defaultValue = "14,11") String type) {
        return jdService.messageGet(jdService.readJdToken().getAccessToken(), type).toString();
    }

    /**
     * 删除消息池中信息
     *
     * @param id 系统单号
     */
    @RequestMapping(value = "/delMessage/{id}", method = RequestMethod.DELETE)
    public void delMessage(@PathVariable("id") String id) {
        jdService.messageDel(jdService.readJdToken().getAccessToken(), id);
    }

    /**
     * 把原有记录的有赞 Tid 更新为‘从新处理’
     *
     * @param id 系统单号
     */
    @RequestMapping(value = "/updateTid/{id}", method = RequestMethod.POST)
    public void updateTid(@PathVariable("id") int id) {
        sysTradeRepository.updateTid(id);
    }

    /**
     * 根据系统订单 id 删除订单 含默认设置 只用于删除失败订单
     *
     * @param id 系统单号
     */
    @RequestMapping(value = "/deleteFailedSysTradeById/{id}", method = RequestMethod.DELETE)
    public void deleteFailedSysTradeById(@PathVariable("id") int id) {
        sysTradeRepository.deleteByIdAndSuccess(id, false);
    }

    /**
     * 批量查询所有在售卖的商品 包括可替换到的商品 价格
     *
     * @return 商品价格列表
     */
    @RequestMapping(value = "/getAllSellPrice", method = RequestMethod.GET)
    public String getAllSellPrice() {
        return jdService.getSellPrice(jdService.readJdToken().getAccessToken(), StringUtils.stripFrontBack(jdService.getAllSellSku().toString(), "[", "]").replaceAll(" ", "")).toString();
    }

    /**
     * 批量查询商品价格
     *
     * @param skuList 商品编号，请以，(英文逗号)分割(最高支持 100 个商品)。例如：129408,129409
     * @return 商品价格列表
     */
    @RequestMapping(value = "/getSellPrice/{skuList}", method = RequestMethod.GET)
    public String getSellPrice(@PathVariable("skuList") String skuList) {
        return jdService.getSellPrice(jdService.readJdToken().getAccessToken(), skuList).toString();
    }

    /**
     * 开取发票
     *
     * @param beginId 开始系统订单编码
     * @param endId   结束系统订单编码
     */
    @RequestMapping(value = "/invoice/{beginId}/{endId}", method = RequestMethod.POST)
    public void invoice(@PathVariable("beginId") int beginId, @PathVariable("endId") int endId) {
        jdService.invoice(jdService.readJdToken().getAccessToken(), beginId, endId);
    }

    /**
     * 按照订单编码找出需要的系统订单
     *
     * @param beginId 开始系统订单编码
     * @param endId   结束系统订单编码
     * @param success 是否成功提交京东
     * @param confirm 是否已经处理发货
     * @return 需要的系统订单
     */
    @RequestMapping(value = "/sysTrade/{beginId}/{endId}/{success}/{confirm}", method = RequestMethod.GET)
    public List<SysTrade> getSysTrade(@PathVariable("beginId") int beginId, @PathVariable("endId") int endId, @PathVariable("success") boolean success, @PathVariable("confirm") boolean confirm) {
        return sysTradeRepository.findByIdBetweenAndSuccessAndConfirm(beginId, endId, success, confirm);
    }


    /**
     * 获取全部替换关系列表
     *
     * @return 带分页的替换列表
     */
    @RequestMapping(value = "/getSkuReplaceList", method = RequestMethod.GET)
    public Page<SkuReplace> getSkuReplace(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "5") int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return skuReplaceRepository.findAll(PageRequest.of(page, size, sort));
    }

    /**
     * 获取全部有赞京东对应关系列表
     *
     * @return 带分页的有赞京东对应列表
     */
    @RequestMapping(value = "/getYzToJdList", method = RequestMethod.GET)
    public Page<YzToJd> getYzToJd(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return yzToJdRepository.findAll(PageRequest.of(page, size, sort));
    }

    /**
     * 根据 ID 删除替换关系
     *
     * @param id 需要删除的兑换关系对应的 ID
     */
    @RequestMapping(value = "/deleteSkuReplaceById/{id}", method = RequestMethod.DELETE)
    public void deleteSkuReplaceById(@PathVariable("id") int id) {
        logger.info("[ deleteSkuReplaceById ] --> " + skuReplaceRepository.findById(id).toString());
        skuReplaceRepository.deleteById(id);
    }

    /**
     * 根据 ID 删除有赞京东对应关系
     *
     * @param id 需要删除的对应关系对应的 ID
     */
    @RequestMapping(value = "/deleteYzToJdById/{id}", method = RequestMethod.DELETE)
    public void deleteYzToJdById(@PathVariable("id") int id) {
        logger.info("[ deleteYzToJdById ] --> " + yzToJdRepository.findById(id).toString());
        yzToJdRepository.deleteById(id);
    }

    @Value("${SYS.SALT}")
    private String sysSalt;


    /**
     * 添加新的兑换关系
     *
     * @param skuReplace 兑换关系
     * @param salt       校验
     * @param who        操作方
     */
    @RequestMapping(value = "/newSkuReplace/{salt}/{who}", method = RequestMethod.POST)
    public void newSkuReplace(@RequestBody SkuReplace skuReplace, @PathVariable("salt") String salt, @PathVariable("who") String who) {
        if (sysSalt.endsWith(salt)) {
            logger.info("[ newSkuReplace ] --> " + who + " 添加了 " + skuReplace.toString());
            skuReplaceRepository.save(skuReplace);
        }
    }

    /**
     * 添加新的兑换关系
     *
     * @param yzToJd 对应关系
     * @param salt   校验
     * @param who    操作方
     */
    @RequestMapping(value = "/newYzToJd/{salt}/{who}", method = RequestMethod.POST)
    public void newYzToJd(@RequestBody YzToJd yzToJd, @PathVariable("salt") String salt, @PathVariable("who") String who) {
        if (sysSalt.endsWith(salt)) {
            logger.info("[ newYzToJd ] --> " + who + " 添加了 " + yzToJd.toString());
            yzToJdRepository.save(yzToJd);
        }
    }

    /**
     * 手动运行定时任务内容
     *
     * @param runReason 填写手动运行的原因
     */
    @RequestMapping(value = "/runShop/{runReason}", method = RequestMethod.POST)
    public void runShop(@PathVariable("runReason") String runReason) {
        logger.info("[ 定时任务手动运行 ] --> " + runReason);
        // 获取两种授权
        String jdToken = jdService.readJdToken().getAccessToken();
        String yzToken = yzService.readYzToken();
        logger.info("[ 开始物流信息回填 ] --> ");
        shopService.shopConfirm(jdToken, yzToken);
        logger.info("[ 开始购买 ] --> ");
        shopService.shopBuy(jdToken, yzToken);
        logger.info(" <-- [ 定时任务结束 ]");
    }
}

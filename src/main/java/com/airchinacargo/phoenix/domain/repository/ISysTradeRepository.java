package com.airchinacargo.phoenix.domain.repository;

import com.airchinacargo.phoenix.domain.entity.SysTrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author ChenYu 2018 04 08
 */
public interface ISysTradeRepository extends JpaRepository<SysTrade, Integer> {
    /**
     * 根据有赞订单号查询是否已存在处理记录
     *
     * @param tid 有赞订单号
     * @return Optional<SysTrade>
     */
    Optional<SysTrade> findByTid(String tid);

    /**
     * 根据是否成功提交京东和是否处理过发货来查询订单 带分页
     *
     * @param success  是否成功提交京东
     * @param confirm  是否已经处理发货
     * @param pageable 分页
     * @return Optional
     */
    Page<List<SysTrade>> findBySuccessAndConfirm(boolean success, boolean confirm, Pageable pageable);

    /**
     * 根据是否成功提交京东和是否处理过发货来查询订单
     *
     * @param success 是否成功提交京东
     * @param confirm 是否已经处理发货
     * @return Optional
     */
    Optional<List<SysTrade>> findBySuccessAndConfirm(boolean success, boolean confirm);

    /**
     * 根据收货人姓名查询系统订单
     *
     * @param receiverName 收货人姓名
     * @return Optional
     */
    Optional<List<SysTrade>> findByReceiverName(String receiverName);

    /**
     * 根据收货人电话查询系统订单
     *
     * @param receiverMobile 收货人电话
     * @return Optional
     */
    Optional<List<SysTrade>> findByReceiverMobile(String receiverMobile);

    /**
     * 根据收货人电话或姓名或京东订单编号查询系统订单
     *
     * @param receiverMobile 收货人电话
     * @param receiverName   收货人姓名
     * @param jdOrderId      京东订单编号
     * @return Optional
     */
    Optional<List<SysTrade>> findByReceiverMobileOrReceiverNameOrJdOrderId(String receiverMobile, String receiverName, String jdOrderId);

    /**
     * 变更为已发货
     *
     * @param tid 有赞订单号
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "UPDATE sys_trade p SET p.confirm = TRUE WHERE p.tid = ?1")
    void updateIsConfirm(String tid);

    /**
     * 变更为从新处理
     *
     * @param id 系统单号
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "UPDATE sys_trade p SET p.tid = '从新处理' WHERE p.id = ?1 AND p.success = FALSE ")
    void updateTid(int id);

    /**
     * 删除系统订单
     *
     * @param id      系统单号
     * @param success 是否成功
     */
    @Transactional
    void deleteByIdAndSuccess(int id, boolean success);

    /**
     * 按照订单编码找出需要开取发票的系统订单
     *
     * @param beginId 开始系统订单编码
     * @param endId   结束系统订单编码
     * @param success 是否成功提交京东
     * @param confirm 是否已经处理发货
     * @return SysTrade 需要开票的系统订单
     */
    List<SysTrade> findByIdBetweenAndSuccessAndConfirm(int beginId, int endId, boolean success, boolean confirm);
}

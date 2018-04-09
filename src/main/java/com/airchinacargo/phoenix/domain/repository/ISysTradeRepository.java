package com.airchinacargo.phoenix.domain.repository;

import com.airchinacargo.phoenix.domain.entity.SysTrade;
import org.springframework.data.jpa.repository.JpaRepository;

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
}

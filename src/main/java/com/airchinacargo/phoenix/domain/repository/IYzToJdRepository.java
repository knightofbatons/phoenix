package com.airchinacargo.phoenix.domain.repository;

import com.airchinacargo.phoenix.domain.entity.YzToJd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author ChenYu 2018 03 26
 */
public interface IYzToJdRepository extends JpaRepository<YzToJd, Integer> {

    /**
     * 根据 itemId 找到对应关系
     *
     * @param itemId 有赞商品编号
     * @return Optional<YzToJd> 对应关系
     */
    Optional<YzToJd> findByItemId(String itemId);

    /**
     * 根据 skuId 和 num 找到对应关系
     *
     * @param skuId 京东商品编号
     * @param num   商品数量
     * @return Optional<YzToJd> 对应关系
     */
    Optional<YzToJd> findBySkuIdAndNum(String skuId, int num);

}

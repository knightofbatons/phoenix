package com.airchinacargo.phoenix.domain.repository;

import com.airchinacargo.phoenix.domain.entity.SkuReplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ChenYu 2018 03 28
 */
public interface ISkuReplaceRepository extends JpaRepository<SkuReplace, Integer> {

    /**
     * 查询相同数量替代货物
     *
     * @param beforeSku 要被替代的缺货商品
     * @param num       要被替代的数量
     * @return List<SkuReplace> 可以替代的结果
     */
    List<SkuReplace> findByBeforeSkuAndBeforeNum(String beforeSku, int num);

    /**
     * 查询替代后货品数量的原货品数量
     *
     * @param afterSku 替代后商品
     * @param num      替代后数量
     * @return SkuReplace 原商品数量
     */
    SkuReplace findByAfterSkuAndAfterNum(String afterSku, int num);


}

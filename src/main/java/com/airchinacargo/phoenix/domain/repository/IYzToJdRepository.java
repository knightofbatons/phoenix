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
     * @param itemId 有赞商品 id
     * @return 对应关系
     */
    Optional<YzToJd> findByItemId(String itemId);
}

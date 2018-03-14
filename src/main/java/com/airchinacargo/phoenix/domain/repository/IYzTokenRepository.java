package com.airchinacargo.phoenix.domain.repository;

import com.airchinacargo.phoenix.domain.entity.YzToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author ChenYu 2018 03 13
 */
public interface IYzTokenRepository extends JpaRepository<YzToken, Integer> {
    /**
     * 更新 Token 目前没用到
     *
     * @param id
     * @param token
     * @param date
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "update yz_token p set p.token = ?2,p.date = ?3 where p.id = ?1")
    void update(int id, String token, Date date);
}

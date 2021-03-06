package com.airchinacargo.phoenix.domain.repository;

import com.airchinacargo.phoenix.domain.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ChenYu 2018 03 15
 */
public interface ITokenRepository extends JpaRepository<Token, Integer> {
//    @Transactional
//    @Modifying(clearAutomatically = true)
//    @Query(nativeQuery = true, value = "update token p set p.access_token = ?2,p.refresh_token = ?3,p.date = ?4 where p.id = ?1")
//    void update(int id, String accessToken, String refreshToken, Date date);
}

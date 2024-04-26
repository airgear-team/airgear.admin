package com.airgear.admin.repository;

import com.airgear.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    void deleteByEmail(String email);

    Long countByCreatedAtBetween(OffsetDateTime fromDate, OffsetDateTime toDate);

    Long countByDeleteAtBetween(OffsetDateTime fromDate, OffsetDateTime toDate);

    @Query("SELECT u.email AS email, COUNT(g) AS goodsCount " +
            "FROM User u JOIN u.goods g " +
            "GROUP BY u.email")
    Page<Object> findUserGoodsCount(Pageable pageable);
}

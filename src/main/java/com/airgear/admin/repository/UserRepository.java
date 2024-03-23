package com.airgear.admin.repository;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    Long countByCreatedAtBetween(OffsetDateTime fromDate, OffsetDateTime toDate);
    Long countByDeleteAtBetween(OffsetDateTime fromDate, OffsetDateTime toDate);

    @Query("SELECT u.username AS username, COUNT(g) AS goodsCount " +
            "FROM User u JOIN u.goods g " +
            "GROUP BY u.username")
    Page<Object> findUserGoodsCount(Pageable pageable);

}

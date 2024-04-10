package com.airgear.admin.service;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.dto.CountDto;
import com.airgear.admin.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

public interface UserService {
    List<UserResponse> findAll();

    CountDto getCountOfNewUsers(OffsetDateTime fromDate, OffsetDateTime toDate);
    CountDto getCountOfDeletedUsers(OffsetDateTime fromDate, OffsetDateTime toDate);
    Page<CountByNameDto> getUserGoodsCount(Pageable pageable);


}

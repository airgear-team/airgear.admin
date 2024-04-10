package com.airgear.admin.service.impl;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.dto.CountDto;
import com.airgear.admin.model.CustomUserDetailsService;
import com.airgear.admin.model.User;
import com.airgear.admin.repository.UserRepository;
import com.airgear.admin.dto.UserResponse;
import com.airgear.admin.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));

        return new CustomUserDetailsService(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(users::add);
        return UserResponse.fromUsers(users);
    }

    @Override
    public CountDto getCountOfNewUsers(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return new CountDto(userRepository.countByCreatedAtBetween(fromDate, toDate));
    }

    @Override
    public CountDto getCountOfDeletedUsers(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return new CountDto(userRepository.countByDeleteAtBetween(fromDate, toDate));
    }

    @Override
    public Page<CountByNameDto> getUserGoodsCount(Pageable pageable) {
        Page<Object> page =userRepository.findUserGoodsCount(pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new CountByNameDto((String) x[0], (Long) x[1]));
    }
}

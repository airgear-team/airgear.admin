package com.airgear.admin.service.impl;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.dto.CountDto;
import com.airgear.admin.model.Category;
import com.airgear.admin.model.User;
import com.airgear.admin.repository.UserRepository;
import com.airgear.admin.response.UserResponse;
import com.airgear.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service(value = "userService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(users::add);
        return UserResponse.fromUsers(users);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        return authorities;
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

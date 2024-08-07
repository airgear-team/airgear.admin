package com.airgear.admin.service.impl;

import com.airgear.admin.dto.*;
import com.airgear.admin.exception.UserExceptions;
import com.airgear.admin.repository.UserRepository;
import com.airgear.admin.service.UserService;
import com.airgear.admin.specifications.SearchOperation;
import com.airgear.admin.specifications.UserSpecificationsBuilder;
import com.airgear.model.CustomUserDetails;
import com.airgear.model.Role;
import com.airgear.model.User;
import com.airgear.model.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    @Value("${entity.constraint}")
    private int constraintEntity;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));

        return new CustomUserDetails(user);
    }

    @Override
    public UserResponse createAdmin(UserSaveRequest request) {
        validateUniqueFields(request);
        return UserResponse.fromUser(save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), constraintEntity, pageable.getSort());
        return userRepository.findAll(pageable)
                .map(UserResponse::fromUserWithBasicAttributes);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findById(long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserCountResponse getCountOfNewUsers(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return UserCountResponse.fromCount(userRepository.countByCreatedAtBetween(fromDate, toDate));
    }

    @Override
    @Transactional(readOnly = true)
    public UserCountResponse getCountOfDeletedUsers(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return UserCountResponse.fromCount(userRepository.countByDeleteAtBetween(fromDate, toDate));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserCountByNameResponse> getCountOfUserGoods(Pageable pageable) {
        Page<Object> page = userRepository.findUserGoodsCount(pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new UserCountByNameResponse((String) x[0], (Long) x[1]));
    }

    @Override
    public UserResponse mergeById(long id, UserMergeRequest request) {
        User user = getUser(id);
        return UserResponse.fromUser(merge(user, request));
    }

    @Override
    public UserResponse changeStatusById(long id, UserStatus status) {
        User user = getUser(id);
        if (user.getStatus() != status) {
            user.setStatus(status);
        }
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse changePasswordById(long id, UserOverridePasswordRequest request) {
        User user = getUser(id);
        user.setPassword(passwordEncoder.encode(request.password()));
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse appointRole(long id, Role role) {
        User user = getUser(id);
        Set<Role> roles = user.getRoles();
        roles.add(role);
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse cancelRole(long id, Role role) {
        User user = getUser(id);
        Set<Role> roles = user.getRoles();
        roles.remove(role);
        return UserResponse.fromUser(user);
    }

    @Override
    public void deleteById(long id) {
        if (!userRepository.existsById(id)) throw UserExceptions.userNotFound(id);
        userRepository.deleteById(id);
    }

    public void mergeAdmins(List<UserSaveRequest> requests) {
        if (requests.isEmpty()) return;
        for (UserSaveRequest request : requests) {
            String email = request.email();
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                var newUser = new User();
                newUser.setCreatedAt(OffsetDateTime.now());
                newUser.setEmail(email);
                return newUser;
            });
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setPhone(request.phone());
            user.setName(request.name());
            user.setRoles(createRoles());
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }

    private void validateUniqueFields(UserSaveRequest request) {
        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw UserExceptions.duplicateEmail(email);
        }
        String phone = request.phone();
        if (userRepository.existsByPhone(phone)) {
            throw UserExceptions.duplicatePhone(phone);
        }
    }

    private User save(UserSaveRequest request) {
        var user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setName(request.name());
        user.setRoles(createRoles());
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(OffsetDateTime.now());
        userRepository.save(user);
        return user;
    }

    private Set<Role> createRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        return roles;
    }

    private User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> UserExceptions.userNotFound(id));
    }

    private User merge(User user, UserMergeRequest request) {
        String email = request.email();
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) throw UserExceptions.duplicateEmail(email);
            user.setEmail(email);
        }
        String phone = request.phone();
        if (phone != null && !phone.equals(user.getPhone())) {
            if (userRepository.existsByPhone(phone)) throw UserExceptions.duplicatePhone(phone);
            user.setPhone(phone);
        }
        String name = request.name();
        if (name != null && !name.equals(user.getName())) {
            user.setName(name);
        }
        return user;
    }

    @Override
    public Page<UserSearchResponse> searchUsers(String search, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), constraintEntity, pageable.getSort());
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        String operationSetExp = String.join("|", SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile(
                "(\\w+?)(" + operationSetExp + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
        }
        Specification<User> spec = builder.build();
        List<UserCountByNameResponse> goodsCount = getCountOfUserGoods(Pageable.unpaged()).stream().toList();
        return userRepository.findAll(spec, pageable).map(user -> new UserSearchResponse(user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getRoles(),
                goodsCount.stream().filter(x -> x.name().equals(user.getEmail())).findFirst().get().count(),
                user.getCreatedAt(),
                user.getDeleteAt(),
                user.getLastActivity(),
                user.getStatus(),
                user.getRating()
        ));
    }
}

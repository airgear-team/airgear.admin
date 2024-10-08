package com.airgear.admin.service.impl;

import com.airgear.admin.dto.*;
import com.airgear.admin.exception.UserExceptions;
import com.airgear.admin.repository.UserRepository;
import com.airgear.admin.service.UserService;
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
    public Page<UserSearchResponse> findUsers(String name, String email, String phone, UserStatus status,
                                              OffsetDateTime createdAt, OffsetDateTime deletedAt, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), constraintEntity, pageable.getSort());
        Specification<User> spec = createSpecification(name, email, phone, status, createdAt, deletedAt);
        return userRepository.findAll(spec, pageable)
                .map(UserSearchResponse::fromUser);
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
        return page == null ? null
                : page.map(x -> (Object[]) x).map(x -> new UserCountByNameResponse((String) x[0], (Long) x[1]));
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

    private Specification<User> createSpecification(String name, String email, String phone, UserStatus status,
                                                    OffsetDateTime createdAt, OffsetDateTime deletedAt) {
        return Specification
                .where(nameLike(name))
                .and(emailLike(email))
                .and(phoneLike(phone))
                .and(statusLike(status))
                .and(createdAtLike(createdAt))
                .and(deletedAtLike(deletedAt));
    }

    private Specification<User> nameLike(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    private Specification<User> emailLike(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("email"), "%" + email + "%");
    }

    private Specification<User> phoneLike(String phone) {
        return (root, query, criteriaBuilder) ->
                phone == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("phone"), "%" + phone + "%");
    }

    private Specification<User> statusLike(UserStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    private Specification<User> createdAtLike(OffsetDateTime createdAt) {
        return (root, query, criteriaBuilder) ->
                createdAt == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdAt);
    }

    private Specification<User> deletedAtLike(OffsetDateTime deletedAt) {
        return (root, query, criteriaBuilder) ->
                deletedAt == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("deletedAt"), deletedAt);
    }
}

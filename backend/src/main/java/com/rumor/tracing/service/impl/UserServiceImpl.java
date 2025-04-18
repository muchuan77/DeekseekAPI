package com.rumor.tracing.service.impl;

import com.rumor.tracing.dto.request.UserCreateRequest;
import com.rumor.tracing.dto.request.UserUpdateRequest;
import com.rumor.tracing.dto.response.UserResponse;
import com.rumor.tracing.entity.User;
import com.rumor.tracing.entity.User.UserStatus;
import com.rumor.tracing.repository.UserRepository;
import com.rumor.tracing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setStatus(UserStatus.ENABLED);
        return new UserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUser(Long id) {
        return new UserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        return new UserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Page<UserResponse> getUsers(String keyword, String status, Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable)
                .map(UserResponse::new);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void batchCreateUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public void batchUpdateUserStatus(List<Long> userIds, UserStatus status) {
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> user.setStatus(status));
        userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public void batchDeleteUsers(List<Long> userIds) {
        userRepository.deleteAllById(userIds);
    }
}

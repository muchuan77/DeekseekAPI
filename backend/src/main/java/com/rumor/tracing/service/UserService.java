package com.rumor.tracing.service;

import com.rumor.tracing.dto.request.UserCreateRequest;
import com.rumor.tracing.dto.request.UserUpdateRequest;
import com.rumor.tracing.dto.response.UserResponse;
import com.rumor.tracing.entity.User;
import com.rumor.tracing.entity.User.UserStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUser(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void resetPassword(Long id);
    void changePassword(Long id, String newPassword);
    Page<UserResponse> getUsers(String keyword, String status, Integer page, Integer size);
    boolean existsByUsername(String username);
    List<User> getAllUsers();
    void batchCreateUsers(List<User> users);
    void batchUpdateUserStatus(List<Long> userIds, UserStatus status);
    void batchDeleteUsers(List<Long> userIds);
}

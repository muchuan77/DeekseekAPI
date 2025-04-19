package com.rumor.tracing.service;

import com.rumor.tracing.dto.UserDTO;
import com.rumor.tracing.entity.User;
import com.rumor.tracing.entity.User.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    Page<UserDTO> getUsers(Specification<User> spec, Pageable pageable);
    void updateUserStatus(Long id, UserStatus status);
    void updateUserRoles(Long id, List<String> roles);
    void resetPassword(Long id);
    void changePassword(Long id, String oldPassword, String newPassword);
    void batchUpdateStatus(List<Long> ids, UserStatus status);
    void batchDelete(List<Long> ids);
    void importUsers(List<UserDTO> userDTOs);
    List<UserDTO> exportUsers(Specification<User> spec);
    void lockUser(Long id);
    void unlockUser(Long id);
    void updateLastLogin(Long id);
    UserDTO getCurrentUser();
}

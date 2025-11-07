package com.github.gelald.tinyss.service;

import com.github.gelald.tinyss.entity.User;
import com.github.gelald.tinyss.entity.Role;
import com.github.gelald.tinyss.entity.UserRole;
import com.github.gelald.tinyss.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole assignRoleToUser(User user, Role role, String assignedBy) {
        Optional<UserRole> existingUserRole = userRoleRepository.findByUserIdAndRoleId(user.getId(), role.getId());

        if (existingUserRole.isPresent()) {
            UserRole ur = existingUserRole.get();
            ur.setEnabled(true);
            ur.setAssignedBy(assignedBy);
            return userRoleRepository.save(ur);
        } else {
            UserRole userRole = new UserRole(user, role, assignedBy);
            UserRole saved = userRoleRepository.save(userRole);
            log.info("Assigned role {} to user {} by {}", role.getName(), user.getUsername(), assignedBy);
            return saved;
        }
    }

    public Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId) {
        return userRoleRepository.findByUserIdAndRoleId(userId, roleId);
    }

    public List<UserRole> findByUserId(Long userId) {
        return userRoleRepository.findByUserIdAndEnabledTrue(userId);
    }

    public List<UserRole> findByUsername(String username) {
        return userRoleRepository.findByUsernameAndEnabledTrue(username);
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        Optional<UserRole> userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId);
        if (userRole.isPresent()) {
            UserRole ur = userRole.get();
            ur.setEnabled(false);
            userRoleRepository.save(ur);
            log.info("Removed role {} from user {}", roleId, userId);
        }
    }

    @Transactional
    public void disableUserRole(Long userRoleId) {
        Optional<UserRole> userRole = userRoleRepository.findById(userRoleId);
        if (userRole.isPresent()) {
            UserRole ur = userRole.get();
            ur.setEnabled(false);
            userRoleRepository.save(ur);
            log.info("Disabled user role assignment: {}", userRoleId);
        }
    }

    @Transactional
    public void enableUserRole(Long userRoleId) {
        Optional<UserRole> userRole = userRoleRepository.findById(userRoleId);
        if (userRole.isPresent()) {
            UserRole ur = userRole.get();
            ur.setEnabled(true);
            userRoleRepository.save(ur);
            log.info("Enabled user role assignment: {}", userRoleId);
        }
    }
}
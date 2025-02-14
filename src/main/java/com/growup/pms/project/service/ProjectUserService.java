package com.growup.pms.project.service;

import com.growup.pms.common.exception.code.ErrorCode;
import com.growup.pms.common.exception.exceptions.BusinessException;
import com.growup.pms.project.controller.dto.response.ProjectUserResponse;
import com.growup.pms.project.controller.dto.response.ProjectUserSearchResponse;
import com.growup.pms.project.domain.Project;
import com.growup.pms.project.domain.ProjectUser;
import com.growup.pms.project.domain.ProjectUserId;
import com.growup.pms.project.repository.ProjectRepository;
import com.growup.pms.project.repository.ProjectUserRepository;
import com.growup.pms.project.service.dto.ProjectUserCreateCommand;
import com.growup.pms.role.domain.ProjectRole;
import com.growup.pms.role.domain.Role;
import com.growup.pms.role.repository.RoleRepository;
import com.growup.pms.user.domain.User;
import com.growup.pms.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void createProjectUser(Long projectId, ProjectUserCreateCommand command) {
        isUserAlreadyInProject(projectId, command.userId());

        checkAllowedRole(command.roleName());

        Project project = projectRepository.findByIdOrThrow(projectId);
        User user = userRepository.findByIdOrThrow(command.userId());
        Role role = roleRepository.findProjectRoleByName(command.roleName());
        ProjectUser projectUser = command.toEntity(project, user, role);

        projectUserRepository.save(projectUser);
    }

    public List<ProjectUserResponse> getProjectUsers(Long projectId) {
        return projectUserRepository.getProjectUsersByProjectId(projectId);
    }

    public List<ProjectUserSearchResponse> searchProjectUsersByPrefix(Long projectId, String prefix) {
        return projectUserRepository.searchProjectUsersByNicknamePrefix(projectId, prefix);
    }

    @Transactional
    public void changeRole(Long projectId, Long targetUserId, String roleName) {
        ProjectUser projectUser = projectUserRepository.findByIdOrThrow(new ProjectUserId(projectId, targetUserId));
        Role role = roleRepository.findProjectRoleByName(roleName);

        projectUser.changeRole(role);
    }

    @Transactional
    public void kickProjectUser(Long projectId, Long targetUserId) {
        ProjectUser projectUser = projectUserRepository.findByIdOrThrow(new ProjectUserId(projectId, targetUserId));
        ensureUserIsAssignee(projectUser.getRole().getName());
        projectUserRepository.delete(projectUser);
    }

    private void ensureUserIsAssignee(String targetUserRoleName) {
        if (!ProjectRole.valueOf(targetUserRoleName).equals(ProjectRole.ASSIGNEE)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    private void isUserAlreadyInProject(Long projectId, Long userId) {
        if (projectUserRepository.existsById(new ProjectUserId(projectId, userId))) {
            throw new BusinessException(ErrorCode.USER_ALREADY_IN_PROJECT);
        }
    }

    private void checkAllowedRole(String roleName) {
        if (roleName.equals(ProjectRole.ADMIN.getRoleName())) {
            throw new BusinessException(ErrorCode.ROLE_ASSIGNMENT_NOT_ALLOWED);
        }
    }
}

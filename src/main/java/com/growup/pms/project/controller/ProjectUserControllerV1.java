package com.growup.pms.project.controller;

import com.growup.pms.common.aop.annotation.ProjectId;
import com.growup.pms.common.aop.annotation.RequirePermission;
import com.growup.pms.project.controller.dto.request.ProjectUserCreateRequest;
import com.growup.pms.project.service.ProjectUserService;
import com.growup.pms.role.domain.PermissionType;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/{projectId}/user")
public class ProjectUserControllerV1 {

    private final ProjectUserService projectUserService;

    @PostMapping
    @RequirePermission(PermissionType.PROJECT_INVITE_MEMBER)
    public ResponseEntity<Void> createProjectUser(@Positive @ProjectId @PathVariable Long projectId, ProjectUserCreateRequest request) {
        log.debug("ProjectUserControllerV1#createProjectUser called.");
        log.debug("프로젝트원 초대를 위한 projectId: {}", projectId);
        log.debug("프로젝트원 초대를 위한 ProjectInvitationRequest: {}", request);

        projectUserService.createProjectUser(projectId, request.toCommand());
        return ResponseEntity.ok().build();
    }
}

package com.growup.pms.team.controller;

import static com.growup.pms.common.constant.RegexConstants.TEAM_NAME_PATTERN;

import com.growup.pms.auth.controller.dto.SecurityUser;
import com.growup.pms.common.aop.annotation.CurrentUser;
import com.growup.pms.common.aop.annotation.RequirePermission;
import com.growup.pms.common.aop.annotation.TeamId;
import com.growup.pms.role.domain.PermissionType;
import com.growup.pms.team.controller.dto.request.TeamCreateRequest;
import com.growup.pms.team.controller.dto.request.TeamUpdateRequest;
import com.growup.pms.team.controller.dto.response.TeamNameCheckResponse;
import com.growup.pms.team.controller.dto.response.TeamResponse;
import com.growup.pms.team.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
public class TeamControllerV1 {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Void> createTeam(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody TeamCreateRequest request
    ) {
        return ResponseEntity.created(URI.create("/api/v1/team/"
                        + teamService.createTeam(user.getId(), request.toCommand())))
                .build();
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeam(@Positive @PathVariable Long teamId) {
        return ResponseEntity.ok()
                .body(teamService.getTeam(teamId));
    }

    @PatchMapping("/{teamId}")
    @RequirePermission(PermissionType.TEAM_UPDATE)
    public ResponseEntity<Void> updateTeam(
            @Positive @PathVariable @TeamId Long teamId,
            @Valid @RequestBody TeamUpdateRequest request
    ) {
        teamService.updateTeam(teamId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/leave")
    public ResponseEntity<Void> leaveTeam(@CurrentUser SecurityUser user, @Positive @PathVariable Long teamId) {
        teamService.leaveTeam(teamId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<TeamNameCheckResponse> isTeamNameAvailable(
            @Valid @NotNull @Pattern(regexp = TEAM_NAME_PATTERN) String teamName
    ) {
        return ResponseEntity.ok().body(teamService.isTeamNameAvailable(teamName));
    }
}

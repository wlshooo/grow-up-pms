package com.growup.pms.test.fixture.project.builder;

import static com.growup.pms.test.fixture.project.builder.ProjectTestBuilder.프로젝트는;
import static com.growup.pms.test.fixture.role.builder.RoleTestBuilder.역할은;
import static com.growup.pms.test.fixture.user.builder.UserTestBuilder.사용자는;

import com.growup.pms.project.domain.Project;
import com.growup.pms.project.domain.ProjectUser;
import com.growup.pms.role.domain.ProjectRole;
import com.growup.pms.role.domain.Role;
import com.growup.pms.role.domain.RoleType;
import com.growup.pms.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@SuppressWarnings("NonAsciiCharacters")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectUserTestBuilder {

    private Project project = 프로젝트는().이다();
    private User user = 사용자는().이다();
    private Role role = 역할은().타입이(RoleType.PROJECT).이름이(ProjectRole.ASSIGNEE.getRoleName()).이다();

    public static ProjectUserTestBuilder 프로젝트_유저는() {
        return new ProjectUserTestBuilder();
    }

    public ProjectUserTestBuilder 프로젝트가(Project project) {
        this.project = project;
        return this;
    }

    public ProjectUserTestBuilder 회원이(User user) {
        this.user = user;
        return this;
    }

    public ProjectUserTestBuilder 권한이(Role role) {
        this.role = role;
        return this;
    }

    public ProjectUser 이다() {
        return ProjectUser.builder()
                .project(project)
                .user(user)
                .role(role)
                .build();
    }
}

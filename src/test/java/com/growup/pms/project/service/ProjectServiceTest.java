package com.growup.pms.project.service;

import static com.growup.pms.test.fixture.project.builder.ProjectCreateRequestTestBuilder.프로젝트_생성_요청은;
import static com.growup.pms.test.fixture.project.builder.ProjectEditRequestTestBuilder.프로젝트_수정_요청은;
import static com.growup.pms.test.fixture.project.builder.ProjectResponseTestBuilder.프로젝트_목록조회_응답은;
import static com.growup.pms.test.fixture.project.builder.ProjectTestBuilder.프로젝트는;
import static com.growup.pms.test.fixture.project.builder.ProjectUserCreateRequestTestBuilder.프로젝트_유저_생성_요청은;
import static com.growup.pms.test.fixture.project.builder.ProjectUserTestBuilder.프로젝트_유저는;
import static com.growup.pms.test.fixture.role.builder.RoleTestBuilder.역할은;
import static com.growup.pms.test.fixture.team.builder.TeamTestBuilder.팀은;
import static com.growup.pms.test.fixture.user.builder.UserTestBuilder.사용자는;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.growup.pms.common.exception.code.ErrorCode;
import com.growup.pms.common.exception.exceptions.BusinessException;
import com.growup.pms.project.controller.dto.request.ProjectUserCreateRequest;
import com.growup.pms.project.controller.dto.response.ProjectResponse;
import com.growup.pms.project.domain.Project;
import com.growup.pms.project.domain.ProjectUser;
import com.growup.pms.project.domain.ProjectUserId;
import com.growup.pms.project.repository.ProjectRepository;
import com.growup.pms.project.repository.ProjectUserRepository;
import com.growup.pms.project.service.dto.ProjectCreateCommand;
import com.growup.pms.project.service.dto.ProjectEditCommand;
import com.growup.pms.role.domain.ProjectRole;
import com.growup.pms.role.domain.Role;
import com.growup.pms.role.domain.RoleType;
import com.growup.pms.role.repository.RoleRepository;
import com.growup.pms.team.domain.Team;
import com.growup.pms.team.repository.TeamRepository;
import com.growup.pms.test.annotation.AutoKoreanDisplayName;
import com.growup.pms.user.domain.User;
import com.growup.pms.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    TeamRepository teamRepository;

    @Mock
    ProjectUserRepository projectUserRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    ProjectService projectService;

    @Nested
    class 사용자가_프로젝트_생성_시에 {

        @Test
        void 성공한다() {
            // given
            Long 예상_프로젝트_ID = 1L;
            Long 예상_팀_ID = 1L;
            Long 예상_생성자_ID = 1L;
            String 예상_역할_이름 = ProjectRole.ADMIN.getRoleName();
            List<ProjectUserCreateRequest> 예상_초대할_팀원들 = List.of(프로젝트_유저_생성_요청은().이다());
            ProjectCreateCommand 예상_프로젝트_생성_요청 = 프로젝트_생성_요청은()
                    .초대할_팀원들은(예상_초대할_팀원들)
                    .이다().toCommand();
            Role 예상_역할 = 역할은().타입이(RoleType.PROJECT).이름이(예상_역할_이름).이다();
            User 예상_생성자 = 사용자는().이다();
            Team 예상_팀 = 팀은().이다();
            Project 예상_프로젝트 = 프로젝트는().이다();

            when(teamRepository.findByIdOrThrow(예상_팀_ID)).thenReturn(예상_팀);
            when(userRepository.findByIdOrThrow(예상_생성자_ID)).thenReturn(예상_생성자);
            when(roleRepository.findProjectRoleByName(예상_역할_이름)).thenReturn(예상_역할);
            when(projectRepository.save(any(Project.class))).thenReturn(예상_프로젝트);

            // when
            Long 실제_결과 = projectService.createProject(예상_팀_ID, 예상_생성자_ID, 예상_프로젝트_생성_요청);

            // then
            assertThat(실제_결과).isEqualTo(예상_프로젝트_ID);
        }

        @Test
        void 팀이_존재하지_않으면_예외가_발생한다() {
            // given
            Long 잘못된_팀_ID = 2L;
            Long 예상_생성자_ID = 1L;
            List<ProjectUserCreateRequest> 예상_초대할_팀원들 = List.of(프로젝트_유저_생성_요청은().이다());
            ProjectCreateCommand 예상_프로젝트_생성_요청 = 프로젝트_생성_요청은()
                    .초대할_팀원들은(예상_초대할_팀원들)
                    .이다().toCommand();
            doThrow(new BusinessException(ErrorCode.TEAM_NOT_FOUND))
                    .when(teamRepository).findByIdOrThrow(잘못된_팀_ID);

            // when & then
            assertThatThrownBy(() -> projectService.createProject(잘못된_팀_ID, 예상_생성자_ID, 예상_프로젝트_생성_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 팀을 찾을 수 없습니다. 팀 정보를 확인해 주세요.");
        }

        @Test
        void 회원이_존재하지_않으면_예외가_발생한다() {
            // given
            Long 예상_팀_ID = 1L;
            Long 잘못된_생성자_ID = 1L;
            List<ProjectUserCreateRequest> 예상_초대할_팀원들 = List.of(프로젝트_유저_생성_요청은().이다());
            Team 예상_팀 = 팀은().이다();
            ProjectCreateCommand 예상_프로젝트_생성_요청 = 프로젝트_생성_요청은()
                    .초대할_팀원들은(예상_초대할_팀원들)
                    .이다().toCommand();

            when(teamRepository.findByIdOrThrow(예상_팀_ID)).thenReturn(예상_팀).thenReturn(예상_팀);
            doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                    .when(userRepository).findByIdOrThrow(잘못된_생성자_ID);

            // when & then
            assertThatThrownBy(() -> projectService.createProject(예상_팀_ID, 잘못된_생성자_ID, 예상_프로젝트_생성_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 사용자를 찾을 수 없습니다. 입력 정보를 확인해 주세요.");
        }
    }

    @Nested
    class 사용자가_프로젝트_목록_조회시에 {

        @Test
        void 성공한다() {
            // given
            Long 예상_팀_ID = 1L;
            ProjectResponse 예상_프로젝트_1 = 프로젝트_목록조회_응답은().프로젝트_식별자는(1L).이다();
            ProjectResponse 예상_프로젝트_2 = 프로젝트_목록조회_응답은().프로젝트_식별자는(2L).이다();
            ProjectResponse 예상_프로젝트_3 = 프로젝트_목록조회_응답은().프로젝트_식별자는(3L).이다();
            List<ProjectResponse> 예상_결과 = List.of(예상_프로젝트_1, 예상_프로젝트_2, 예상_프로젝트_3);
            when(projectRepository.getProjectsByTeamId(예상_팀_ID)).thenReturn(예상_결과);

            // when
            List<ProjectResponse> 실제_결과 = projectService.getProjects(예상_팀_ID);

            // then
            assertThat(실제_결과.size()).isEqualTo(예상_결과.size());
        }

        @Test
        void 팀에_프로젝트가_없으면_빈리스트를_반환한다() {
            // given
            Long 잘못된_팀_ID = Long.MIN_VALUE;
            List<ProjectResponse> 예상_결과 = Collections.emptyList();
            when(projectRepository.getProjectsByTeamId(잘못된_팀_ID)).thenReturn(예상_결과);

            // when
            List<ProjectResponse> 실제_결과 = projectService.getProjects(잘못된_팀_ID);

            // then
            assertThat(실제_결과).isEmpty();
        }
    }

    @Nested
    class 사용자가_프로젝트_수정시에 {

        @Test
        void 성공한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            Project 기존_프로젝트 = 프로젝트는().이다();
            ProjectEditCommand 예상_프로젝트_수정_요청 = 프로젝트_수정_요청은().이다().toCommand();
            when(projectRepository.findByIdOrThrow(기존_프로젝트_ID)).thenReturn(기존_프로젝트);

            // when
            projectService.editProject(기존_프로젝트_ID, 예상_프로젝트_수정_요청);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(기존_프로젝트.getName()).isEqualTo(예상_프로젝트_수정_요청.projectName().get());
                softly.assertThat(기존_프로젝트.getContent()).isEqualTo(예상_프로젝트_수정_요청.content().get());
                softly.assertThat(기존_프로젝트.getStartDate()).isEqualTo(예상_프로젝트_수정_요청.startDate().get());
                softly.assertThat(기존_프로젝트.getEndDate()).isEqualTo(예상_프로젝트_수정_요청.endDate().get());
            });
        }

        @Test
        void 프로젝트가_존재하지_않으면_예외가_발생한다() {
            // given
            Long 잘못된_프로젝트_ID = Long.MAX_VALUE;
            ProjectEditCommand 예상_프로젝트_수정_요청 = 프로젝트_수정_요청은().이다().toCommand();
            doThrow(new BusinessException(ErrorCode.PROJECT_NOT_FOUND))
                    .when(projectRepository).findByIdOrThrow(잘못된_프로젝트_ID);

            // when & then
            assertThatThrownBy(() -> projectService.editProject(잘못된_프로젝트_ID, 예상_프로젝트_수정_요청))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class 사용자가_프로젝트_삭제시에 {

        @Test
        void 성공한다() {
            // given
            Long 예상_프로젝트_ID = 1L;
            Project 기존_프로젝트 = 프로젝트는().이다();
            when(projectRepository.findByIdOrThrow(예상_프로젝트_ID)).thenReturn(기존_프로젝트);

            // when
            projectService.deleteProject(예상_프로젝트_ID);

            // then
            verify(projectRepository).delete(기존_프로젝트);
        }

        @Test
        void 프로젝트가_존재하지_않으면_예외가_발생한다() {
            // given
            Long 잘못된_프로젝트_ID = Long.MAX_VALUE;
            doThrow(new BusinessException(ErrorCode.PROJECT_NOT_FOUND))
                    .when(projectRepository).findByIdOrThrow(잘못된_프로젝트_ID);

            // when & then
            assertThatThrownBy(() -> projectService.deleteProject(잘못된_프로젝트_ID))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class 사용자가_프로젝트_탈퇴시에 {

        @Test
        void 성공한다() {
            // given
            Long 팀_ID = 1L;
            Long 프로젝트_ID = 1L;
            Long 탈퇴할_회원_ID = 1L;
            User 팀장 = 사용자는().식별자가(2L).닉네임이("팀장").이다();
            Team 기존_팀 = 팀은().팀장이(팀장).이다();
            Role 기존_권한 = 역할은().타입이(RoleType.PROJECT).이름이(ProjectRole.ADMIN.getRoleName()).이다();
            ProjectUser 기존_프로젝트원 = 프로젝트_유저는().권한이(기존_권한).이다();
            when(teamRepository.findByIdOrThrow(팀_ID)).thenReturn(기존_팀);
            when(projectUserRepository.findByIdOrThrow(new ProjectUserId(프로젝트_ID, 탈퇴할_회원_ID))).thenReturn(기존_프로젝트원);

            // when
            projectService.leaveProject(팀_ID, 프로젝트_ID, 탈퇴할_회원_ID);

            // then
            verify(projectUserRepository).delete(기존_프로젝트원);
        }

        @Test
        void 팀장이면_예외가_발생한다() {
            // given
            Long 팀_ID = 1L;
            Long 프로젝트_ID = 1L;
            Long 팀장_ID = 1L;
            Team 기존_팀 = 팀은().이다();
            Role 기존_권한 = 역할은().타입이(RoleType.PROJECT).이름이(ProjectRole.ADMIN.getRoleName()).이다();
            ProjectUser 기존_프로젝트원 = 프로젝트_유저는().권한이(기존_권한).이다();
            when(teamRepository.findByIdOrThrow(팀_ID)).thenReturn(기존_팀);
            when(projectUserRepository.findByIdOrThrow(new ProjectUserId(프로젝트_ID, 팀장_ID))).thenReturn(기존_프로젝트원);

            // when & then
            assertThatThrownBy(() -> projectService.leaveProject(팀_ID, 프로젝트_ID, 팀장_ID))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}

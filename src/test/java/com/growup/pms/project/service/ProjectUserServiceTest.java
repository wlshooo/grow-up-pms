package com.growup.pms.project.service;

import static com.growup.pms.test.fixture.project.builder.ProjectTestBuilder.프로젝트는;
import static com.growup.pms.test.fixture.project.builder.ProjectUserCreateRequestTestBuilder.프로젝트_유저_생성_요청은;
import static com.growup.pms.test.fixture.project.builder.ProjectUserResponseTestBuilder.프로젝트원은;
import static com.growup.pms.test.fixture.project.builder.ProjectUserSearchResponseTestBuilder.검색된_프로젝트원은;
import static com.growup.pms.test.fixture.project.builder.ProjectUserTestBuilder.프로젝트_유저는;
import static com.growup.pms.test.fixture.role.builder.RoleTestBuilder.역할은;
import static com.growup.pms.test.fixture.user.builder.UserTestBuilder.사용자는;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.growup.pms.role.domain.RoleType;
import com.growup.pms.role.repository.RoleRepository;
import com.growup.pms.test.annotation.AutoKoreanDisplayName;
import com.growup.pms.user.domain.User;
import com.growup.pms.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class ProjectUserServiceTest {

    @Mock
    ProjectUserRepository projectUserRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    ProjectUserService projectUserService;

    @Nested
    class 사용자가_프로젝트원_추가_시에 {

        @Test
        void 성공한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            ProjectUserCreateCommand 프로젝트원_생성_요청 = 프로젝트_유저_생성_요청은().역할_이름이(ProjectRole.ASSIGNEE.getRoleName())
                    .이다().toCommand();

            Project 기존_프로젝트 = 프로젝트는().이다();
            User 기존_회원 = 사용자는().이다();
            Role 기존_역할 = 역할은()
                    .타입이(RoleType.PROJECT)
                    .이름이(ProjectRole.ADMIN.getRoleName())
                    .이다();

            when(projectUserRepository.existsById(any(ProjectUserId.class))).thenReturn(false);
            when(projectRepository.findByIdOrThrow(기존_프로젝트_ID))
                    .thenReturn(기존_프로젝트);
            when(userRepository.findByIdOrThrow(프로젝트원_생성_요청.userId()))
                    .thenReturn(기존_회원);
            when(roleRepository.findProjectRoleByName(프로젝트원_생성_요청.roleName()))
                    .thenReturn(기존_역할);

            // when & then
            assertThatCode(() -> projectUserService.createProjectUser(기존_프로젝트_ID, 프로젝트원_생성_요청))
                    .doesNotThrowAnyException();
        }

        @Test
        void 프로젝트에_이미_존재하는_회원이면_예외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            ProjectUserCreateCommand 프로젝트원_생성_요청 = 프로젝트_유저_생성_요청은().역할_이름이(ProjectRole.ASSIGNEE.getRoleName())
                    .이다().toCommand();

            // when
            doThrow(BusinessException.class).when(projectUserRepository).existsById(any(ProjectUserId.class));

            // then
            assertThatThrownBy(() -> projectUserService.createProjectUser(기존_프로젝트_ID, 프로젝트원_생성_요청))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 권한이_없는_사용자면_예외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            ProjectUserCreateCommand 프로젝트원_생성_요청 = 프로젝트_유저_생성_요청은().역할_이름이(ProjectRole.ADMIN.getRoleName())
                    .이다().toCommand();

            when(projectUserRepository.existsById(any(ProjectUserId.class))).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> projectUserService.createProjectUser(기존_프로젝트_ID, 프로젝트원_생성_요청))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 회원이_존재하지_않으면_예외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            ProjectUserCreateCommand 프로젝트원_생성_요청 = 프로젝트_유저_생성_요청은().역할_이름이(ProjectRole.ASSIGNEE.getRoleName())
                    .이다().toCommand();

            when(projectUserRepository.existsById(any(ProjectUserId.class))).thenReturn(false);
            doThrow(BusinessException.class).when(userRepository).findByIdOrThrow(프로젝트원_생성_요청.userId());

            // when & then
            assertThatThrownBy(() -> projectUserService.createProjectUser(기존_프로젝트_ID, 프로젝트원_생성_요청))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 권한이_존재하지_않으면_예외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            ProjectUserCreateCommand 프로젝트원_생성_요청 = 프로젝트_유저_생성_요청은().역할_이름이(ProjectRole.ASSIGNEE.getRoleName())
                    .이다().toCommand();

            when(projectUserRepository.existsById(any(ProjectUserId.class))).thenReturn(false);
            doThrow(BusinessException.class).when(roleRepository).findProjectRoleByName(프로젝트원_생성_요청.roleName());

            // when & then
            assertThatThrownBy(() -> projectUserService.createProjectUser(기존_프로젝트_ID, 프로젝트원_생성_요청))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class 관리자가_프로젝트원_제거_시에 {

        @Test
        void 성공한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            Long 기존_회원_ID = 1L;
            ProjectUser projectUser = 프로젝트_유저는().이다();
            when(projectUserRepository.findByIdOrThrow(any(ProjectUserId.class)))
                    .thenReturn(projectUser);

            // when
            projectUserService.kickProjectUser(기존_프로젝트_ID, 기존_회원_ID);

            // then
            verify(projectUserRepository).delete(projectUser);
        }

        @Test
        void 해당_프로젝트원이_없으면_에외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            Long 잘못된_회원_ID = 1L;
            doThrow(BusinessException.class).when(projectUserRepository).findByIdOrThrow(any(ProjectUserId.class));

            // when & then
            assertThatThrownBy(() -> projectUserService.kickProjectUser(기존_프로젝트_ID, 잘못된_회원_ID))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 추방하려는_프로젝트원이_수행자가_아니면_예외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            Long 기존_회원_ID = 1L;
            Role 기존_권한 = 역할은().타입이(RoleType.PROJECT).이름이(ProjectRole.LEADER.getRoleName()).이다();
            ProjectUser projectUser = 프로젝트_유저는().권한이(기존_권한).이다();
            when(projectUserRepository.findByIdOrThrow(any(ProjectUserId.class)))
                    .thenReturn(projectUser);

            // when & then
            assertThatThrownBy(() -> projectUserService.kickProjectUser(기존_프로젝트_ID, 기존_회원_ID))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class 관리자가_프로젝트원_역할_변경시에 {

        @Test
        void 성공한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            Long 기존_회원_ID = 1L;
            ProjectUser projectUser = 프로젝트_유저는().이다();
            Role 변경될_권한 = 역할은().타입이(RoleType.PROJECT).이름이(ProjectRole.LEADER.getRoleName()).이다();
            when(projectUserRepository.findByIdOrThrow(any(ProjectUserId.class)))
                    .thenReturn(projectUser);
            when(roleRepository.findProjectRoleByName(anyString()))
                    .thenReturn(변경될_권한);

            // when
            projectUserService.changeRole(기존_프로젝트_ID, 기존_회원_ID, 변경될_권한.getName());

            // then
            assertThat(projectUser.getRole()).isEqualTo(변경될_권한);
        }

        @Test
        void 해당_프로젝트원이_없으면_에외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            Long 잘못된_회원_ID = 1L;
            Role 변경될_권한 = 역할은().타입이(RoleType.PROJECT).이름이(ProjectRole.LEADER.getRoleName()).이다();
            doThrow(BusinessException.class).when(projectUserRepository).findByIdOrThrow(any(ProjectUserId.class));

            // when & then
            assertThatThrownBy(() -> projectUserService.changeRole(기존_프로젝트_ID, 잘못된_회원_ID, 변경될_권한.getName()))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 권한이_존재하지_않으면_예외가_발생한다() {
            // given
            Long 기존_프로젝트_ID = 1L;
            Long 기존_회원_ID = 1L;
            String 잘못된_권한_이름 = "ADMiN";
            ProjectUser projectUser = 프로젝트_유저는().이다();

            when(projectUserRepository.findByIdOrThrow(any(ProjectUserId.class)))
                    .thenReturn(projectUser);
            doThrow(BusinessException.class).when(roleRepository).findProjectRoleByName(anyString());

            // when & then
            assertThatThrownBy(() -> projectUserService.changeRole(기존_프로젝트_ID, 기존_회원_ID, 잘못된_권한_이름))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class 프로젝트원_목록_조회시에 {

        @Test
        void 성공한다() {
            // given
            Long 예상_프로젝트_ID = 1L;
            ProjectUserResponse 예상_프로젝트원_1 = 프로젝트원은()
                    .회원_식별자가(1L)
                    .닉네임이("브라운")
                    .역할이름이(ProjectRole.ADMIN.getRoleName())
                    .이다();

            ProjectUserResponse 예상_프로젝트원_2 = 프로젝트원은()
                    .회원_식별자가(2L)
                    .닉네임이("레니")
                    .역할이름이(ProjectRole.LEADER.getRoleName())
                    .이다();

            ProjectUserResponse 예상_프로젝트원_3 = 프로젝트원은()
                    .회원_식별자가(3L)
                    .닉네임이("레너드")
                    .역할이름이(ProjectRole.ASSIGNEE.getRoleName())
                    .이다();
            List<ProjectUserResponse> 예상_결과 = List.of(예상_프로젝트원_1, 예상_프로젝트원_2, 예상_프로젝트원_3);
            when(projectUserRepository.getProjectUsersByProjectId(anyLong())).thenReturn(예상_결과);

            // when
            List<ProjectUserResponse> 실제_결과 = projectUserService.getProjectUsers(예상_프로젝트_ID);

            // then
            assertThat(예상_결과.size()).isEqualTo(실제_결과.size());
        }

        @Test
        void 해당_프로젝트가_없으면_빈리스트를_반환한다() {
            // given
            Long 잘못된_프로젝트_ID = Long.MIN_VALUE;
            List<ProjectUserResponse> 예상_결과 = Collections.emptyList();
            when(projectUserRepository.getProjectUsersByProjectId(anyLong())).thenReturn(예상_결과);

            // when
            List<ProjectUserResponse> 실제_결과 = projectUserService.getProjectUsers(잘못된_프로젝트_ID);

            // then
            assertThat(실제_결과).isEmpty();
        }
    }

    @Nested
    class 접두사로_시작하는_닉네임을_가진_프로젝트_일정_수행자_검색시 {

        @Test
        void 성공한다() {
            // given
            Long 프로젝트_ID = 1L;
            String 접두사 = "레";

            ProjectUserSearchResponse 검색_결과_1 = 검색된_프로젝트원은()
                    .회원_식별자가(1L)
                    .닉네임이("레니")
                    .이다();
            ProjectUserSearchResponse 검색_결과_2 = 검색된_프로젝트원은()
                    .회원_식별자가(2L)
                    .닉네임이("레너드")
                    .이다();
            List<ProjectUserSearchResponse> 검색_결과_목록 = List.of(검색_결과_1, 검색_결과_2);

            when(projectUserRepository.searchProjectUsersByNicknamePrefix(anyLong(), anyString()))
                    .thenReturn(검색_결과_목록);

            // when
            List<ProjectUserSearchResponse> result = projectUserService.searchProjectUsersByPrefix(프로젝트_ID, 접두사);

            // then
            assertThat(검색_결과_목록).hasSize(2)
                    .extracting("userId", "nickname")
                    .containsExactlyInAnyOrder(
                            tuple(검색_결과_1.userId(), 검색_결과_1.nickname()),
                            tuple(검색_결과_2.userId(), 검색_결과_2.nickname())
                    );
        }

        @Test
        void 접두사로_시작하는_닉네임이_없다면_빈리스트를_반환한다() {
            // given
            Long 프로젝트_ID = 2L;
            String 접두사 = "!#%@#$%@$%@&";
            List<ProjectUserSearchResponse> 예상_결과 = Collections.emptyList();
            when(projectUserRepository.searchProjectUsersByNicknamePrefix(anyLong(), anyString()))
                    .thenReturn(예상_결과);

            // when
            List<ProjectUserSearchResponse> 실제_결과 = projectUserService.searchProjectUsersByPrefix(프로젝트_ID, 접두사);

            // then
            assertThat(실제_결과).isEmpty();
        }
    }
}

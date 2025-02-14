package com.growup.pms.user.service;

import static com.growup.pms.test.fixture.user.builder.NicknameDuplicationCheckRequestTestBuilder.닉네임_중복_검사는;
import static com.growup.pms.test.fixture.user.builder.RecoverPasswordRequestTestBuilder.비밀번호_찾기_요청은;
import static com.growup.pms.test.fixture.user.builder.RecoverUsernameRequestTestBuilder.아이디_찾기_요청은;
import static com.growup.pms.test.fixture.user.builder.UserCreateRequestTestBuilder.가입하는_사용자는;
import static com.growup.pms.test.fixture.user.builder.UserLinksUpdateRequestTestBuilder.사용자_링크_변경_요청은;
import static com.growup.pms.test.fixture.user.builder.UserPasswordUpdateTestBuilder.비밀번호_변경은;
import static com.growup.pms.test.fixture.user.builder.UserSearchResponseTestBuilder.사용자_검색_응답은;
import static com.growup.pms.test.fixture.user.builder.UserTestBuilder.사용자는;
import static com.growup.pms.test.fixture.user.builder.UserUpdateRequestTestBuilder.사용자_정보_변경_요청은;
import static com.growup.pms.test.fixture.user.builder.VerificationCodeCheckRequestTestBuilder.인증_코드_확인은;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.growup.pms.auth.service.mail.RedisEmailVerificationService;
import com.growup.pms.common.exception.code.ErrorCode;
import com.growup.pms.common.exception.exceptions.BusinessException;
import com.growup.pms.test.annotation.AutoKoreanDisplayName;
import com.growup.pms.user.controller.dto.request.UserUpdateRequest;
import com.growup.pms.user.controller.dto.response.RecoverPasswordResponse;
import com.growup.pms.user.controller.dto.response.RecoverUsernameResponse;
import com.growup.pms.user.controller.dto.response.UserResponse;
import com.growup.pms.user.controller.dto.response.UserSearchResponse;
import com.growup.pms.user.controller.dto.response.UserUpdateResponse;
import com.growup.pms.user.domain.User;
import com.growup.pms.user.domain.UserLink;
import com.growup.pms.user.repository.UserRepository;
import com.growup.pms.user.service.dto.NicknameDuplicationCheckCommand;
import com.growup.pms.user.service.dto.PasswordUpdateCommand;
import com.growup.pms.user.service.dto.RecoverPasswordCommand;
import com.growup.pms.user.service.dto.RecoverUsernameCommand;
import com.growup.pms.user.service.dto.UserCreateCommand;
import com.growup.pms.user.service.dto.UserLinksUpdateCommand;
import com.growup.pms.user.service.dto.UserUpdateCommand;
import com.growup.pms.user.service.dto.VerificationCodeCheckCommand;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RedisEmailVerificationService emailVerificationService;

    @InjectMocks
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Nested
    class 사용자가_자신_정보_조회_시에 {

        @Test
        void 성공한다() {
            // given
            Long 현재_사용자_ID = 1L;
            String 현재_아이디 = "brown";
            String 현재_이메일 = "brown@growup.kr";
            String 현재_닉네임 = "브라운";
            String 현재_자기소개 = "안녕하세요, 브라운입니다!";
            String 현재_프로필_이미지 = "dfc52089-d1ce-4652-b810-ab07ecd342ce.png";

            User 현재_사용자 = 사용자는()
                    .아이디가(현재_아이디)
                    .이메일이(현재_이메일)
                    .닉네임이(현재_닉네임)
                    .자기소개가(현재_자기소개)
                    .프로필_이미지_이름이(현재_프로필_이미지)
                    .이다();
            UserResponse 예상_응답 = UserResponse.from(현재_사용자);

            when(userRepository.findByIdOrThrow(현재_사용자_ID)).thenReturn(현재_사용자);

            // when
            UserResponse 실제_응답 = userService.getUser(현재_사용자_ID);

            // then
            assertThat(실제_응답).isEqualTo(예상_응답);
        }
    }

    @Nested
    class 사용자가_회원가입_시에 {

        @Test
        void 성공적으로_계정을_생성한다() {
            // given
            Long 예상하는_새_사용자_ID = 1L;
            User 새_사용자 = 사용자는().식별자가(예상하는_새_사용자_ID).이다();
            UserCreateCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(eq(새_사용자.getEmail()), anyString())).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(새_사용자);

            // when
            Long 실제_새_사용자_ID = userService.save(사용자_생성_요청);

            // then
            assertThat(실제_새_사용자_ID).isEqualTo(예상하는_새_사용자_ID);
        }

        @Test
        void 중복된_아이디를_사용하면_예외가_발생한다() {
            // given
            User 새_사용자 = 사용자는().이메일이("중복된 이메일").이다();
            UserCreateCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(eq(새_사용자.getEmail()), anyString())).thenReturn(true);
            doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

            // when & then
            assertThatThrownBy(() -> userService.save(사용자_생성_요청))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 이메일_인증에_실패하면_예외가_발생한다() {
            // given
            User 새_사용자 = 사용자는().이다();
            UserCreateCommand 사용자_생성_요청 = 가입하는_사용자는(새_사용자).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(anyString(), anyString())).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.save(사용자_생성_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        }
    }

    @Nested
    class 전체_사용자_검색_시에 {

        @Test
        void 성공한다() {
            // given
            String 닉네임_접두사 = "브";
            List<UserSearchResponse> 예상_결과 = List.of(사용자_검색_응답은().닉네임이("브라운").이다());

            when(userRepository.findUsersByNicknameStartingWith(닉네임_접두사)).thenReturn(예상_결과);

            // when
            List<UserSearchResponse> 실제_결과 = userService.searchUsersByNicknamePrefix(닉네임_접두사);

            // then
            assertThat(실제_결과).isEqualTo(예상_결과);
        }
    }

    @Nested
    class 비밀번호_변경_시에 {

        @Test
        void 성공적으로_비밀번호를_변경한다() {
            // given
            String 기존_비밀번호 = "test1234!@#$";
            String 새로운_비밀번호 = "test2345!@#$";
            User 기존_사용자 = 사용자는().이다();
            LocalDateTime 기존_비밀번호_수정일 = 기존_사용자.getPasswordChangeDate();

            PasswordUpdateCommand 비밀번호_변경_요청 = 비밀번호_변경은().기존_비밀번호가(기존_비밀번호).새로운_비밀번호가(새로운_비밀번호).이다().toCommand();

            when(userRepository.findByIdOrThrow(기존_사용자.getId())).thenReturn(기존_사용자);
            when(passwordEncoder.matches(비밀번호_변경_요청.password(), 기존_사용자.getPassword())).thenReturn(true);

            // when
            userService.updatePassword(기존_사용자.getId(), 비밀번호_변경_요청);

            // then
            assertThat(기존_비밀번호_수정일).isNotEqualTo(기존_사용자.getPasswordChangeDate());
        }

        @Test
        void 저장된_비밀번호와_매치에_실패하면_예외가_발생한다() {
            // given
            String 기존_비밀번호 = "test1234!@#$";
            String 새로운_비밀번호 = "test2345!@#$";
            User 기존_사용자 = 사용자는().이다();
            PasswordUpdateCommand 비밀번호_변경_요청 = 비밀번호_변경은().기존_비밀번호가(기존_비밀번호).새로운_비밀번호가(새로운_비밀번호).이다().toCommand();

            when(userRepository.findByIdOrThrow(기존_사용자.getId())).thenReturn(기존_사용자);
            when(passwordEncoder.matches(비밀번호_변경_요청.password(), 기존_사용자.getPassword())).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.updatePassword(기존_사용자.getId(), 비밀번호_변경_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
        }
    }

    @Nested
    class 유저_정보_변경_시에 {

        @Test
        void 성공한다() {
            // given
            Long 기존_사용자_아이디 = 1L;
            User 기존_사용자 = 사용자는().이다();
            String 변경할_닉네임 = "wlshooo";
            String 변경할_자기소개 = "신입입니다. 잘 부탁드려요!";
            String 변경할_프로필_이미지_이름 = "d4657d96-064e-4899-b13d-9bdda2840adc.png";

            UserUpdateCommand 사용자_정보_변경_요청 = 사용자_정보_변경_요청은()
                    .닉네임이(변경할_닉네임).자기소개는(변경할_자기소개).프로필_이미지_이름이(변경할_프로필_이미지_이름).이다().toCommand();

            when(userRepository.findByIdOrThrow(기존_사용자_아이디)).thenReturn(기존_사용자);

            // when
            UserUpdateResponse 변경된_유저_정보 = userService.updateUserDetails(기존_사용자_아이디, 사용자_정보_변경_요청);

            // then
            assertSoftly(softly -> {
                softly.assertThat(변경된_유저_정보.links()).hasSize(0);
                softly.assertThat(변경된_유저_정보)
                        .extracting("userId", "nickname", "profileImageName", "bio", "links")
                        .contains(기존_사용자_아이디, 변경할_닉네임, 변경할_자기소개, 변경할_프로필_이미지_이름, Collections.emptyList());
            });
        }

        @Test
        void 부분_변경도_성공한다() {
            // given
            Long 기존_사용자_아이디 = 1L;
            User 기존_사용자 = 사용자는().이다();
            String 변경할_닉네임 = "wlshooo";

            UserUpdateRequest 사용자_정보_부분_변경_요청 = UserUpdateRequest.builder()
                    .nickname(JsonNullable.of(변경할_닉네임))
                    .bio(JsonNullable.undefined())
                    .profileImageName(JsonNullable.undefined())
                    .build();

            when(userRepository.findByIdOrThrow(기존_사용자_아이디)).thenReturn(기존_사용자);

            // when
            UserUpdateResponse 사용자_정보_부분_변경_응답 = userService.updateUserDetails(기존_사용자_아이디, 사용자_정보_부분_변경_요청.toCommand());

            // then
            assertSoftly(softly -> softly.assertThat(사용자_정보_부분_변경_응답)
                    .extracting("userId", "nickname", "links")
                    .contains(기존_사용자_아이디, 변경할_닉네임, Collections.emptyList()));
        }
    }

    @Nested
    class 링크_변경_시 {

        @Test
        void 성공한다() {
            // Given
            User 기존_사용자 = 사용자는().이다();
            Long 기존_사용자_아이디 = 1L;
            List<String> 변경할_링크 = List.of(
                    "http://github.com",
                    "http://blog.example.com",
                    "http://GU-99.com",
                    "http://longBright.com"
            );

            UserLinksUpdateCommand 사용자_링크_변경_요청 = 사용자_링크_변경_요청은().링크가(변경할_링크).이다().toCommand();
            when(userRepository.findByIdOrThrow(기존_사용자_아이디)).thenReturn(기존_사용자);

            // When
            userService.updateUserLinks(기존_사용자_아이디, 사용자_링크_변경_요청);

            // Then
            assertSoftly(softly -> {
                softly.assertThat(기존_사용자.getLinks()).hasSize(변경할_링크.size());
                softly.assertThat(
                        기존_사용자.getLinks().stream()
                                .map(UserLink::getLink)
                                .toList()
                ).containsExactlyInAnyOrderElementsOf(변경할_링크);
            });
        }

        @Test
        void 링크가_5개를_초과하면_예외가_발생한다() {
            // given
            User 기존_사용자 = 사용자는().이다();
            List<String> 링크 = List.of("http://github.com", "http://blog.example.com",
                    "http://GU-99.com", "http://longBright.com", "http://yachimiy.com", "http://wlshooo.com");

            UserLinksUpdateCommand 사용자_정보_변경_요청 = 사용자_링크_변경_요청은().링크가(링크).이다().toCommand();

            when(userRepository.findByIdOrThrow(기존_사용자.getId())).thenReturn(기존_사용자);

            // when & then
            assertThatThrownBy(() -> userService.updateUserLinks(기존_사용자.getId(), 사용자_정보_변경_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXCEEDED_LINKS);
        }
    }

    @Nested
    class 아이디_찾기_시 {

        @Test
        void 성공한다() {
            // given
            String 연결된_이메일 = "brown@example.com";
            User 잃어버린_계정 = 사용자는().이메일이(연결된_이메일).이다();
            RecoverUsernameCommand 아이디_찾기_요청 = 아이디_찾기_요청은().이메일이(연결된_이메일).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(연결된_이메일,
                    String.valueOf(아이디_찾기_요청.verificationCode()))).thenReturn(true);
            when(userRepository.findByEmailOrThrow(연결된_이메일)).thenReturn(잃어버린_계정);

            // when
            RecoverUsernameResponse 실제_결과 = userService.recoverUsername(아이디_찾기_요청);

            // then
            assertThat(실제_결과.username()).isEqualTo(잃어버린_계정.getUsername());
        }

        @Test
        void 이메일_인증에_실패하면_예외가_발생한다() {
            // given
            String 연결된_이메일 = "brown@example.com";
            RecoverUsernameCommand 아이디_찾기_요청 = 아이디_찾기_요청은().이메일이(연결된_이메일).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(연결된_이메일,
                    String.valueOf(아이디_찾기_요청.verificationCode()))).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.recoverUsername(아이디_찾기_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        }
    }

    @Nested
    class 비밀번호_찾기_시 {

        @Test
        void 성공한다() {
            // given
            String 연결된_이메일 = "brown@example.com";
            String 연결된_아이디 = "brown";
            User 잃어버린_계정 = 사용자는().이메일이(연결된_이메일).아이디가(연결된_아이디).이다();
            RecoverPasswordCommand 비밀번호_찾기_요청 = 비밀번호_찾기_요청은().이메일이(연결된_이메일).아이디가(연결된_아이디).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(연결된_이메일,
                    String.valueOf(비밀번호_찾기_요청.verificationCode()))).thenReturn(true);
            when(userRepository.findByEmailOrThrow(연결된_이메일)).thenReturn(잃어버린_계정);

            // when
            RecoverPasswordResponse 실제_결과 = userService.recoverPassword(비밀번호_찾기_요청);

            // then
            assertThat(실제_결과.password()).isNotEmpty();
        }

        @Test
        void 이메일_인증에_실패하면_예외가_발생한다() {
            // given
            String 연결된_이메일 = "brown@example.com";
            String 연결된_아이디 = "brown";
            RecoverPasswordCommand 비밀번호_찾기_요청 = 비밀번호_찾기_요청은().이메일이(연결된_이메일).아이디가(연결된_아이디).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(연결된_이메일,
                    String.valueOf(비밀번호_찾기_요청.verificationCode()))).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.recoverPassword(비밀번호_찾기_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        }

        @Test
        void 사용자_아이디가_일치하지_않으면_예외가_발생한다() {
            // given
            String 연결된_이메일 = "brown@example.com";
            String 연결된_아이디 = "brown";
            String 입력된_아이디 = "incorrect_username";
            User 잃어버린_계정 = 사용자는().이메일이(연결된_이메일).아이디가(연결된_아이디).이다();
            RecoverPasswordCommand 비밀번호_찾기_요청 = 비밀번호_찾기_요청은().이메일이(연결된_이메일).아이디가(입력된_아이디).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(연결된_이메일,
                    String.valueOf(비밀번호_찾기_요청.verificationCode()))).thenReturn(true);
            when(userRepository.findByEmailOrThrow(연결된_이메일)).thenReturn(잃어버린_계정);

            // when & then
            assertThatThrownBy(() -> userService.recoverPassword(비밀번호_찾기_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        }
    }

    @Nested
    class 닉네임_중복_검사_시 {

        @Test
        void 성공한다() {
            // given
            String 새로운_닉네임 = "브라운";
            NicknameDuplicationCheckCommand 닉네임_중복_검사_요청 = 닉네임_중복_검사는().닉네임이(새로운_닉네임).이다().toCommand();

            when(userRepository.existsByNickname(새로운_닉네임)).thenReturn(false);

            // when
            userService.checkNicknameDuplication(닉네임_중복_검사_요청);

            // then
            verify(userRepository, times(1)).existsByNickname(새로운_닉네임);
        }

        @Test
        void 닉네임_중복_시_예외가_발생한다() {
            // given
            String 새로운_닉네임 = "브라운";
            NicknameDuplicationCheckCommand 닉네임_중복_검사_요청 = 닉네임_중복_검사는().닉네임이(새로운_닉네임).이다().toCommand();

            when(userRepository.existsByNickname(새로운_닉네임)).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.checkNicknameDuplication(닉네임_중복_검사_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    @Nested
    class 이메일_인증_시 {

        @Test
        void 성공한다() {
            // given
            String 이메일 = "test@test.com";
            String 인증코드 = "123456";
            VerificationCodeCheckCommand 인증_코드_확인_요청 = 인증_코드_확인은().이메일은(이메일).인증코드는(인증코드).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(이메일, 인증코드)).thenReturn(true);

            // when
            userService.checkVerificationCode(인증_코드_확인_요청);

            // then
            verify(emailVerificationService, times(1)).verifyAndInvalidateEmail(이메일, 인증코드);
        }

        @Test
        void 이메일_인증에_실패하면_예외가_발생한다() {
            // given
            String 이메일 = "test@test.com";
            String 인증코드 = "123456";
            VerificationCodeCheckCommand 인증_코드_확인_요청 = 인증_코드_확인은().이메일은(이메일).인증코드는(인증코드).이다().toCommand();

            when(emailVerificationService.verifyAndInvalidateEmail(이메일, 인증코드)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.checkVerificationCode(인증_코드_확인_요청))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        }
    }
}

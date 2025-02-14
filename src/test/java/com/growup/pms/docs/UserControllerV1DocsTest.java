package com.growup.pms.docs;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.growup.pms.test.fixture.user.builder.NicknameDuplicationCheckRequestTestBuilder.닉네임_중복_검사는;
import static com.growup.pms.test.fixture.user.builder.RecoverPasswordRequestTestBuilder.비밀번호_찾기_요청은;
import static com.growup.pms.test.fixture.user.builder.RecoverUsernameRequestTestBuilder.아이디_찾기_요청은;
import static com.growup.pms.test.fixture.user.builder.UserCreateRequestTestBuilder.가입하는_사용자는;
import static com.growup.pms.test.fixture.user.builder.UserPasswordUpdateTestBuilder.비밀번호_변경은;
import static com.growup.pms.test.fixture.user.builder.UserResponseTestBuilder.사용자_조회_응답은;
import static com.growup.pms.test.fixture.user.builder.UserTeamResponseTestBuilder.가입한_팀_응답은;
import static com.growup.pms.test.fixture.user.builder.UserUpdateRequestTestBuilder.사용자_정보_변경_요청은;
import static com.growup.pms.test.fixture.user.builder.VerificationCodeCheckRequestTestBuilder.인증_코드_확인은;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.growup.pms.test.annotation.AutoKoreanDisplayName;
import com.growup.pms.test.annotation.WithMockSecurityUser;
import com.growup.pms.test.support.ControllerSliceTestSupport;
import com.growup.pms.user.controller.dto.request.NicknameDuplicationCheckRequest;
import com.growup.pms.user.controller.dto.request.PasswordUpdateRequest;
import com.growup.pms.user.controller.dto.request.RecoverPasswordRequest;
import com.growup.pms.user.controller.dto.request.RecoverUsernameRequest;
import com.growup.pms.user.controller.dto.request.UserCreateRequest;
import com.growup.pms.user.controller.dto.request.UserLinksUpdateRequest;
import com.growup.pms.user.controller.dto.request.UserUpdateRequest;
import com.growup.pms.user.controller.dto.request.VerificationCodeCheckRequest;
import com.growup.pms.user.controller.dto.response.RecoverPasswordResponse;
import com.growup.pms.user.controller.dto.response.RecoverUsernameResponse;
import com.growup.pms.user.controller.dto.response.UserResponse;
import com.growup.pms.user.controller.dto.response.UserTeamResponse;
import com.growup.pms.user.controller.dto.response.UserUpdateResponse;
import com.growup.pms.user.service.UserService;
import com.growup.pms.user.service.dto.NicknameDuplicationCheckCommand;
import com.growup.pms.user.service.dto.PasswordUpdateCommand;
import com.growup.pms.user.service.dto.RecoverPasswordCommand;
import com.growup.pms.user.service.dto.RecoverUsernameCommand;
import com.growup.pms.user.service.dto.UserCreateCommand;
import com.growup.pms.user.service.dto.UserLinksUpdateCommand;
import com.growup.pms.user.service.dto.UserUpdateCommand;
import com.growup.pms.user.service.dto.VerificationCodeCheckCommand;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
class UserControllerV1DocsTest extends ControllerSliceTestSupport {
    static final String TAG = "User";

    @Autowired
    UserService userService;

    @Test
    @WithMockSecurityUser(id = 1L)
    void 현재_사용자_정보_조회_API_문서를_생성한다() throws Exception {
        // given
        Long 현재_사용자_ID = 1L;
        UserResponse 예상_응답 = 사용자_조회_응답은().이다();

        when(userService.getUser(현재_사용자_ID)).thenReturn(예상_응답);

        // when & then
        mockMvc.perform(get("/api/v1/user/me"))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("현재 사용자 정보 조회")
                                .description("현재 로그인한 사용자의 정보를 조회합니다.")
                                .responseFields(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("식별자"),
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("provider").type(JsonFieldType.STRING).description("인증 프로바이더"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                                        fieldWithPath("profileImageName").type(JsonFieldType.STRING).description("프로필 이미지 이름"),
                                        fieldWithPath("links").type(JsonFieldType.ARRAY).description("링크 목록"))
                                .responseHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .build())));
    }

    @Test
    @WithMockSecurityUser(id = 1L)
    void 가입한_팀_목록_조회_API_문서를_생성한다() throws Exception {
        // given
        Long 사용자_ID = 1L;
        List<UserTeamResponse> 예상_응답 = List.of(가입한_팀_응답은().이다());

        when(userService.getAllUserTeams(사용자_ID)).thenReturn(예상_응답);

        // when & then
        mockMvc.perform(get("/api/v1/user/team"))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("가입한 팀 목록 조회")
                                .description("가입한 팀이거나 가입 대기 중인 팀의 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("[].teamId").type(JsonFieldType.NUMBER).description("팀 ID"),
                                        fieldWithPath("[].teamName").type(JsonFieldType.STRING).description("팀 이름"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("팀 소개"),
                                        fieldWithPath("[].creator").type(JsonFieldType.STRING).description("팀장 닉네임"),
                                        fieldWithPath("[].creatorId").type(JsonFieldType.NUMBER).description("팀장 ID"),
                                        fieldWithPath("[].isPendingApproval").type(JsonFieldType.BOOLEAN).description("가입 대기 여부"),
                                        fieldWithPath("[].roleName").type(JsonFieldType.STRING).description("팀 내에서의 역할"))
                                .responseHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .build())));
    }

    @Test
    void 사용자_일반_회원가입_API_문서를_생성한다() throws Exception {
        // given
        Long 사용자_ID = 1L;
        UserCreateRequest 사용자_생성_요청 = 가입하는_사용자는().이다();

        when(userService.save(any(UserCreateCommand.class))).thenReturn(사용자_ID);

        // when & then
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(사용자_생성_요청)))
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "/api/v1/user/" + 사용자_ID)
                )
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("사용자 일반 회원가입")
                                .description("사용자의 계정을 서버에 등록합니다.")
                                .requestFields(
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 아이디"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                                        fieldWithPath("profileImageName").type(JsonFieldType.STRING).description("프로필 이미지 이름"),
                                        fieldWithPath("links").type(JsonFieldType.ARRAY).description("사용자 링크"),
                                        fieldWithPath("verificationCode").type(JsonFieldType.STRING).description("인증코드"))
                                .requestHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .build())));
    }

    @Test
    void 인증코드_전송_API_문서를_생성한다() throws Exception {
        // given
        Long 사용자_ID = 1L;
        Map<String, String> 가입하려는_사용자_이메일 = Map.of("email", "test@example.org");

        when(userService.save(any(UserCreateCommand.class))).thenReturn(사용자_ID);

        // when & then
        mockMvc.perform(post("/api/v1/user/verify/send")
                        .content(objectMapper.writeValueAsString(가입하려는_사용자_이메일))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("인증코드 전송")
                                .description("사용자의 이메일에 인증코드를 전송합니다. 만료기간은 3분입니다.")
                                .requestFields(fieldWithPath("email").type(JsonFieldType.STRING).description("인증하려는 사용자 이메일"))
                                .requestHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .build())));
    }

    @Test
    void 아이디_찾기_API_문서를_생성한다() throws Exception {
        // given
        String 복구된_아이디 = "brown";
        RecoverUsernameRequest 아이디_찾기_요청 = 아이디_찾기_요청은().이다();
        RecoverUsernameResponse 아이디_찾기_응답 = new RecoverUsernameResponse(복구된_아이디);

        when(userService.recoverUsername(any(RecoverUsernameCommand.class))).thenReturn(아이디_찾기_응답);

        // when & then
        mockMvc.perform(post("/api/v1/user/recover/username")
                        .content(objectMapper.writeValueAsString(아이디_찾기_요청))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("아이디 찾기")
                                .description("이메일 인증을 통해 사용자의 아이디를 찾는다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("가입 시 입력한 이메일"),
                                        fieldWithPath("verificationCode").type(JsonFieldType.STRING).description("이메일로 전송된 인증번호"))
                                .requestHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .responseFields(fieldWithPath("username").type(JsonFieldType.STRING).description("복구된 사용자의 아이디"))
                                .build())));
    }

    @Test
    void 비밀번호_찾기_API_문서를_생성한다() throws Exception {
        // given
        String 새로_발급된_비밀번호 = "napl1m!A";
        RecoverPasswordRequest 비밀번호_찾기_요청 = 비밀번호_찾기_요청은().이다();
        RecoverPasswordResponse 비밀번호_찾기_응답 = new RecoverPasswordResponse(새로_발급된_비밀번호);

        when(userService.recoverPassword(any(RecoverPasswordCommand.class))).thenReturn(비밀번호_찾기_응답);

        // when & then
        mockMvc.perform(post("/api/v1/user/recover/password")
                        .content(objectMapper.writeValueAsString(비밀번호_찾기_요청))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("비밀번호 찾기")
                                .description("이메일 인증과 아이디를 통해서 사용자에게 임시 비밀번호를 발급한다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("가입 시 입력한 이메일"),
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("가입 시 입력한 아이디"),
                                        fieldWithPath("verificationCode").type(JsonFieldType.STRING).description("이메일로 전송된 인증번호"))
                                .requestHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .responseFields(fieldWithPath("password").type(JsonFieldType.STRING).description("임시로 발급된 비밀번호"))
                                .build())));
    }

    @Test
    @WithMockSecurityUser(id = 1L)
    void 비밀번호_변경_API_문서를_생성한다() throws Exception {
        // given
        String 기존_비밀번호 = "test1234!@#$";
        String 새로운_비밀번호 = "test2345!@#$";
        PasswordUpdateRequest 비밀번호_변경_요청 = 비밀번호_변경은().기존_비밀번호가(기존_비밀번호).새로운_비밀번호가(새로운_비밀번호).이다();

        doNothing().when(userService).updatePassword(anyLong(), any(PasswordUpdateCommand.class));

        // when & then
        mockMvc.perform(patch("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(비밀번호_변경_요청))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("비밀번호 변경")
                                .description("새로운 비밀번호를 통해 사용자의 기존 비밀번호를 변경한다.")
                                .requestFields(
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("기존 비밀번호"),
                                        fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로운 비밀번호"))
                                .requestHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .build())));
    }

    @Test
    void 닉네임_중복_검사_API_문서를_생성한다() throws Exception {
        // given
        String 새로운_닉네임 = "브라운";
        NicknameDuplicationCheckRequest 닉네임_중복_검사_요청 = 닉네임_중복_검사는().닉네임이(새로운_닉네임).이다();

        doNothing().when(userService).checkNicknameDuplication(any(NicknameDuplicationCheckCommand.class));

        // when & then
        mockMvc.perform(post("/api/v1/user/nickname")
                        .content(objectMapper.writeValueAsString(닉네임_중복_검사_요청))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("닉네임 중복 검사")
                                .description("닉네임이 서버에 존재하는지 확인합니다.")
                                .requestFields(fieldWithPath("nickname").type(JsonFieldType.STRING).description("검사할 닉네임"))
                                .build())));
    }

    @Test
    void 이메일_인증_코드_검사_API_문서를_생성한다() throws Exception {
        // given
        String 이메일 = "test@test.com";
        String 인증코드 = "123456";
        VerificationCodeCheckRequest 인증_코드_확인_요청 = 인증_코드_확인은().이메일은(이메일).인증코드는(인증코드).이다();

        doNothing().when(userService).checkVerificationCode(any(VerificationCodeCheckCommand.class));

        // when & then
        mockMvc.perform(post("/api/v1/user/verify/code")
                        .content(objectMapper.writeValueAsString(인증_코드_확인_요청))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("이메일 인증 코드 확인")
                                .description("인증 코드와 이메일로 이메일 인증을 검사한다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("verificationCode").type(JsonFieldType.STRING).description("인증 코드"))
                                .build())));
    }

    @Test
    @WithMockSecurityUser(id = 1L)
    void 유저_정보_변경_API_문서를_생성한다() throws Exception {
        // given
        Long 기존_사용자_아이디 = 1L;
        String 변경할_닉네임 = "wlshooo";
        String 변경할_자기소개 = "신입입니다. 잘 부탁드려요!";
        String 변경할_프로필_이미지_이름 = "d4657d96-064e-4899-b13d-9bdda2840adc.png";

        UserUpdateRequest 사용자_정보_변경_요청 = 사용자_정보_변경_요청은().닉네임이(변경할_닉네임).자기소개는(변경할_자기소개).프로필_이미지_이름이(변경할_프로필_이미지_이름).이다();

        UserUpdateResponse 예상_응답 = new UserUpdateResponse(기존_사용자_아이디, 변경할_닉네임, 변경할_프로필_이미지_이름, 변경할_자기소개, null);

        when(userService.updateUserDetails(anyLong(), any(UserUpdateCommand.class))).thenReturn(예상_응답);

        // when & then
        mockMvc.perform(patch("/api/v1/user")
                        .content(objectMapper.writeValueAsString(사용자_정보_변경_요청))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("현재 사용자 정보 변경")
                                .description("현재 로그인한 사용자의 정보를 변경합니다.")
                                .requestFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                                        fieldWithPath("profileImageName").type(JsonFieldType.STRING).description("프로필 이미지"))
                                .requestHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .responseFields(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 아이디"),
                                        fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                                        fieldWithPath("profileImageName").type(JsonFieldType.STRING).description("프로필 이미지 이름"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("links").type(JsonFieldType.NULL).description("링크"))
                                .responseHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE))
                                .build())));
    }

    @Test
    @WithMockSecurityUser(id = 1L)
    void 사용자_링크_변경_API_문서를_생성한다() throws Exception {
        // given
        List<String> 변경할_링크 = List.of(
                "http://github.com",
                "http://blog.example.com",
                "http://GU-99.com",
                "http://longBright.com"
        );

        UserLinksUpdateRequest 사용자_링크_변경_요청 = UserLinksUpdateRequest.builder()
                .links(JsonNullable.of(변경할_링크))
                .build();

        doNothing().when(userService).updateUserLinks(anyLong(), any(UserLinksUpdateCommand.class));

        // when & then
        mockMvc.perform(patch("/api/v1/user/links")
                        .content(objectMapper.writeValueAsString(사용자_링크_변경_요청))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(docs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("사용자 링크 변경")
                                .description("현재 로그인한 사용자의 링크를 변경합니다.")
                                .requestFields(
                                        fieldWithPath("links").type(JsonFieldType.ARRAY).description("링크 목록"))
                                .build())));
    }
}

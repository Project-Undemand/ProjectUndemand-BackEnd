package PU.pushop.global.authentication.jwts.filters;

import PU.pushop.global.ResponseMessageConstants;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.SocialType;
import PU.pushop.members.service.MemberService;
import PU.pushop.members.service.RefreshService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static PU.pushop.global.authentication.jwts.utils.CookieUtil.createCookie;

@Slf4j
public class LoginFilter extends CustomJsonEmailPasswordAuthenticationFilter {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;
    private final ObjectMapper objectMapper;

    private static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리

    public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, MemberService memberService, JWTUtil jwtUtil, RefreshService refreshService, ObjectMapper objectMapper1) {
        super(authenticationManager, objectMapper);
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
        this.objectMapper = objectMapper1;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if(request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)  ) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }
        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        // 자바 8 이상부터, TypeReference 를 통해 원하는 형(Type)을 넣어주지 않으면 경고문이 뜸. NullPointException 등등 (ex) get("email"), readValue("meesage") 등등 . 읽어오지 못할 경우도 생기기 때문
        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, new TypeReference<>() {
        });

        //클라이언트 요청에서 email, password 추출
        String email = usernamePasswordMap.get("email");
        String password = usernamePasswordMap.get("password");

        // 이메일로 모든 회원을 조회
        List<Member> members = memberService.findMembersByEmail(email);
        log.info("members found with email {}: {}", email, members.size());

        // GENERAL 타입의 회원만 필터링
        List<Member> generalMembers = members.stream()
                .filter(member -> member.getSocialType() == SocialType.GENERAL)
                .toList();

        if (generalMembers.isEmpty()) {
            log.info("존재하지 않는 이메일입니다: {}", email);
            throw new EmailNotFoundException(ResponseMessageConstants.AUTHENTICATION_NOT_FOUND_EMAIL);
        } else if (generalMembers.size() > 1) {
            throw new AuthenticationServiceException("There are multiple users associated with this email: " + email);
        }

        Member generalMember = generalMembers.get(0);

        boolean isPasswordAuthenticated = memberService.checkPassword(generalMember.getEmail(), password);
        if (!isPasswordAuthenticated) {
            throw new BadCredentialsException(ResponseMessageConstants.AUTHENTICATION_INVALID_PASSWORD);
        }
        // Principal(인증-유저이메일), Credentials(권한), Authenticated 등의 정보
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
//            log.info(String.valueOf(authToken.toString()));
        return this.getAuthenticationManager().authenticate(authToken);
    }

    @Override
    // 로그인 성공 시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        Member member = memberService.memberLogin(email);
        // 액세스, 리프레쉬 토큰 생성 시 id, role 필요
        String memberId = member.getId().toString();
        String role = member.getMemberRole().toString();

        // 토큰 종류(카테고리), 유저이름, 역할 등을 페이로드에 담는다.
        String newAccess = jwtUtil.createAccessToken("access", memberId, role);
        String newRefresh = jwtUtil.createRefreshToken("refresh", memberId, role);

        // [Refresh 토큰 - DB 에서 관리합니다.] 리프레쉬 토큰 관리권한이 서버에 있습니다.
        refreshService.saveOrUpdateRefreshEntity(member, newRefresh);

        // [response.data] 에 Json 형태로 accessToken 과 refreshToken 을 넣어주는 방식
        addResponseDataV3(response, newAccess, newRefresh, email);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인에 실패했습니다. 실패 원인: {}", failed.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> responseData = new HashMap<>();

        if (failed instanceof BadCredentialsException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseData.put("error", ResponseMessageConstants.AUTHENTICATION_INVALID_PASSWORD);
        } else if (failed instanceof EmailNotFoundException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            responseData.put("error", ResponseMessageConstants.AUTHENTICATION_NOT_FOUND_EMAIL);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseData.put("error", ResponseMessageConstants.AUTHENTICATION_FAILED);
        }

//        response.getWriter().write(responseData.toString());
        response.getWriter().write(objectMapper.writeValueAsString(responseData));

//        super.unsuccessfulAuthentication(request, response, failed);
    }

    /**
     *
     * 쿠키에 refreshToken 을 넣어주는 방식
     */
    private void addResponseDataV3(HttpServletResponse response, String accessToken, String refreshToken, String email) throws IOException {
        // 액세스 토큰을 JsonObject 형식으로 응답 데이터에 포함하여 클라이언트에게 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // JSON 객체를 생성하고 액세스 토큰을 추가
        JsonObject responseData = new JsonObject();
        responseData.addProperty("accessToken", accessToken);
        response.getWriter().write(responseData.toString());
        // 리프레시 토큰을 쿠키에 저장
        response.addCookie(createCookie("refreshAuthorization", "Bearer+" +refreshToken));
        // HttpStatus 200 OK
        response.setStatus(HttpStatus.OK.value());
    }

    public static class EmailNotFoundException extends AuthenticationException {
        public EmailNotFoundException(String message) {
            super(message);
        }
    }


    // 사용자의 권한 정보를 가져옴
    private String extractAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER"); // 기본 권한 설정. [따로 설정하지 않았을때]
    }

    /**
     * 로그인 성공시 -> [reponse Header] : Access Token 추가, [reponse Cookie] : Refresh Token 추가
     */
    private void setTokenResponseV1(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // [reponse Header] : Access Token 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        // [reponse Cookie] : Refresh Token 추가
        response.addCookie(createCookie("RefreshToken", refreshToken));
        // HttpStatus 200 OK
        response.setStatus(HttpStatus.OK.value());
    }

    /**
     * [response.data] 에 Json 형태로 accessToken 을 넣어주고, 쿠키에 refreshToken 을 넣어주는 방식
     */
    private void addResponseDataV2(HttpServletResponse response, String accessToken, String refreshToken, String email) throws IOException {
        // 액세스 토큰을 JsonObject 형식으로 응답 데이터에 포함하여 클라이언트에게 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // response.data 에 accessToken, refreshToken 담아주기.
        JsonObject responseData = new JsonObject();
        responseData.addProperty("accessToken", accessToken);
        responseData.addProperty("refreshToken", refreshToken);
        response.getWriter().write(responseData.toString());
        // HttpStatus 200 OK
        response.setStatus(HttpStatus.OK.value());
    }

}

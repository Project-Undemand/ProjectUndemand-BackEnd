package PU.pushop.global.authentication.jwt.filters;

import PU.pushop.global.authentication.jwt.login.CustomUserDetails;
import PU.pushop.global.authentication.jwt.util.JWTUtil;
import PU.pushop.members.entity.RefreshEntity;
import PU.pushop.members.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class LoginFilter extends CustomJsonUsernamePasswordAuthenticationFilter{

    private Long accessTokenExpirationPeriod = 3600000L;

    private Long refreshTokenExpirationPeriod = 1209600000L;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper;

    private static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리

    public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JWTUtil jwtUtil, RefreshRepository refreshRepository, ObjectMapper objectMapper1) {
        super(authenticationManager, objectMapper);
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.objectMapper = objectMapper1;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if(request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)  ) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

        //클라이언트 요청에서 email, password 추출
        String email = usernamePasswordMap.get("email");
        String password = usernamePasswordMap.get("password");

        // Principal(인증-유저이메일), Credentials(권한), Authenticated 등의 정보
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        //token검증을 위한 AuthenticationManager로 전달
        return this.authenticationManager.authenticate(authToken);
    }


    @Override
    // 로그인 성공 시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("로그인에 성공했습니다. ");
        // 사용자명을 가져옴
        String username = authentication.getName();
        // 권한을 문자열로 변환
        String role = extractAuthority(authentication);
        // 토큰 종류(카테고리), 유저이름, 역할 등을 페이로드에 담는다.
        String access = jwtUtil.createAccessToken("access", username, role);
        String refresh = jwtUtil.createRefreshToken("refresh", username, role);

        // Refresh 토큰 저장
        saveRefreshEntity(username, refresh);
        // 응답 헤더에 JWT 토큰 추가
        setTokenResponse(response, access, refresh);
    }

    // 사용자의 권한 정보를 가져옴
    private String extractAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER"); // 기본 권한 설정
    }

    private void setTokenResponse(HttpServletResponse response, String accessToken, String refreshToken) {
        // 액세스 토큰 - 헤더, 리프레시 토큰 - 쿠키
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie("RefreshToken", refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    private void saveRefreshEntity(String username, String refresh) {
        // 현재 시간에 refreshTokenExpirationPeriod을 더한 후 LocalDateTime으로 변환
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(refreshTokenExpirationPeriod);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(expirationDateTime);

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60 ); // 1일
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS에서만 쿠키 전송
//         cookie.setPath("/"); // 필요에 따라 설정
        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        log.info("로그인에 실패했습니다. ");
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
        super.unsuccessfulAuthentication(request, response, failed);
    }
}

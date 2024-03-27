package PU.pushop.global.authentication.jwt.login.filters;

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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Date;

public class LoginFilter extends CustomJsonUsernamePasswordAuthenticationFilter{
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        super(authenticationManager, objectMapper);
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //클라이언트 요청에서 email, password 추출
        String email = obtainUsername(request);
        String password = obtainPassword(request);

        //스프링 시큐리티에서 email과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        System.out.println(authToken);
        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return this.authenticationManager.authenticate(authToken);
    }


    @Override
    // 로그인 성공 시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        System.out.println("로그인에 성공했습니다. ");
        // 사용자명을 가져옴

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        // 권한을 문자열로 변환
        String role = extractAuthority(authentication);
        // JWT 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 60 * 60 * 10L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 60 * 60 * 10L);

        //Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L);
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

    private void setTokenResponse(HttpServletResponse response, String access, String refresh) {
        response.setHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("Refresh-Token", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 1일
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS에서만 쿠키 전송
//         cookie.setPath("/"); // 필요에 따라 설정
        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        System.out.println("로그인에 실패했습니다. ");
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
        super.unsuccessfulAuthentication(request, response, failed);
    }
}

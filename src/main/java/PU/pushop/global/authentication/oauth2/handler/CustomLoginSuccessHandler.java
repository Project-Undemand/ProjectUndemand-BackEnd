package PU.pushop.global.authentication.oauth2.handler;


import PU.pushop.global.authentication.jwt.util.JWTUtil;
import PU.pushop.global.authentication.oauth2.custom.entity.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) throws IOException, ServletException {

//        401 에러 및 모든 페이지가 나오지 않는 상황 -> JWT필터 및 JWT유틸 문제 가능성 -> 시도 (2024.03.31)
//        Object principal = authentication.getPrincipal();
//        log.info("------------------------------------------");
//        log.info("principal = " + principal);
//        log.info("------------------------------------------");
//        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
//        log.info("------------------------------------------");
//        log.info("customUserDetails = " + customUserDetails);
//        log.info("------------------------------------------");

//        String username = customUserDetails.getName();
        String username = authentication.getName();
        String role = extractOAuthRole(authentication);

        // 액세스 토큰을 생성합니다.
        String accessToken = jwtUtil.createAccessToken("access", username, role);
        // 리프레시 토큰을 생성합니다.
        String refreshToken = jwtUtil.createRefreshToken("refresh", username, role);

        // 액세스 토큰을 HTTP 응답 헤더에 추가합니다.
        response.addHeader("Authorization", "Bearer " + accessToken);
        // 리프레시 토큰은 쿠키에 저장합니다.
        response.addCookie(createCookie("RefreshToken", refreshToken));

        response.sendRedirect("http://localhost:3000/");
    }

    private static String extractOAuthRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        return role;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}

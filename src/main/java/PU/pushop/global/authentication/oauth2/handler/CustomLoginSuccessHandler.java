package PU.pushop.global.authentication.oauth2.handler;


import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final MemberRepositoryV1 memberRepositoryV1;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        String role = extractOAuthRole(authentication);
        log.info("=============소셜 로그인 성공, 유저 데이터 시작 ==============");
        log.info("email = " + email);
        log.info("role = " + role);
        log.info("=============소셜 로그인 성공, 유저 데이터 시작 ==============");
        Member requestMember = memberRepositoryV1.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));
        log.info("requestMember = " + requestMember);
        // 액세스 토큰을 생성합니다.
        String accessToken = jwtUtil.createAccessToken("access", String.valueOf(requestMember.getId()), role);
        // 리프레시 토큰을 생성합니다.
        String refreshToken = jwtUtil.createRefreshToken("refresh", String.valueOf(requestMember.getId()), role);
        log.info(accessToken);
        log.info(refreshToken);

        // 액세스 토큰을 HTTP 응답 헤더에 추가합니다.
        response.addHeader("Authorization", "Bearer " + accessToken);
        // 리프레시 토큰은 쿠키에 저장합니다.
        response.addCookie(createCookie("RefreshToken", refreshToken));

        response.sendRedirect("http://localhost:8080/");
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

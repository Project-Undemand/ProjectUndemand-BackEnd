package PU.pushop.global.authentication.oauth2.handler;


import PU.pushop.global.authentication.jwt.util.JWTUtil;
import PU.pushop.global.authentication.oauth2.custom.entity.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        // 로그인 유저의 SocialId << 식별자값을 nickname으로 둠.
        String nickname = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 액세스 토큰을 생성합니다.
        String accessToken = jwtUtil.createAccessToken("access", nickname, role);
        // 리프레시 토큰을 생성합니다.
        String refreshToken = jwtUtil.createRefreshToken("refresh", nickname, role);
        System.out.println("refreshToken = " + refreshToken);
        System.out.println("accessToken = " + accessToken);

        // 액세스 토큰을 HTTP 응답 헤더에 추가합니다.
        response.addHeader("Authorization", "Bearer " + accessToken);
        // 리프레시 토큰은 쿠키에 저장합니다.
        response.addCookie(createCookie("RefreshToken", refreshToken));

        response.sendRedirect("http://localhost:8080/");
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

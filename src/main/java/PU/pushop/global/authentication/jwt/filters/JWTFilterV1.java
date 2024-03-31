package PU.pushop.global.authentication.jwt.filters;

import PU.pushop.global.authentication.jwt.util.JWTUtil;
import PU.pushop.global.authentication.oauth2.custom.entity.CustomOAuth2User;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.model.OAuthUserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JWTFilterV1 extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractAuthorizationTokenFromHeader(request);

        if (accessToken == null) {
            log.info("accesstoken null, please login");
            filterChain.doFilter(request, response); // 로그인 하지 않은 유저들 -> 에러가 아닌, 다음 필터로
            return;
        }

        if (jwtUtil.isExpired(accessToken)) {
            log.info("accesstoken expired user !");
            unauthorizedResponse(response, "accesstoken expired.");
            return;
        }
        // access 에 있는 username, role 을 통해 Authentication 사용자 정보를
        Authentication authToken = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private String extractAuthorizationTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7); // "Bearer " 다음 문자열이 토큰이므로 잘라냄
    }

    private void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        logger.info(message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private Authentication getAuthentication(String token) {
        if (!jwtUtil.validateToken(token)) {
            // 토큰이 유효하지 않을 경우 예외 처리
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }

        String username = jwtUtil.getUsername(token);
        MemberRole role = jwtUtil.getRole(token);

        OAuthUserDTO userDTO = new OAuthUserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        return new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
    }

}

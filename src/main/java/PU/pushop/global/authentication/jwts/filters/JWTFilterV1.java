package PU.pushop.global.authentication.jwts.filters;

import PU.pushop.global.authentication.jwts.login.CustomUserDetails;
import PU.pushop.global.authentication.jwts.login.dto.CustomMemberDto;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.global.authentication.oauth2.custom.entity.CustomOAuth2User;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.model.OAuthUserDTO;
import PU.pushop.members.repository.MemberRepositoryV1;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JWTFilterV1 extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request에서 Authorization 헤더 찾음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증
        // Authorization 헤더가 비어있거나 "Bearer " 로 시작하지 않은 경우
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("로그인 하지 않은 상태입니다. 액세스토큰 없음");
            // 토큰이 유효하지 않으므로 클라이언트에게 에러 메시지 전송
//            sendErrorResponse(response, "액세스 토큰이 없습니다.");
//            filterChain.doFilter(request, response);
            // 메서드 종료
            return;
        }

        // Authorization에서 Bearer 접두사 제거
        String accessToken = authorization.split(" ")[1];

        // 유효기간이 만료한 경우
        if (jwtUtil.isExpired(accessToken)) {
            log.info("토큰이 만료되었습니다.");
            // 토큰이 만료되었으므로 클라이언트에게 에러 메시지 전송
//            sendErrorResponse(response, "액세스 토큰이 만료되었습니다.");
            filterChain.doFilter(request, response);
            // 메서드 종료
            return;
        }

        // access 에 있는 username, role 을 통해 Authentication 사용자 정보를
        Authentication authToken = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private ResponseEntity<String> unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        log.info(message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    private Authentication getAuthentication(String token) {
        if (!jwtUtil.validateToken(token)) {
            // 토큰이 유효하지 않을 경우 예외 처리
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }

        String memberId = jwtUtil.getMemberId(token);
        MemberRole role = jwtUtil.getRole(token);

        CustomMemberDto customMemberDto = CustomMemberDto.createCustomMember(Long.valueOf(memberId), role, true);

        CustomUserDetails customOAuth2User = new CustomUserDetails(customMemberDto);
        return new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
    }
    // 로그인 안된 상태, 토큰 만료되었을 때 response에 담아버리면 , 모든 페이지에서 권한 에러 401 가 뜬다.
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        // 클라이언트에게 전송할 에러 메시지와 상태 코드 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}"); // 에러 메시지 전송
    }


}

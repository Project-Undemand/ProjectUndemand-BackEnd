package PU.pushop.global.authentication.jwts.filters;

import PU.pushop.global.authentication.jwts.entity.CustomMemberDto;
import PU.pushop.global.authentication.jwts.entity.CustomUserDetails;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.repository.RefreshRepository;
import io.jsonwebtoken.Claims;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import static PU.pushop.global.ResponseMessageConstants.PRODUCT_NOT_FOUND;
import static PU.pushop.global.ResponseMessageConstants.REFRESH_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class JWTFilterV1 extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request에서 Authorization 헤더 찾음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증
        // Authorization 헤더가 비어있거나 "Bearer " 로 시작하지 않은 경우
        if(authorization == null || !authorization.startsWith("Bearer ")){

            log.info("로그인 하지 않은 상태입니다. 액세스토큰 없음");
            // 토큰이 유효하지 않으므로 request와 response를 다음 필터로 넘겨줌
            filterChain.doFilter(request, response);
            // 메서드 종료
            return;
        }

        // Authorization에서 Bearer 접두사 제거
        String accessToken = authorization.split(" ")[1];

        // 유효기간이 만료한 경우
        if(jwtUtil.isExpired(accessToken)){
            log.info("token expired");
            String memberId = jwtUtil.getMemberId(accessToken);

            Refresh refreshEntity = refreshRepository.findByMemberId(Long.valueOf(memberId)).orElseThrow(() -> new NoSuchElementException(REFRESH_NOT_FOUND));
            // memberId 에 해당하는 Refresh 엔티티가 존재합니다. 그 Refresh 의 expiration 을 확인합니다.
            LocalDateTime refreshExpiration = refreshEntity.getExpiration();
            if (refreshExpiration.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Refresh Token is expired.");
            }
            // refreshEntity 를 통해서 새로운 엑세스토큰을 발급합니다.
            String newAccessToken = generateNewAccessToken(refreshEntity);
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

    public String generateNewAccessToken(Refresh refreshEntity) {
        String refreshToken = refreshEntity.getRefreshToken();


        // Parse details from the refresh token.
        String category = jwtUtil.getCategory(refreshToken);
        String memberId = jwtUtil.getMemberId(refreshToken);
        String role = jwtUtil.getRole(refreshToken).name();

        // create a new access token and return
        return jwtUtil.createAccessToken(category, memberId, role);
    }
}

package PU.pushop.global.authentication.jwts.filters;

import PU.pushop.global.authentication.jwts.entity.CustomUserDetails;
import PU.pushop.global.authentication.jwts.entity.CustomMemberDto;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.repository.MemberRepositoryV1;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilterV0 extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepositoryV1 memberRepositoryV1;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access 키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
            jwtUtil.isExpired(accessToken);

            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = jwtUtil.getCategory(accessToken);
            if (!category.equals("access")) {
                unauthorizedResponse(response, "invalid access token");
                return;
            }

            // accessToken 으로부터 username, role 값을 획득
            String memberId = jwtUtil.getMemberId(accessToken);
            MemberRole role = jwtUtil.getRole(accessToken);
            Member member = memberRepositoryV1.findById(Long.valueOf(memberId))
                    .orElseThrow(() -> new UsernameNotFoundException("id에 맞는 해당 회원이 존재하지 않습니다."));

            // 멤버 엔터티 생성
            CustomMemberDto customMemberDto = CustomMemberDto.createCustomMember(Long.valueOf(memberId), member, role, true);

            // 멤버 엔터티를 -> CustomUserDetails 로 변환
            CustomUserDetails customUserDetails = new CustomUserDetails(customMemberDto);

            // 인증 토큰 생성 및 설정
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 필터 체인으로 요청과 응답을 전달
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 토큰 만료시 예외 처리
            unauthorizedResponse(response, "access token expired");
        }
    }

    private void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        // response body
        PrintWriter writer = response.getWriter();
        writer.print(message);

        // response status code
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

}

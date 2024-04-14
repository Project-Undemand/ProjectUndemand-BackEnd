package PU.pushop.global.authentication.jwts.controller;


import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.model.RefreshDto;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReissueRefreshController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepositoryV1 memberRepositoryV1;

    private Long accessTokenExpirationPeriod = 3600L; // 1일

    private Long refreshTokenExpirationPeriod = 1209600L; // 14일

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws BadRequestException, ClassNotFoundException {
        // 쿠키로부터 RefreshToken Value 값을 가져옵니다.
        String savedRefreshInCookie = getRefreshCookieValue(request);
        if (savedRefreshInCookie == null || savedRefreshInCookie.isEmpty()) {
            throw new BadRequestException("Refresh token is missing or empty");
        }

        String BeforeRefresh = savedRefreshInCookie;

        try {
            jwtUtil.isExpired(BeforeRefresh);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("refresh token expired");
        }

        String category = jwtUtil.getCategory(BeforeRefresh);
        if (!category.equals("refresh")) {
            return ResponseEntity.badRequest().body("invalid refresh token. token의 category가 refresh가 아닙니다.");
        }

        if (!refreshRepository.existsByRefreshToken(BeforeRefresh)) {
            return ResponseEntity.badRequest().body("invalid refresh token. not exist refresh token");
        }

        String memberId = jwtUtil.getMemberId(BeforeRefresh);
        MemberRole role = jwtUtil.getRole(BeforeRefresh);

        String newAccess = jwtUtil.createAccessToken("access", memberId, role.toString());
        String newRefresh = jwtUtil.createRefreshToken("refresh", memberId, role.toString());
        // DB에 저장되어 있던, 예전 리프레쉬토큰을 삭제
        refreshRepository.deleteByRefreshToken(BeforeRefresh);
        Member findMember = memberRepositoryV1.findById(Long.valueOf(memberId)).orElseThrow(() -> new EntityNotFoundException("토큰 memberId에 해당하는 회원이 존재하지 않습니다."));
        // 새롭게 생성한 리프레쉬 토큰을 DB에 저장
        saveRefreshEntity(findMember, newRefresh);

        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("RefreshToken", newRefresh));

        return ResponseEntity.ok().build();
    }

    private String getRefreshCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("RefreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void saveRefreshEntity(Member member, String refresh) throws ClassNotFoundException {
        // 현재 시간에 refreshTokenExpirationPeriod을 더한 후 LocalDateTime으로 변환
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(refreshTokenExpirationPeriod);

        Refresh refreshEntity = refreshRepository.findById(member.getId())
                .orElseThrow(() -> new ClassNotFoundException("해당 Refresh가 존재하지 않습니다."));
        // Dto 를 통해서, 새롭게 생성한 RefreshToken 값, 유효기간 등을 받아줍니다.
        RefreshDto refreshDto = RefreshDto.createRefreshDto(refresh, expirationDateTime);
        // Dto 정보들로 기존에 있던 Refresh 엔티티를 업데이트합니다.
        refreshEntity.updateRefreshToken(refreshDto);
        // 저장합니다.
        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60 ); // 1일
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS에서만 쿠키 전송
        cookie.setPath("/"); // 필요에 따라 설정
        return cookie;
    }
}

package PU.pushop.global.authentication.jwt.controller;


import PU.pushop.global.authentication.jwt.util.JWTUtil;
import PU.pushop.members.entity.RefreshEntity;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReissueRefreshController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    private Long accessTokenExpirationPeriod = 3600000L;

    private Long refreshTokenExpirationPeriod = 1209600000L;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        String refreshCookieValue = getRefreshCookieValue(request);
        if (refreshCookieValue == null || refreshCookieValue.isEmpty()) {
            throw new BadRequestException("Refresh token is missing or empty");
        }

        String refresh = refreshCookieValue;

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("refresh token expired");
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return ResponseEntity.badRequest().body("invalid refresh token");
        }

        if (!refreshRepository.existsByRefresh(refresh)) {
            return ResponseEntity.badRequest().body("invalid refresh token");
        }

        String username = jwtUtil.getUsername(refresh);
        MemberRole role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createAccessToken("access", username, role.toString());
        String newRefresh = jwtUtil.createRefreshToken("refresh", username, role.toString());

        refreshRepository.deleteByRefresh(refresh);
        saveRefreshEntity(username, newRefresh);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return ResponseEntity.ok().build();
    }

    private String getRefreshCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
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
        cookie.setPath("/"); // 필요에 따라 설정
        return cookie;
    }
}

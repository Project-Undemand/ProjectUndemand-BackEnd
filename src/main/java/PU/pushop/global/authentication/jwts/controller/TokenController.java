package PU.pushop.global.authentication.jwts.controller;


import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.model.RefreshDto;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import com.google.gson.JsonObject;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private static final Logger log = LoggerFactory.getLogger(TokenController.class);
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepositoryV1 memberRepositoryV1;
    /**
     * long accessTokenExpirationPeriod = 60L * 10;
     * long refreshTokenExpirationPeriod = 3600L * 24;
     */
    private Long accessTokenExpirationPeriod = 60L * 10; // 10 분
    private Long refreshTokenExpirationPeriod = 3600L * 24; // 24 시간

    @PostMapping("/api/v1/reissue/access")
    public @ResponseBody void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = fetchTokenFromAuthorizationHeader(request);

        String memberId = jwtUtil.getMemberId(accessToken);
        MemberRole role = jwtUtil.getRole(accessToken);

        Optional<Refresh> optionalRefresh = refreshRepository.findByMemberId(Long.valueOf(memberId));

        if (optionalRefresh.isPresent()) {
            handleRefreshExists(response, memberId, role, optionalRefresh.get());
        }
    }

    @PostMapping("/api/v1/reissue/refresh")
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

        setResponseData(response, BeforeRefresh);

        return ResponseEntity.ok().build();
    }

    private void handleRefreshExists(HttpServletResponse response, String memberId, MemberRole role, Refresh refresh) throws IOException {
        LocalDateTime expiration = refresh.getExpiration();

        if (expiration.isAfter(LocalDateTime.now())) {
            createAndSendNewAccessToken(response, memberId, role);
        } else {
            sendLoginRequiredResponse(response, memberId);
        }
    }

    private void createAndSendNewAccessToken(HttpServletResponse response, String memberId, MemberRole role) throws IOException {
        String newAccessToken = jwtUtil.createAccessToken("access", memberId, role.toString());

        sendJsonResponseWithAccessToken(response, newAccessToken);
        log.info("New access token created. memberId : {}", memberId);
        response.setStatus(HttpStatus.OK.value());
    }

    private void sendLoginRequiredResponse(HttpServletResponse response, String memberId) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"Please Login. Refresh expired!.\"}");
        log.info("Refresh expired!. memberId : {}", memberId);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    private void setResponseData(HttpServletResponse response, String BeforeRefresh) throws ClassNotFoundException {
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
        response.addCookie(createCookie("refreshToken", newRefresh));
    }

    private String getRefreshCookieValueV1(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getRefreshCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
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


    private String fetchTokenFromAuthorizationHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Subtract 'Bearer ' part of token
        return bearerToken.substring(7);
    }

    private void sendJsonResponseWithAccessToken(HttpServletResponse response, String newAccessToken) throws IOException {
        // 액세스 토큰을 JsonObject 형식으로 응답 데이터에 포함하여 클라이언트에게 반환
        JsonObject responseData = new JsonObject();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        responseData.addProperty("accessToken", newAccessToken);
        response.getWriter().write(responseData.toString());
    }

    private void addResponseData(HttpServletResponse response, String accessToken, String refreshToken, String email) throws IOException {
        // 액세스 토큰을 JsonObject 형식으로 응답 데이터에 포함하여 클라이언트에게 반환
        com.google.gson.JsonObject responseData = new JsonObject();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // response.data 에 accessToken, refreshToken 두값 설정
        responseData.addProperty("accessToken", accessToken);
        responseData.addProperty("refreshToken", refreshToken);
        responseData.addProperty("email", email);
        response.getWriter().write(responseData.toString());
        // HttpStatus 200 OK
        response.setStatus(HttpStatus.OK.value());
    }
}

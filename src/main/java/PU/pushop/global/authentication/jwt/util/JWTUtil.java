package PU.pushop.global.authentication.jwt.util;

import PU.pushop.members.entity.enums.MemberRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    private SecretKey secretKey;

    private Long accessTokenExpirationPeriod = 3600L; // 1일

    private Long refreshTokenExpirationPeriod = 1209600L; // 14일

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8)
                , Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }


    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public MemberRole getRole(String token) {
        Claims claims = parseToken(token);
        return MemberRole.valueOf(claims.get("role", String.class));
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createAccessToken(String category, String username, String role) {
        // 현재 시간에 refreshTokenExpirationPeriod 을 더한 후 LocalDateTime 으로 변환
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(accessTokenExpirationPeriod);
        // LocalDateTime을 Date로 변환
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String category, String username, String role) {
        // 현재 시간에 refreshTokenExpirationPeriod 을 더한 후 LocalDateTime 으로 변환
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(refreshTokenExpirationPeriod);
        // LocalDateTime을 Date로 변환
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());


        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);

    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            // Handle parsing errors here
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    /**
     * 토큰 유효성 체크
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            e.printStackTrace();
        }

        return false;
    }
}

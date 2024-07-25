package PU.pushop.global.authentication.jwts.exception;


import PU.pushop.global.authentication.jwts.filters.LoginFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("인증 실패 엔트리 포인트 호출됨. 예외: {}", authException.getMessage());
        log.info("실패 예외 클래스: {}", authException.getClass().getName());

        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("timestamp", System.currentTimeMillis());
        responseData.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseData.put("error", "Unauthorized");
        responseData.put("message", authException.getMessage());
        responseData.put("path", request.getRequestURI());

        if (authException instanceof LoginFilter.EmailNotFoundException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("status", HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "No user found with this email");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
}

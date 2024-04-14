package PU.pushop.global.config;

import PU.pushop.global.authentication.jwts.filters.*;
import PU.pushop.global.authentication.jwts.login.CustomUserDetailsService;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.global.authentication.oauth2.handler.CustomLoginFailureHandler;
import PU.pushop.global.authentication.oauth2.custom.service.CustomOAuth2UserService;
import PU.pushop.global.authentication.oauth2.handler.CustomLoginSuccessHandlerV2;
import PU.pushop.global.authentication.oauth2.handler.CustomLoginSuccessHandlerV3;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final RefreshRepository refreshRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLoginSuccessHandlerV3 customLoginSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;

    @Bean
    @Primary
    public AuthenticationConfiguration authenticationConfiguration() {
        return new AuthenticationConfiguration();
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService(memberRepositoryV1);
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return customLoginSuccessHandler;
    }
    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return customLoginFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() throws Exception {
        CustomJsonUsernamePasswordAuthenticationFilter filter = new CustomJsonUsernamePasswordAuthenticationFilter(authenticationManager(authenticationConfiguration()), objectMapper);
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        // csrf disable
        http
                .csrf((auth) -> auth.disable());

        // From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                // 메인 페이지, 로그인, 회원가입 페이지에 대한 권한: ALL
                .requestMatchers("/login", "/api/v1/logout",  "/", "/join", "/auth/**").permitAll()
                // 상품 카테고리, 상품
                .requestMatchers("/api/v1/categorys/**", "/api/v1/thumbnail/**", "/api/v1/members/**").permitAll()
//                .requestMatchers(antMatcher(
//                        HttpMethod.GET, "/api/v1/products/**")).permitAll()
//                .requestMatchers(antMatcher(
//                        HttpMethod.POST, "/api/v1/products/**")).hasRole("ADMIN, SELLER")
//                .requestMatchers(antMatcher(
//                        HttpMethod.PUT, "/api/v1/products/**")).hasRole("ADMIN, SELLER")
//                .requestMatchers(antMatcher(
//                        HttpMethod.DELETE, "/api/v1/products/**")).hasRole("ADMIN, SELLER")
                // 상품 썸네일 이미지
                .requestMatchers("/api/v1/thumbnail/**").permitAll()
                .requestMatchers(antMatcher(
                        HttpMethod.POST, "/api/v1/thumbnail/**")).hasRole("ADMIN, SELLER")
                .requestMatchers(antMatcher(
                        HttpMethod.PUT, "/api/v1/thumbnail/**")).hasRole("ADMIN, SELLER")
                .requestMatchers(antMatcher(
                        HttpMethod.DELETE, "/api/v1/thumbnail/**")).hasRole("ADMIN, SELLER")
                // 관리자 페이지 권한: 관리자
//                .requestMatchers("/admin", "/api/v1/inventory/**").hasRole("ADMIN")
                // access, refresh token 만료시 재발행: ALL
                .requestMatchers("/reissue").permitAll()
                // 문의
                .requestMatchers("/api/v1/inquiry/**").permitAll()
                // 문의 답변
                .requestMatchers("/api/v1/inquiry/reply/**").hasRole("ADMIN, SELLER")
                // 나머지 페이지 권한: 로그인 멤버
                .anyRequest().permitAll());



        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        http
                .addFilterBefore(new JWTFilterV1(jwtUtil), CustomLogoutFilter.class);

//        http
//                .addFilterBefore(customJsonUsernamePasswordAuthenticationFilter(), JWTFilterV1.class);
        http
                .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration()), objectMapper, jwtUtil, refreshRepository, objectMapper, memberRepositoryV1, passwordEncoder()), JWTFilterV1.class);


        // oauth2 에서 우리가 원하는 customOAuth2UserService 를 등록하는 것. 구글, 네이버. 각각 response 방법이 다르다.
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint(
                                (userInfoEndpointConfig -> userInfoEndpointConfig
                                        .userService(customOAuth2UserService)
                                )
                        )
                        .successHandler(loginSuccessHandler()) // 쿠키에 refresh 토큰을 저장한다.
                        .failureHandler(loginFailureHandler())
                );

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}

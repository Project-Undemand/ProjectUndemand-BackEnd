package PU.pushop.global.config;

import PU.pushop.global.authentication.jwt.login.CustomUserDetailsService;
import PU.pushop.global.authentication.jwt.util.JWTUtil;
import PU.pushop.global.authentication.jwt.login.filters.CustomJsonUsernamePasswordAuthenticationFilter;
import PU.pushop.global.authentication.jwt.login.filters.LoginFilter;
import PU.pushop.global.authentication.jwt.login.handler.LoginSuccessHandler;
import PU.pushop.global.authentication.oauth2.custom.service.CustomOAuth2UserService;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final RefreshRepository refreshRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(MemberRepositoryV1 memberRepositoryV1, JWTUtil jwtUtil, RefreshRepository refreshRepository, ObjectMapper objectMapper, CustomOAuth2UserService customOAuth2UserService) {
        this.memberRepositoryV1 = memberRepositoryV1;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.objectMapper = objectMapper;
        this.customOAuth2UserService = customOAuth2UserService;
    }


    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
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
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtUtil);
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

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                // 메인 페이지, 로그인, 회원가입 페이지에 대한 권한: ALL
                .requestMatchers("/login", "/", "/join").permitAll()
                // 관리자 페이지 권한: 관리자
                .requestMatchers("/admin").hasRole("ADMIN")
                // access, refresh token 만료시 재발행: ALL
                .requestMatchers("/reissue").permitAll()
                // 나머지 페이지 권한: 로그인 멤버
                .anyRequest().authenticated());

        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
//        http.addFilterAt(
//                new LoginFilter(authenticationManager(authenticationConfiguration()), objectMapper, jwtUtil, refreshRepository)
//                , CustomJsonUsernamePasswordAuthenticationFilter.class
//        );
        // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작 addFilterBefore addFilterAfter
        // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
        // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration()), objectMapper, jwtUtil, refreshRepository), CustomJsonUsernamePasswordAuthenticationFilter.class);


        // oauth2 에서 우리가 원하는 customOAuth2UserService 를 등록하는 것. 구글, 네이버. 각각 response 방법이 다르다.
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint(
                                (userInfoEndpointConfig -> userInfoEndpointConfig
                                        .userService(customOAuth2UserService)
                                )
                        )
                        .successHandler(loginSuccessHandler()) // 쿠키에 토큰을 저장한다.
                );

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}

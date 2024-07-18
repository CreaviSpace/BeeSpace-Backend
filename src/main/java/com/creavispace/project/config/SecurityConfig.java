package com.creavispace.project.config;

import com.creavispace.project.domain.auth.jwt.JWTFilter;
import com.creavispace.project.domain.auth.jwt.JWTService;
import com.creavispace.project.domain.auth.jwt.JWTUtil;
import com.creavispace.project.domain.auth.jwt.repository.RefreshTokenRepository;
import com.creavispace.project.domain.auth.oauth2.CustomClientRegistrationRepo;
import com.creavispace.project.domain.auth.oauth2.LoginSuccessHandler;
import com.creavispace.project.domain.auth.oauth2.RefreshTokenDeleteingLogoutHandler;
import com.creavispace.project.domain.auth.oauth2.service.CustomOauth2Service;
import com.creavispace.project.domain.member.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@Component
public class SecurityConfig {
    private final CustomOauth2Service customOauth2Service;
    private final JWTUtil jwtUtil;
    private final JWTService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;

    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        // cors custom
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("https://beespace.vercel.app"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return configuration;
                    }
                }));

        // http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업
        http
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/env").permitAll()
                                .requestMatchers(HttpMethod.POST, "/member/expire").permitAll()
                                .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                                .requestMatchers("/member/read/profile/**", "/member/read/contents/**", "/api/auth/**",
                                        "config/login", "/login/**", "member/login", "/join", "/swagger-ui/**",
                                        "/v3/api-docs/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/community/**", "/hashtag/**", "/project/**",
                                        "/comment/**", "/recruit/**", "/like/count", "/search/**",
                                        "/tackStack/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/member/**", "/review", "/file/**", "/like/**",
                                        "/project/**", "/comment/**", "/recruit/**", "/bookmark/**", "/report/**",
                                        "/community/**", "/feedback/**", "/tackStack/**")
                                .hasAnyRole(Role.MEMBER.name(), Role.ADMIN.name(), Role.INCOMPLETE_MEMBER.name())
                                .anyRequest()
                                .authenticated());

        // logout handler 생성
        RefreshTokenDeleteingLogoutHandler refresh = new RefreshTokenDeleteingLogoutHandler(refreshTokenRepository);

        // logout handler 추가 (POST axios withCredentials:true 옵션설정)
        http
                .logout(logout -> logout.addLogoutHandler(refresh));

        // oauth2Login 설정
        http
                .oauth2Login(login -> login
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                        .userInfoEndpoint(endPoint -> endPoint.userService(customOauth2Service))
                        .successHandler(new LoginSuccessHandler(jwtUtil, jwtService)));

        // 세션 설정 : STATELESS
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWT filter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

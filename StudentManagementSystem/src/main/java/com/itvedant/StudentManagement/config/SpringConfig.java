package com.itvedant.StudentManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            .headers(headers ->
                headers.cacheControl(cache -> {})
            )

            .authorizeHttpRequests(auth -> auth

                // =========================================
                // PUBLIC URLS
                // =========================================
                .requestMatchers(
                    "/login",
                    "/forgot-password",
                    "/reset-password",
                    "/student/signup",
                    "/student/register",
                    "/settings",
                    "/settings/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error",

                    // =========================================
                    // FAVICON — allow all icon formats
                    // =========================================
                    "/favicon.ico",
                    "/favicon-*.png",
                    "/favicon.png",
                    "/apple-touch-icon.png",
                    "/site.webmanifest",
                    "/manifest.json",

                    // Root-level static assets
                    "/*.png",
                    "/*.ico",
                    "/*.webmanifest",
                    "/*.json",
                    "/*.svg"
                )
                .permitAll()

                // =========================================
                // LEADERSHIP BOARD — both ADMIN and STUDENT
                // =========================================
                .requestMatchers(
                    "/leadership",
                    "/admin/leadership"
                )
                .hasAnyRole("ADMIN", "STUDENT")

                // =========================================
                // STUDENT URLS
                // (covers /student/ai and /student/ai/ask too)
                // =========================================
                .requestMatchers("/student/**")
                .hasRole("STUDENT")

                // =========================================
                // ADMIN URLS
                // =========================================
                .requestMatchers(
                    "/students/**",
                    "/course/**",
                    "/enrollments/**",

                    "/tests/create/**",
                    "/tests/save/**",
                    "/tests/edit/**",
                    "/tests/update/**",
                    "/tests/delete/**",
                    "/tests/list",
                    "/tests/results/**",
                    "/tests/analysis",

                    "/fees-details/**",

                    "/dashboard",
                    "/profile/**",
                    "/id-card",
                    "/ai-assistant",
                    "/study/**"
                )
                .hasRole("ADMIN")

                // =========================================
                // EVERYTHING ELSE
                // =========================================
                .anyRequest()
                .authenticated()
            )

            // LOGIN
            .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/login")

                .successHandler((request, response, authentication) -> {

                    boolean admin =
                        authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                "ROLE_ADMIN".equals(
                                    authority.getAuthority()
                                )
                            );

                    if (admin) {
                        response.sendRedirect("/dashboard");
                    } else {
                        response.sendRedirect("/student/dashboard");
                    }
                })

                .failureUrl("/login?error")

                .permitAll()
            )

            // LOGOUT
            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/login?logout")

                .invalidateHttpSession(true)

                .clearAuthentication(true)

                .deleteCookies("JSESSIONID")

                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
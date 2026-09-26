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
                    "/forgot-password",       // ← NEW
                    "/reset-password",        // ← NEW
                    "/student/signup",
                    "/student/register",
                    "/settings",
                    "/settings/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error"
                )
                .permitAll()

                // =========================================
                // STUDENT URLS
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
                    "/leadership",
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
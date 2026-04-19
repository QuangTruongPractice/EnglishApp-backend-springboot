package com.tqt.englishApp.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigs {

        @Autowired
        private IdentityAuthenticationProvider identityAuthenticationProvider;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authenticationProvider(identityAuthenticationProvider)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/loaderio-*.txt")
                                                .permitAll()
                                                .requestMatchers("/api/**").permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/vocabulary/**",
                                                                "/api/videos/**",
                                                                "/api/main-topics/**",
                                                                "/api/sub-topics/**",
                                                                "/api/quiz/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/vocabulary/**")
                                                .hasAuthority("MANAGE_VOCABULARY")
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/videos/**",
                                                                "/api/main-topics/**",
                                                                "/api/sub-topics/**",
                                                                "/api/quiz/**")
                                                .hasAuthority("MANAGE_LESSON")
                                                .requestMatchers(HttpMethod.PUT,
                                                                "/api/vocabulary/**")
                                                .hasAuthority("MANAGE_VOCABULARY")
                                                .requestMatchers(HttpMethod.PUT,
                                                                "/api/videos/**",
                                                                "/api/main-topics/**",
                                                                "/api/sub-topics/**",
                                                                "/api/quiz/**")
                                                .hasAuthority("MANAGE_LESSON")
                                                .requestMatchers(HttpMethod.DELETE,
                                                                "/api/vocabulary/**")
                                                .hasAuthority("MANAGE_VOCABULARY")
                                                .requestMatchers(HttpMethod.DELETE,
                                                                "/api/videos/**",
                                                                "/api/main-topics/**",
                                                                "/api/sub-topics/**",
                                                                "/api/quiz/**")
                                                .hasAuthority("MANAGE_LESSON")
                                                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "STAFF")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/admin", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login")
                                                .permitAll())
                                .exceptionHandling(ex -> ex.accessDeniedPage("/login?denied=true"));
                ;

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                // Cho phép tất cả origins cho development (React Native)
                config.setAllowedOriginPatterns(List.of("*"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                config.setAllowedHeaders(List.of("*")); // Cho phép tất cả headers
                config.setExposedHeaders(List.of("Authorization", "Content-Type"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return source;
        }
}

package com.kirtivispute.physio.security;

import com.kirtivispute.physio.patient.PatientRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import java.time.Clock;
import java.util.Locale;

@Configuration
public class SecurityConfiguration {
    @Bean Clock clinicClock() { return Clock.systemUTC(); }
    @Bean HttpSessionCsrfTokenRepository csrfTokens() { return new HttpSessionCsrfTokenRepository(); }
    @Bean HttpSessionSecurityContextRepository securityContexts() { return new HttpSessionSecurityContextRepository(); }

    @Bean
    UserDetailsService patientDetails(PatientRepository patients) {
        return email -> patients.findByEmail(email.strip().toLowerCase(Locale.ROOT))
                .map(patient -> User.withUsername(patient.getEmail()).password(patient.getPasswordHash()).roles("PATIENT").build())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsService patients, PasswordEncoder passwords) {
        var provider = new DaoAuthenticationProvider(patients);
        provider.setPasswordEncoder(passwords);
        return new ProviderManager(provider);
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, AuthenticationManager authentication,
                                 HttpSessionCsrfTokenRepository tokens,
                                 HttpSessionSecurityContextRepository contexts) throws Exception {
        http.authenticationManager(authentication)
                .authorizeHttpRequests(rules -> rules
                        .requestMatchers("/", "/register", "/login", "/css/**", "/error", "/actuator/health", "/actuator/health/**",
                                "/physiotherapists", "/physiotherapists/*/slots", "/api/physiotherapists", "/api/physiotherapists/*/slots",
                                "/api/auth/register", "/api/auth/login", "/api/auth/csrf").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.csrfTokenRepository(tokens))
                .securityContext(context -> context.securityContextRepository(contexts))
                .sessionManagement(session -> session.sessionFixation(fixation -> fixation.changeSessionId()))
                .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").usernameParameter("email")
                        .defaultSuccessUrl("/physiotherapists", true).failureUrl("/login?error").permitAll())
                .logout(logout -> logout.logoutRequestMatcher(request -> "POST".equals(request.getMethod())
                                && ("/logout".equals(request.getServletPath()) || "/api/auth/logout".equals(request.getServletPath())))
                        .logoutSuccessHandler((request, response, auth) -> {
                            if (request.getServletPath().startsWith("/api/")) response.setStatus(204);
                            else response.sendRedirect("/login?logout");
                        }).deleteCookies("JSESSIONID").permitAll())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, failure) -> {
                            if (request.getServletPath().startsWith("/api/")) {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"SIGN_IN_REQUIRED\",\"message\":\"Please sign in to manage appointments.\"}");
                            } else response.sendRedirect("/login");
                        })
                        .accessDeniedHandler((request, response, failure) -> {
                            if (request.getServletPath().startsWith("/api/")) {
                                response.setStatus(403);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"REQUEST_FORBIDDEN\",\"message\":\"Refresh the page or CSRF token and try again.\"}");
                            } else response.sendError(403, "Refresh the page and try again.");
                        }));
        return http.build();
    }
}

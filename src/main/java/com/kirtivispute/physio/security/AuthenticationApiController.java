package com.kirtivispute.physio.security;

import com.kirtivispute.physio.patient.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationApiController {
    private final AuthenticationManager authentication;
    private final HttpSessionSecurityContextRepository contexts;
    private final HttpSessionCsrfTokenRepository tokens;
    private final PatientRepository patients;

    public AuthenticationApiController(AuthenticationManager authentication, HttpSessionSecurityContextRepository contexts,
                                       HttpSessionCsrfTokenRepository tokens, PatientRepository patients) {
        this.authentication = authentication;
        this.contexts = contexts;
        this.tokens = tokens;
        this.patients = patients;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrf) {
        return Map.of("token", csrf.getToken(), "headerName", csrf.getHeaderName(), "parameterName", csrf.getParameterName());
    }

    @PostMapping("/login")
    public PatientResponse login(@Valid @RequestBody LoginRequest login, HttpServletRequest request, HttpServletResponse response) {
        var result = authentication.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(login.email(), login.password()));
        new ChangeSessionIdAuthenticationStrategy().onAuthentication(result, request, response);
        new CsrfAuthenticationStrategy(tokens).onAuthentication(result, request, response);
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(result);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
        return PatientResponse.from(patients.findByEmail(result.getName()).orElseThrow());
    }
}

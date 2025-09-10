// AuthController.java
package edu.unam.springsecurity.controller;

import edu.unam.springsecurity.model.JwtResponse;
import edu.unam.springsecurity.model.LoginRequest;
import edu.unam.springsecurity.security.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.expirationDateInMs}")
    private long jwtExpirationSeconds;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena())
            );

            String token = jwtTokenUtil.generateToken(authentication);
            ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(jwtExpirationSeconds)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                     @CookieValue(name = "ACCESS_TOKEN", required = false) String cookieToken,
                                     HttpServletResponse response) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (cookieToken != null && !cookieToken.isBlank()) {
            token = cookieToken;
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta token (Bearer o Cookie)");
        }
        if (!jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        // Reutilizamos el username, pero para un refresh real conviene validar revocación/blacklist
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, null);
        String newToken = jwtTokenUtil.generateToken(auth);
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", newToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtExpirationSeconds)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(new JwtResponse(newToken));
    }

    @GetMapping("/admin/secure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Acceso permitido al endpoint protegido.");
    }

    @GetMapping("/api/privado")
    public ResponseEntity<String> soloConJwt() {
        return ResponseEntity.ok("¡Acceso con JWT exitoso!");
    }
}

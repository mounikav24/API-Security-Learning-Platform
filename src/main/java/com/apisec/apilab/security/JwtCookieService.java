package com.apisec.apilab.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieService {

    private final String cookieName;
    private final long expirationMs;

    public JwtCookieService(
            @Value("${jwt.cookie-name}") String cookieName,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.cookieName = cookieName;
        this.expirationMs = expirationMs;
    }

    public void write(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(expirationMs / 1000)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clear(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        Cookie fallback = new Cookie(cookieName, "");
        fallback.setPath("/");
        fallback.setMaxAge(0);
        response.addCookie(fallback);
    }
}

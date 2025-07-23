package com.meokplaylist.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.meokplaylist.domain.service.JwtTokenService;  // 토큰 검증 서비스

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; // 요청당 1회 실행되는 Security 필터

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);   // "Bearer " 이후 부분만
        }
        return null;                      // 토큰 없음
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractToken(request);

            // 토큰이 있으면 무결성, 만료 검증
            if (token != null) {
                DecodedJWT jwt = jwtTokenService.verify(token);
                Long userId = Long.parseLong(jwt.getSubject());
                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                userId, //유저 고유 id
                                null,
                                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                // SecurityContext에 저장 → 이후 @AuthenticationPrincipal 등에서 사용 가능
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception ex) {
            // 토큰 위·변조, 만료, 형식 오류 → 401 응답 후 중단
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
            return;
        }

        // 토큰 없거나(게스트), 검증 성공 시 나머지 필터·컨트롤러로 진행
        filterChain.doFilter(request, response);
    }
}

package org.classnation.dashboardservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.client.UserServiceClient;
import org.classnation.dashboardservice.client.dto.VerifyTokenResponse;
import org.classnation.dashboardservice.exception.ForbiddenException;
import org.classnation.dashboardservice.exception.UnauthorizedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuthenticationFilter extends OncePerRequestFilter {

    private final UserServiceClient userServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip authentication for actuator endpoints
        if (path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Require authentication for all /api/dashboard/** endpoints
        if (path.startsWith("/api/dashboard")) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            try {
                // Verify token with user-service
                VerifyTokenResponse verifyResponse = userServiceClient.verifyToken(token);

                if (!verifyResponse.getValid()) {
                    throw new UnauthorizedException("Invalid token");
                }

                // Check if role is ADMIN
                if (!"ADMIN".equals(verifyResponse.getRole())) {
                    throw new ForbiddenException("Admin role required");
                }

                // Set authentication context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        verifyResponse.getUid(),
                        verifyResponse.getSid(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + verifyResponse.getRole()))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Admin authenticated: uid={}, sid={}", verifyResponse.getUid(), verifyResponse.getSid());

            } catch (UnauthorizedException | ForbiddenException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error during token verification", e);
                throw new UnauthorizedException("Token verification failed");
            }
        }

        filterChain.doFilter(request, response);
    }
}

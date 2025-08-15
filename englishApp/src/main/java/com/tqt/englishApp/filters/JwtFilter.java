package com.tqt.englishApp.filters;

import com.tqt.englishApp.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getRequestURI().startsWith(
                String.format("%s/api/secure", httpRequest.getContextPath()))) {

            String header = httpRequest.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing or invalid Authorization header.");
                return;
            } else {
                String token = header.substring(7);
                try {
                    Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
                    if (claims != null) {
                        String username = (String) claims.get("username");
                        String role = (String) claims.get("role");

                        httpRequest.setAttribute("username", username);
                        httpRequest.setAttribute("role", role);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        chain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token không hợp lệ hoặc hết hạn");
            return;
        }

        chain.doFilter(request, response);
    }
}
package com.foodapp.foodapp_backend.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foodapp.foodapp_backend.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // if token is not present sent to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Token extract
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        // ✅ User still NOT authenticated in this request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ✅ Load user from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ✅ Validate token
            if (jwtService.isTokenValid(token, username)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ✅ Manually Auth setting auth
                SecurityContextHolder.getContext().setAuthentication(authToken);  //we are saying this request came from the valid user
            }																		//and passing the userdetails to spring security.
        }

        // ✅ Continue filter chain
        filterChain.doFilter(request, response);
    }
}

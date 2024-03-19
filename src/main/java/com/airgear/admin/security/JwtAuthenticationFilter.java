package com.airgear.admin.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header.string}")
    public String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    public String TOKEN_PREFIX;

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        System.out.println("Header in filter + " + header);
        String username = null;
        String authToken = null;

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            System.out.println("In first if");
            authToken = header.replace(TOKEN_PREFIX, "");

            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
                System.out.println("Username = " + username);
                log.info("User name : {}", username);
            } catch (IllegalArgumentException e) {
                log.error("Error occurred while retrieving Username from Token", e);
            } catch (ExpiredJwtException e) {
                log.warn("The token has expired", e);
            } catch (SignatureException e) {
                log.error("Authentication Failed. Invalid username or password.");
            }
        } else {
            log.warn("Bearer string not found, ignoring the header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Authen = " + SecurityContextHolder.getContext().getAuthentication());
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                System.out.println("In validate = " + jwtTokenUtil.validateToken(authToken, userDetails) );
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                System.out.println("authentication = " + jwtTokenUtil.getAuthenticationToken(authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                log.info("User authenticated: " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        System.out.println("Before chain");
        chain.doFilter(req, res);
    }

}

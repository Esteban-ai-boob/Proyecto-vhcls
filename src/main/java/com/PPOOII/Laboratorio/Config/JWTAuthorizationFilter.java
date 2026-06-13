package com.PPOOII.Laboratorio.Config;

import com.PPOOII.Laboratorio.Config.Model.Constans;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String header = request.getHeader(Constans.HEADER_AUTHORIZACION_KEY);

        if (header == null || !header.startsWith(Constans.TOKEN_BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = header.replace(Constans.TOKEN_BEARER_PREFIX, "");
            if (isJWTValid(token)) {
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Constans.getSigningKey(Constans.SUPER_SECRET_KEY))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                setAuthentication(claims);
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException ex) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(ex.getMessage());
        }
    }

    private boolean isJWTValid(String token) {
        return Objects.nonNull(token) && !token.isBlank();
    }

    private void setAuthentication(Claims claims) {
        String username = claims.getSubject();
        Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        List<String> roles = claims.get("authorities", List.class);
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roles != null) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return authorities;
    }
}

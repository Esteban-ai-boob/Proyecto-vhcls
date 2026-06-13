package com.PPOOII.Laboratorio.Config;

import com.PPOOII.Laboratorio.Config.Model.Constans;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class JWTAuthtenticationConfig {

    public String getJWTToken(String username) {
        List<String> authorities = AuthorityUtils
            .commaSeparatedStringToAuthorityList("ROLE_USER")
            .stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + Constans.TOKEN_EXPIRATION_TIME))
            .signWith(Constans.getSigningKey(Constans.SUPER_SECRET_KEY), SignatureAlgorithm.HS512)
            .compact();
    }
}

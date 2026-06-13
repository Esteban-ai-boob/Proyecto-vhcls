package com.PPOOII.Laboratorio.Config.Model;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public final class Constans {

    public static final String LOGIN_URL = "/authenticate";
    public static final String HEADER_AUTHORIZACION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

    public static final String SUPER_SECRET_KEY =
        "TXlTdXBlclNlY3JldEtleUZvckpXVEhTMTEyQml0c1RoaXNJc0FMb25nQmFzZTY0S2V5Rm9yU3ByaW5nQm9vdDM=";
    public static final long TOKEN_EXPIRATION_TIME = 864_000_000L;

    private Constans() {
    }

    public static byte[] getSigningKeyB64(String secret) {
        return Decoders.BASE64.decode(secret);
    }

    public static SecretKey getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(getSigningKeyB64(secret));
    }
}

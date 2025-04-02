package com.bilibili.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.bilibili.service.exception.ConditionalException;

import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;

public class TokenUtil {
    private static final String ISSUER = "Hao-UNSW";

    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add((Calendar.SECOND), 30);
        return JWT.create()
                .withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public static Long verifyToken(String token) {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.RSA256((RSAPublicKey) RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        } catch (TokenExpiredException e) {
            throw new ConditionalException("555", "Token expired");
        } catch (Exception e) {
            throw new ConditionalException("Illegal token");
        }
        return Long.valueOf(JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .getKeyId());
    }
}

package cn.sinjinsong.common.web.security.token.impl;

import cn.sinjinsong.common.web.exception.security.TokenStateInvalidException;
import cn.sinjinsong.common.web.properties.AuthenticationProperties;
import cn.sinjinsong.common.web.security.domain.TokenCheckResult;
import cn.sinjinsong.common.web.security.token.TokenManager;
import cn.sinjinsong.common.web.security.token.TokenState;
import cn.sinjinsong.eshop.common.cache.RedisCacheManager;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@Component
@Slf4j
public class TokenManagerImpl implements TokenManager {
    @Autowired
    private RedisCacheManager redisCacheManager;
    @Autowired
    private AuthenticationProperties authenticationProperties;

    /**
     * 一个JWT实际上就是一个字符串，它由三部分组成，头部、载荷与签名。
     * iss: 该JWT的签发者，是否使用是可选的；
     * sub: 该JWT所面向的用户，是否使用是可选的；
     * aud: 接收该JWT的一方，是否使用是可选的；
     * exp(expires): 什么时候过期，这里是一个Unix时间戳，是否使用是可选的；
     * iat(issued at): 在什么时候签发的(UNIX时间)，是否使用是可选的；
     * 其他还有：
     * nbf (Not Before)：如果当前时间在nbf里的时间之前，则Token不被接受；一般都会留一些余地，比如几分钟；，是否使用是可选的；
     * <p>
     * JWT还需要一个头部，头部用于描述关于该JWT的最基本的信息，例如其类型以及签名所用的算法等。这也可以被表示成一个JSON对象。
     * {
     * "typ": "JWT",
     * "alg": "HS256"
     * }
     *
     * @param username 用户名
     * @return
     */
    @Override
    public String createToken(String username) {
        //获取加密算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成签名密钥  
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(authenticationProperties.getSecretKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //添加构成JWT的参数  
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .setId(username)
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, signingKey);

        //添加Token过期时间  
        long expireTime = System.currentTimeMillis() + authenticationProperties.getTokenExpireTime() * 1000;
        Date expireDateTime = new Date(expireTime);
        builder.setExpiration(expireDateTime);
        //生成JWT  
        String token = builder.compact();
        //放入缓存
        redisCacheManager.putWithExpireTime(String.valueOf(username.hashCode()), token, authenticationProperties.getTokenExpireTime());
        return token;
    }

    @Override
    public TokenCheckResult checkToken(String token) {
        if (token == null) {
            return new TokenCheckResult.TokenCheckResultBuilder().inValid().exception(new TokenStateInvalidException(TokenState.NOT_FOUND.toString())).build();
        }
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(authenticationProperties.getSecretKey()))
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.info("Token过期 {}",token);
            return new TokenCheckResult.TokenCheckResultBuilder().inValid().exception(new TokenStateInvalidException(TokenState.EXPIRED.toString())).build();
        } catch (Exception e) {
            return new TokenCheckResult.TokenCheckResultBuilder().inValid().exception(new TokenStateInvalidException(TokenState.INVALID.toString())).build();
        }
        String username = claims.getId();
        String cachedToken = redisCacheManager.get(String.valueOf(username.hashCode()), String.class);
        if (cachedToken == null || !cachedToken.equals(token)) {
            return new TokenCheckResult.TokenCheckResultBuilder().inValid().exception(new TokenStateInvalidException(TokenState.INVALID.toString())).build();
        }
        return new TokenCheckResult.TokenCheckResultBuilder().valid().username(username).build();
    }

    @Override
    public void deleteToken(String username) {
        redisCacheManager.delete(String.valueOf(username.hashCode()));
    }


}

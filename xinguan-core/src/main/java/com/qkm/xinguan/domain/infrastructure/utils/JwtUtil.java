package com.qkm.xinguan.domain.infrastructure.utils;

import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/10 16:37
 * @description
 */

@Slf4j
@Component
public class JwtUtil {

    @Value("${token.expireTime}")
    private Long expireTime;

    @Value("${token.secret}")
    private String secret;

    /**
     * 构建 jwt token串
     *
     * @param accountName 账户名
     * @return String
     */
    public String generateToken(String accountName) {

        Date nowDate = new Date();
        Date expireDate = new Date(System.currentTimeMillis() + expireTime * 1000L);
        /*
         * jwt的头部承载两部分信息：
         * 声明类型，这里是jwt
         * 声明加密的算法 通常直接使用 HMAC SHA256
         * {
         *   'typ': 'JWT',
         *   'alg': 'HS256'
         * }
         *
         * playload
         * 载荷就是存放有效信息的地方。这个名字像是特指飞机上承载的货品，这些有效信息包含三个部分
         * 标准中注册的声明
         * 公共的声明
         * 私有的声明
         *
         * signature
         * jwt的第三部分是一个签证信息，这个签证信息由三部分组成：
         * header (base64后的)
         * payload (base64后的)
         * secret 私钥
         */
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(accountName)
                .setIssuedAt(nowDate)   //设置生成 token 的时间
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 获取凭证信息
     *
     * @param token jwt token串
     * @return Claims
     */
    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            log.error("[getClaimByToken]:token 凭证信息有误! {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取过期时间
     *
     * @param token jwt token 串
     * @return Date
     */
    public Date getExpiration(String token) {
        return getClaimByToken(token).getExpiration();
    }

    /**
     * 验证token是否失效
     *
     * @param token token
     * @return true:过期   false:没过期
     */
    public boolean isExpired(String token) {
        try {
            final Date expiration = getExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("[JwtUtils --> isExpired]: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 检验是否为 jwt 格式的字符串
     *
     * 说明: jwt 字符串由三部分组成, 分别用 . 分隔开, 所以认为有两个 . 的字符串就是jwt形式的字符串
     * @param token jwt token串
     * @return boolean
     */
    public boolean isJwtStr(String token){
        return StringUtils.countOccurrencesOf(token, ".") == 2;
    }

    /**
     * 获取 jwt 中的账户名
     *
     * @param token jwt token 串
     * @return String
     */
    public String getAccountName(String token){
        return getClaimByToken(token).getSubject();
    }

    /**
     * 判断 token 是否是当前登录用户
     *
     * @param token token
     * @param loginUser 当前登录用户
     * @return boolean
     */
    public boolean checkIsCurrentLoginUser(String token, LoginUser loginUser) {
        return loginUser.getUsername().equals(getAccountName(token));
    }
}

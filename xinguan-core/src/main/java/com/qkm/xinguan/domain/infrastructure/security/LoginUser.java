package com.qkm.xinguan.domain.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.entity.Menu;
import com.qkm.xinguan.domain.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/04/10 15:43
 * @description
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginUser extends User implements UserDetails {

    private static final long serialVersionUID = -1379274258881257107L;

    private List<String> authorities;

    private List<Menu> menus;

    private String token;

    /** 登陆时间戳（毫秒） */
    private Long loginTime;

    /** 过期时间戳 */
    private Long expireTime;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.parallelStream().filter(auth -> !StringUtils.isEmpty(auth))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return SystemConst.ACTIVE.equals(getForbidden());
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return SystemConst.ACTIVE.equals(getForbidden());
    }
}

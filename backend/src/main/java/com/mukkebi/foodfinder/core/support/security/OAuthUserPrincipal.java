package com.mukkebi.foodfinder.core.support.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class OAuthUserPrincipal implements OAuth2User {

    private final Long userId;
    private final Map<String, Object> attributes;

    public OAuthUserPrincipal(Long userId, Map<String, Object> attributes) {
        this.userId = userId;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");
    }

    @Override
    public String getName() {
        return userId.toString();
    }
}

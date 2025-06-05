package com.reviewyme.userservice.existinsetup.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {

    ROLE_ADMIN, // Represent the Role for Administrators
    ROLE_CLIENT // Represent the Role for Regular Users
    ;

    @Override
    public String getAuthority() {
        return name();
    }
}

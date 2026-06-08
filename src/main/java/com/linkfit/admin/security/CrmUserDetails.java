package com.linkfit.admin.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CrmUserDetails implements UserDetails {

    private final String id;
    private final String branchCode;
    private final String username;
    private final Long gymId;
    private final String role;

    public CrmUserDetails(String id, String branchCode, String username, Long gymId, String role) {
        this.id         = id;
        this.branchCode = branchCode;
        this.username   = username;
        this.gymId      = gymId;
        this.role       = role;
    }

    public String getId()         { return id; }
    public String getBranchCode() { return branchCode; }
    public Long   getGymId()      { return gymId; }
    public String getRole()       { return role; }

    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return ""; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }
}

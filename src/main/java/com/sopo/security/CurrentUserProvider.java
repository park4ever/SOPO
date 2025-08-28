package com.sopo.security;

public interface CurrentUserProvider {

    Long currentUserId();

    boolean hasRole(String role);
}

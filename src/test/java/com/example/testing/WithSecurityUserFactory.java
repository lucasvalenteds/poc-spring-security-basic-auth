package com.example.testing;

import com.example.internal.SecurityUserDetails;
import com.example.user.Authority;
import com.example.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class WithSecurityUserFactory implements WithSecurityContextFactory<WithSecurityUser> {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @Autowired(required = false)
    void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    @Override
    public SecurityContext createSecurityContext(WithSecurityUser annotation) {
        final var user = new User();

        user.setId(annotation.id());
        user.setUsername(annotation.username());
        user.setPassword(annotation.password());
        user.setAuthorities(stringsToAuthority(annotation.authorities()));

        final var userDetails = new SecurityUserDetails(user);
        final var authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities());

        final var context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    private static Authority stringToAuthority(final String string) {
        final var authority = new Authority();
        authority.setAuthority(string);
        return authority;
    }

    private static Set<Authority> stringsToAuthority(final String... strings) {
        return Arrays.stream(strings)
                .map(WithSecurityUserFactory::stringToAuthority)
                .collect(Collectors.toUnmodifiableSet());
    }
}

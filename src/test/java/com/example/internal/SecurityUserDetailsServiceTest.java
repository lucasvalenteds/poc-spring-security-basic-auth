package com.example.internal;

import com.example.testing.AutoConfigurePostgres;
import com.example.testing.UserSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigurePostgres
@Import(UserSecurityConfiguration.class)
class SecurityUserDetailsServiceTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void findingUserByItsUsername() {
        final var userDetails = userDetailsService.loadUserByUsername("john.smith");
        assertThat(userDetails).isInstanceOf(SecurityUserDetails.class);

        final var securityUserDetails = (SecurityUserDetails) userDetails;
        assertThat(securityUserDetails.getId()).isEqualTo(1L);
        assertThat(securityUserDetails.getUsername()).isEqualTo("john.smith");
        assertThat(securityUserDetails.getPassword()).isNotNull();
        assertThat(securityUserDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    void errorFindingUnknownUserByUsername() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("edgar.williams"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Username edgar.williams not found")
                .hasNoCause();
    }
}

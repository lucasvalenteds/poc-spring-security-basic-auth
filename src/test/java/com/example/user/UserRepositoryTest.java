package com.example.user;

import com.example.testing.AutoConfigurePostgres;
import com.example.testing.PasswordEncryptionConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigurePostgres
@Import(PasswordEncryptionConfiguration.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void findingUserByUsername() {
        final var johnSmith = userRepository.findByUsername("john.smith").orElseThrow();
        assertThat(johnSmith.getId()).isEqualTo(1L);
        assertThat(johnSmith.getUsername()).isEqualTo("john.smith");
        assertThat(passwordEncoder.matches("s3cr3t", johnSmith.getPassword())).isTrue();
        assertThat(johnSmith.getAuthorities())
                .extracting(Authority::getAuthority)
                .containsOnly("ADMIN", "USER");

        final var maryJane = userRepository.findByUsername("mary.jane").orElseThrow();
        assertThat(maryJane.getId()).isEqualTo(2L);
        assertThat(maryJane.getUsername()).isEqualTo("mary.jane");
        assertThat(passwordEncoder.matches("p4ssw0rd", maryJane.getPassword())).isTrue();
        assertThat(maryJane.getAuthorities())
                .extracting(Authority::getAuthority)
                .containsOnly("USER");

        final var guest = userRepository.findByUsername("guest").orElseThrow();
        assertThat(guest.getId()).isEqualTo(3L);
        assertThat(guest.getUsername()).isEqualTo("guest");
        assertThat(passwordEncoder.matches("guest", guest.getPassword())).isTrue();
        assertThat(guest.getAuthorities()).isEmpty();
    }
}

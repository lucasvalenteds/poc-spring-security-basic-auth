package com.example.testing;

import com.example.internal.SecurityUserDetailsService;
import com.example.user.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.PlatformTransactionManager;

@TestConfiguration
public class UserSecurityConfiguration {

    @Bean
    PlatformTransactionManager platformTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    UserDetailsService userDetailsService(PlatformTransactionManager platformTransactionManager,
                                          UserRepository userRepository) {
        return new SecurityUserDetailsService(platformTransactionManager, userRepository);
    }
}

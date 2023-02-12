package com.example.internal;

import com.example.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
public final class SecurityUserDetailsService implements UserDetailsService {

    private final PlatformTransactionManager platformTransactionManager;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setReadOnly(true);

        return transactionTemplate.execute(status ->
                userRepository.findByUsername(username)
                        .map(SecurityUserDetails::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Username %s not found".formatted(username))));
    }
}

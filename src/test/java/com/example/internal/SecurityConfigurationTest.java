package com.example.internal;

import com.example.testing.ExampleRestController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(ExampleRestController.class)
@AutoConfigureWebTestClient
@Import(SecurityConfiguration.class)
class SecurityConfigurationTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    public void afterEach() {
        Mockito.verify(userDetailsService, Mockito.never())
                .loadUserByUsername(Mockito.anyString());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void actuatorCanBeAccessedByUsersWithAdminAuthority() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    static Stream<Arguments> actuatorAuthoritiesForbidden() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of("USER")),
                Arguments.of(List.of("GUEST"))
        );
    }

    @ParameterizedTest
    @MethodSource("actuatorAuthoritiesForbidden")
    @Disabled("https://github.com/spring-projects/spring-security/issues/9257")
    void actuatorCannotBeAccessedByUsersWithoutAdminAuthority(List<String> authorities) {
        final var userExchangeMutator = SecurityMockServerConfigurers.mockUser()
                .authorities(authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList());

        webTestClient.mutateWith(userExchangeMutator)
                .get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    static Stream<Arguments> exampleAuthoritiesAuthorized() {
        return Stream.of(
                Arguments.of(),
                Arguments.of(List.of("ADMIN")),
                Arguments.of(List.of("USER")),
                Arguments.of(List.of("ADMIN", "USER"))
        );
    }

    @ParameterizedTest
    @MethodSource("exampleAuthoritiesAuthorized")
    @Disabled("https://github.com/spring-projects/spring-security/issues/9257")
    void exampleCanBeAccessedByAnyAuthenticatedUser(List<String> authorities) {
        final var userExchangeMutator = SecurityMockServerConfigurers.mockUser()
                .authorities(authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList());

        webTestClient.mutateWith(userExchangeMutator)
                .get()
                .uri("/example")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    @Tag("WorkaroundIssue9257")
    @WithMockUser(authorities = "USER")
    void actuatorCannotBeAccessedByUsersWithUserAuthorityButWithoutAdminAuthority() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    @Tag("WorkaroundIssue9257")
    @WithMockUser(authorities = {})
    void actuatorCannotBeAccessedByUsersWithWithoutAdminAuthority() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    @Tag("WorkaroundIssue9257")
    @WithMockUser(authorities = "USER")
    void exampleResourceIsAccessibleByAnyUser() {
        webTestClient.get()
                .uri("/example")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Hello World!");
    }

    @Test
    @Tag("WorkaroundIssue9257")
    @WithMockUser(authorities = "ADMIN")
    void exampleResourceIsAccessibleByAnyAdmin() {
        webTestClient.get()
                .uri("/example")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Hello World!");
    }

    @Test
    @Tag("WorkaroundIssue9257")
    @WithMockUser(authorities = {})
    void exampleResourceIsAccessibleByAnyoneAuthenticated() {
        webTestClient.get()
                .uri("/example")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Hello World!");
    }

    @Test
    void exampleRequiresAuthentication() {
        webTestClient.get()
                .uri("/example")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }
}

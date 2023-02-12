package com.example;

import com.example.testing.AutoConfigurePostgres;
import com.example.testing.StringIsInstantMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Base64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigurePostgres
@AutoConfigureWebTestClient
class ApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void findingJohnSmithProducts() {
        final var credentials = Base64.getEncoder()
                .encodeToString("john.smith:s3cr3t".getBytes());

        webTestClient.get()
                .uri("/products")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.products").value(Matchers.iterableWithSize(3))
                .jsonPath("$.products[0].id").value(Matchers.equalTo(1))
                .jsonPath("$.products[0].name").value(Matchers.equalTo("Notebook"))
                .jsonPath("$.products[1].id").value(Matchers.equalTo(2))
                .jsonPath("$.products[1].name").value(Matchers.equalTo("Pencil"))
                .jsonPath("$.products[2].id").value(Matchers.equalTo(5))
                .jsonPath("$.products[2].name").value(Matchers.equalTo("Eraser"));
    }

    @Test
    void findingMaryJaneProducts() {
        final var credentials = Base64.getEncoder()
                .encodeToString("mary.jane:p4ssw0rd".getBytes());

        webTestClient.get()
                .uri("/products")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.products").value(Matchers.iterableWithSize(2))
                .jsonPath("$.products[0].id").value(Matchers.equalTo(3))
                .jsonPath("$.products[0].name").value(Matchers.equalTo("Pen"))
                .jsonPath("$.products[1].id").value(Matchers.equalTo(4))
                .jsonPath("$.products[1].name").value(Matchers.equalTo("Paper"));
    }

    @Test
    void guestsCannotHaveProducts() {
        final var credentials = Base64.getEncoder()
                .encodeToString("guest:guest".getBytes());

        webTestClient.get()
                .uri("/products")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.timestamp").value(equalToStringConvertibleToInstant())
                .jsonPath("$.status").value(Matchers.equalTo(HttpStatus.FORBIDDEN.value()))
                .jsonPath("$.error").value(Matchers.equalTo("Forbidden"))
                .jsonPath("$.path").value(Matchers.equalTo("/products"));
    }

    @Test
    void adminsCanAccessActuatorHealth() {
        final var credentials = Base64.getEncoder()
                .encodeToString("john.smith:s3cr3t".getBytes());

        webTestClient.get()
                .uri("/actuator/health")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").value(Matchers.equalTo("UP"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mary.jane:p4ssw0rd", "guest:guest"})
    void usersAndGuestsCannotAccessActuatorHealth(String plainTextCredentials) {
        final var credentials = Base64.getEncoder()
                .encodeToString(plainTextCredentials.getBytes());

        webTestClient.get()
                .uri("/actuator/health")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.timestamp").value(equalToStringConvertibleToInstant())
                .jsonPath("$.status").value(Matchers.equalTo(HttpStatus.FORBIDDEN.value()))
                .jsonPath("$.error").value(Matchers.equalTo("Forbidden"))
                .jsonPath("$.path").value(Matchers.equalTo("/actuator/health"));
    }

    private static Matcher<String> equalToStringConvertibleToInstant() {
        return new StringIsInstantMatcher();
    }
}

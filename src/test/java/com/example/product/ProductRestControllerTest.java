package com.example.product;

import com.example.internal.SecurityConfiguration;
import com.example.testing.WithSecurityUser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ProductRestController.class)
@AutoConfigureWebTestClient
@Import(SecurityConfiguration.class)
class ProductRestControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithSecurityUser(username = "julia.adams")
    void findingProductsFromCustomer() {
        mockRepositoryToFindProducts(createProducts("Banana", "Apple", "Pineapple"));

        webTestClient.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.products").value(Matchers.iterableWithSize(3))
                .jsonPath("$.products[*].id").value(Matchers.hasItems(Matchers.isA(Number.class)))
                .jsonPath("$.products[*].name").value(Matchers.hasItems(Matchers.isA(String.class)));
    }

    @SuppressWarnings("SameParameterValue")
    private void mockRepositoryToFindProducts(final List<Product> products) {
        Mockito.when(productRepository.findAllByUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(products));
    }

    private List<Product> createProducts(final String... names) {
        final List<Product> products = new ArrayList<>(names.length);

        var id = 1L;
        for (final var name : names) {
            final var product = new Product();
            product.setId(id);
            product.setName(name);

            products.add(product);
            id++;
        }

        return products;
    }
}

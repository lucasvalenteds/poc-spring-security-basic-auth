package com.example.product;

import com.example.testing.AutoConfigurePostgres;
import com.example.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigurePostgres
class ProductRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findingProductsFromUser() {
        final var user = userRepository.findByUsername("john.smith").orElseThrow();
        final var page = productRepository.findAllByUserId(user.getId(), Pageable.unpaged());

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).element(0).satisfies(product -> {
            assertThat(product.getId()).isEqualTo(1L);
            assertThat(product.getName()).isEqualTo("Notebook");
        });
        assertThat(page.getContent()).element(1).satisfies(product -> {
            assertThat(product.getId()).isEqualTo(2L);
            assertThat(product.getName()).isEqualTo("Pencil");
        });
        assertThat(page.getContent()).element(2).satisfies(product -> {
            assertThat(product.getId()).isEqualTo(5L);
            assertThat(product.getName()).isEqualTo("Eraser");
        });
    }
}
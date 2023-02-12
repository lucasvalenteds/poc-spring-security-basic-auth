package com.example.product;

import com.example.internal.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<ProductsResponse> findAllProducts(Pageable pageable) {
        final var user = (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final var page = productRepository.findAllByUserId(user.getId(), pageable)
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .build());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ProductsResponse.builder()
                        .products(page.getContent())
                        .build());
    }
}

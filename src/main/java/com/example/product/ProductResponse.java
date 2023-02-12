package com.example.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public final class ProductResponse {

    private Long id;
    private String name;
}

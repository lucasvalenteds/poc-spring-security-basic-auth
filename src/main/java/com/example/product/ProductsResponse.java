package com.example.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public final class ProductsResponse {

    private List<ProductResponse> products;
}

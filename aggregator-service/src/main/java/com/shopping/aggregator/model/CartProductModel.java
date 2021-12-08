package com.shopping.aggregator.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartProductModel {
    private String cartId;
    private String productId;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String offer;
    private BigDecimal calculatedPrice;

}
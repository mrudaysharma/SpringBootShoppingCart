package com.shopping.cart.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PUBLIC)
public class CartProductModel {
    private String productId;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String offer;
    private BigDecimal calculatedPrice;

}
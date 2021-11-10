package com.wayfair.cart.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PUBLIC)
@Data
@Builder
public class CartModel {
    private  String cartId;
    private  String userId;
    private Map<String, CartProductModel> cartItems = new ConcurrentHashMap<>();
    private BigDecimal finalCalculatedPrice;
}

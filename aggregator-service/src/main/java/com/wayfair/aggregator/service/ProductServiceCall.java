package com.wayfair.aggregator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wayfair.aggregator.model.CartModel;
import com.wayfair.aggregator.model.CartProductModel;
import com.wayfair.aggregator.model.Product;

public interface ProductServiceCall {
    public Product getProductPrice(String productId);
    public CartModel getCartProductPrice(String cartId);
    public CartProductModel saveCartProductPrice(CartProductModel model) throws JsonProcessingException;
}

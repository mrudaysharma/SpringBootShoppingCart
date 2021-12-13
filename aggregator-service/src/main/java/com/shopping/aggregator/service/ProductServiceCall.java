package com.shopping.aggregator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopping.aggregator.model.CartModel;
import com.shopping.aggregator.model.CartProductModel;
import com.shopping.aggregator.model.Product;

public interface ProductServiceCall {
    public Product getProductPrice(String productId);
    public CartModel getCartProductPrice(String cartId);
    public CartProductModel saveCartProductPrice(CartProductModel model) throws JsonProcessingException;
}

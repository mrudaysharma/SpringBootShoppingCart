package com.wayfair.cart.service;

import com.wayfair.cart.model.CartModel;
import com.wayfair.cart.model.CartProductModel;

public interface CartService {

    public CartModel getCartByCartId(String cartId);

    public CartModel saveItem(CartModel shoppingcart);

    public boolean deleteAllItem(String cartId);

    public boolean deleteSpecificItem(String cartId, String productId);

    public CartModel deleteItemQuantity(String cartId, String productId, int quantity);

    public CartModel calculateFinalAmount(CartModel cartModel);

    public CartProductModel updateProductPrice(String cartId,CartProductModel cartProductModel);
}

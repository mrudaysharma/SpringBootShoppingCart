package com.wayfair.cart.repository;

import com.wayfair.cart.model.CartModel;
import com.wayfair.cart.model.CartProductModel;

import java.math.BigDecimal;

public interface CartRepository {
    public boolean isUserShoppingCartExist(String cartId);

    public CartModel getCartByCartId(String userId);

    public CartModel saveOrUpdate(CartModel shoppingcart);

    public BigDecimal calculatePriceWithOffers(CartProductModel shoppingcart);

   public boolean deleteAllItemFromCart(String cartId);

    public boolean deleteShoppingcartItem(String cartId, String productId);

    public CartModel deleteItemQunatity(String userId, String productId, int quantity);

    public CartModel finalAmountCalculator(CartModel cartModel);

   public CartProductModel updateProductPrice(String cartId,CartProductModel cartProductModel);
}

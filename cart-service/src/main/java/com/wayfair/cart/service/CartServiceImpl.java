package com.wayfair.cart.service;

import com.wayfair.cart.datastore.CartMemoryStorage;
import com.wayfair.cart.model.CartModel;
import com.wayfair.cart.model.CartProductModel;
import com.wayfair.cart.repository.CartRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
    CartMemoryStorage shoppingcartStore = CartMemoryStorage.getInstance();

    public CartServiceImpl() {
    }

    @Autowired
    CartRepositoryImpl cartRepository;

    @Override
    public CartModel getCartByCartId(String cartId){
        return cartRepository.getCartByCartId(cartId);
    }

    @Override
    public CartModel saveItem(CartModel cart) {
           CartModel storedCartModel= cartRepository.saveOrUpdate(cart);
           return storedCartModel;
    }

    @Override
    public boolean deleteAllItem(String cartId) {
        return cartRepository.isUserShoppingCartExist(cartId)? cartRepository.deleteAllItemFromCart(cartId): false;
    }

    @Override
    public boolean deleteSpecificItem(String cartId, String productId) {
        return cartRepository.deleteShoppingcartItem(cartId,productId);
    }

    @Override
    public CartModel deleteItemQuantity(String userId, String productId, int quantity) {
        return cartRepository.deleteItemQunatity(userId,productId,quantity);
    }

    @Override
    public CartModel calculateFinalAmount(CartModel cartModel) {
        return cartRepository.finalAmountCalculator(cartModel);
    }

    @Override
    public CartProductModel updateProductPrice(String cartId,CartProductModel cartProductModel) {
        return cartRepository.updateProductPrice(cartId,cartProductModel);
    }


}

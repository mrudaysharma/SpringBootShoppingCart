package com.shopping.cart.datastore;


import com.shopping.cart.model.CartModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum CartMemoryStorage {
    INSTANCE;

    private final ConcurrentHashMap<String, CartModel> map = new ConcurrentHashMap<>();

    public void insert(String cartId, CartModel cart) {
        map.put(cartId,cart);
    }
    public void update(String cartId, CartModel cart) {
        if(map.containsKey(cartId))
        {
            map.replace(cartId,cart);
        }
    }
    public void cleanAll(String cartId) {

        map.get(cartId).getCartItems().clear();
    }
    public boolean isEmpty(String cartId) {
       return map.get(cartId).getCartItems().isEmpty();
    }
    public CartModel getByUserId(String userId) {
       return map.get(userId);
    }
    public CartModel getCartByUserId(String userId){
        return map.get(userId);
    }
    public CartModel getByCartId(String cartId) {
        return map.get(cartId);
    }
    public boolean containsKey(String cartId) {
        return map.containsKey(cartId);
    }

}

package com.wayfair.aggregator.datastorage;

import com.wayfair.aggregator.model.CartProductModel;

public class AggregateStorage {
    private static CartProductModel cartProductModel=null ;

    private static AggregateStorage instance = null;

    private AggregateStorage() {}

    public static AggregateStorage getInstance() {
        if (instance == null) {
            instance = new AggregateStorage();
            cartProductModel=new CartProductModel();
        }
        return instance;
    }
    public static CartProductModel getModelInstance() {
        if (instance == null) {
            cartProductModel=new CartProductModel();
        }
        return cartProductModel;
    }

    public void storeCartProduct(CartProductModel model)
    {
        this.cartProductModel=model;
    }

    public CartProductModel get()
    {
        return cartProductModel;
    }

}

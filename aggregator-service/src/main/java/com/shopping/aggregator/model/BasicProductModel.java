package com.shopping.aggregator.model;

import java.math.BigDecimal;

public class BasicProductModel {
    public static String CARTID;
    public static String PRODUCTID;
    public static BigDecimal PRICE;
    public static BigDecimal CALCULATEDPRICE;
    public static int QUANTITY;

    public static void populateField(CartProductModel savedCartModel) {
        PRICE=savedCartModel.getPrice();
        CALCULATEDPRICE=savedCartModel.getCalculatedPrice();
        QUANTITY=savedCartModel.getQuantity();
    }
}

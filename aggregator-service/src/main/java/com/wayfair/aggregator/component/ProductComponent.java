package com.wayfair.aggregator.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wayfair.aggregator.datastorage.AggregateStorage;
import com.wayfair.aggregator.model.CartModel;
import com.wayfair.aggregator.model.CartProductModel;
import com.wayfair.aggregator.model.Product;
import com.wayfair.aggregator.service.ProductServiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@EnableScheduling
public class ProductComponent {

    @Autowired
    private ProductServiceCall informationClient;

    @Scheduled(fixedRate = 500000)
    public void scheduleUpdateCartProductModel()
    {
        AggregateStorage aggregateStorage = AggregateStorage.getInstance();
        Optional<Product> product = Optional.ofNullable(informationClient.getProductPrice(aggregateStorage.get().getProductId()));
        Optional<CartModel> cartModel = Optional.ofNullable(informationClient.getCartProductPrice(aggregateStorage.get().getCartId()));
        CartProductModel savedCartModel = null;
        try {
            if (cartModel.isPresent() && product.isPresent()) {
                CartProductModel cartProductModel = cartModel.get().getCartItems().get(aggregateStorage.get().getProductId());
                if (cartProductModel.getPrice().compareTo(product.get().getPrice())!=0) {
                    cartProductModel.setPrice(product.get().getPrice());
                    savedCartModel= informationClient.saveCartProductPrice(cartProductModel);
                    savedCartModel.setCartId(cartModel.get().getCartId());
                    aggregateStorage.storeCartProduct(savedCartModel);
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}

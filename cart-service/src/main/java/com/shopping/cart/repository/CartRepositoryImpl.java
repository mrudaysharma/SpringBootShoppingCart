package com.shopping.cart.repository;

import com.shopping.cart.datastore.CartMemoryStorage;
import com.shopping.cart.model.CartModel;
import com.shopping.cart.model.CartProductModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Repository
public class CartRepositoryImpl implements CartRepository {
    private static final Logger LOGGER = LogManager.getLogger(CartRepositoryImpl.class);
    CartMemoryStorage shoppingcartStore = CartMemoryStorage.INSTANCE;

    public CartRepositoryImpl() {
    }

    @Override
    public boolean isUserShoppingCartExist(String cartId) {
        return shoppingcartStore.containsKey(cartId);
    }

    @Override
    public CartModel getCartByCartId(String cartId) {
        if (isUserShoppingCartExist(cartId)) {
            return shoppingcartStore.getByCartId(cartId);
        }
        return null;
    }

    @Override
    public CartModel saveOrUpdate(CartModel cart) {
        CartModel savedCartModel = null;
        if (isUserShoppingCartExist(cart.getCartId())) {
            savedCartModel = getCartByCartId(cart.getCartId());
            for (String productId : cart.getCartItems().keySet()) {
                CartProductModel shoppingcart = findShoppingcartItem(cart.getCartId(), productId);
                if (shoppingcart == null) {
                    cartExistButNewItemArrive(cart, savedCartModel, productId);
                } else {
                    cartItemUpdateExisting(cart, savedCartModel, productId, shoppingcart);
                }
            }
        } else {
            createNewCartStoreItem(cart);
        }
        return shoppingcartStore.getByCartId(cart.getCartId());

    }


    private void createNewCartStoreItem(CartModel cart) {

        cart.getCartItems().values().forEach(cartProductModel -> {
            BigDecimal calculatedValue = calculatePriceWithOffers(cartProductModel);
            cartProductModel.setCalculatedPrice(calculatedValue);
            cart.getCartItems().put(cartProductModel.getProductId(), cartProductModel);
        });
        shoppingcartStore.insert(cart.getCartId(), cart);
    }

    private void cartItemUpdateExisting(CartModel cart, CartModel cartModel, String productId, CartProductModel shoppingcart) {
        int newQuantity = cart.getCartItems().get(productId).getQuantity();
        int productQuantityInShoppingcart = shoppingcart.getQuantity() + newQuantity;
        shoppingcart=cart.getCartItems().get(productId);
        shoppingcart.setQuantity(productQuantityInShoppingcart);
        shoppingcart.setCalculatedPrice(calculatePriceWithOffers(shoppingcart));
        cartModel.getCartItems().put(productId, shoppingcart);
        shoppingcartStore.update(cart.getCartId(), cartModel);
    }

    private void cartExistButNewItemArrive(CartModel cart, CartModel cartModel, String productId) {
        CartProductModel shoppingcart = cart.getCartItems().get(productId);
        BigDecimal calculatedValue = calculatePriceWithOffers(shoppingcart);
        shoppingcart.setCalculatedPrice(calculatedValue);
        cartModel.getCartItems().put(productId, shoppingcart);
        shoppingcartStore.insert(cart.getCartId(), cartModel);
    }

    /*
       Logic to calculate offer based price:
       Price for 1 Item: 10
       Total Quantity: 5
       Offer : 3 for 2
       Offer Quantity: 3 and groupPriceFor : 2
       Equation:
       DevidedQuantity= Quantity/Offer Quantity  (5/3 = 1)
            DevidedQuantity * Price for 1 Item * Group Price (1 * 10 * 2 = 20)
       ReminderQuantity = Quantity/Offer Quantity (5%3 = 2)
            ReminderQuantity * Price for 1 Item  (2 * 10 = 20)
       Total = DevidedQuantity + ReminderQuantity (20+20=40)
     */

    @Override
    public BigDecimal calculatePriceWithOffers(CartProductModel shoppingcart) {
        LOGGER.info("CALCULATE PRICE ACCORDING TO QUANTITY " + shoppingcart.getQuantity() + " OFFER" + shoppingcart.getOffer());
        String offer = shoppingcart.getOffer();
        BigDecimal totalQuantity = new BigDecimal(shoppingcart.getQuantity());
        BigDecimal priceOfOneItem = shoppingcart.getPrice();
        if (!offer.isBlank() || !offer.isEmpty()) {
            List<String> offers = Arrays.asList(offer.split("for"));
            BigDecimal totalOfferQuantity = new BigDecimal(offers.get(0));
            BigDecimal totalOfferPrice = new BigDecimal(offers.get(1));
            if (totalOfferQuantity.doubleValue() <= totalQuantity.doubleValue()) {
                BigDecimal devidedValue = totalQuantity.divide(totalOfferQuantity, RoundingMode.DOWN);
                BigDecimal calculationPartOne = (devidedValue).multiply((shoppingcart.getPrice()).multiply(totalOfferPrice));
                BigDecimal calculationPartTwo = (totalQuantity.remainder(totalOfferQuantity)).multiply(shoppingcart.getPrice());
                return calculationPartOne.add(calculationPartTwo);
            }
        }
        BigDecimal calculatePrice = totalQuantity.multiply(priceOfOneItem);
        LOGGER.info("CALCULATED PRICE IS " + calculatePrice);
        return calculatePrice;
    }

    @Override
    public boolean deleteAllItemFromCart(String cartId) {
        shoppingcartStore.cleanAll(cartId);
        return shoppingcartStore.isEmpty(cartId);
    }

    @Override
    public boolean deleteShoppingcartItem(String cartId, String productId) {
        if (isProductExistInCart(cartId, productId)) {
            shoppingcartStore.getByCartId(cartId).getCartItems().remove(productId);
            return true;
        }
        return false;
    }

    @Override
    public CartModel deleteItemQunatity(String cartId, String productId, int quantity) {
        CartModel cartModel = null;
        if (isProductExistInCart(cartId, productId)) {
            cartModel = shoppingcartStore.getByCartId(cartId);
            CartProductModel model = cartModel.getCartItems().get(productId);
            int savedQuantity = model.getQuantity();
            if (quantity < savedQuantity) {
                savedQuantity = savedQuantity - quantity;
                model.setQuantity(savedQuantity);
                model.setCalculatedPrice(calculatePriceWithOffers(model));
                cartModel.getCartItems().replace(productId, model);
                shoppingcartStore.update(cartId,cartModel);
            }
            else if (quantity == savedQuantity) {
                deleteShoppingcartItem(cartId, productId);
            }
        }

        return cartModel;
    }

    @Override
    public CartModel finalAmountCalculator(CartModel model) {
        BigDecimal finalPriceCalculator = new BigDecimal(0);
        if (model.getCartItems() != null || !model.getCartItems().isEmpty()) {
            for (CartProductModel productModel : model.getCartItems().values()) {
                finalPriceCalculator = finalPriceCalculator.add(productModel.getCalculatedPrice());
            }
            model.setFinalCalculatedPrice(finalPriceCalculator);
            shoppingcartStore.update(model.getCartId(),model);
            return getCartByCartId(model.getCartId());
        }
        return null;
    }

    @Override
    public CartProductModel updateProductPrice(String cartId,CartProductModel cartProductModel) {
        if(isProductExistInCart(cartId,cartProductModel.getProductId()))
        {
            cartProductModel.setCalculatedPrice(calculatePriceWithOffers(cartProductModel));
            shoppingcartStore.getByCartId(cartId).getCartItems().replace(cartProductModel.getProductId(),cartProductModel);
        }
        return findShoppingcartItem(cartId,cartProductModel.getProductId());
    }

    public CartProductModel findShoppingcartItem(String cartId, String productId) {
        if (isProductExistInCart(cartId, productId)) {
            return shoppingcartStore.getByCartId(cartId).getCartItems().get(productId);
        }
        return null;

    }

    public boolean isProductExistInCart(String cardId, String productId) {
        if (isUserShoppingCartExist(cardId)) {
            return shoppingcartStore.getByCartId(cardId).getCartItems().containsKey(productId);

        }
        return false;
    }



}

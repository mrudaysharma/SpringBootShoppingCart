package com.wayfair.cart.controller;


import com.wayfair.cart.model.CartModel;
import com.wayfair.cart.model.CartProductModel;
import com.wayfair.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
@Tag(name = "CartController", description = "Shopping Cart Api Documentation")
public class CartController {

    private static final Logger LOGGER = LogManager.getLogger(CartController.class);

    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Save Product in shopping cart",
            description =
                    "In this stub implementation to store product inside shopping cart. \n " +
                            "Every user assign a shopping cart when they create the user account \n " +
                            "This shopping has functionality such as \n " +
                            "1. Add Multiple Products \n" +
                            "2. If product already exit then increase the quantity \n" +
                            "3. Calculate product price according to quantity and offer provided")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CartModel.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong Cart Model Supplied", content = @Content)})
    @PostMapping("/add")
    public ResponseEntity<CartModel> addItem(@RequestBody CartModel cart) {
        try {
            Optional<CartModel> savedItem = Optional.ofNullable(cartService.saveItem(cart));

            if (savedItem.isEmpty()) {
                return new ResponseEntity<CartModel>(HttpStatus.BAD_REQUEST);
            } else {
                LOGGER.info("Given Item Saved Inside Cart=====>" + cart.toString());
                return new ResponseEntity<CartModel>(savedItem.get(), HttpStatus.OK);
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage() + " adding Shoppingcart Item for guest =======> " + cart.getUserId());
            return new ResponseEntity<CartModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get All Product By Cart Id",
            description =
                    "In this stub implementation to get all products from the given cart id ")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CartModel.class))}),
            @ApiResponse(responseCode = "404", description = "Wrong Cart Id Supplied", content = @Content)})
    @GetMapping("/list/{cartId}")
    public ResponseEntity<CartModel> getShoppingcartItems(@NotNull @PathVariable String cartId) {
        LOGGER.info("Getting Shoppingcart Items for guest =======> " + cartId);
        try {
            Optional<CartModel> cart = Optional.ofNullable(cartService.getCartByCartId(cartId));
            if (cart.isPresent() && cart.get().getCartItems().size() > 0) {
                return new ResponseEntity<CartModel>(cart.get(), HttpStatus.OK);
            } else {
                LOGGER.info("Given Cart Not Exist Or It Empty for guest =======> " + cartId);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage() + " getting Shoppingcart Items for guest =======> " + cartId);
            return new ResponseEntity<CartModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "Delete all products from cart by given cart id",
            description =
                    "In this stub implementation to delete all products from the given cart id ")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CartModel.class))}),
            @ApiResponse(responseCode = "204", description = "If cart already empty", content = @Content)})
    @DeleteMapping("/deleteAll/{cartId}")
    public ResponseEntity<CartModel> deleteAll(@NotNull @PathVariable String cartId) {
        try {
            boolean isDeleted = cartService.deleteAllItem(cartId);
            if (isDeleted) {
                Optional<CartModel> cartModel = Optional.of(cartService.getCartByCartId(cartId));
                return new ResponseEntity<CartModel>(cartModel.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<CartModel>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage() + " deleting all products from cart =======> " + cartId);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "Delete specific products from cart using product id",
            description =
                    "In this stub implementation to delete product from the given cart by using product id ")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CartModel.class))}),
            @ApiResponse(responseCode = "404", description = "If product not exist in cart", content = @Content)})
    @DeleteMapping("/delete/{cartId}/{productId}")
    public ResponseEntity<CartModel> deleteItem(@NotNull @PathVariable String cartId, @NotNull @PathVariable String productId) {
        try {
            boolean isDeleted = cartService.deleteSpecificItem(cartId, productId);
            CartModel cart = cartService.getCartByCartId(cartId);
            if (isDeleted) {
                return new ResponseEntity<CartModel>(cart, HttpStatus.OK);
            } else {
                LOGGER.info("Shoppingcart Empty for guest =======> " + cartId);
                return new ResponseEntity<CartModel>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage() + " deleting Shoppingcart Item for guest =======> " + cartId);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "Reduce product quantity from cart using provided quantity ",
            description =
                    "In this stub implementation to reduce product quantity from the cart by using provided quantity\n" +
                            "1. If Given Quantity < Existing Quantity THEN Existing Quantity - Given Quantity AND calcualte products price again \n" +
                            "2. If Given Quantity == Existing Quantity THEN Remove complete Product from Cart\n" +
                            "3. If Given Quantity > Existing Quantity No Action")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CartModel.class))}),
            @ApiResponse(responseCode = "404", description = "If product not exist in cart", content = @Content)})
    @DeleteMapping("/delete/{cartId}/{productId}/{quantity}")
    public ResponseEntity<CartModel> deleteItemQuantity(@NotNull @PathVariable String cartId, @NotNull @PathVariable String productId, @NotNull @PathVariable int quantity) {
        try {
            CartModel cartModel = cartService.deleteItemQuantity(cartId, productId, quantity);
            if (cartModel != null) {
                return new ResponseEntity<CartModel>(cartModel, HttpStatus.OK);
            } else {
                LOGGER.info("Shoppingcart Empty for guest =======> " + cartId);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage() + " deleting Shoppingcart Item for guest =======> " + cartId);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "Calculate final amount of added products ",
            description =
                    "In this stub implementation to Calculate final amount of added products")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CartModel.class))}),
            @ApiResponse(responseCode = "404", description = "If Cart not exist", content = @Content)})
    @GetMapping("/calculateFinalPrice/{cartId}")
    public ResponseEntity<CartModel> finalPriceCalculator(@NotNull @PathVariable String cartId) {
        try {
            CartModel model = cartService.getCartByCartId(cartId);
            if (model != null) {
                cartService.calculateFinalAmount(model);
                return new ResponseEntity<CartModel>(model, HttpStatus.OK);
            } else {
                LOGGER.info("Shoppingcart Empty for guest =======> " + cartId);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage() + " calcualating final amount for products added in cart =======> " + cartId);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "Save Product in shopping cart",
            description =
                    "In this stub implementation to store product inside shopping cart. \n " +
                            "Every user assign a shopping cart when they create the user account \n " +
                            "This shopping has functionality such as \n " +
                            "1. Add Multiple Products \n" +
                            "2. If product already exit then increase the quantity \n" +
                            "3. Calculate product price according to quantity and offer provided")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CartModel.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong Cart Model Supplied", content = @Content)})
    @PostMapping("/updateValue/{cartId}")
    public ResponseEntity<CartProductModel> addItem(@NotNull @PathVariable String cartId, @RequestBody CartProductModel cartProductModel) {

        try {
            Optional<CartProductModel> savedItem = Optional.ofNullable(cartService.updateProductPrice(cartId,cartProductModel));

            if (savedItem.isEmpty()) {
                return new ResponseEntity<CartProductModel>(HttpStatus.BAD_REQUEST);
            } else {
                LOGGER.info("Given Item Saved Inside Cart=====>" + cartProductModel.toString());
                return new ResponseEntity<CartProductModel>(savedItem.get(), HttpStatus.OK);
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage() + " adding Shoppingcart Item for guest =======> " + cartId);
            return new ResponseEntity<CartProductModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Data
    public static class ShoppingcartResponse {
        CartModel shoppingCartItems;
        boolean isCartEmpty;

        public CartModel getShoppingcartItems() {
            return shoppingCartItems;
        }

        public void setShoppingcartItems(CartModel cartItems) {
            this.shoppingCartItems = cartItems;
        }

        public boolean isCartEmpty() {
            return isCartEmpty;
        }

        public void setCartEmpty(boolean cartEmpty) {
            isCartEmpty = cartEmpty;
        }
    }
}

# SpringBootShoppingCart
This is spring boot based shopping cart contains (Microservice, Aggregator and scheduler)

I have covered below mentioned use cases

## Shopping Cart Service

### Use Cases

1. Get all products from cart  by cart id : 
2. Save single or multiple products in cart: - Calculate price according to offers and quantity
3. Delete All Products 
4. Delete Single Products from cart
5. Delete/reduce quantity of added products in cart : IF Given quantity < Store Quantity THEN Given quantity - Store Quantity ELSE IF Given quantity == Store Quantity THEN Delete Product from cart
6. Calculate Final Amount of Added products in cart 

## Aggregator Service

### Use Cases
1. Start Stop and List Schedular 
2. Schedular get given product price and compare it with added products in the cart IF product price != Cart product price THEN update Cart Product price
3. Update that product price as well. 

![General Architecture For Shooping Cart](https://github.com/mrudaysharma/SpringBootShoppingCart/blob/develop/ArchitectureShoppingCart.JPG)

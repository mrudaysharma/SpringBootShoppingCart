package com.wayfair.aggregator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayfair.aggregator.model.CartModel;
import com.wayfair.aggregator.model.CartProductModel;
import com.wayfair.aggregator.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
@Service
public class ProductServiceCallImpl implements ProductServiceCall {
    private static final Logger LOGGER = LogManager.getLogger(ProductServiceCallImpl.class);
    @Override
    public Product getProductPrice(String productId) {
        ObjectMapper objectMapper = new ObjectMapper();
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8081/api/v1/products/"+productId))
                .build();
        var client = HttpClient.newHttpClient();
        try {
            var httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(httpResponse.body(), Product.class);
        } catch (IOException ioe) {
            LOGGER.error("IOException Occurred", ioe);
        } catch (InterruptedException ie) {
            LOGGER.error("InterruptedException Occurred", ie);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @Override
    public CartModel getCartProductPrice(String cartId) {
        ObjectMapper objectMapper = new ObjectMapper();
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8083/cart/list/"+cartId))
                .build();
        var client = HttpClient.newHttpClient();
        try {
            var httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(httpResponse.body(), CartModel.class);
        } catch (IOException ioe) {
            LOGGER.error("IOException Occurred", ioe);
        } catch (InterruptedException ie) {
            LOGGER.error("InterruptedException Occurred", ie);
            Thread.currentThread().interrupt();
        }
        return null;
    }
    @Override
    public CartProductModel saveCartProductPrice(CartProductModel model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(model);
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject))
                .uri(URI.create("http://localhost:8083/cart/updateValue/1234"))
                .header("Content-Type", "application/json")
                .build();
        var client = HttpClient.newHttpClient();
        try {
            var httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(httpResponse.body(), CartProductModel.class);
        } catch (IOException ioe) {
            LOGGER.error("IOException Occurred", ioe);
        } catch (InterruptedException ie) {
            LOGGER.error("InterruptedException Occurred", ie);
            Thread.currentThread().interrupt();
        }
        return null;
    }
}


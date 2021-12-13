package com.shopping.products;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    private static final String API_BASE_PATH_V1 = "/api/v1/products";

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testProductFound() throws Exception {
        when(productService.getProductById("WF12345")).thenReturn(Optional.of(ProductService.LAMP));
        mockMvc.perform(get(API_BASE_PATH_V1 + "/WF12345"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "{\"id\":\"WF12345\",\"name\":\"Mishler Standing Lamp\",\"price\":36.49,\"offers\":[\"3for2\"]}"));
    }

    @Test
    void testProductNotFound() throws Exception {
        when(productService.getProductById("WF999")).thenReturn(Optional.empty());
        mockMvc.perform(get(API_BASE_PATH_V1 + "/WF999")).andExpect(status().isNotFound());
    }

    @Test
    void testProductInvalidId() throws Exception {
        mockMvc.perform(get(API_BASE_PATH_V1 + "/123")).andExpect(status().isBadRequest());
    }

    @Test
    void testProductNotFoundUrl() throws Exception {
        mockMvc.perform(get(API_BASE_PATH_V1)).andExpect(status().isNotFound());
    }

}

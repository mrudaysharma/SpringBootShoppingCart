package com.shopping.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.cart.controller.CartController;
import com.shopping.cart.model.CartModel;
import com.shopping.cart.model.CartProductModel;
import com.shopping.cart.repository.CartRepositoryImpl;
import com.shopping.cart.service.CartService;
import com.shopping.cart.service.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
public class CartControllerTest {

	private static final String API_BASE_PATH_V1 = "/cart";
	CartModel cartModel;
	CartProductModel cart;


	@TestConfiguration
	static class CartServiceImplTestContextConfiguration {
		@Bean
		public CartService cartService() {
			return new CartServiceImpl();
		}
	}

	@Autowired
	private CartService cartService;

	@MockBean
	private CartRepositoryImpl cartRepository;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {

		cart = new CartProductModel();
		cart.setProductId("WF123");
		cart.setName("Druid Corner Sofa");
		cart.setPrice(new BigDecimal("10"));
		cart.setQuantity(5);
		cart.setOffer("2for1");
		cart.setCalculatedPrice(new BigDecimal(20));

		Map<String,CartProductModel> cartProductModelMap = new HashMap<>();
		cartProductModelMap.put(cart.getProductId(),cart);

		cart  = new CartProductModel();
		cart.setProductId("WF12345");
		cart.setName("Mishler Standing Lamp");
		cart.setPrice(new BigDecimal("10"));
		cart.setQuantity(7);
		cart.setOffer("3for2");
		cart.setCalculatedPrice(new BigDecimal(20));

		cartProductModelMap.put(cart.getProductId(),cart);
		cartModel = new CartModel("1234", "123", cartProductModelMap, null);

	}

	@Test
	public void whenItemAdd_thenCartHasItem() throws Exception {
		assertThat(cartModel.getCartItems()).isNotEmpty();
		CartModel newCartModel = new CartModel();
		newCartModel = cartModel;
		newCartModel.getCartItems().get("WF123").setCalculatedPrice(new BigDecimal(30));
		newCartModel.getCartItems().get("WF12345").setCalculatedPrice(new BigDecimal(50));
		when(cartService.saveItem(cartModel)).thenReturn(newCartModel);
		mockMvc.perform( post(API_BASE_PATH_V1 + "/add")
				.content(asJsonString(cartModel))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertThat(cartRepository.saveOrUpdate(cartModel).getCartItems().size()).isEqualTo(2);
	}

	@Test
	public void whenInValidCartId_thenCartShouldNotBeFound() throws Exception {
		CartModel fromDb = cartService.getCartByCartId("wrong_id");
		assertThat(fromDb).isNull();
		mockMvc.perform(get(API_BASE_PATH_V1 + "/list/3456")).andExpect(status().isNotFound());
		verifyFindByIdIsCalledOnce("wrong_id");
	}
	@Test
	public void whenCartNotExist_thenCartShouldNotFound() throws Exception {
		String cartId = "1234";
		CartModel found = cartService.getCartByCartId(cartId);
		mockMvc.perform(get(API_BASE_PATH_V1 + "/list/1234")).andExpect(status().isNotFound());
		assertThat(found).isNull();
	}

	@Test
	public void whenCartExist_thenCartFoundReturnModel() throws Exception {
		String cartId = "1234";
		when(cartService.getCartByCartId("1234")).thenReturn(cartModel);
		mockMvc.perform(get(API_BASE_PATH_V1 + "/list/1234")).andExpect(status().isOk());
		assertThat(cartService.getCartByCartId("1234").getCartId()).isEqualTo("1234");
	}

	@Test
	public void whenDeleteAllItem_ThenReturnNoContent() throws Exception{
		assertThat(cartModel.getCartItems()).isNotEmpty();
		when(cartService.deleteAllItem(cartModel.getCartId())).thenReturn(true);
		mockMvc.perform( delete(API_BASE_PATH_V1 + "/deleteAll/"+cartModel.getCartId()))
				.andExpect(status().isNoContent());
		assertThat(cartRepository.deleteAllItemFromCart(cartModel.getCartId())).isEqualTo(false);

	}

	@Test
	public void whenProductIdPass_DeleteThatProductFromCart() throws Exception{
		assertThat(cartModel.getCartItems()).isNotEmpty();
		when(cartService.deleteSpecificItem(cartModel.getCartId(),"WF123")).thenReturn(true);
		MockHttpServletResponse response=mockMvc.perform( delete(API_BASE_PATH_V1 + "/delete/1234/WF123").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	public void whenWrongProductIdPass_DeleteControllerReturnsNOTFOUND() throws Exception{
		assertThat(cartModel.getCartItems()).isNotEmpty();
		when(cartService.deleteSpecificItem(cartModel.getCartId(),"WF13")).thenReturn(false);
		MockHttpServletResponse response=mockMvc.perform( delete(API_BASE_PATH_V1 + "/delete/1234/WF13").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void whenReduceProductQunatity_DeleteGivenProdcutQuantityFromCart() throws Exception{
		assertThat(cartModel.getCartItems()).isNotEmpty();
		when(cartService.deleteItemQuantity(cartModel.getCartId(),"WF123",2)).thenReturn(cartModel);
		MockHttpServletResponse response=mockMvc.perform( delete(API_BASE_PATH_V1 + "/delete/1234/WF123/2").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	public void whenCalculateFinalAmount_ReturnModel() throws Exception{
		assertThat(cartModel.getCartItems()).isNotEmpty();
		cartModel.setFinalCalculatedPrice(new BigDecimal(40));
		when(cartService.getCartByCartId("1234")).thenReturn(cartModel);
		MockHttpServletResponse response=mockMvc.perform( get(API_BASE_PATH_V1 + "/calculateFinalPrice/1234").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}




	private void verifyFindByIdIsCalledOnce(String cartId) {
		Mockito.verify(cartRepository, VerificationModeFactory.times(1)).getCartByCartId(cartId);
		Mockito.reset(cartRepository);
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

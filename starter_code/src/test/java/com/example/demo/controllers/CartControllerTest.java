package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private CartController cartController;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private final String username = "test username";
    private final Long itemId = 1L;

    @Before
    public void before() {
        User user = createUser();
        when(userRepository.findByUsername(username)).thenReturn(user);
        Item item = createItem();
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
    }

    @Test
    public void addToCart_userNotFound_returnsNotFound() {
        ModifyCartRequest request = createRequest("username", 1L, 1);

        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addToCart_itemNotFound_returnsNotFound() {
        ModifyCartRequest request = createRequest(username, 2L, 1);

        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addToCart_userAndItemFound_returnsOkAndSaves() {
        ModifyCartRequest request = createRequest(username, itemId, 1);

        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void addToCart_exceptionThrown_returnsInternalServerError() {
        ModifyCartRequest request = createRequest(username, itemId, 1);
        doThrow(new RuntimeException()).when(userRepository).findByUsername(username);

        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void removeFromCart_userNotFound_returnsNotFound() {
        ModifyCartRequest request = createRequest("username", 1L, 1);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void removeFromCart_itemNotFound_returnsNotFound() {
        ModifyCartRequest request = createRequest(username, 2L, 1);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void removeFromCart_userAndItemFound_returnsOkAndSaves() {
        ModifyCartRequest request = createRequest(username, itemId, 1);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void removeFromCart_exceptionThrown_returnsInternalServerError() {
        ModifyCartRequest request = createRequest(username, itemId, 1);
        doThrow(new RuntimeException()).when(userRepository).findByUsername(username);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    private User createUser() {
        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        user.setId(1);
        user.setUsername(username);
        user.setPassword("test password");
        user.setCart(cart);
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setName("test item");
        item.setId(itemId);
        BigDecimal price = BigDecimal.valueOf(3.50);
        item.setPrice(price);
        item.setDescription("test description");
        return item;
    }

    private ModifyCartRequest createRequest(String username, long itemId, int quantity) {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(itemId);
        request.setQuantity(quantity);
        return request;
    }

}

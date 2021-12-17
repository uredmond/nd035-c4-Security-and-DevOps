package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderController orderController;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private final String username = "test username";

    @Before
    public void before() {
        User user = createUser();
        when(userRepository.findByUsername(username)).thenReturn(user);
    }

    @Test
    public void submit_userNotFound_returnsNotFound() {
        ResponseEntity<UserOrder> response = orderController.submit("name");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void submit_userAndItemFound_returnsOkAndSaves() {
        ResponseEntity<UserOrder> response = orderController.submit(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    public void submit_exceptionThrown_returnsInternalServerError() {
        doThrow(new RuntimeException()).when(userRepository).findByUsername(username);

        ResponseEntity<UserOrder> response = orderController.submit(username);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getOrdersForUser_userNotFound_returnsNotFound() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("name");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getOrdersForUser_userFound_returnsOk() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getOrdersForUser_exceptionThrown_returnsInternalServerError() {
        doThrow(new RuntimeException()).when(userRepository).findByUsername(username);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

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
}

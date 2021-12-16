package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private UserController userController;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private final String username = "test username";
    private final Long userId = 1L;

    @Before
    public void before() {
        User user = createTestUser();
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    }

    @Test
    public void findById_returnsOk() {
        ResponseEntity<User> response = userController.findById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void findByUserName_userNotFound_returnsNotFound() {
        ResponseEntity<User> response = userController.findByUserName("name");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findByUserName_userFound_returnsOk() {
        ResponseEntity<User> response = userController.findByUserName(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createUser_passwordTooShort_returnsBadRequest() {
        CreateUserRequest request = createUserRequest("", "");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createUser_passwordDoesNotMatchConfirm_returnsBadRequest() {
        CreateUserRequest request = createUserRequest("thisIsLongEnough", "thisIsDifferent");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createUser_passwordOk_returnsOkAndSaves() {
        CreateUserRequest request = createUserRequest("thisIsLongEnough", "thisIsLongEnough");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any());
    }

    private User createTestUser() {
        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        user.setId(userId);
        user.setUsername(username);
        user.setPassword("test password");
        user.setCart(cart);
        return user;
    }

    private CreateUserRequest createUserRequest(String password, String confirmPassword) {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return request;
    }
}

package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
		try {
			String username = request.getUsername();
			User user = userRepository.findByUsername(username);
			if (user == null) {
				log.error("User not found with name {}", username);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if (!item.isPresent()) {
				log.error("Item not found with id {}", request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.addItem(item.get()));
			cartRepository.save(cart);
			log.info("Saved item to cart for user {}", username);
			return ResponseEntity.ok(cart);
		} catch (Exception e) {
			log.error("Error adding to cart: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
		try {
			String username = request.getUsername();
			User user = userRepository.findByUsername(username);
			if (user == null) {
				log.error("User not found with name {}", username);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if (!item.isPresent()) {
				log.error("Item not found with id {}", request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.removeItem(item.get()));
			cartRepository.save(cart);
			log.info("Removed item from cart for user {}", username);
			return ResponseEntity.ok(cart);
		} catch (Exception e) {
			log.error("Error removing from cart: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
		
}

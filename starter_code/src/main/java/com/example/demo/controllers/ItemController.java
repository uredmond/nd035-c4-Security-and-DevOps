package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		try {
			log.info("Retrieving all items");
			return ResponseEntity.ok(itemRepository.findAll());
		} catch (Exception e) {
			log.error("Error retrieving items: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		try {
			log.info("Searching for item by id {}", id);
			return ResponseEntity.of(itemRepository.findById(id));
		} catch (Exception e) {
			log.error("Error retrieving item by id: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		try {
			List<Item> items = itemRepository.findByName(name);
			if (items == null || items.isEmpty()) {
				log.error("Items not found for {}", name);
				return ResponseEntity.notFound().build();
			}
			log.info("Items found for {}", name);
			return ResponseEntity.ok(items);
		}  catch (Exception e) {
			log.error("Error retrieving item by name: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}

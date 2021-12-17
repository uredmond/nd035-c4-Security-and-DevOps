package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemController itemController;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private final String itemName = "test name";
    private final Long itemId = 1L;

    @Before
    public void before() {
        Item item = createItem();
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.findByName(itemName)).thenReturn(Collections.singletonList(item));
    }

    @Test
    public void getItems_returnsOk() {
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getItems_exceptionThrown_returnsInternalServerError() {
        doThrow(new RuntimeException()).when(itemRepository).findAll();

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getItemById_returnsOk() {
        ResponseEntity<Item> response = itemController.getItemById(itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getItemById_exceptionThrown_returnsInternalServerError() {
        doThrow(new RuntimeException()).when(itemRepository).findById(itemId);

        ResponseEntity<Item> response = itemController.getItemById(itemId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getItemsByName_itemNotFound_returnsNotFound() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("name");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getItemsByName_returnsOk() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getItemsByName_exceptionThrown_returnsInternalServerError() {
        doThrow(new RuntimeException()).when(itemRepository).findByName(itemName);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    private Item createItem() {
        Item item = new Item();
        item.setName(itemName);
        item.setId(itemId);
        BigDecimal price = BigDecimal.valueOf(3.50);
        item.setPrice(price);
        item.setDescription("test description");
        return item;
    }

}

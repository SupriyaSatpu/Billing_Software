package com.cdac.billingsoftware.controller;

import com.cdac.billingsoftware.io.ItemRequest;
import com.cdac.billingsoftware.io.ItemResponse;
import com.cdac.billingsoftware.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/items")
    public ItemResponse addItem(@RequestPart("item") String itemString,
                                @RequestPart("file") MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemRequest itemRequest = null;
        try {
            // Convert incoming JSON string to ItemRequest object
            itemRequest = objectMapper.readValue(itemString, ItemRequest.class);

            // Call service to add item
            return itemService.add(itemRequest, file);
        } catch (IOException ex) {
            // Handle invalid JSON parsing
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occured while processing the json");
        }
    }

    @GetMapping("/items")
    public List<ItemResponse> readItems() {
        return itemService.fetchItems();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/items/{itemId}")
    public void removeItem(@PathVariable String itemId) {
        try {
            // Call service to delete item
            itemService.deleteItem(itemId);
        } catch (Exception e) {
            // If item does not exist, return 404 NOT FOUND
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
    }

}

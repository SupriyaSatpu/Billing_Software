package com.cdac.billingsoftware.service;

import com.cdac.billingsoftware.io.ItemRequest;
import com.cdac.billingsoftware.io.ItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ItemService {

    // Add a new item along with an image file, returns the created item's details
    ItemResponse add(ItemRequest request, MultipartFile file) throws IOException;

    // Fetch a list of all items with their details
    List<ItemResponse> fetchItems();

    // Delete an item by its unique itemId
    void deleteItem(String itemId);
}

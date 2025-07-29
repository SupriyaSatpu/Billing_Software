package com.cdac.billingsoftware.service.impl;

import com.cdac.billingsoftware.entity.CategoryEntity;
import com.cdac.billingsoftware.entity.ItemEntity;
import com.cdac.billingsoftware.io.ItemRequest;
import com.cdac.billingsoftware.io.ItemResponse;
import com.cdac.billingsoftware.repository.CategoryRepository;
import com.cdac.billingsoftware.repository.ItemRepository;
import com.cdac.billingsoftware.service.FileUploadService;
import com.cdac.billingsoftware.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final FileUploadService fileUploadService;  // Service to handle file upload (currently unused, commented out)
    private final CategoryRepository categoryRepository;  // Repository for category data
    private final ItemRepository itemRepository;  // Repository for item data

    /**
     * Adds a new item with an uploaded image.
     * - Saves image to local "uploads" directory
     * - Links item to an existing category
     * - Stores item details in database
     */

    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) throws IOException {
        // Generate a unique file name
        String fileName = UUID.randomUUID().toString()+"."+ StringUtils.getFilenameExtension(file.getOriginalFilename());

        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Copy uploaded file to uploads directory
        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Generate image URL for accessing the uploaded file
        String imgUrl = "http://localhost:8080/api/v1.0/uploads/"+fileName;

        // Convert request DTO to entity
        ItemEntity newItem = convertToEntity(request);

        // Validate category and set it to the item
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: "+request.getCategoryId()));
        newItem.setCategory(existingCategory);
        newItem.setImgUrl(imgUrl);
        newItem = itemRepository.save(newItem);
        return convertToResponse(newItem);
    }

    // Convert ItemEntity to ItemResponse DTO
    private ItemResponse convertToResponse(ItemEntity newItem) {
        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .imgUrl(newItem.getImgUrl())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .build();
    }

    // Convert ItemRequest DTO to ItemEntity
    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

    /**
     * Fetches all items from the database.
     * - Converts each ItemEntity into ItemResponse DTO
     */
    @Override
    public List<ItemResponse> fetchItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemEntity -> convertToResponse(itemEntity))
                .collect(Collectors.toList());
    }

    /**
     * Deletes an item by its itemId.
     * - Removes item image from uploads folder
     * - Deletes item record from database
     */
    @Override
    public void deleteItem(String itemId) {

        // Find existing item or throw exception
        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: "+itemId));

        // Extract image file name from image URL
        String imgUrl = existingItem.getImgUrl();
        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(fileName);

        // Delete image file and then delete item record
        try {
            Files.deleteIfExists(filePath);
            itemRepository.delete(existingItem);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete the image");
        }
    }
}

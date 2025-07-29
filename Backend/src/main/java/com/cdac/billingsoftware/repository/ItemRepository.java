package com.cdac.billingsoftware.repository;

import com.cdac.billingsoftware.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Extends JpaRepository to provide CRUD operations and custom queries
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    // Find an item by its unique business itemId
    Optional<ItemEntity> findByItemId(String id);

    // Count how many items belong to a given category (by category ID)
    Integer countByCategoryId(Long id);
}


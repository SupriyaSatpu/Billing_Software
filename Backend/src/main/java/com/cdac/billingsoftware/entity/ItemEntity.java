package com.cdac.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity // Marks this class as a JPA entity mapped to a database table
@Table(name = "tbl_items") // Specifies the table name as tbl_items
@Data // Lombok: generates getters, setters, equals, hashCode, and toString
@AllArgsConstructor // Lombok: generates constructor with all fields
@NoArgsConstructor // Lombok: generates default constructor
@Builder // Lombok: enables builder pattern for object creation
public class ItemEntity {

    @Id // Marks the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(unique = true) // Ensures itemId is unique in the table
    private String itemId;

    private String name;

    private BigDecimal price;

    private String description;

    @CreationTimestamp // Automatically sets timestamp when row is created
    @Column(updatable = false) // Prevents modification after creation
    private Timestamp createdAt;

    @UpdateTimestamp // Automatically updates timestamp when row is updated
    private Timestamp updatedAt;

    private String imgUrl;

    @ManyToOne // Many items can belong to one category
    @JoinColumn(name = "category_id", nullable = false) // Foreign key column
    @OnDelete(action = OnDeleteAction.RESTRICT) // Prevents category deletion if items exist
    private CategoryEntity category; // Reference to CategoryEntity
}


package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory",
       indexes = {
           @Index(name = "idx_inventory_name", columnList = "name"),
           @Index(name = "idx_inventory_category", columnList = "category")
       })
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @JsonProperty("name")
    private String name;

    @NotBlank
    @Column(nullable = false)
    @JsonProperty("category")
    private String category;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    @JsonProperty("quantity")
    private Integer quantity;

    @NotNull
    @Min(0)
    @Column(nullable = false, precision = 10, scale = 2)
    @JsonProperty("unit_cost")
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("description")
    private String description;

    @Min(0)
    @Column(name = "reorder_level")
    @JsonProperty("reorder_level")
    private Integer reorderLevel;

    @Column(name = "supplier_info")
    @JsonProperty("supplier_info")
    private String supplierInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @JsonProperty("item_id")
    Integer itemId;

    @JsonProperty("brand")
    String brand;

    @JsonProperty("unit_of_measure")
    String unitOfMeasure;

    @JsonProperty("reorder_quantity")
    Integer reorderQuantity;

    @JsonProperty("par_level")
    Integer parLevel;

    @JsonProperty("total_cost")
    Float totalCost;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("purchase_date")
    Date purchaseDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("expiry_date")
    Date expiryDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("last_updated")
    Date lastUpdated;

    @JsonProperty("sku")
    String sku;

    @JsonProperty("image_url")
    String imageUrl;

    @PrePersist
    protected void onCreate() {
        if (reorderLevel == null) {
            reorderLevel = 10; // Default reorder level
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (quantity <= reorderLevel) {
            // TODO: Implement reorder notification logic
        }
    }
}

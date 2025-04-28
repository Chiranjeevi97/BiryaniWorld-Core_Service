package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.menu;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.order.OrderItem;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = {"orderItems", "menu"})
@Entity
@Table(name = "item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    public Long itemId;

    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "price")
    public double price;

    @Column(name = "item_quantity")
    public String itemQuantity;

    @Column(name = "seasonal")
    public boolean seasonal;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    @JsonBackReference("menu-items")
    public Menu menu;

    @OneToMany(mappedBy = "item")
    @JsonManagedReference("item-orderItems")
    private List<OrderItem> orderItems;
}
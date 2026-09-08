package NarayanGroup.example.E_Commerce.model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "name")
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    /**
     * Kept temporarily for backward compatibility with
     * the existing product/cart functionality.
     *
     * Inventory will become the source of truth for stock
     * during checkout.
     */
    @Column
    private Integer quantity;

    @Column(length = 10)
    private String currency;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
package NarayanGroup.example.E_Commerce.model.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category categoryEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;
}
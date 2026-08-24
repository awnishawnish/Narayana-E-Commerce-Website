package NarayanGroup.example.E_Commerce.model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many Cart Items -> One Cart
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id",
            nullable = false)
    private Cart cart;

    /**
     * Many Cart Items -> One Product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",
            nullable = false)
        private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_addition", nullable = false)
    private BigDecimal price_at_addition;

    @Column(nullable = false)
    private String currency;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
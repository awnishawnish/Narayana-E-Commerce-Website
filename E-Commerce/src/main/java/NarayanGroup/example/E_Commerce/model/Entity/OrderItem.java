package NarayanGroup.example.E_Commerce.model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @Column(
            name = "source_cart_item_id"
    )
    private Long sourceCartItemId;

    @Column(
            name = "product_title",
            nullable = false
    )
    private String productTitle;

    @Column(
            name = "quantity",
            nullable = false
    )
    private Long quantity;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal unitPrice;

    @Column(
            name = "total_price",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal totalPrice;

    @Column(
            name = "currency",
            nullable = false,
            length = 10
    )
    private String currency;

    @Column(
            name = "is_deleted",
            nullable = false
    )
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}
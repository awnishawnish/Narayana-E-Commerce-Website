package NarayanGroup.example.E_Commerce.model.Entity;


import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_order_number",
                        columnNames = "order_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "order_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    private OrderStatus status;

    @Column(
            name = "payment_status",
            nullable = false,
            length = 30
    )
    private String paymentStatus;

    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private String paymentMethod;

    @Column(
            name = "subtotal",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal subtotal;

    @Column(
            name = "tax",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal tax;

    @Column(
            name = "delivery_charge",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal deliveryCharge;

    @Column(
            name = "discount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal discount;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal totalAmount;

    @Column(
            name = "currency",
            nullable = false,
            length = 10
    )
    private String currency;

    /*
     * Shipping address snapshot
     */
    @Column(name = "shipping_full_name", nullable = false)
    private String shippingFullName;

    @Column(name = "shipping_phone", nullable = false)
    private String shippingPhone;

    @Column(name = "shipping_address_line1", nullable = false)
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2")
    private String shippingAddressLine2;

    @Column(name = "shipping_city", nullable = false)
    private String shippingCity;

    @Column(name = "shipping_state", nullable = false)
    private String shippingState;

    @Column(name = "shipping_pincode", nullable = false)
    private String shippingPincode;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

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
package NarayanGroup.example.E_Commerce.model.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Internal payment identifier
     */
    @Column(
            name = "payment_id",
            nullable = false,
            unique = true,
            length = 100
    )
    private String paymentId;

    /*
     * Our application's Order
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true
    )
    private Order order;

    /*
     * Razorpay Order ID
     *
     * Example:
     * order_RZP123456789
     */
    @Column(
            name = "gateway_order_id",
            unique = true,
            length = 100
    )
    private String gatewayOrderId;

    /*
     * Razorpay Payment ID
     *
     * Example:
     * pay_ABC123456
     */
    @Column(
            name = "gateway_payment_id",
            unique = true,
            length = 100
    )
    private String gatewayPaymentId;

    /*
     * Razorpay signature
     */
    @Column(
            name = "gateway_signature",
            length = 255
    )
    private String gatewaySignature;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "currency",
            nullable = false,
            length = 10
    )
    private String currency;

    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private String paymentMethod;

    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private String status;

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
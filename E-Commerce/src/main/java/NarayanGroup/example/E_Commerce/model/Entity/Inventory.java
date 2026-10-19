package NarayanGroup.example.E_Commerce.model.Entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_inventory_product",
                        columnNames = "product_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            unique = true
    )
    private Product product;

    @Column(
            name = "available_quantity",
            nullable = false
    )
    private Long availableQuantity;

    @Column(
            name = "reserved_quantity",
            nullable = false
    )
    private Long reservedQuantity;

    // createdAt/updatedAt/isDeleted are inherited from BaseEntity

    @PrePersist
    protected void onCreate() {
        super.onCreate();

        if (availableQuantity == null) {
            availableQuantity = 0L;
        }

        if (reservedQuantity == null) {
            reservedQuantity = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // updatedAt handled by BaseEntity
    }
}
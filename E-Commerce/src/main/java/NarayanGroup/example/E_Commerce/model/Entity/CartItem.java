package NarayanGroup.example.E_Commerce.model.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cart_id",
            nullable = false
    )
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(
            name = "price_at_addition",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal priceAtAddition;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

}
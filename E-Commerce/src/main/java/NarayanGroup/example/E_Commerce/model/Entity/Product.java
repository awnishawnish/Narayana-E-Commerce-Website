package NarayanGroup.example.E_Commerce.model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal; // ✅ CHANGED: was Long, now supports decimal prices like ₹99.99

@Entity
@Table(name = "product")
@Data
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

    // ✅ CHANGED: was `Long price` — BigDecimal supports decimal prices (₹99.99)
    @Column(nullable = false)
    private BigDecimal price;

    // ✅ CHANGED: was `Long quantity` — Integer is sufficient for stock count
    private Integer quantity;

    private String currency;

    @Column(name = "image_key")
    private String imageKey; // stores S3 URL / key for the product image

    @Column(name = "is_deleted")
    private boolean isDeleted;
}

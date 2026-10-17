package NarayanGroup.example.E_Commerce.model.Entity;

import NarayanGroup.example.E_Commerce.model.Enum.BusinessType;
import NarayanGroup.example.E_Commerce.model.Enum.SellerStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Seller extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "mobile_verified", nullable = false)
    private boolean mobileVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;

    @Column(name = "business_address", nullable = false, length = 1000)
    private String businessAddress;
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    private Category productCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SellerStatus status;

}

package NarayanGroup.example.E_Commerce.model.Repositry;


import NarayanGroup.example.E_Commerce.model.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IAddressRepository
        extends JpaRepository<Address, Long> {

    List<Address> findByUserIdAndIsDeletedFalse(
            Long userId
    );

    Optional<Address> findByIdAndUserIdAndIsDeletedFalse(
            Long addressId,
            Long userId
    );

    Optional<Address> findByIdAndIsDeletedFalse(
            Long addressId
    );
}
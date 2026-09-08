package NarayanGroup.example.E_Commerce.service;



import NarayanGroup.example.E_Commerce.model.Entity.Address;

import java.util.List;

public interface IAddressService {

    Address save(Address address);

    List<Address> findActiveAddressesByUserId(Long userId);

    Address findActiveAddress(
            Long addressId,
            Long userId
    );

    Address findById(Long addressId);

    void delete(Address address);
}

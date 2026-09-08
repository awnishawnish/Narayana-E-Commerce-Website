package NarayanGroup.example.E_Commerce.service.Impl;
import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.Address;
import NarayanGroup.example.E_Commerce.model.Repositry.IAddressRepository;
import NarayanGroup.example.E_Commerce.service.IAddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AddressService implements IAddressService {

    private final IAddressRepository addressRepository;

    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public List<Address> findActiveAddressesByUserId(
            Long userId) {

        return addressRepository
                .findByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    public Address findActiveAddress(
            Long addressId,
            Long userId) {

        return addressRepository
                .findByIdAndUserIdAndIsDeletedFalse(
                        addressId,
                        userId
                )
                .orElseThrow(() ->
                        new CustomException.AddressNotFoundException(
                                "Address not found"
                        )
                );
    }

    @Override
    public Address findById(Long addressId) {

        return addressRepository
                .findByIdAndIsDeletedFalse(addressId)
                .orElseThrow(() ->
                        new CustomException.AddressNotFoundException(
                                "Address not found"
                        )
                );
    }

    @Override
    public void delete(Address address) {

        address.setDeleted(true);

        addressRepository.save(address);
    }
}

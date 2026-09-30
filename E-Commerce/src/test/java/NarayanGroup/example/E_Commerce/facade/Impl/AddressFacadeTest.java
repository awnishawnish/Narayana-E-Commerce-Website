package NarayanGroup.example.E_Commerce.facade.Impl;

import NarayanGroup.example.E_Commerce.DTO.request.AddressRequestDTO;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.model.Entity.Address;
import NarayanGroup.example.E_Commerce.model.Entity.UserEntity;
import NarayanGroup.example.E_Commerce.service.IAddressService;
import NarayanGroup.example.E_Commerce.service.IUserService;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressFacadeTest {
    private final IAddressService addressService=mock(IAddressService.class);
    private final IUserService userService=mock(IUserService.class);
    private final AddressFacade facade=new AddressFacade(addressService,userService);

    private AddressRequestDTO request(Boolean def) {
        return AddressRequestDTO.builder().fullName("User").phone("9876543210").addressLine1("Line 1")
                .addressLine2("Line 2").city("Noida").state("UP").pincode("201301").isDefault(def).build();
    }
    @Test void addAddress_shouldClearExistingDefaultsWhenRequested() {
        var user=UserEntity.builder().id(1L).build();
        var old=Address.builder().id(2L).isDefault(true).build();
        when(userService.getUserById(1L)).thenReturn(user); when(addressService.findActiveAddressesByUserId(1L)).thenReturn(new ArrayList<>(List.of(old)));
        when(addressService.save(any(Address.class))).thenAnswer(i->i.getArgument(0));
        var r=facade.addAddress(1L,request(true));
        assertEquals(201,r.getHttpStatus()); assertFalse(old.isDefault());
        verify(addressService,times(2)).save(any(Address.class));
    }
    @Test void addAddress_shouldNotClearExistingDefaultsWhenNotDefault() {
        var user=UserEntity.builder().id(1L).build(); when(userService.getUserById(1L)).thenReturn(user);
        when(addressService.save(any(Address.class))).thenAnswer(i->i.getArgument(0));
        var r=facade.addAddress(1L,request(false));
        assertEquals(CommonConstants.ADDRESS_ADDED_SUCCESSFULLY,r.getMessage()); verify(addressService).save(any(Address.class));
        verify(addressService,never()).findActiveAddressesByUserId(1L);
    }
    @Test void getAddresses_shouldMapResponses() {
        var address=Address.builder().id(1L).fullName("U").phone("9876543210").addressLine1("L").city("C").state("S").pincode("123456").isDefault(true).build();
        when(addressService.findActiveAddressesByUserId(1L)).thenReturn(List.of(address));
        var r=facade.getAddresses(1L);
        assertEquals(1,((List<?>)r.getData()).size()); assertEquals(200,r.getHttpStatus());
    }
    @Test void updateAddress_shouldClearOtherDefaults() {
        var address=Address.builder().id(1L).build(); var other=Address.builder().id(2L).isDefault(true).build();
        when(addressService.findActiveAddress(1L,1L)).thenReturn(address); when(addressService.findActiveAddressesByUserId(1L)).thenReturn(List.of(address,other));
        when(addressService.save(any(Address.class))).thenAnswer(i->i.getArgument(0));
        var r=facade.updateAddress(1L,1L,request(true));
        assertEquals(200,r.getHttpStatus()); assertFalse(other.isDefault()); assertTrue(address.isDefault());
        verify(addressService).save(other); verify(addressService).save(address);
    }
    @Test void updateAddress_shouldPreserveDefaultWhenRequestIsNull() {
        var address=Address.builder().id(1L).isDefault(true).build();
        var req=request(null);
        when(addressService.findActiveAddress(1L,1L)).thenReturn(address); when(addressService.save(address)).thenReturn(address);
        facade.updateAddress(1L,1L,req);
        assertTrue(address.isDefault()); verify(addressService,never()).findActiveAddressesByUserId(1L);
    }
    @Test void deleteAddress_shouldSoftDeleteThroughService() {
        var address=Address.builder().id(1L).build(); when(addressService.findActiveAddress(1L,1L)).thenReturn(address);
        var r=facade.deleteAddress(1L,1L);
        assertEquals(CommonConstants.ADDRESS_DELETED_SUCCESSFULLY,r.getMessage()); verify(addressService).delete(address);
    }
}

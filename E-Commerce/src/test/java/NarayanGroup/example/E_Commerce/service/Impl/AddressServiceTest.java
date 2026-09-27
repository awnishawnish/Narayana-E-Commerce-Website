package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.Address;
import NarayanGroup.example.E_Commerce.model.Repositry.IAddressRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {
    @Test void saveAndFind_shouldDelegate() {
        IAddressRepository repo=mock(IAddressRepository.class); Address a=Address.builder().id(1L).build();
        when(repo.save(a)).thenReturn(a); when(repo.findByUserIdAndIsDeletedFalse(2L)).thenReturn(List.of(a)); when(repo.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(a)); when(repo.findByIdAndUserIdAndIsDeletedFalse(1L,2L)).thenReturn(Optional.of(a));
        AddressService service=new AddressService(repo);
        assertSame(a,service.save(a)); assertEquals(List.of(a),service.findActiveAddressesByUserId(2L)); assertSame(a,service.findActiveAddress(1L,2L)); assertSame(a,service.findById(1L));
    }
    @Test void findMissing_shouldThrow() { IAddressRepository repo=mock(IAddressRepository.class); when(repo.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty()); assertThrows(RuntimeException.class,()->new AddressService(repo).findById(1L)); }
    @Test void delete_shouldSoftDelete() { IAddressRepository repo=mock(IAddressRepository.class); Address a=Address.builder().isDeleted(false).build(); new AddressService(repo).delete(a); assertTrue(a.isDeleted()); verify(repo).save(a); }
}

package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentIdAndIsDeletedFalse(
            String paymentId
    );

    Optional<Payment> findByOrderIdAndIsDeletedFalse(
            Long orderId
    );
}

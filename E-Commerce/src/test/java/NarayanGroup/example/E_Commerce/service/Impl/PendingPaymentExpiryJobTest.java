package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PendingPaymentExpiryJobTest {
    @Test void expirePendingPayments_shouldExpireEveryReturnedOrder() throws Exception {
        IOrderRepository repository=mock(IOrderRepository.class); OrderService orderService=mock(OrderService.class);
        Order a=Order.builder().id(1L).status(OrderStatus.PENDING_PAYMENT).createdAt(LocalDateTime.now().minusHours(1)).build();
        Order b=Order.builder().id(2L).status(OrderStatus.PENDING_PAYMENT).createdAt(LocalDateTime.now().minusHours(2)).build();
        when(repository.findByStatusAndCreatedAtBeforeAndIsDeletedFalse(eq(OrderStatus.PENDING_PAYMENT),any())).thenReturn(List.of(a,b));
        PendingPaymentExpiryJob job=new PendingPaymentExpiryJob(repository,orderService);
        java.lang.reflect.Field field=PendingPaymentExpiryJob.class.getDeclaredField("timeoutMinutes"); field.setAccessible(true); field.set(job,15L);
        job.expirePendingPayments();
        verify(orderService).expirePendingPayment(1L); verify(orderService).expirePendingPayment(2L);
    }
}

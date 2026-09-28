package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.model.Entity.Order;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.model.Repositry.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PendingPaymentExpiryJob {
    private final IOrderRepository orderRepository;
    private final OrderService orderService;

    @Value("${app.checkout.payment-timeout-minutes:15}")
    private long timeoutMinutes;

    @Scheduled(fixedDelayString = "${app.checkout.payment-expiry-check-ms:300000}")
    public void expirePendingPayments() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(timeoutMinutes);
        for (Order order : orderRepository.findByStatusAndCreatedAtBeforeAndIsDeletedFalse(OrderStatus.PENDING_PAYMENT, cutoff)) {
            orderService.expirePendingPayment(order.getId());
        }
    }
}

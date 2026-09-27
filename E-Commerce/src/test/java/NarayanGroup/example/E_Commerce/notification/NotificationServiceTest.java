package NarayanGroup.example.E_Commerce.notification;

import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    @Test void sendPaymentSuccessEmail_shouldSendMail() {
        JavaMailSender sender = mock(JavaMailSender.class);
        NotificationService service = new NotificationService(sender);
        PaymentSuccessfulEvent event = PaymentSuccessfulEvent.builder().userEmail("user@test.com")
                .orderNumber("ORD-1").currency("INR").amount(new BigDecimal("500.00")).build();
        service.sendPaymentSuccessEmail(event);
        verify(sender).send(any(SimpleMailMessage.class));
    }

    @Test void sendOrderConfirmationEmail_shouldSendMail() {
        JavaMailSender sender = mock(JavaMailSender.class);
        NotificationService service = new NotificationService(sender);
        OrderConfirmedEvent event = OrderConfirmedEvent.builder().userEmail("user@test.com")
                .orderNumber("ORD-2").currency("INR").amount(new BigDecimal("300.00")).build();
        service.sendOrderConfirmationEmail(event);
        verify(sender).send(any(SimpleMailMessage.class));
    }
}

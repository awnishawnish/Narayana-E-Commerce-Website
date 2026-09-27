package NarayanGroup.example.E_Commerce.notification;

import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(OrderConfirmedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject("Order confirmed - " + event.getOrderNumber());
        message.setText("Your order " + event.getOrderNumber() + " has been confirmed. Total amount: "
                + event.getCurrency() + " " + event.getAmount() + ".");
        mailSender.send(message);
    }

    public void sendPaymentSuccessEmail(PaymentSuccessfulEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject("Payment successful - Order " + event.getOrderNumber());
        message.setText("Your payment of " + event.getCurrency() + " " + event.getAmount()
                + " was successful for order " + event.getOrderNumber() + ".");
        mailSender.send(message);
    }
    public void sendOrderCancellationEmail(OrderCancelledEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject("Order cancelled - " + event.getOrderNumber());
        message.setText("Your order " + event.getOrderNumber() + " has been cancelled. "
                + "Reason: " + event.getReason() + ". Payment status: " + event.getPaymentStatus() + ".");
        mailSender.send(message);
    }

}

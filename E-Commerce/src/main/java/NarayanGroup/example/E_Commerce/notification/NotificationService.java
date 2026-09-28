package NarayanGroup.example.E_Commerce.notification;

import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(OrderConfirmedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject(String.format(CommonConstants.ORDER_CONFIRMED_SUBJECT, event.getOrderNumber()));
        message.setText(String.format(CommonConstants.ORDER_CONFIRMED_BODY, event.getOrderNumber(),
                event.getCurrency(), event.getAmount()));
        mailSender.send(message);
    }

    public void sendPaymentSuccessEmail(PaymentSuccessfulEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject(String.format(CommonConstants.PAYMENT_SUCCESSFUL_SUBJECT, event.getOrderNumber()));
        message.setText(String.format(CommonConstants.PAYMENT_SUCCESSFUL_BODY, event.getCurrency(),
                event.getAmount(), event.getOrderNumber()));
        mailSender.send(message);
    }
    public void sendOrderCancellationEmail(OrderCancelledEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject(String.format(CommonConstants.ORDER_CANCELLED_SUBJECT, event.getOrderNumber()));
        message.setText(String.format(CommonConstants.ORDER_CANCELLED_BODY, event.getOrderNumber(),
                event.getReason(), event.getPaymentStatus()));
        mailSender.send(message);
    }

}

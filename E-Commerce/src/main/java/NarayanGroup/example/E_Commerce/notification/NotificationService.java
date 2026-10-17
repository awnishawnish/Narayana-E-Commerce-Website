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

        if (event.getSellerEmails() != null) {
            for (String sellerEmail : event.getSellerEmails()) {
                if (sellerEmail == null || sellerEmail.isBlank()
                        || sellerEmail.equalsIgnoreCase(event.getUserEmail())) {
                    continue;
                }
                SimpleMailMessage sellerMessage = new SimpleMailMessage();
                sellerMessage.setTo(sellerEmail);
                sellerMessage.setSubject("New order received: " + event.getOrderNumber());
                sellerMessage.setText("A customer placed an order containing your product(s).\n\n"
                        + "Order: " + event.getOrderNumber() + "\n"
                        + "Payment method: " + event.getPaymentMethod() + "\n"
                        + "Products: " + sellerProducts(event.getItems(), sellerEmail) + "\n"
                        + "Deliver to: " + (event.getShippingAddress() == null ? "See order details" : event.getShippingAddress()) + "\n"
                        + "Customer phone: " + (event.getShippingPhone() == null ? "Not provided" : event.getShippingPhone()) + "\n\n"
                        + "Please sign in to the seller dashboard to review the order.");
                mailSender.send(sellerMessage);
            }
        }
    }

    public void sendPaymentSuccessEmail(PaymentSuccessfulEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject(String.format(CommonConstants.PAYMENT_SUCCESSFUL_SUBJECT, event.getOrderNumber()));
        message.setText(String.format(CommonConstants.PAYMENT_SUCCESSFUL_BODY, event.getCurrency(),
                event.getAmount(), event.getOrderNumber()));
        mailSender.send(message);

        if (event.getSellerEmails() != null) {
            for (String sellerEmail : event.getSellerEmails()) {
                if (sellerEmail == null || sellerEmail.isBlank()
                        || sellerEmail.equalsIgnoreCase(event.getUserEmail())) {
                    continue;
                }
                SimpleMailMessage sellerMessage = new SimpleMailMessage();
                sellerMessage.setTo(sellerEmail);
                sellerMessage.setSubject("New paid order received: " + event.getOrderNumber());
                sellerMessage.setText("A customer placed an order containing your product(s), and payment was verified.\n\n"
                        + "Order: " + event.getOrderNumber() + "\n"
                        + "Payment method: " + event.getPaymentMethod() + "\n"
                        + "Products: " + sellerProducts(event.getItems(), sellerEmail) + "\n"
                        + "Deliver to: " + (event.getShippingAddress() == null ? "See order details" : event.getShippingAddress()) + "\n"
                        + "Customer phone: " + (event.getShippingPhone() == null ? "Not provided" : event.getShippingPhone()) + "\n\n"
                        + "Please sign in to the seller dashboard to review the order.");
                mailSender.send(sellerMessage);
            }
        }
    }
    private String sellerProducts(java.util.List<NarayanGroup.example.E_Commerce.kafka.event.PaymentItemEvent> items,
                                  String sellerEmail) {
        if (items == null) return "See seller dashboard";
        String products = items.stream()
                .filter(item -> item.getSellerEmail() != null && item.getSellerEmail().equalsIgnoreCase(sellerEmail))
                .map(item -> (item.getProductTitle() == null ? "Product #" + item.getProductId() : item.getProductTitle())
                        + " x " + item.getQuantity())
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));
        return products.isBlank() ? "See seller dashboard" : products;
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

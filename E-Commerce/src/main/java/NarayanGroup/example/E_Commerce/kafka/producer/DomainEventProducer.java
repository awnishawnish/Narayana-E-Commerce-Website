package NarayanGroup.example.E_Commerce.kafka.producer;

import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentFailedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.payment-successful:payment-successful}")
    private String paymentSuccessfulTopic;

    @Value("${app.kafka.topics.payment-failed:payment-failed}")
    private String paymentFailedTopic;

    @Value("${app.kafka.topics.order-confirmed:order-confirmed}")
    private String orderConfirmedTopic;

    @Value("${app.kafka.topics.order-cancelled:order-cancelled}")
    private String orderCancelledTopic;

    public void publishPaymentSuccessful(PaymentSuccessfulEvent event) {
        kafkaTemplate.send(paymentSuccessfulTopic, event.getOrderId().toString(), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        kafkaTemplate.send(paymentFailedTopic, event.getOrderId().toString(), event);
    }

    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        kafkaTemplate.send(orderConfirmedTopic, event.getOrderId().toString(), event);
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        kafkaTemplate.send(orderCancelledTopic, event.getOrderId().toString(), event);
    }
}

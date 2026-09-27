package NarayanGroup.example.E_Commerce.kafka.config;

import NarayanGroup.example.E_Commerce.kafka.event.OrderConfirmedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.OrderCancelledEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentFailedEvent;
import NarayanGroup.example.E_Commerce.kafka.event.PaymentSuccessfulEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, PaymentSuccessfulEvent> paymentSuccessfulConsumerFactory(KafkaProperties properties) {
        return factory(properties, PaymentSuccessfulEvent.class);
    }

    @Bean
    public ConsumerFactory<String, PaymentFailedEvent> paymentFailedConsumerFactory(KafkaProperties properties) {
        return factory(properties, PaymentFailedEvent.class);
    }

    @Bean
    public ConsumerFactory<String, OrderConfirmedEvent> orderConfirmedConsumerFactory(KafkaProperties properties) {
        return factory(properties, OrderConfirmedEvent.class);
    }

    @Bean
    public ConsumerFactory<String, OrderCancelledEvent> orderCancelledConsumerFactory(KafkaProperties properties) {
        return factory(properties, OrderCancelledEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessfulEvent> paymentSuccessfulKafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentSuccessfulEvent> consumerFactory, DefaultErrorHandler errorHandler) {
        return listenerFactory(consumerFactory, errorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> paymentFailedKafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentFailedEvent> consumerFactory, DefaultErrorHandler errorHandler) {
        return listenerFactory(consumerFactory, errorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderConfirmedEvent> orderConfirmedKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderConfirmedEvent> consumerFactory, DefaultErrorHandler errorHandler) {
        return listenerFactory(consumerFactory, errorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> orderCancelledKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderCancelledEvent> consumerFactory, DefaultErrorHandler errorHandler) {
        return listenerFactory(consumerFactory, errorHandler);
    }

    private <T> ConsumerFactory<String, T> factory(KafkaProperties properties, Class<T> eventType) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<T> deserializer = new JsonDeserializer<>(eventType);
        deserializer.addTrustedPackages("NarayanGroup.example.E_Commerce.kafka.event");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    private <K, V> ConcurrentKafkaListenerContainerFactory<K, V> listenerFactory(ConsumerFactory<K, V> consumerFactory, DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<K, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}

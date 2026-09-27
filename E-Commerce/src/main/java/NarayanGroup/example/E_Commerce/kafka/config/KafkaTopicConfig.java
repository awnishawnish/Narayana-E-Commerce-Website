package NarayanGroup.example.E_Commerce.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties properties) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic paymentSuccessfulTopic(@Value("${app.kafka.topics.payment-successful:payment-successful}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedTopic(@Value("${app.kafka.topics.payment-failed:payment-failed}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderConfirmedTopic(@Value("${app.kafka.topics.order-confirmed:order-confirmed}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic orderCancelledTopic(@Value("${app.kafka.topics.order-cancelled:order-cancelled}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

}

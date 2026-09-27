package NarayanGroup.example.E_Commerce.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        // Two retries with a one-second delay. After retries are exhausted,
        // the exception is propagated so Kafka can retry according to the
        // container's configured acknowledgement/error semantics.
        return new DefaultErrorHandler(new FixedBackOff(1000L, 2L));
    }
}

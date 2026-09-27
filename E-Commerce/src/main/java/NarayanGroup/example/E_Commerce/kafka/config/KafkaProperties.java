package NarayanGroup.example.E_Commerce.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {
    private String bootstrapServers = "localhost:9092";
}

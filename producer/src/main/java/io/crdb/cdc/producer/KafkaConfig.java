package io.crdb.cdc.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic producerTopic() {
        return new NewTopic("source_table", 1, (short) 1);
    }

}

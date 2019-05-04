package com.cockroach.cdc;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class CdcApplication {

    private static final Logger log = LoggerFactory.getLogger(CdcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CdcApplication.class, args);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "json");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        return props;
    }

    @Bean
    public ConsumerFactory<String, UserTable> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(UserTable.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserTable> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserTable> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    @KafkaListener(topics = "usertable")
    public void listen(@Payload UserTable userTable, @Headers MessageHeaders headers) {

        jdbcTemplate.update("UPSERT INTO usertable (ycsb_key, field1, field2, field3, field4, field5, field6, field7, field8, field9) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                userTable.getYcsb_key(),
                userTable.getField1(),
                userTable.getField2(),
                userTable.getField3(),
                userTable.getField4(),
                userTable.getField5(),
                userTable.getField6(),
                userTable.getField7(),
                userTable.getField8(),
                userTable.getField9()
        );

        log.debug("loaded usertable into destination:  {}", userTable.toString());
    }
}

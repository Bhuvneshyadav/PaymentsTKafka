package com.example.kDemo.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.topics.payment-initiated}")
    private String paymentInitiatedTopic;
    @Value("${app.topics.payment-validated}")
    private String paymentValidatedTopic;
    @Value("${app.topics.payment-fraud-check}")
    private String paymentFraudCheckTopic;
    @Value("${app.topics.payment-authorized}")
    private String paymentAuthorizedTopic;
    @Value("${app.topics.payment-declined}")
    private String paymentDeclinedTopic;
    @Value("${app.topics.payment-notifications}")
    private String paymentNotificationsTopic;
    @Value("${app.topics.payment-dlq}")
    private String paymentDlqTopic;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Topic definitions (auto-create)
    @Bean
    public NewTopic paymentInitiatedTopic() {
        return TopicBuilder.name(paymentInitiatedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentValidatedTopic() {
        return TopicBuilder.name(paymentValidatedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFraudCheckTopic() {
        return TopicBuilder.name(paymentFraudCheckTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentAuthorizedTopic() {
        return TopicBuilder.name(paymentAuthorizedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentDeclinedTopic() {
        return TopicBuilder.name(paymentDeclinedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentNotificationsTopic() {
        return TopicBuilder.name(paymentNotificationsTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentDlqTopic() {
        return TopicBuilder.name(paymentDlqTopic).partitions(1).replicas(1).build();
    }
}

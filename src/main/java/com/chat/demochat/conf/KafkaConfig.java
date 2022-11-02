package com.chat.demochat.conf;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig
{

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap_servers;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean enable_auto_commit;

    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private long auto_commit_interval;

    @Value("${spring.kafka.consumer.group-id}")
    private String group_id;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String auto_offset_reset;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String key_deserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String value_deserializer;

    @Bean
    public Consumer getKafkaConsumer()
    {
        Properties prop = new Properties();
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, key_deserializer);
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, value_deserializer);
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, group_id);
        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enable_auto_commit);
        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, auto_offset_reset);
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(prop);
        return kafkaConsumer;
    }
}

package com.chat.demochat.component;

import com.chat.demochat.cons.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Slf4j
@Component
public class MessageConsumer
{

    @Resource
    private SessionPool sessionPool;

    @Resource
    private Consumer<String, String> consumer;

    @KafkaListener(topicPattern = Constant.SESSION_ID_PREFIX + "*", containerFactory = "kafkaListenerFactory")
    public void onConsume(ConsumerRecord<String, String> record, Acknowledgment ack) throws IOException
    {
        String topic = record.topic();
        String[] accounts = topic.replace(Constant.SESSION_ID_PREFIX, "").split("-");
        String msg = record.value();
        for (String account : accounts)
        {
            if (sessionPool.containsKey(account))
            {
                sessionPool.sendText(account, msg);
                ack.acknowledge();
            }
        }
    }

    // 消费已经消费的消息
    public void consumeBefore(String sessionId) throws IOException
    {
        String[] accounts = sessionId.replace(Constant.SESSION_ID_PREFIX, "").split("-");
        Set<TopicPartition> assignment = new HashSet<>();
        TopicPartition topicPartition = new TopicPartition(sessionId, 0);
        assignment.add(topicPartition);
        consumer.assign(assignment);
        long position = consumer.position(topicPartition);
        consumer.seek(topicPartition, position > 100 ? position - 100 : 0);
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
        while (iterator.hasNext())
        {
            ConsumerRecord<String, String> record = iterator.next();
            for (String account : accounts)
            {
                if (sessionPool.containsKey(account))
                {
                    sessionPool.sendText(account, record.value());
                }
            }
        }


    }
}

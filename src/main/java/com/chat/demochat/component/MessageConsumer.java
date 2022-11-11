package com.chat.demochat.component;

import com.alibaba.fastjson.JSON;
import com.chat.demochat.cons.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class MessageConsumer
{


    @Value("${kafka.seek.time}")
    private int kafkaSeekTime;

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
        log.info("消费[{}]历史数据", sessionId);
        String[] accounts = sessionId.replace(Constant.SESSION_ID_PREFIX, "").split("-");
        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        long fetchDataTime = System.currentTimeMillis() / 1000 - kafkaSeekTime;
        Set<TopicPartition> assignment = new HashSet<>();
        TopicPartition topicPartition = new TopicPartition(sessionId, 0);
        timestampsToSearch.put(topicPartition, fetchDataTime);
        assignment.add(topicPartition);
        consumer.assign(assignment);
        Map<TopicPartition, OffsetAndTimestamp> map = consumer.offsetsForTimes(timestampsToSearch);
        log.info("消费[{}]历史数据，消费位置[{}]", sessionId, map.get(topicPartition).offset());
        consumer.seek(topicPartition, map.get(topicPartition).offset());
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

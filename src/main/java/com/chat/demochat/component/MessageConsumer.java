package com.chat.demochat.component;

import com.chat.demochat.cons.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
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

    @KafkaListener(topicPattern = Constant.SESSION_ID_PREFIX + "*")
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
    public void consumeBefore(String account) throws IOException
    {
        log.info("查询用户[{}]过往聊天信息", account);
        Map<String, List<PartitionInfo>> topicMap = consumer.listTopics();
        for (Map.Entry<String, List<PartitionInfo>> entry : topicMap.entrySet())
        {
            String sessionId = entry.getKey();
            List<String> accounts = Arrays.asList(sessionId.replace(Constant.SESSION_ID_PREFIX, "").split("-"));
            if (accounts.contains(account))
            {
                log.info("查询聊天会话[{}]过往聊天信息", sessionId);
                Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
                long fetchDataTime = System.currentTimeMillis() - kafkaSeekTime * 1000;
                Set<TopicPartition> assignment = new HashSet<>();
                TopicPartition topicPartition = new TopicPartition(sessionId, 0);
                timestampsToSearch.put(topicPartition, fetchDataTime);
                assignment.add(topicPartition);
                consumer.assign(assignment);
                Map<TopicPartition, OffsetAndTimestamp> map = consumer.offsetsForTimes(timestampsToSearch);
                OffsetAndTimestamp offsetAndTimestamp = map.get(topicPartition);
                if (offsetAndTimestamp != null)
                {
                    log.info("消费[{}]历史数据，消费位置[{}]", sessionId, offsetAndTimestamp.offset());
                    consumer.seek(topicPartition, offsetAndTimestamp.offset());
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
                    while (iterator.hasNext())
                    {
                        ConsumerRecord<String, String> record = iterator.next();
                        sessionPool.sendText(account, record.value());
                    }
                }
            }

        }
    }
}

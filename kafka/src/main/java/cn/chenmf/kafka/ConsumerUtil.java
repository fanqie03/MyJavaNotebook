package cn.chenmf.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * @Author chenmf
 * @Date 2019/1/13 22:54
 * @Description
 * @Modified By
 */
public class ConsumerUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerUtil.class);

    /**
     * 默认接收主题
     */
    private static final String DEFAULT_TOPIC = LiteralEnum.LOG_RECORD.name();

    public static void main(String[] args){
        Properties props = new Properties();
        try {
            props.load(ConsumerUtil.class.getClassLoader().getResourceAsStream("consumer.properties"));
        } catch (IOException e) {
            logger.error("kafka消费者加载配置文件consumer.properties失败");
            e.printStackTrace();
        }

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(DEFAULT_TOPIC));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                ESUtil.save(record.value());
            }
        }

    }

    private static void init(){

    }
}

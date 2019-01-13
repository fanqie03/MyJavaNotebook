package cn.chenmf.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author chenmf
 * @Date 2019/1/13 15:42
 * @Description
 * @Modified By
 */
public class KafkaUtil {

    private final static Logger log = LoggerFactory.getLogger(KafkaUtil.class);

    /**
     * kafka配置文件，默认为resources下的`producer.properties`
     */
    private static Properties props;

    private static Producer<String, String> producer;

    /**
     * 默认发送主题
     */
    private static final String DEFAULT_TOPIC = LiteralEnum.LOG_RECORD.name();

    /**
     * 线程池
     */
    private static ExecutorService executor;


    /**
     * 进行初始化
     */
    static {
        props = new Properties();
        try {
            props.load(KafkaUtil.class.getClassLoader().getResourceAsStream("producer.properties"));
        } catch (IOException e) {
            log.error("Kafka读取配置文件失败");
            e.printStackTrace();
        }
        producer = new KafkaProducer<>(props);
        //使用系统可用的处理器数目
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * 以默认主题异步向kafka发送消息
     * 默认键为uuid
     *
     * @param value 要发送的值
     */
    public static void send(String value) {
        send(DEFAULT_TOPIC, UUID.randomUUID().toString(), value);
    }

    /**
     * 指定主题异步向kafka发送消息
     * 默认键为uuid
     *
     * @param topic 要发送的主题
     * @param value 要发送的值
     */
    public static void send(String topic, String value) {
        send(topic, UUID.randomUUID().toString(), value);
    }

    /**
     * 指定主题异步向kfka发送消息
     *
     * @param topic 要发送的主题
     * @param key   消息的键
     * @param value 消息的值
     */
    public static void send(String topic, String key, String value) {
        executor.submit(() -> producer.send(new ProducerRecord<String, String>(topic, key, value)));
    }

    /**
     * map推送的便捷方法
     * 默认key为uuid
     * 默认主题为DEFAULT_TOPIC
     *
     * @param map
     */
    public static void send(Map<String, String> map) {
        send(DEFAULT_TOPIC, UUID.randomUUID().toString(), map);
    }

    /**
     * 提供了map类进行推送
     *
     * @param topic
     * @param key
     * @param map
     */
    public static void send(String topic, String key, Map<String, String> map) {
        executor.submit(() -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String value = mapper.writeValueAsString(map);
                producer.send(new ProducerRecord<String, String>(topic, key, value));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

}

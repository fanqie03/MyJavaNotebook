package cn.chenmf.kafka;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author chenmf
 * @Date 2019/1/13 17:01
 * @Description
 * @Modified By
 */
public class KafkaUtilTest {
    private int times = 1000;

    @Test
    public void test0() {
        KafkaUtil.send("start");
    }

    @Test
    public void test1() {
        for (int i = 0; i < times; i++) {
            KafkaUtil.send("demo1");
        }
    }

    @Test
    public void test2() {
        for (int i = 0; i < times; i++) {
            KafkaUtil.send("log_record4", "demo2");
        }
    }

    @Test
    public void test3() {
        for (int i = 0; i < times; i++) {
            KafkaUtil.send("log_record4", "key", "demo3");
        }
    }

    @Test
    public void test4() {
        KafkaUtil.send("end");
    }

    @Test
    public void test5(){
        Map map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");
        map.put("key5", "value5");
        for (int i = 0; i < times; i++) {
            KafkaUtil.send(map);
        }
    }
}

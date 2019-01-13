package cn.chenmf.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author chenmf
 * @Date 2019/1/13 23:28
 * @Description
 * @Modified By
 */
public class MapperTest {
    @Test
    public void test1() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");
        map.put("key5", "value5");
        String ans = mapper.writeValueAsString(map);
        System.out.println(ans);
    }

    @Test
    public void test2(){
        System.out.println(LiteralEnum.LOG_RECORD);
    }
}

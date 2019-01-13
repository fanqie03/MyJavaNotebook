import cn.chenmf.kafka.KafkaUtil;
import org.junit.Test;

/**
 * @Author chenmf
 * @Date 2019/1/13 17:01
 * @Description
 * @Modified By
 */
public class KafkaUtilTest {
    private int times = 1000000;
    @Test
    public void test0(){
        KafkaUtil.send("start");
    }
    @Test
    public void test1(){
        for(int i = 0; i < times; i++){
            KafkaUtil.send("demo");
        }
    }

    @Test
    public void test2(){
        for(int i = 0; i < times; i++){
            KafkaUtil.send("demo");
        }
    }

    @Test
    public void test3(){
        for(int i = 0; i < times; i++){
            KafkaUtil.send("demo");
        }
    }

    @Test
    public void test4(){
        KafkaUtil.send("end");
    }
}

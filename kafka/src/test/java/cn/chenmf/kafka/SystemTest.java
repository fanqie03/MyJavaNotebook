package cn.chenmf.kafka;

import org.junit.Test;

/**
 * @Author chenmf
 * @Date 2019/1/13 16:55
 * @Description
 * @Modified By
 */
public class SystemTest {
    @Test
    public void test1(){
        System.getProperties().entrySet().forEach(x-> System.out.println(x));
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}

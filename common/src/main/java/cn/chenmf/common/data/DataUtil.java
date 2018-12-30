package cn.chenmf.common.data;

import java.util.Random;

/**
 * @Author chenmf
 * @Date 2018/12/30 11:31
 * @Description
 * @Modified By
 */
public class DataUtil {
    public static final Random RANDOM = new Random();

    public static int nextInt(int bound){
        return RANDOM.nextInt(bound);
    }

    public static int nextInt(){
        return RANDOM.nextInt();
    }
}

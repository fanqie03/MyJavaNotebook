package cn.chenmf.common.collection;

import java.util.Map;

/**
 * @Author chenmf
 * @Date 2019/1/10 22:27
 * @Description
 * @Modified By
 */
public class MapUtil {
    /**
     * 递归遍历打印map
     *
     * @param map  需要打印的map
     * @param deep 为0即可
     */
    private static void showMap(Map map, int deep) {
        if (null == map || deep < 0) {
            throw new IllegalArgumentException("参数有问题");
        }
        for (Object object : map.entrySet()) {
            Map.Entry entry = (Map.Entry) object;
            for (int i = 0; i < deep; i++) {
                System.out.print("\t");
            }
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println("Key = " + key + ", Value = " + value
                    + ", Key.class = " + key.getClass().getName() + ", Value.class = " + value.getClass().getName());
            if (entry.getValue() instanceof Map) {
                showMap((Map) entry.getValue(), deep + 1);
            }
        }
    }

    public static void showMap(Map map){
        showMap(map,0);
    }
}

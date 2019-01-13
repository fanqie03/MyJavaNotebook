package cn.chenmf.kafka;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.util.Collections;
import java.util.UUID;

/**
 * @Author chenmf
 * @Date 2019/1/13 23:05
 * @Description
 * @Modified By
 */
public class ESUtil {

    private static RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200, "http")).build();

    private static final String END_POINT="/"+LiteralEnum.DEFAULT_INDEX +"/"+LiteralEnum.DEFAULT_TYPE+"/";

    public static void save(String... json) {
        for (int i = 0; i < json.length; i++) {
            HttpEntity entity = new NStringEntity(json[i], ContentType.APPLICATION_JSON);
            restClient.performRequestAsync("PUT",
                    END_POINT+UUID.randomUUID().toString(),
                    Collections.EMPTY_MAP,
                    entity,
                    new ResponseListener() {
                        @Override
                        public void onSuccess(Response response) {
                            System.out.println("response = " + response);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            System.out.println("e = " + e);
                        }
                    });
        }
    }

}

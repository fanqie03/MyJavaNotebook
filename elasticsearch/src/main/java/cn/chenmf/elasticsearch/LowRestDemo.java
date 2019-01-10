package cn.chenmf.elasticsearch;

import cn.chenmf.common.collection.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * @Author chenmf
 * @Date 2019/1/10 20:26
 * @Description more in https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.4/_example_requests.html
 * @Modified By
 */
public class LowRestDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")).build();
        Response response = restClient.performRequest("GET", "/",
                Collections.singletonMap("pretty", "true"));

        System.out.println(EntityUtils.toString(response.getEntity()));
        //index a document
        HttpEntity entity = new NStringEntity(
                "{\n" +
                        "    \"user\" : \"kimchy\",\n" +
                        "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                        "    \"message\" : \"trying out Elasticsearch\"\n" +
                        "}", ContentType.APPLICATION_JSON);

        Response indexResponse = restClient.performRequest(
                "get",
                "/twitter/tweet/1",
                Collections.<String, String>emptyMap(),
                entity);

        System.out.println(indexResponse.getRequestLine());//GET /twitter/tweet/1 HTTP/1.1
        System.out.println(indexResponse.getStatusLine());//HTTP/1.1 200 OK
        System.out.println(indexResponse.getHost());//http://localhost:9200
        System.out.println(Arrays.toString(indexResponse.getHeaders()));//[content-type: application/json; charset=UTF-8, content-length: 194]
        System.out.println(indexResponse.getEntity());//[Content-Length: 194,Chunked: false]
        System.out.println(indexResponse);//Response{requestLine=GET /twitter/tweet/1 HTTP/1.1, host=http://localhost:9200, response=HTTP/1.1 200 OK}
//        读取内容
//        {"_index":"twitter","_type":"tweet","_id":"1","_version":21,"found":true,"_source":{    "user" : "kimchy",    "post_date" : "2009-11-15T14:12:12",    "message" : "trying out Elasticsearch"}}
        HttpEntity entity1 = indexResponse.getEntity();
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity1.getContent()));
        StringBuilder content = reader.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
        System.out.println(content.toString());


//        json转换器
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(content.toString(), Map.class);
        System.out.println(map);
        System.out.println(map.get("_source"));
        MapUtil.showMap(map);
        restClient.close();
    }

    /**
     * The following is a basic example of how async requests can be sent:
     */
    public static void async(RestClient restClient, HttpEntity... entities) throws InterruptedException {
        int numRequests = 10;
        final CountDownLatch latch = new CountDownLatch(numRequests);

        for (int i = 0; i < numRequests; i++) {
            restClient.performRequestAsync(
                    "PUT",
                    "/twitter/tweet/" + i,
                    Collections.<String, String>emptyMap(),
                    //assume that the documents are stored in an entities array
                    entities[i],
                    new ResponseListener() {
                        @Override
                        public void onSuccess(Response response) {
                            System.out.println(response);
                            latch.countDown();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            latch.countDown();
                        }
                    }
            );
        }

//wait for all requests to be completed
        latch.await();

    }
}

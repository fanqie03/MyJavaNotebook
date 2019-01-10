package cn.chenmf.elasticsearch;

import cn.chenmf.common.collection.MapUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author chenmf
 * @Date 2019/1/10 22:44
 * @Description 相关文档https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-document-get.html
 * @Modified By
 */
public class HighRestDemo {
    public static void main(String[] args) throws IOException {
        RestClient lowLevelRestClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")).build();
        RestHighLevelClient client =
                new RestHighLevelClient(lowLevelRestClient);
        GetRequest request = new GetRequest("twitter", "tweet", "1");
        try {
            GetResponse getResponse = client.get(request);
            if (getResponse.isExists()) {
                long version = getResponse.getVersion();
                String sourceAsString = getResponse.getSourceAsString();
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
                byte[] sourceAsBytes = getResponse.getSourceAsBytes();
                System.out.println(version);
                System.out.println(sourceAsString);
                MapUtil.showMap(sourceAsMap);
                System.out.println(Arrays.toString(sourceAsBytes));
            } else {

            }
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            if (e.status() == RestStatus.NOT_FOUND) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        lowLevelRestClient.close();
    }
}

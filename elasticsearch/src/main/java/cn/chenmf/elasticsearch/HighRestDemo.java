package cn.chenmf.elasticsearch;

import cn.chenmf.common.collection.MapUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.threadpool.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author chenmf
 * @Date 2019/1/10 22:44
 * @Description 相关文档https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-document-get.html
 * @Modified By
 */
public class HighRestDemo {

    private static Logger logger = LoggerFactory.getLogger(HighRestDemo.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        RestClient lowLevelRestClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")).build();
        RestHighLevelClient client =
                new RestHighLevelClient(lowLevelRestClient);

        index1(client);
        index2(client);
        index3(client);
        index4(client);
        get(client);
        bulk(client);
        bulkProcessor(client, Settings.EMPTY);

        lowLevelRestClient.close();
    }


    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-document-index.html#java-rest-high-document-index-request
     *
     * @param client
     */
    public static void index1(RestHighLevelClient client) throws IOException {
        IndexRequest indexRequest = new IndexRequest(
                "posts",
                "doc",
                "1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        indexRequest.source(jsonString, XContentType.JSON);
        System.out.println("indexRequest = " + indexRequest);
        IndexResponse indexResponse = client.index(indexRequest);
        System.out.println("indexResponse = " + indexResponse);
    }

    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-document-index.html#_providing_the_document_source
     *
     * @param client
     * @throws IOException
     */
    public static void index2(RestHighLevelClient client) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("posts", "doc", "1")
                .source(jsonMap);
        System.out.println("indexRequest = " + indexRequest);
        IndexResponse indexResponse = client.index(indexRequest);
        System.out.println("indexResponse = " + indexResponse);
    }

    /**
     * @param client
     * @throws IOException
     */
    public static void index3(RestHighLevelClient client) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.field("postDate", new Date());
            builder.field("message", "trying out Elasticsearch");
        }
        builder.endObject();
        IndexRequest indexRequest = new IndexRequest("posts", "doc", "1")
                .source(builder);
        System.out.println("indexRequest = " + indexRequest);
        IndexResponse indexResponse = client.index(indexRequest);
        System.out.println("indexResponse = " + indexResponse);
    }

    /**
     * @param client
     * @throws IOException
     */
    public static void index4(RestHighLevelClient client) throws IOException {
        IndexRequest indexRequest = new IndexRequest("posts", "doc", "1")
                .source("user", "kimchy",
                        "postDate", new Date(),
                        "message", "trying out Elasticsearch");
        System.out.println("indexRequest = " + indexRequest);
        IndexResponse indexResponse = client.index(indexRequest);
        System.out.println("indexResponse = " + indexResponse);
    }

    /**
     * get api
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-document-get.html
     *
     * @param client
     */
    public static void get(RestHighLevelClient client) {
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
                System.err.println("get 失败");
            }
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            if (e.status() == RestStatus.NOT_FOUND) {
                System.err.println("rest not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void index() {

    }

    /**
     * A BulkRequest can be used to execute multiple index, update and/or delete operations using a single request.
     *
     * @param client
     * @throws IOException
     */
    public static void bulk(RestHighLevelClient client) throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("posts", "doc", "1")
                .source(XContentType.JSON, "field", "foo"));
        request.add(new IndexRequest("posts", "doc", "2")
                .source(XContentType.JSON, "field", "bar"));
        request.add(new IndexRequest("posts", "doc", "3")
                .source(XContentType.JSON, "field", "baz"));

        request.add(new DeleteRequest("posts", "doc", "3"));
        request.add(new UpdateRequest("posts", "doc", "2")
                .doc(XContentType.JSON, "other", "test"));
        request.add(new IndexRequest("posts", "doc", "4")
                .source(XContentType.JSON, "field", "baz"));


//        Timeout to wait for the bulk request to be performed as a TimeValue
        request.timeout(TimeValue.timeValueMinutes(2));
//        Timeout to wait for the bulk request to be performed as a String
        request.timeout("2m");

//        Refresh policy as a WriteRequest.RefreshPolicy instance
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
//        Refresh policy as a String
        request.setRefreshPolicy("wait_for");

//        Sets the number of shard copies that must be active before proceeding with the index/update/delete operations.
        request.waitForActiveShards(2);
//        Number of shard copies provided as a ActiveShardCount: can be ActiveShardCount.ALL, ActiveShardCount.ONE or ActiveShardCount.DEFAULT (default)
        request.waitForActiveShards(ActiveShardCount.ALL);

//        同步执行
        BulkResponse bulkResponse = client.bulk(request);
//        异步执行
//        client.bulkAsync(request, new ActionListener<BulkResponse>() {
//            @Override
//            public void onResponse(BulkResponse bulkResponse) {
//                System.out.println("bulkResponse = " + bulkResponse);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                System.out.println("e = " + e);
//            }
//        });

//        回应
//        测试是有有失败任务
        if (bulkResponse.hasFailures()) {
            System.out.println("有任务失败");
            System.out.println("bulkResponse = " + bulkResponse);
        }
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
//            Retrieve the response of the operation (successful or not),
//             can be IndexResponse, UpdateResponse or DeleteResponse
//             which can all be seen as DocWriteResponse instances
            DocWriteResponse itemResponse = bulkItemResponse.getResponse();
//            Handle the response of an index operation
            if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                    || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                IndexResponse indexResponse = (IndexResponse) itemResponse;
                System.out.println("indexResponse = " + indexResponse);
            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                System.out.println("updateResponse = " + updateResponse);
            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                System.out.println("deleteResponse = " + deleteResponse);
            }
//            失败
            if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                System.out.println("failure = " + failure);
            }
        }
    }

    /**
     * 批量处理器
     *
     * @param client
     * @param settings
     * @throws InterruptedException
     */
    public static void bulkProcessor(RestHighLevelClient client, Settings settings) throws InterruptedException {

        ThreadPool threadPool = new ThreadPool(settings);

        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                int numberOfActions = request.numberOfActions();
                logger.debug("Executing bulk [{}] with {} requests", executionId, numberOfActions);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {

                if (response.hasFailures()) {
                    logger.warn("Bulk [{}] executed with failures", executionId);
                } else {
                    logger.debug("Bulk [{}] completed in {} milliseconds", executionId, response.getTook().getMillis());
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.error("Failed to execute bulk", failure);
            }
        };

        BulkProcessor.Builder builder = new BulkProcessor.Builder(client::bulkAsync, listener, threadPool);
//        Set when to flush a new bulk request based on the number of actions currently added (defaults to 1000, use -1 to disable it)
        builder.setBulkActions(500);
//        Set when to flush a new bulk request based on the size of actions currently added (defaults to 5Mb, use -1 to disable it)
        builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
//        Set the number of concurrent requests allowed to be executed (default to 1, use 0 to only allow the execution of a single request)
        builder.setConcurrentRequests(0);
//        Set a flush interval flushing any BulkRequest pending if the interval passes (defaults to not set)
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));
//        Set a constant back off policy that initially waits for 1 second and retries up to 3 times.
//        See BackoffPolicy.noBackoff(), BackoffPolicy.constantBackoff() and BackoffPolicy.exponentialBackoff() for more options.
        builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3));

        BulkProcessor bulkProcessor = builder.build();

        IndexRequest one = new IndexRequest("posts", "doc", "1").
                source(XContentType.JSON, "title", "In which order are my Elasticsearch queries executed?");
        IndexRequest two = new IndexRequest("posts", "doc", "2")
                .source(XContentType.JSON, "title", "Current status and upcoming changes in Elasticsearch");
        IndexRequest three = new IndexRequest("posts", "doc", "3")
                .source(XContentType.JSON, "title", "The Future of Federated Search in Elasticsearch");

//        这些请求将由BulkProcessor执行，BulkProcessor负责为每个批量请求调用BulkProcessor.Listener。
        bulkProcessor.add(one);
        bulkProcessor.add(two);
        bulkProcessor.add(three);

//        The awaitClose() method can be used to wait until all requests have been processed
//        or the specified waiting time elapses:
//        如果所有批量请求都已完成，则该方法返回true;
//        如果在所有批量请求完成之前等待时间已过，则返回false
        boolean terminated = bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);

//        The close() method can be used to immediately close the BulkProcessor:
//        bulkProcessor.close();
    }

}

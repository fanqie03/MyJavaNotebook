package cn.chenmf.elasticsearch;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

/**
 * @Author chenmf
 * @Date 2019/1/1 12:40
 * @Description 对elasticsearch文档CRUD操作的api demo
 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.4/java-docs.html
 * @Modified By
 */
public class DocumentDemo {

    private final static Logger logger = LoggerFactory.getLogger(DocumentDemo.class);

    public static void main(String[] args) throws IOException {
        logger.info("on start");

//        配置方式1. 使用java api进行配置 2. 使用配置文件，支持json和yaml 3. 默认配置Settings.EMPTY
        Settings settings = Settings.builder()
//                设置集群名称
//                .put("cluster.name", "docker-cluster")
                //客户端开启集群嗅探功能，通过集群内节点自动获取集群整个节点
                .put("client.transport.sniff", true)
                //其他配置项 https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.4/transport-client.html
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

//        CRUD api,仅针对单索引，使用client
//        生成json文档的方法 https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.4/java-docs-index.html#java-docs-index-thread
//        支持1. byte[] or String 2. Map 3. 使用jackson序列化bean 4. 使用XContentFactory Builder创建
        XContentBuilder doc = XContentFactory.jsonBuilder()
                .startObject()
                .field("user", "kimchy")
                .field("postDate", new Date())
                .field("message", "trying out Elasticsearch")
                .endObject();
        logger.info("Index：{}", doc.string());
//        Index操作
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
                .setSource(doc).get();
        logger.info("Index结果：{}", response.toString());

//        get
        GetResponse getResponse = client.prepareGet("twitter", "tweet", "1")
//                false为使用调用线程去执行
                .setOperationThreaded(false)
                .get();
        logger.info("Get结果：{}", getResponse.toString());

//        delete
//        DeleteResponse deleteResponse = client.prepareDelete("twitter", "tweet", "1").get();
//        logger.info("delete结果：{}", deleteResponse);

//        delete by query

//        BulkByScrollResponse bbsr = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
////                过滤query
//                .filter(QueryBuilders.matchQuery("gender","male"))
////                要删除的索引
//                .source("persons")
////                执行操作返回结果
//                .get();
//        logger.info("delete by query:{}",bbsr);


//        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
//                .filter(QueryBuilders.matchQuery("user", "kimchy"))
//                .source("twitter")
////                添加监听器
//                .execute(new ActionListener<BulkByScrollResponse>() {
//                    @Override
//                    public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
//                        logger.info("删除成功：{}", bulkByScrollResponse);
//                    }
//
//                    @Override
//                    public void onFailure(Exception e) {
//                        logger.info("删除失败");
//                    }
//                });

//        update
        UpdateResponse updateResponse = client.prepareUpdate()
                .setIndex("twitter")
                .setType("tweet")
                .setId("1")
                .setDoc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("gender", "male")
                        .endObject())
                .get();
        logger.info("update:{}", updateResponse);

//        bulk
        BulkResponse bulkResponse = client.prepareBulk()
                .add(client.prepareIndex("twitter", "tweet", "2")
                        .setSource(XContentFactory.jsonBuilder()
                                .startObject()
                                .field("user", "chenmf")
                                .field("postDate", new Date())
                                .field("message", "trying bulk")
                                .endObject()))
                .add(client.prepareIndex("twitter", "tweet", "3")
                        .setSource(XContentFactory.jsonBuilder()
                                .startObject()
                                .field("user", "kkk")
                                .field("postDate", new Date())
                                .field("message", "trying bulk")
                                .endObject()))
                .get();
        logger.info("bulk response:{}", bulkResponse.status());
        if (bulkResponse.hasFailures()) {
            logger.info("some bulk failed");
        }

//        BulkProcessor类提供了一个简单的接口，可根据请求的数量或大小自动刷新批量操作，或者在给定时间段之后。
//        https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.4/java-docs-bulk-processor.html
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {
                        logger.info("beforeBulk executionId:{} request:{}", executionId, request);
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                        logger.info("afterBulk executionId:{} request:{} response:{}", executionId, request, response);
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                        logger.info("afterBulk executionId:{} request:{} failure:{}", executionId, request, failure);
                    }
                })
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        // Add your requests
        bulkProcessor.add(new IndexRequest("twitter", "tweet", "1").source(doc));
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));
        // Flush any remaining requests
        bulkProcessor.flush();

        // Or close the bulkProcessor if you don't need it anymore
        bulkProcessor.close();

        logger.info("on shutdown");
        client.close();
    }
}

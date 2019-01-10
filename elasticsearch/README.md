# Elasticsearch

## 基本概念

与SQL的对应关系（旧版本，type在6.0后被抛弃）：

| elasticsearch    | SQL        |
| ---------------- | ---------- |
| cluster          | 关系数据库 |
| Index            | 数据库     |
| type(deprecated) | 表         |
| document         | 行         |
| field            | 列         |

与sql的对应关系

| elasticsearch               | SQL                               |
| --------------------------- | --------------------------------- |
| cluster federated(多个集群) | cluster                           |
| cluster instance(一个集群)  | 数据库或目录(database or catalog) |
| *implicit*                  | 模式(schema)                      |
| index                       | 表(table)                         |
| document                    | 行(row)                           |
| field                       | 列(column)                        |

### 集群

集群是多个节点的集合，默认表示名为“elasticsearch”，设置相同的名字可以自动加入。

### 节点

存储数据，参与索引和搜索，默认分配随机UUID。默认情况下，每个节点都设置为加入名为elasticsearch的群集，并自动加入。

### 索引

索引是具有某些类似特征的文档集合。索引由名称标识（必须全部小写），此名称用于在对其中的文档执行索引，搜索，更新和删除操作时引用索引。

例如，您可以拥有客户数据的索引，产品目录的另一个索引以及订单数据的另一个索引。

### 文档

文档是可以编辑的基本信息单元。该文档以JSON存储。如员工信息index中的一名员工可以作为一个document存储。

```json
{
    "name":             "Zhang San",
    "age":              26,
    "on_board_date":    "2015-10-31",
    "school":           "Nanjing University"
}
```

### 分片（shards）和复制（replicas）

将索引细分成多个`分片`，创建索引时只需要指定分片数目，每个分片都是一个独立的索引。

`分片`和`复制`可提高索引吞吐量。

`复制`提供高可用，*为0表示没有副本*。

创建索引后，您可以随时**动态更改副本数**，但**不能在更改分片数**。

总结：一个索引可以有多个分片，一个分片可以有多个副本。

## REST API

格式为`<REST Verb> /<Index>/<Type>/<ID>`

| REST Verb | 意思             | 例子                   |
| --------- | ---------------- | ---------------------- |
| GET       |                  | GET    /customer/doc/1 |
| DELETE    |                  | DELETE /customer       |
| POST      | 修改(没有则添加) | POST   /customer/doc/1 |
| PUT       | 创建/添加        | PUT   /customer        |

| 指令             | 例子                              |
| ---------------- | --------------------------------- |
| _cat             | GET  /_cat/health?v               |
| _update          | POST /customer/doc/1/_update      |
| _delete_by_query | POST /twitter/_delete_by_query    |
| _update_by_query | POST /twitter/_update_by_query    |
| _bulk            | POST /customer/doc/_bulk          |
| _search          | GET  /bank/_search                |
| _mget            | GET  /_mget                       |
| _reindex         | POST /_reindex                    |
| _termvectors     | GET  /twitter/_doc/1/_termvectors |
| _mtermvectors    | POST /_mtermvectors               |

### 集群状态

1. 整体情况`GET /_cat/health?v`
2. 单个服务器`GET /_cat/nodes?v`

#### health级别

- Green - everything is good (cluster is fully functional)
- Yellow - all data is available but some replicas are not yet allocated (cluster is fully functional)
- Red - some data is not available for whatever reason (cluster is partially functional)

### 显示索引

- `GET /_cat/indices?v`

  ```text
  health status index    uuid                   pri rep docs.count docs.deleted store.size pri.store.size
  ```

  | 名称           | 意思       |
  | -------------- | ---------- |
  | health         |
  | status         |
  | index          | 索引名称   |
  | uuid           | 唯一标识符 |
  | pri            | 分片数目   |
  | rep            | 复制品数目 |
  | docs.count     | 文档数目   |
  | docs.deleted   |
  | store.size     |
  | pri.store.size |

### 创建索引

- `PUT /customer?pretty` --创建一个名叫“customer”的索引，`pretty`表示格式化返回结果

  ```text
  health status index    uuid                   pri rep docs.count docs.deleted store.size pri.store.size
  yellow open   customer 95SQ4TSUT7mWBT7VNHH67A   5   1          0            0       260b           260b
  ```

### 添加和查询文档

- 对customer添加id为1值为xxx的数据

  ```url
  PUT /customer/doc/1?pretty
  {
    "name": "John Doe"
  }
  ```

- 不指定id添加数据

  ```shell
  curl -X POST "localhost:9200/customer/doc/1/_update?pretty" -H 'Content-Type: application/json' -d'
  {
    "doc": { "name": "Jane Doe" }
  }
  '
  ```

- `GET /customer/doc/1?pretty`

### 删除索引

- 删除整个索引

  ```url
  DELETE /customer?pretty
  GET /_cat/indices?v
  ```

- 删除其中一部分

  ```url
  DELETE /customer/doc/2?pretty
  ```

[`_delete_by_query`](https://www.elastic.co/guide/en/elasticsearch/reference/6.0/docs-delete-by-query.html)

### 更新文档

```url
POST /customer/doc/1/_update
```

[`_update_by_query`](https://www.elastic.co/guide/en/elasticsearch/reference/6.0/docs-update-by-query.html)

## [Java Api](https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.4/java-api.html)

- 异步，返回未来结果
- 核心是`Client` - `org.elasticsearch.client`；实现类为`TransportClient` - `org.elasticsearch.client.transport`；

## Mapping

类似于sql中的表结构的定义，可以用于定义index和field。

每个索引都有一种映射类型，用于确定文档的索引方式。

查看mapping`GET /[index_name]/_mapping`

```json
{
  // 索引
  "twitter" : {
    // 该索引的mappings，相当于schema
    "mappings" : {
      // type
      "_doc" : {
        // type下的字段
        "properties" : {
          "email" : {"type" : "keyword"}
        }
      }
    }
  }
}
```

## Field

### [数据类型](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html)

- 核心
  - string：text，keyword
  - number：long，integer，short, byte, double, float, half_float, scaled_float
  - date
  - boolean
  - binary
  - range：integer_range, float_range, long_range, double_range, date_range
- 复杂
  - array
  - object：单一json对象
  - nested：多个json对象

## 聚合

### bucketing

### metric

### matrix

### pipeline
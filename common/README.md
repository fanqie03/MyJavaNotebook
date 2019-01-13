## [Jackson学习](https://www.cnblogs.com/kakag/p/5054772.html)

### Jackson fasterxml 的结构

- `jackson-core`: 核心包
- `jackson-annotations` : 注解包
- `jackson-databind` : 数据绑定（依赖 core 和 annotations）

### 使用方式

Jackson 提供了三种 json 处理方式：

- `Streaming API` : 其他两种方式都依赖于它而实现，如果要从底层细粒度控制 json 的解析生成，可以使用这种方式;
- `Tree Model` : 通过基于内存的树形结构来描述 json 数据。json 结构树由 JsonNode 组成。不需要绑定任何类和实体，可以方便的对 JsonNode 来进行操作。
- `Data Binding` : 最常用的方式，基于属性的 get 和 set方法以及注解来实现 JavaBean 和 json 的互转，底层实现还是 Streaming API.
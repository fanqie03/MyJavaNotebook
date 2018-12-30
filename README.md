# JavaNotebook

Ready to conclude java and learn more.

## 关于Maven

- 父子模块：子模块继承父模块的属性

```xml
<!-- 父模块添加module属性 -->
<modules>
    <module>javaee</module>
    <module>javase</module>
    <module>common</module>
</modules>
<!-- 子模块声明parent -->
<parent>
    <groupId>cn.chenmf</groupId>
    <artifactId>MyJavaNotebook</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>
```

- 模块间相互调用

```xml
<!--要调用的模块直接声明需要依赖的子模块的坐标-->
<dependencies>
    <dependency>
        <groupId>cn.chenmf</groupId>
        <artifactId>common</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

- 声明版本为jdk1.8版本

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```
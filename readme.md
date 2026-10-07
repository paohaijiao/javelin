<p align="center">
  <img src="./images/jquick-logo.svg" width="680" alt="jquick-pdf logo" />
</p>

<h1 align="center">javelin</h1>

<p align="center">
  A lean, high-performance Java foundation framework — essential infrastructure components without the bloat.
</p>

<p align="center">
  <b>简体中文</b> | <a href="./readme-en.md">English</a>
</p>
<p align="center">
  🌐 <a href="https://www.jquick.org">JQuick Website</a> ·
  📖 <a href="https://github.com/paohaijiao">GitHub</a> ·
  📦 <a href="https://central.sonatype.com/artifact/io.github.paohaijiao/javelin">Maven Central</a>
</p>
<p align="center">
 <a href="https://central.sonatype.com/artifact/io.github.paohaijiao/javelin"><img src="https://img.shields.io/maven-central/v/io.github.paohaijiao/javelin.svg?style=for-the-badge&label=Maven%20Central" alt="Maven Central" /></a>
 <a href="https://github.com/paohaijiao/javelin/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-Apache--2.0-blue.svg?style=for-the-badge" alt="License" /></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-8%2B-orange.svg" alt="Java 8+" />
</p>

---

# javelin - 轻量级Java基础框架

简体中文 | [英文](./readme-en.md)

javelin - 轻量级 Java 基础框架

## 目录

- [第一章：概述](#第一章概述)
    - [核心模块](#核心模块)
- [第二章：快速开始](#第二章快速开始)
    - [要求](#要求)
    - [Maven 依赖](#maven-依赖)
- [第三章：核心](#第三章核心)
    - [JEvaluator 函数参考](#jevaluator-函数参考)
        - [类型转换函数](#类型转换函数)
            - [toInteger](#1-tointeger-函数)
            - [toDouble](#2-todouble-函数)
            - [toFloat](#3-tofloat-函数)
            - [toString](#4-tostring-函数)
            - [parseToDate](#5-parsetodate-函数)
        - [数学函数](#数学函数)
            - [ceil](#1-ceil-函数)
            - [floor](#2-floor-函数)
            - [round](#3-round-函数)
            - [sum](#4-sum-函数)
            - [max](#5-max-函数)
            - [min](#6-min-函数)
            - [avg](#7-avg-函数)
        - [字符串函数](#字符串函数)
            - [toLower](#1-tolower-函数)
            - [toUpper](#2-toupper-函数)
            - [contains](#3-contains-函数)
            - [join](#4-join-函数)
            - [split](#5-split-函数)
            - [substring](#6-substring-函数)
            - [replace](#7-replace-函数)
            - [startsWith](#8-startswith-函数)
            - [endsWith](#9-endswith-函数)
        - [日期函数](#日期函数)
            - [dateFormat](#1-dateformat-函数)
        - [集合函数](#集合函数)
            - [length](#1-length-函数)
            - [trans](#2-trans-函数)
        - [✨ 自定义函数](#自定义函数)
    - [树函数参考](#树函数参考)
        - [构建树](#构建树)
        - [通过自定义方法访问树](#通过自定义方法访问树)
- [第四章：资源](#第四章资源)
    - [文件加载](#加载文件到字符串)
    - [配置文件加载](#加载-spring-配置文件)
- [第五章：提供者](#第五章提供者)
- [第六章：扫描](#第六章扫描)
- [第七章：事件](#第七章事件)
    - [事件服务](#1-定义一个-eventservice)
    - [事件定义](#2-定义一个-event)
    - [事件发布](#3-发布一个事件)
- [第八章：国际化（i18n）](#第八章国际化i18n)
    - [资源文件约定](#1-资源文件约定)
    - [调用方式](#2-调用方式)
    - [查找顺序](#3-查找顺序)
    - [切换语言](#4-切换语言)
    - [全局配置](#5-全局配置)
    - [SPI 扩展点](#6-spi-扩展点)
    - [注意事项](#7-注意事项)

## 第一章：概述

```string
javelin 是一个精简、高性能的 Java 框架，旨在以最小的开销加速后端开发。
以其速度和精确性命名，javelin 提供了必要的基础设施组件，而无需臃肿的全栈解决方案。
```

---

| Module       | Description                     |
|--------------|---------------------------------|
| **Core**     | Lightweight DI container        |
| **Resource** | Enhanced resource management    |
| **Provider** | Type-safe configuration binding |
| **Scan**     | Automatic component detection   |
| **Event**    | Pub/sub event system            |
| **MyBatis**  | Simplified MyBatis integration  |

## 第二章：快速开始

### 环境要求

- Java 8
- Maven/Gradle

```xml
<!-- Maven -->
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>javelin</artifactId>
    <version>${version}</version>
</dependency>
```

## 第三章：核心功能

### JEvaluator函数参考

#### 基础用法

```java
List<Object> args = new ArrayList<>();
args.add(argument1);
args.add(argument2);
Object result = JEvaluator.evaluateFunction(JMethodEnums.[functionName].getMethod(), args);
```

## 📊 类型转换函数

| Function      | Syntax                    | Parameters   | Return Type | Description               |
|---------------|---------------------------|--------------|-------------|---------------------------|
| `toInteger`   | `toInteger(value)`        | 1 (any type) | Integer     | Converts value to Integer |
| `toDouble`    | `toDouble(value)`         | 1 (any type) | Double      | Converts value to Double  |
| `toFloat`     | `toFloat(value)`          | 1 (any type) | Float       | Converts value to Float   |
| `toString`    | `toString(value)`         | 1 (any type) | String      | Converts value to String  |
| `parseToDate` | `parseToDate(str,format)` | 2 (String)   | Date        | Parses string to Date     |

1. toInteger function

```java      
  List<Object> args = new ArrayList<>();
        args.add("1");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toInteger.getMethod(), args);
        System.out.println(result); 
```

2. toDouble function

```java   
  List<Object> args = new ArrayList<>();
        args.add(1.5);
        Object result1 = JEvaluator.evaluateFunction(JMethodEnums.toDouble.getMethod(), args);
```

3. toFloat

```java   
  List<Object> args = new ArrayList<>();
        args.add(1.5);
        Object result1 = JEvaluator.evaluateFunction(JMethodEnums.toFloat.getMethod(), args);
```

4. toString

```java
     List<Object> args = new ArrayList<>();
        args.add(1.5);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toString.getMethod(), args);
```

5. parseToDate

```java   
        List<Object> args = new ArrayList<>();
        args.add("2019-04-25 16:23:23");
        args.add("yyyy-MM-dd HH:mm:ss");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.parseToDate.getMethod(), args);
        System.out.println(result);
```

## 🔢 数学函数

| Function | Syntax              | Parameters          | Return Type | Description                    |
|----------|---------------------|---------------------|-------------|--------------------------------|
| `ceil`   | `ceil(number)`      | 1 (Number)          | Double      | Rounds up to nearest integer   |
| `floor`  | `floor(number)`     | 1 (Number)          | Double      | Rounds down to nearest integer |
| `round`  | `round(num,digits)` | 2 (Number, Integer) | Double      | Rounds to specified decimals   |
| `sum`    | `sum(values...)`    | ≥1 (Numbers)        | Number      | Sums all arguments             |
| `max`    | `max(values...)`    | ≥1 (Numbers)        | Number      | Returns maximum value          |
| `min`    | `min(values...)`    | ≥1 (Numbers)        | Number      | Returns minimum value          |
| `avg`    | `avg(values...)`    | ≥1 (Numbers)        | Double      | Calculates average             |

1. ceil function

```java   
List<Object> args = new ArrayList<>();
args.add(1.5);
Object ceil= JEvaluator.evaluateFunction(JMethodEnums.ceil.getMethod(), args); 
```

2. floor function

```java   
        List<Object> args = new ArrayList<>();
        args.add(1.5);
        Object floor= JEvaluator.evaluateFunction(JMethodEnums.floor.getMethod(), args); 
```

3. round function

```java   
        List<Object> args1 = new ArrayList<>();
        args1.add(1.5321321312);
        args1.add(2);
        Object round= JEvaluator.evaluateFunction(JMethodEnums.round.getMethod(), args1);
        System.out.println(result);
```

4. sum function

```java
        List<Object> args = new ArrayList<>();
        args.add(10);
        args.add(11);
        args.add(12);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.sum.getMethod(), args);
        System.out.println(result);
```

5. max function

```java
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        args.add(4);
        args.add(5);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.max.getMethod(), args);
        System.out.println(result); 
```

6. min function

```java
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        args.add(4);
        args.add(5);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.min.getMethod(), args);
        System.out.println(result); 
```

7. avg function

```java
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        args.add(4);
        args.add(5);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.avg.getMethod(), args);
        System.out.println(result); 
```

## 🔤 字符串函数

| Function     | Syntax                     | Parameters           | Return Type | Description                  |
|--------------|----------------------------|----------------------|-------------|------------------------------|
| `toLower`    | `toLower(str)`             | 1 (String)           | String      | Converts to lowercase        |
| `toUpper`    | `toUpper(str)`             | 1 (String)           | String      | Converts to uppercase        |
| `contains`   | `contains(str,substr)`     | 2 (String)           | Boolean     | Checks if contains substring |
| `join`       | `join(delimiter,items...)` | ≥2 (String, Objects) | String      | Joins with delimiter         |
| `split`      | `split(str,delimiter)`     | 2 (String)           | String[]    | Splits string by delimiter   |
| `substring`  | `substring(str,start,end)` | 3 (String, int, int) | String      | Extracts substring           |
| `replace`    | `replace(str,target,rep)`  | 3 (String)           | String      | Replaces all occurrences     |
| `startsWith` | `startsWith(str,prefix)`   | 2 (String)           | Boolean     | Checks string prefix         |
| `endsWith`   | `endsWith(str,suffix)`     | 2 (String)           | Boolean     | Checks string suffix         |

1. toLower function

```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toLower.getMethod(), args);
        System.out.println(result); 
```

2. toUpper function

```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toUpper.getMethod(), args);
        System.out.println(result); 
```

3. contains function

```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hello");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.contains.getMethod(), args);
        System.out.println(result);
 ```

4. join function

```java
        List<Object> args = new ArrayList<>();
        List<String> items = new ArrayList<>();
        items.add("12344");
        items.add("12345");
        args.add(items);
        args.add(",");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.join.getMethod(), args);
        System.out.println(result);
```

5. split function

```java
   List<Object> args = new ArrayList<>();
   args.add("123,12344");
   args.add(",");
   Object result = JEvaluator.evaluateFunction(JMethodEnums.split.getMethod(), args);
   System.out.println(result); 
```

6. substring function

```java
        List<Object> args = new ArrayList<>();
        args.add("substring");
        args.add(1);
        args.add(3);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.substring.getMethod(), args);
        System.out.println(result);
```

7. replace function

```java
        List<Object> args = new ArrayList<>();
        args.add("replace");
        args.add("ep");
        args.add("2345");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.replace.getMethod(), args);
        System.out.println(result); 
```

8. startsWith function

```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hel1lo");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.startsWith.getMethod(), args);
        System.out.println(result);
```

9. endsWith function

```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.endsWith.getMethod(), args);
        System.out.println(result);
```

## 📅 日期函数

| Function     | Syntax                    | Parameters       | Return Type | Description            |
|--------------|---------------------------|------------------|-------------|------------------------|
| `dateFormat` | `dateFormat(date,format)` | 2 (Date, String) | String      | Formats date to string |

1. dateFormat function

```java
        List<Object> args = new ArrayList<>();
        args.add(new Date());
        args.add("yyyy-MM-dd HH:mm:ss");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.dateFormat.getMethod(), args);
        System.out.println(result);
```

## ✨ 集合函数

| Function | Syntax            | Parameters  | Return Type | Description               |
|----------|-------------------|-------------|-------------|---------------------------|
| `length` | `length(array)`   | 1 (Array)   | Integer     | Returns array/list length |
| `trans`  | `trans(src,dest)` | 2 (Objects) | Object      | Transforms between types  |

1. length function

```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.length.getMethod(), args);
        System.out.println(result);
```

2. trans function

```java
        JContext contextParams = new JContext();
        contextParams.put("1","男");
        contextParams.put("2","女");
        List<Object> args = new ArrayList<>();
        args.add(contextParams);
        args.add("1");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.trans.getMethod(), args);
        System.out.println(result);
```

## 📦 自定义函数(插件函数)

```java
        JEvaluator.registerFunction("daysBetween", (BiFunction<Object, Object, Object>) (date1, date2) -> {
            long diff = ((Date) date2).getTime() - ((Date) date1).getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        });
        Date today = new Date();
        Date nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
        Object result = JEvaluator.evaluateFunction("daysBetween", Arrays.asList(today,nextWeek));
        System.out.println(result);
```

## 树型数据构建

1. build tree

```java
     List<JDept> deptList = new ArrayList<>();
        deptList.add(new JDept(1L, 0L, "总公司"));
        deptList.add(new JDept(2L, 1L, "技术部"));
        deptList.add(new JDept(3L, 1L, "市场部"));
        deptList.add(new JDept(4L, 2L, "后端组"));
        deptList.add(new JDept(5L, 2L, "前端组"));
        List<JDept> tree = JTreeUtil.build(deptList, 0L);
        List<JDept> flattenList = JTreeUtil.flatten(tree, JDept::getChildren);
        JDept node = JTreeUtil.findNode(tree, 2L, JDept::getId, JDept::getChildren);
        System.out.println(node);
```

2. 通过内置函数获取树节点

```java
    List<JDept> deptList = new ArrayList<>();
        deptList.add(new JDept(1L, 0L, "总公司"));
        deptList.add(new JDept(2L, 1L, "技术部"));
        deptList.add(new JDept(3L, 1L, "市场部"));
        deptList.add(new JDept(4L, 2L, "后端组"));
        deptList.add(new JDept(5L, 2L, "前端组"));
        deptList.add(new JDept(6L, 4L, "程序员小李"));
        deptList.add(new JDept(7L, 4L, "程序员小网"));
        deptList.add(new JDept(8L, 5L, "售后1"));
        deptList.add(new JDept(9L, 5L, "售后2"));
        deptList.add(new JDept(10L, 6L, "程序员小李的孩子1"));
        deptList.add(new JDept(11L, 6L, "程序员小李的孩子2"));
        List<JDept> tree = JTreeUtil.build(deptList, 0L);
        Map<Long, JDept> nodeMap = deptList.stream().collect(Collectors.toMap(JDept::getId, dept -> dept));
        // 获取所有子节点
        JDept techDept = JTreeUtil.findNode(tree, 4L, JDept::getId, JDept::getChildren);
        List<JDept> allChildren = JTreeUtil.getAllChildren(techDept, JDept::getChildren);
        System.out.println("技术部所有子部门: " + allChildren.stream().map(JDept::getName).collect(Collectors.toList()));
        // 获取直接子节点
        List<JDept> directChildren = JTreeUtil.getDirectChildren(techDept, JDept::getChildren);
        System.out.println("技术部直接子部门: " + directChildren.stream().map(JDept::getName).collect(Collectors.toList()));
        // 获取所有父节点（从近到远）
        JDept backendGroup = JTreeUtil.findNode(tree, 4L, JDept::getId, JDept::getChildren);
        List<JDept> parents = JTreeUtil.getParents(backendGroup, null, JDept::getId, JDept::getParentId, nodeMap, false);
        System.out.println("后端组的上级部门: " + parents.stream().map(JDept::getName).collect(Collectors.toList()));
        // 获取兄弟节点
        List<JDept> siblings = JTreeUtil.getSiblings(backendGroup, null, JDept::getId, JDept::getParentId, JDept::getChildren, nodeMap, false);
        System.out.println("后端组的兄弟部门: " + siblings.stream().map(JDept::getName).collect(Collectors.toList()));
```

## 第四章：资源加载

1. 文件加载

```java
        JReader fileReader = new JFileReader("data/rule.txt");
        JAdaptor context = new JAdaptor(fileReader);
        System.out.println(context.getRuleContent());
```

2. 配置文件加载

```java
@Test
public void test() throws IOException {
JEnvironmentAware configLoader = new JEnvironmentAware();
System.out.println("Dev Environment:");
printConfigs(configLoader);
configLoader.setActiveProfile("prod");
System.out.println("\nProd Environment:");
printConfigs(configLoader);
}
private static void printConfigs(JEnvironmentAware configLoader) {
System.out.println("App Name: " + configLoader.getProperty("app.name"));
System.out.println("DB URL: " + configLoader.getProperty("database.url"));
System.out.println("DB Username: " + configLoader.getProperty("database.username"));
System.out.println("DB Pool Size: " + configLoader.getProperty("database.pool-size"));
}
```

## 第四章：bean 的加载

```java
Properties config = new Properties();
config.setProperty("bean.container.mode", "simple"); // 或 "simple"
JBeanProvider container = JBeanProviderFactory.createProvider(config);
JBeanDefinitionModel serviceDef = new JBeanDefinitionModel(ProviderUserServiceImpl.class);
container.registerBeanDefinition("myService", serviceDef);
if (container instanceof JProxyEnhancedBeanProvider) {
container.registerInterceptor("myService", invocation -> {
System.out.println("拦截方法: " + invocation.getMethod().getName());
long start = System.currentTimeMillis();
try {
return invocation.proceed();
} finally {
System.out.println("方法执行耗时: " + (System.currentTimeMillis() - start) + "ms");
}
});
}
ProviderUserService service = container.getBean("myService", ProviderUserService.class);
ProviderUserService service1 = container.getBean(ProviderUserService.class);
service.sayHello("haha");
service1.sayHello("haha1");
```

## 第六章 : bean 扫描

```java
        JAnnotationConfigApplicationContext context =
                new JAnnotationConfigApplicationContext("com.github.paohaijiao");
        JUserRule userService = context.getBean("jUserRule", JUserRule.class);
        System.out.println(userService.findUser(1L));
```

## 第七章：事件系统

#### 1.定义事件服务

```java
@JComponent
public class ParentEventService {
private boolean parentEventReceived = false;
private String lastParentMessage;

    @JEventListener
    public void handleParentEvent(JunitTest.ParentTestEvent event) {
        this.parentEventReceived = true;
        this.lastParentMessage = event.getMessage();
    }

    public boolean isParentEventReceived() {
        return parentEventReceived;
    }

    public String getLastParentMessage() {
        return lastParentMessage;
    }
}
```

#### 2. 定义一个事件

```java
    public static class AnotherTestEvent extends JApplicationEvent {
        public AnotherTestEvent(Object source, String message) {
            super(source);
        }
    }


    public static class ParentTestEvent extends JApplicationEvent {
        private final String message;

        public ParentTestEvent(Object source, String message) {
            super(source);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ChildTestEvent extends ParentTestEvent {
        public ChildTestEvent(Object source, String message) {
            super(source, message);
        }
    }
```

#### 3. 发布事件

```java
 JEventSupportedApplicationContext context = new JEventSupportedApplicationContext("com.github.paohaijiao.test");
 System.out.println("Registered beans: " );
 ParentEventService service = context.getBean("parentEventService", ParentEventService.class);
 context.publishEvent(new AnotherTestEvent(context, "Child Message"));
```



## 第八章：国际化（i18n）

javelin 的 i18n 采用与 `Messages` 一致的 **包级资源包 + 逐级回溯** 模型：

- 资源文件按 Java 包存放，**同一包下所有类共享同一个 bundle**；
- `key` 是人工手写的业务语义字符串，**不会根据类名、字段名自动拼接生成**；
- 查找时从 `refClass` 所在包开始，**就近优先**，命中即返回；未命中则逐级向上回溯父包，最后落到全局兜底 bundle；
- 任何层级都找不到时返回 `key` 原文并打印 DEBUG 日志，**不抛异常**。

### 1. 资源文件约定

在 `src/main/resources` 下按包路径建目录，放 `messages_{locale}.properties`：

```text
src/main/resources/
└── com/github/paohaijiao/util/
    ├── messages_zh_CN.properties
    └── messages_en_US.properties
```

`com/github/paohaijiao/util/messages_zh_CN.properties`：

```properties
# 同包共享，key 人工维护
welcome=欢迎使用 javelin
greeting=你好，{0}！当前共有 {1} 个任务
```

`com/github/paohaijiao/util/messages_en_US.properties`：

```properties
welcome=Welcome to javelin
greeting=Hello {0}! You have {1} tasks
```

> 文件必须用 **UTF-8** 保存，中文可直接书写，不需要 `\uXXXX` 转义。

### 2. 调用方式

```java
// 1. 取本包文案：refClass 只用于提取所在包
String text = Messages.getString(JQuickStringUtils.class, "welcome");

// 2. 带占位符（JDK MessageFormat 语法 {0} {1}）
String greeting = Messages.getString(JQuickStringUtils.class, "greeting", "张三", 3);
// 结果：你好，张三！当前共有 3 个任务

// 3. 只有包信息时使用 Package 重载
Package pkg = JQuickStringUtils.class.getPackage();
String fromPackage = Messages.getString(pkg, "greeting", "李四", 5);

// 4. 未命中返回 key 原文，不抛异常
String raw = Messages.getString(JQuickStringUtils.class, "not.exists.key");
// 结果：not.exists.key

// 5. 清空资源包缓存（语言包热更新或测试隔离）
Messages.clearCache();
```

| 方法 | 说明 |
|------|------|
| `Messages.getString(Class<?> refClass, String key)` | 按 refClass 所在包获取文案 |
| `Messages.getString(Class<?> refClass, String key, Object... args)` | 同上，并替换 `{0}`、`{1}` 占位符 |
| `Messages.getString(Package pkg, String key, Object... args)` | 按包名获取文案 |
| `Messages.clearCache()` | 清空资源包缓存 |

### 3. 查找顺序

假设 `JQuickStringUtils` 位于 `com.github.paohaijiao.util`，Locale 为 `zh_CN`：

```text
com.github.paohaijiao.util.messages_zh_CN   ① 就近优先，命中即返回，不再向上查找
com.github.paohaijiao.messages_zh_CN        ② 未命中，回溯父包
com.github.messages_zh_CN                   ③
com.messages_zh_CN                          ④ 逐级上溯直到顶级包
i18n/messages_zh                            ⑤ 全局兜底 bundle
─────────────────────────────────────────────
全部未命中 → 返回 key 原文 + DEBUG 日志，不抛异常
```

### 4. 切换语言

`Messages` 复用原有 `I18nUtils` 的 Locale 设置，两者共享同一套语言环境，原有代码无需改动：

```java
I18nUtils.setLocale(Locale.SIMPLIFIED_CHINESE);
System.out.println(Messages.getString(JQuickStringUtils.class, "welcome"));  // 欢迎使用 javelin

I18nUtils.setLocale(Locale.US);
System.out.println(Messages.getString(JQuickStringUtils.class, "welcome"));  // Welcome to javelin

// 线程级 Locale 清理
I18nUtils.clearThreadLocale();
```

> 资源包缓存键为 `(包名, Locale)`，切换语言只是换缓存键，天然隔离，无需手动刷新。

### 5. 全局配置

```java
// 关闭逐级回溯：只查 refClass 所在包，随后直接走全局兜底。默认 true
I18nConfig.setEnablePackageLookup(false);

// 更换全局兜底 bundle 名称，默认 i18n/messages
I18nConfig.setFallbackBundleName("i18n/global-messages");

// 恢复全部默认配置
I18nConfig.reset();
```

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `enablePackageLookup` | `true` | 是否开启逐级向上回溯父包查找 |
| `fallbackBundleName` | `i18n/messages` | 全局兜底 bundle 名称 |

### 6. SPI 扩展点

#### ① PackageLookupSPI —— 自定义包回溯遍历策略

```java
// 父包优先：适合“父包统一覆盖子包文案”的场景
PackageLookupSPI parentFirst = startPackage -> {
    List<String> packages = new ArrayList<>();
    String parent = JQuickPackageUtils.getParentPackage(startPackage);
    if (!parent.isEmpty()) {
        packages.add(parent);
    }
    packages.add(startPackage);
    return packages;
};
I18nConfig.setPackageLookupSPI(parentFirst);
```

#### ② BundleLoaderSPI —— 自定义资源加载

把文案来源换成数据库、配置中心或远程服务：

```java
public class DatabaseBundleLoaderSPI implements BundleLoaderSPI {
    @Override
    public ResourceBundle load(String baseName, Locale locale, ClassLoader classLoader) {
        Map<String, String> rows = messageDao.select(baseName, locale.toString());
        return rows.isEmpty() ? null : new MapResourceBundle(rows);
    }
}

I18nConfig.setBundleLoaderSPI(new DatabaseBundleLoaderSPI());
```

> 约定：找不到资源时返回 `null`，不要抛 `MissingResourceException`，由 `Messages` 统一决定是否继续回溯或走兜底。

#### ③ 注册方式

1. **显式注册**（优先级最高）：`I18nConfig.setPackageLookupSPI(...)` / `I18nConfig.setBundleLoaderSPI(...)`；
2. **SPI 自动发现**：在 `src/main/resources/META-INF/services/` 下新建文件，文件名为接口全限定名，内容为实现类全限定名：

```text
src/main/resources/META-INF/services/com.github.paohaijiao.i18n.spi.BundleLoaderSPI
```

```text
com.example.mybatis.DatabaseBundleLoaderSPI
```

### 7. 注意事项

- **就近优先可能不符合“统一覆盖子包”的预期**：父子包存在同名 key 时子包胜出；需要反过来就注册父包优先的 `PackageLookupSPI`。
- **包内放 `messages.properties`（无 locale 后缀）会在所有语种下作为最后候选命中**，这是 JDK 标准语义，容易被忽略。
- **未命中的包不会进缓存**：只有成功加载的 bundle 才缓存，未命中 key 的包每次都会重新探测，包层级深且有高频未命中时有额外开销。
- **缓存使用 `SoftReference`**：内存紧张时会被 GC 回收并重新加载，这是防止类加载器泄漏的设计代价。
- **`Locale` 与 `I18nUtils` 共享**：这是为向后兼容刻意设计的，无法独立于 `I18nUtils` 设置语言。
- **向后兼容**：原有 `I18nUtils` 的 `getMessage` / `containsKey` / `getKeys` / `clearCache` 等 API 全部保留，`Messages` 是新增入口，存量调用无需修改。

# **捐献 ☕**

感谢您使用这个开源项目！它完全免费并将持续维护，但开发者确实需要您的支持。

---

## **如何支持我们**

1. **请我喝杯咖啡**  
   果这个项目为您节省了时间或金钱，请考虑通过小额捐赠支持我。

2. **您的捐赠用途**

- 维持项目运行的服务器成本.
- 开发新功能以提供更多价值.
- 优化文档以提升用户体验.

3. **每一分都很重要**  
   即使是1分钱的捐赠也能激励我熬夜调试！

## **为什么捐赠?**

✔️ 保持项目永远免费且无广告.  
✔️ 支持及时响应问题和社区咨询.  
✔️ 实现计划中的未来功能.

感谢您成为让开源世界更美好的伙伴！

--- 

### **补充说明**

- 本项目和产品维护.
- 您的支持确保其可持续性和成长 .

---

## **🌟 立即支持**

赞助时欢迎通过 [email](mailto:goudingcheng@gmail.com) 留言。您的名字将被列入项目README文件的 **"特别感谢"** 名单中！
![Ali Pay](./pay/alipay.jpg)
![Wechat Pay](./pay/wechat.jpg)

---
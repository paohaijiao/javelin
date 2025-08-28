# javelin - lightweight java foundational framework
## Table of Contents
- [Chapter One: Overview](#chapter-one-overview)
    - [Core Modules](#core-modules)
- [Chapter Two: Quick Start](#chapter-two-quick-start)
    - [Requirements](#requirements)
    - [Maven Dependency](#maven-dependency)
- [Chapter Three: Core](#chapter-three-core)
    - [JEvaluator Function Reference](#jevaluator-function-reference)
        - [Type Conversion Functions](#type-conversion-functions)
            - [toInteger](#1-tointeger-function)
            - [toDouble](#2-todouble-function)
            - [toFloat](#3-tofloat-function)
            - [toString](#4-tostring-function)
            - [parseToDate](#5-parsetodate-function)
        - [Math Functions](#math-functions)
            - [ceil](#1-ceil-function)
            - [floor](#2-floor-function)
            - [round](#3-round-function)
            - [sum](#4-sum-function)
            - [max](#5-max-function)
            - [min](#6-min-function)
            - [avg](#7-avg-function)
        - [String Functions](#string-functions)
            - [toLower](#1-tolower-function)
            - [toUpper](#2-toupper-function)
            - [contains](#3-contains-function)
            - [join](#4-join-function)
            - [split](#5-split-function)
            - [substring](#6-substring-function)
            - [replace](#7-replace-function)
            - [startsWith](#8-startswith-function)
            - [endsWith](#9-endswith-function)
        - [Date Functions](#date-functions)
            - [dateFormat](#1-dateformat-function)
        - [Collection Functions](#collection-functions)
            - [length](#1-length-function)
            - [trans](#2-trans-function)
        - [✨ Custom Functions](#custom-functions)  <!-- 已更换图标 -->
    - [Tree Function Reference](#tree-function-reference)
        - [Build Tree](#build-tree)
        - [Access Tree](#access-tree-by-custom-method)
- [Chapter Four: Resource](#chapter-four-resource)
    - [File Loading](#load-the-file-to-string)
    - [Profile Loading](#load-the-spring-profiles)
- [Chapter Five: Provider](#chapter-five-provider)
- [Chapter Six: Scan](#chapter-six-scan)
- [Chapter Seven: Event](#chapter-seven-event)
    - [Event Service](#1-define-a-eventservice)
    - [Event Definition](#2-define-a-event)
    - [Event Publishing](#3-public-an-event)
- [Chapter Eight: MyBatis](#chapter-eight-mybatis)
    - [JDBC Configuration](#1-define-the-jdbc-configuration)
    - [CRUD Operations](#crud-operations)
        - [Insert](#2-insert)
        - [Update](#3-updatebyid)
        - [Select](#4-selectbyid)
        - [Delete](#5-deletebyid)
        - [Query](#6-query)
        - [Update with Conditions](#7-update)
        - [Custom SQL](#8-sql)
        - [Pagination](#10-page)
        - [Single Result](#11-select-one)
        - [Count](#12-count)
    - [Conditional Queries](#conditional-queries)
        - [Like](#13-like)
        - [Equals](#14-eq)
        - [Not Equals](#15-ne)
        - [Greater Than](#16-gt)
        - [Greater or Equal](#17-ge)
        - [Less Than](#18-lt)
        - [Less or Equal](#19-le)
## Chapter One：Overview
```string
    javelin is a streamlined, high-performance java framework designed to accelerate 
backend development with minimal overhead. named for its speed and precision, 
javelin provides essential infrastructure components without the bloat of full-stack solutions.
```

---
| Module       | Description                          |
|--------------|--------------------------------------|
| **Core**     | Lightweight DI container       |
| **Resource** | Enhanced resource management         |
| **Provider** | Type-safe configuration binding      |
| **Scan**     | Automatic component detection        |
| **Event**    | Pub/sub event system                 |
| **MyBatis**  | Simplified MyBatis integration       |

## Chapter Two: Quick Start

### Requirements
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
## Chapter Three: Core
### JEvaluator Function Reference
#### Basic Usage Pattern
```string
All JEvaluator functions follow the same basic usage pattern:
```
```java
List<Object> args = new ArrayList<>();
args.add(argument1);
args.add(argument2);
Object result = JEvaluator.evaluateFunction(JMethodEnums.[functionName].getMethod(), args);
```
## 📊 Type Conversion Functions
| Function       | Syntax                  | Parameters       | Return Type | Description                     |
|----------------|-------------------------|------------------|-------------|---------------------------------|
| `toInteger`    | `toInteger(value)`      | 1 (any type)     | Integer     | Converts value to Integer       |
| `toDouble`     | `toDouble(value)`       | 1 (any type)     | Double      | Converts value to Double        |
| `toFloat`      | `toFloat(value)`        | 1 (any type)     | Float       | Converts value to Float         |
| `toString`     | `toString(value)`       | 1 (any type)     | String      | Converts value to String        |
| `parseToDate`  | `parseToDate(str,format)` | 2 (String)     | Date        | Parses string to Date           |
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
### parseToDate function

## 🔢 Math Functions
| Function | Syntax              | Parameters            | Return Type | Description                     |
|----------|---------------------|-----------------------|-------------|---------------------------------|
| `ceil`   | `ceil(number)`      | 1 (Number)            | Double      | Rounds up to nearest integer    |
| `floor`  | `floor(number)`     | 1 (Number)            | Double      | Rounds down to nearest integer  |
| `round`  | `round(num,digits)` | 2 (Number, Integer)   | Double      | Rounds to specified decimals    |
| `sum`    | `sum(values...)`    | ≥1 (Numbers)          | Number      | Sums all arguments              |
| `max`    | `max(values...)`    | ≥1 (Numbers)          | Number      | Returns maximum value           |
| `min`    | `min(values...)`    | ≥1 (Numbers)          | Number      | Returns minimum value           |
| `avg`    | `avg(values...)`    | ≥1 (Numbers)          | Double      | Calculates average              |
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
## 🔤 String Functions
| Function     | Syntax                    | Parameters            | Return Type | Description                     |
|--------------|---------------------------|-----------------------|-------------|---------------------------------|
| `toLower`    | `toLower(str)`            | 1 (String)            | String      | Converts to lowercase           |
| `toUpper`    | `toUpper(str)`            | 1 (String)            | String      | Converts to uppercase           |
| `contains`   | `contains(str,substr)`    | 2 (String)            | Boolean     | Checks if contains substring    |
| `join`       | `join(delimiter,items...)`| ≥2 (String, Objects)  | String      | Joins with delimiter            |
| `split`      | `split(str,delimiter)`    | 2 (String)            | String[]    | Splits string by delimiter      |
| `substring`  | `substring(str,start,end)`| 3 (String, int, int)  | String      | Extracts substring              |
| `replace`    | `replace(str,target,rep)` | 3 (String)            | String      | Replaces all occurrences        |
| `startsWith` | `startsWith(str,prefix)`  | 2 (String)            | Boolean     | Checks string prefix            |
| `endsWith`   | `endsWith(str,suffix)`    | 2 (String)            | Boolean     | Checks string suffix            |
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
## 📅 Date Functions

| Function      | Syntax                  | Parameters       | Return Type | Description                     |
|---------------|-------------------------|------------------|-------------|---------------------------------|
| `dateFormat`  | `dateFormat(date,format)` | 2 (Date, String) | String    | Formats date to string          |
1. dateFormat function
```java
        List<Object> args = new ArrayList<>();
        args.add(new Date());
        args.add("yyyy-MM-dd HH:mm:ss");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.dateFormat.getMethod(), args);
        System.out.println(result);
```
## ✨ Collection Functions

| Function | Syntax            | Parameters | Return Type | Description                     |
|----------|-------------------|------------|-------------|---------------------------------|
| `length` | `length(array)`   | 1 (Array)  | Integer     | Returns array/list length       |
| `trans`  | `trans(src,dest)` | 2 (Objects)| Object      | Transforms between types        |
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
## 📦 Custume Functions
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

## Tree Function Reference
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
2.  access tree by specific method
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
## Chapter Four: Resource
1. load the file to string
```java
        JReader fileReader = new JFileReader("data/rule.txt");
        JAdaptor context = new JAdaptor(fileReader);
        System.out.println(context.getRuleContent());
```
2. load the spring profiles
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
## Chapter Five: Provider
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
## Chapter Six: Scan

```java
        JAnnotationConfigApplicationContext context =
                new JAnnotationConfigApplicationContext("com.github.paohaijiao");
        JUserRule userService = context.getBean("jUserRule", JUserRule.class);
        System.out.println(userService.findUser(1L));
```

## Chapter Sevent: Event
#### 1.define a EventService
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
#### 2.define a Event
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
#### 3. public an event
```java
 JEventSupportedApplicationContext context = new JEventSupportedApplicationContext("com.github.paohaijiao.test");
 System.out.println("Registered beans: " );
 ParentEventService service = context.getBean("parentEventService", ParentEventService.class);
 context.publishEvent(new AnotherTestEvent(context, "Child Message"));
```
## Chapter Eight: Mybtatis
1. define the jdbc configuration
```java
         String userName="root";
         String password="13579admin";
         String clazz="com.mysql.cj.jdbc.Driver";
         String url="jdbc:mysql://192.168.32.132:3306/test?serverTimezone=UTC";
        JDBCBaseConnectionConfig config=new JBasicJDBConnectionConfig(clazz,url,userName,password);
        
```
2. insert
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo=new JUser();
        userPo.setId(3L);
        userPo.setName("kimoo");
        userPo.setAge(12);
        int i=userMapper.insert(userPo);
        System.out.println(i);
```
3. updateById
```java
   JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo=new JUser();
        userPo.setId(1L);
        userPo.setName("kimoo1");
        userPo.setAge(12);
        int i=userMapper.updateById(userPo);
        System.out.println(i);
```
4. selectById
```java
Map<String, JMappedStatement> map=new HashMap<>();
JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
JUser userPo=userMapper.selectById(1);
System.out.println(userPo);
```
5. deleteById
```java
JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
int   userPo=userMapper.deleteById(1);
System.out.println(userPo);
```
6. query
```java
    JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> list=userMapper.query().eq(JUser::getAge,12).list();
        System.out.println(list.size());
```
7. update
```java
      JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        userMapper.update().eq(JUser::getId,1).set(JUser::getAge,18).set(JUser::getName,"admin").execute();
        System.out.println("update");
```
8. sql
```java
  JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        String sql="select * from j_user where id>?";
        HashMap<String,Object> map=new HashMap<>();
        map.put("id",1L);
        List<JUser> list=userMapper.select(sql,map);
        System.out.println(list.size());
```
9. sql
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        String sql="select * from j_user where id>?";
        JParam param=new JParam();
        param.setIndex(1);
        param.setValue(4L);
        List<JUser> list=userMapper.select(sql, Arrays.asList(param));
        System.out.println(list.size());
```
10. page
```java
 JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JPage<JUser> list=userMapper.page().orderByDesc(JUser::getAge).page(1,10);
        System.out.println(list);
```
11. select one
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser list=userMapper.query().orderByDesc(JUser::getAge).one();
        System.out.println(list);
```
12. count
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        Long count=userMapper.query().orderByDesc(JUser::getAge).count();
        System.out.println(count);
```
13. like
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().like(JUser::getName,"张").list();
        System.out.println(count);
```
14. eq
```java
      JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().eq(JUser::getName,"张").list();
        System.out.println(count);
```
15. ne
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().ne(JUser::getName,"张三").list();
        System.out.println(count);
```
16. gt
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().gt(JUser::getAge,40).list();
        System.out.println(count);
```
17. ge
```java
    JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
    JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
    JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
    List<JUser> count=userMapper.query().ge(JUser::getAge,40).list();
    System.out.println(count);
```
18. ge
```java
       JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().lt(JUser::getAge,40).list();
        System.out.println(count);
```
18. le
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().le(JUser::getAge,40).list();
        System.out.println(count);
```

# **Generating Power with Love (and Caffeine) ☕**

Thank you for using this open-source project! It is completely free and will be maintained continuously, but the developers do need your support.

---

## **How You Can Help**

1. **Buy Me a Coffee**  
   If this project has saved you time or money, please consider supporting me with a small donation.

2. **Where Your Donation Goes**
- Server costs to keep the project running.
- Feature development to add more value.
- Documentation optimization for a better user experience.

3. **Every Cent Counts**  
   Even a donation of just 1 cent motivates me to debug late into the night!



## **Why Donate?**
✔️ Keep the project **free and ad-free** forever.  
✔️ Support timely responses to issues and community inquiries.  
✔️ Enable planned features for the future.

Thank you for being a partner in making the open-source world better!

--- 

### **Additional Notes**
- The project is maintained with love and caffeine.
- Your support ensures its sustainability and growth.
---

## **🌟 Support Now**
Feel free to leave a message via [email](mailto:goudingcheng@gmail.com) when sponsoring. Your name will be included in the **"Special Thanks"** list in the project's README file!
![OCBC Pay Now](./pay/paynow.jpg)
![Touch n Go ](./pay/tngGo.jpg)
---
# javelin - lightweight java foundational framework

## overview
javelin is a streamlined, high-performance java framework designed to accelerate 
backend development with minimal overhead. named for its speed and precision, Javelin provides essential infrastructure components without the bloat of full-stack solutions.

## Features
### Core Modules
| Module       | Description                          |
|--------------|--------------------------------------|
| **Core**     | Lightweight DI container (<2MB)      |
| **Resource** | Enhanced resource management         |
| **Provider** | Type-safe configuration binding      |
| **Scan**     | Automatic component detection        |
| **Event**    | Pub/sub event system                 |
| **MyBatis**  | Simplified MyBatis integration       |

## Quick Start

### Requirements
- Java 8
- Maven/Gradle

### Installation
```xml
<!-- Maven -->
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>javelin</artifactId>
    <version>versionNum</version>
</dependency>
```
### core 
#### JEvaluator Function Reference
##### Basic Usage Pattern
```string
All JEvaluator functions follow the same basic usage pattern:
```
```java
List<Object> args = new ArrayList<>();
// Add arguments in order specified in function signature
args.add(argument1);
args.add(argument2);
// ...
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
### toInteger function
```java      
  List<Object> args = new ArrayList<>();
        args.add("1");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toInteger.getMethod(), args);
        System.out.println(result); 
```
### ceil floor toString toDouble round function
```java   
     List<Object> args = new ArrayList<>();
        args.add(1.5);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toString.getMethod(), args);
        Object result1 = JEvaluator.evaluateFunction(JMethodEnums.toDouble.getMethod(), args);
        Object ceil= JEvaluator.evaluateFunction(JMethodEnums.ceil.getMethod(), args);
        Object floor= JEvaluator.evaluateFunction(JMethodEnums.floor.getMethod(), args);
        List<Object> args1 = new ArrayList<>();
        args1.add(1.5321321312);
        args1.add(2);
        Object round= JEvaluator.evaluateFunction(JMethodEnums.round.getMethod(), args1);
        System.out.println(result);
```
### parseToDate function
```java   
        List<Object> args = new ArrayList<>();
        args.add("2019-04-25 16:23:23");
        args.add("yyyy-MM-dd HH:mm:ss");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.parseToDate.getMethod(), args);
        System.out.println(result);
```
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
### sum function
```java
        List<Object> args = new ArrayList<>();
        args.add(10);
        args.add(11);
        args.add(12);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.sum.getMethod(), args);
        System.out.println(result);
```
### max function
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
### min function
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
### avg function
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
### toLower function
```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toLower.getMethod(), args);
        System.out.println(result); 
```
### contains function
```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hello");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.contains.getMethod(), args);
        System.out.println(result);
 ```
### startsWith function
```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hel1lo");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.startsWith.getMethod(), args);
        System.out.println(result);
```
### endsWith function
```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.endsWith.getMethod(), args);
        System.out.println(result);
```
### substring function
```java
        List<Object> args = new ArrayList<>();
        args.add("substring");
        args.add(1);
        args.add(3);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.substring.getMethod(), args);
        System.out.println(result); 
```
### replace function
```java
        List<Object> args = new ArrayList<>();
        args.add("replace");
        args.add("ep");
        args.add("2345");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.replace.getMethod(), args);
        System.out.println(result); 
```
### split function
```java
        List<Object> data = new ArrayList<>();
        data.add("replace");
        data.add("ep");
        List<Object> args = new ArrayList<>();
        args.add("123,12344");
        args.add(",");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.split.getMethod(), args);
        System.out.println(result); 
```
## 📅 Date Functions

| Function      | Syntax                  | Parameters       | Return Type | Description                     |
|---------------|-------------------------|------------------|-------------|---------------------------------|
| `dateFormat`  | `dateFormat(date,format)` | 2 (Date, String) | String    | Formats date to string          |
### dateFormat function
```java
        List<Object> args = new ArrayList<>();
        args.add(new Date());
        args.add("yyyy-MM-dd HH:mm:ss");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.dateFormat.getMethod(), args);
        System.out.println(result);
```
## 📦 Collection Functions

| Function | Syntax            | Parameters | Return Type | Description                     |
|----------|-------------------|------------|-------------|---------------------------------|
| `length` | `length(array)`   | 1 (Array)  | Integer     | Returns array/list length       |
| `trans`  | `trans(src,dest)` | 2 (Objects)| Object      | Transforms between types        |
### length function
```java
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.length.getMethod(), args);
        System.out.println(result);
```
### trans function
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
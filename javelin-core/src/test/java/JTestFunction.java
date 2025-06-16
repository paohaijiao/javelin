import com.paohaijiao.javelin.evalue.JEvaluator;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JTestFunction {
    @Test
    public void length() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction("length", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void contains() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hello");
        Object result = JEvaluator.evaluateFunction("contains", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void sum() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(10);
        args.add(11);
        args.add(12);
        Object result = JEvaluator.evaluateFunction("sum", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void startsWith() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hel1lo");
        Object result = JEvaluator.evaluateFunction("startsWith", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void endsWith() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("World");
        Object result = JEvaluator.evaluateFunction("endsWith", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void toLower() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction("toLower", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void max() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        args.add(4);
        args.add(5);
        Object result = JEvaluator.evaluateFunction("max", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void min() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        args.add(4);
        args.add(5);
        Object result = JEvaluator.evaluateFunction("min", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void avg() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        args.add(4);
        args.add(5);
        Object result = JEvaluator.evaluateFunction("avg", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void substring() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("substring");
        args.add(1);
        args.add(3);
        Object result = JEvaluator.evaluateFunction("substring", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void replace() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("replace");
        args.add("ep");
        args.add("2345");
        Object result = JEvaluator.evaluateFunction("replace", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void join() throws IOException {
        List<Object> data = new ArrayList<>();
        data.add("replace");
        data.add("ep");
        List<Object> args = new ArrayList<>();
        args.add("123,12344");
        args.add(",");
        Object result = JEvaluator.evaluateFunction("split", args);
        System.out.println(result); // 输出: 11
    }

    @Test
    public void toInteger() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("1");
        Object result = JEvaluator.evaluateFunction("toInteger", args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void toStringq() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(1.5);
        Object result = JEvaluator.evaluateFunction("toString", args);
        Object result1 = JEvaluator.evaluateFunction("toDouble", args);
        Object ceil= JEvaluator.evaluateFunction("ceil", args);
        Object floor= JEvaluator.evaluateFunction("floor", args);
        List<Object> args1 = new ArrayList<>();
        args1.add(1.5321321312);
        args1.add(2);
        Object round= JEvaluator.evaluateFunction("round", args1);

        System.out.println(result); // 输出: 11
    }


}

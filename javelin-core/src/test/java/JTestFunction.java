import com.paohaijiao.javelin.enums.JMethodEnums;
import com.paohaijiao.javelin.evalue.JEvaluator;
import com.paohaijiao.javelin.param.JContext;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JTestFunction {
    @Test
    public void length() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.length.getMethod(), args);
        System.out.println(result);
    }
    @Test
    public void contains() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hello");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.contains.getMethod(), args);
        System.out.println(result);
    }
    @Test
    public void sum() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(10);
        args.add(11);
        args.add(12);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.sum.getMethod(), args);
        System.out.println(result);
    }
    @Test
    public void startsWith() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("Hel1lo");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.startsWith.getMethod(), args);
        System.out.println(result);
    }
    @Test
    public void endsWith() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        args.add("World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.endsWith.getMethod(), args);
        System.out.println(result);
    }
    @Test
    public void toLower() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("Hello World");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toLower.getMethod(), args);
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
        Object result = JEvaluator.evaluateFunction(JMethodEnums.max.getMethod(), args);
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
        Object result = JEvaluator.evaluateFunction(JMethodEnums.min.getMethod(), args);
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
        Object result = JEvaluator.evaluateFunction(JMethodEnums.avg.getMethod(), args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void substring() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("substring");
        args.add(1);
        args.add(3);
        Object result = JEvaluator.evaluateFunction(JMethodEnums.substring.getMethod(), args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void replace() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("replace");
        args.add("ep");
        args.add("2345");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.replace.getMethod(), args);
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
        Object result = JEvaluator.evaluateFunction(JMethodEnums.split.getMethod(), args);
        System.out.println(result); // 输出: 11
    }

    @Test
    public void toInteger() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("1");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.toInteger.getMethod(), args);
        System.out.println(result); // 输出: 11
    }
    @Test
    public void toStringq() throws IOException {
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
    }
    @Test
    public void testTrans() throws IOException {
        JContext contextParams = new JContext();
        contextParams.put("1","男");
        contextParams.put("2","女");
        List<Object> args = new ArrayList<>();
        args.add(contextParams);
        args.add("1");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.trans.getMethod(), args);
        System.out.println(result);
    }
    @Test
    public void dateFormat() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(new Date());
        args.add("yyyy-MM-dd HH:mm:ss");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.dateFormat.getMethod(), args);
        System.out.println(result);
    }
    @Test
    public void parseToDate() throws IOException {
        List<Object> args = new ArrayList<>();
        args.add("2019-04-25 16:23:23");
        args.add("yyyy-MM-dd HH:mm:ss");
        Object result = JEvaluator.evaluateFunction(JMethodEnums.parseToDate.getMethod(), args);
        System.out.println(result);
    }

}

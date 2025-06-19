package util;

import com.paohaijiao.javelin.date.JDateCompareUtil;
import com.paohaijiao.javelin.model.JKeyValueModel;
import com.paohaijiao.javelin.util.ObjectConverter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.paohaijiao.javelin.enums.JOperatorEnums.*;
import static com.paohaijiao.javelin.enums.JOperatorEnums.NOT_EQUAL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JListTest {
    @Test
    public void testCompareWithEnumOperator() {
        List<JKeyValueModel> data = new ArrayList<>();
        JKeyValueModel jkv = new JKeyValueModel<String>();
        jkv.setKey("test");
        jkv.setValue("test1");
        data.add(jkv);
        JKeyValueModel jk = new JKeyValueModel<String>();
        jk.setKey("test");
        jk.setValue("test1");
        data.add(jk);
        List<Map<String, Object>> list=ObjectConverter.convert(data);
        System.out.println(list);


    }
}

package com.github.paohaijiao;

import com.github.paohaijiao.map.JMultiValuedMap;
import com.github.paohaijiao.model.JDept;
import com.github.paohaijiao.tree.JTreeUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JTestMap {

    @Test
    public void length() throws IOException {
        JMultiValuedMap<String, String> map = new JMultiValuedMap<>();

        // 添加键值对 - 允许key重复
        map.put("fruit", "apple");
        map.put("fruit", "banana");
        map.put("fruit", "orange");
        map.put("vegetable", "carrot");
        map.put("vegetable", "potato");

    }
}

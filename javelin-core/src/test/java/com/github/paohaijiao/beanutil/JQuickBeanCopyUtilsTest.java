/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.beanutil;


import com.github.paohaijiao.bean.JQuickBeanCopyUtils;
import com.github.paohaijiao.beanutil.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JQuickBeanCopyUtils 单元测试
 *
 * @author YourName
 */
class JQuickBeanCopyUtilsTest {

    private JQuickSourceUser sourceUser;

    @BeforeEach
    void setUp() {
        sourceUser = new JQuickSourceUser();
        sourceUser.setId(1L);
        sourceUser.setName("张三");
        sourceUser.setAge(25);
        sourceUser.setEmail("zhangsan@example.com");
        sourceUser.setActive(true);
        sourceUser.setScore(95.5);
        JQuickAddress address = new JQuickAddress("中山路123号", "上海市");
        sourceUser.setAddress(address);
        sourceUser.setTags(Arrays.asList("java", "spring", "mysql"));
    }

    @Test
    void testCopy_ObjectToObject_Normal() {
        JQuickTargetUser target = new JQuickTargetUser();
        JQuickBeanCopyUtils.copy(sourceUser, target);
        assertEquals(sourceUser.getId(), target.getId());
        assertEquals(sourceUser.getName(), target.getName());
        assertEquals(sourceUser.getAge(), target.getAge());
        assertEquals(sourceUser.getEmail(), target.getEmail());
        assertEquals(sourceUser.getActive(), target.getActive());
        assertEquals(sourceUser.getScore(), target.getScore());
        assertSame(sourceUser.getAddress(), target.getAddress());
        assertSame(sourceUser.getTags(), target.getTags());
        assertNull(target.getExtraField());
    }

    @Test
    void testCopy_ObjectToObject_SourceNull() {
        JQuickTargetUser target = new JQuickTargetUser();
        target.setName("原有名称");
        JQuickBeanCopyUtils.copy(null, target);
        assertEquals("原有名称", target.getName());
    }


    @Test
    void testCopy_ObjectToObject_DifferentClass() {
        JQuickTargetUser target = new JQuickTargetUser();
        JQuickBeanCopyUtils.copy(sourceUser, target);
        assertEquals(sourceUser.getId(), target.getId());
        assertEquals(sourceUser.getName(), target.getName());
        assertEquals(sourceUser.getAge(), target.getAge());
    }

    @Test
    void testCopy_ObjectToObject_Inheritance() {
        JQuickChild child = new JQuickChild();
        child.setParentField("父类字段值");
        child.setChildField("子类字段值");
        JQuickChild target = new JQuickChild();
        JQuickBeanCopyUtils.copy(child, target);
        assertEquals("父类字段值", target.getParentField());
        assertEquals("子类字段值", target.getChildField());
    }

    @Test
    void testCopy_ObjectToObject_IgnoreProperties() {
        JQuickTargetUser target = new JQuickTargetUser();
        JQuickBeanCopyUtils.copy(sourceUser, target, "name", "age", "email");
        assertEquals(sourceUser.getId(), target.getId());
        assertNull(target.getName());
        assertNull(target.getAge());
        assertNull(target.getEmail());
        assertEquals(sourceUser.getActive(), target.getActive());
    }

    @Test
    void testCopy_ObjectToObject_IgnoreEmptyArray() {
        JQuickTargetUser target = new JQuickTargetUser();
        JQuickBeanCopyUtils.copy(sourceUser, target);
        assertEquals(sourceUser.getName(), target.getName());
    }

    @Test
    void testCopy_ObjectToClass_Normal() {
        JQuickTargetUser target = JQuickBeanCopyUtils.copy(sourceUser, JQuickTargetUser.class);
        assertNotNull(target);
        assertEquals(sourceUser.getId(), target.getId());
        assertEquals(sourceUser.getName(), target.getName());
        assertEquals(sourceUser.getAge(), target.getAge());
        assertEquals(sourceUser.getEmail(), target.getEmail());
    }

    @Test
    @DisplayName("测试 copy - 源对象为 null")
    void testCopy_ObjectToClass_SourceNull() {
        JQuickTargetUser target = JQuickBeanCopyUtils.copy(null, JQuickTargetUser.class);
        assertNull(target);
    }

    @Test
    @DisplayName("测试 copy - 目标类型无无参构造")
    void testCopy_ObjectToClass_NoDefaultConstructor() {
        class NoDefaultConstructor {
            private final String value;
            public NoDefaultConstructor(String value) {
                this.value = value;
            }
            public String getValue() {
                return value;
            }
        }

        assertThrows(RuntimeException.class, () ->
                JQuickBeanCopyUtils.copy(new Object(), NoDefaultConstructor.class)
        );
    }

    @Test
    void testCopy_ObjectToClass_IgnoreProperties() {
        JQuickTargetUser target = JQuickBeanCopyUtils.copy(sourceUser, JQuickTargetUser.class, "name", "email");
        assertNotNull(target);
        assertEquals(sourceUser.getId(), target.getId());
        assertNull(target.getName());
        assertEquals(sourceUser.getAge(), target.getAge());
        assertNull(target.getEmail());
        assertEquals(sourceUser.getActive(), target.getActive());
    }

    @Test
    @DisplayName("测试 copyToList - 正常拷贝集合")
    void testCopyToList_Normal() {
        List<JQuickSourceUser> sourceList = Arrays.asList(sourceUser, createAnotherUser());
        List<JQuickTargetUser> targetList = JQuickBeanCopyUtils.copyToList(sourceList, JQuickTargetUser.class);
        assertNotNull(targetList);
        assertEquals(2, targetList.size());
        assertEquals(sourceList.get(0).getId(), targetList.get(0).getId());
        assertEquals(sourceList.get(0).getName(), targetList.get(0).getName());
        assertEquals(sourceList.get(1).getId(), targetList.get(1).getId());
        assertEquals(sourceList.get(1).getName(), targetList.get(1).getName());
    }

    @Test
    @DisplayName("测试 copyToList - 源集合为 null")
    void testCopyToList_SourceNull() {
        List<JQuickTargetUser> result = JQuickBeanCopyUtils.copyToList(null, JQuickTargetUser.class);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCopyToList_EmptyList() {
        List<JQuickTargetUser> result = JQuickBeanCopyUtils.copyToList(new ArrayList<>(), JQuickTargetUser.class);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 copyToList - 集合中包含 null 元素")
    void testCopyToList_WithNullElement() {
        List<JQuickSourceUser> sourceList = Arrays.asList(sourceUser, null, createAnotherUser());
        List<JQuickTargetUser> targetList = JQuickBeanCopyUtils.copyToList(sourceList, JQuickTargetUser.class);
        assertEquals(3, targetList.size());
        assertNotNull(targetList.get(0));
        assertNull(targetList.get(1));
        assertNotNull(targetList.get(2));
    }

    @Test
    void testCopyToList_IgnoreProperties() {
        List<JQuickSourceUser> sourceList = Arrays.asList(sourceUser, createAnotherUser());
        List<JQuickTargetUser> targetList = JQuickBeanCopyUtils.copyToList(sourceList, JQuickTargetUser.class, "name", "email");
        assertNull(targetList.get(0).getName());
        assertNull(targetList.get(0).getEmail());
        assertEquals(sourceList.get(0).getId(), targetList.get(0).getId());
    }

    @Test
    void testCopyToList_WithSupplier() {
        List<JQuickSourceUser> sourceList = Arrays.asList(sourceUser, createAnotherUser());
        List<JQuickTargetUser> targetList = JQuickBeanCopyUtils.copyToList(sourceList, JQuickTargetUser::new);
        assertEquals(2, targetList.size());
        assertEquals(sourceList.get(0).getName(), targetList.get(0).getName());
        assertEquals(sourceList.get(1).getName(), targetList.get(1).getName());
    }

    @Test
    void testCopyToList_SupplierWithNull() {
        List<JQuickSourceUser> sourceList = Arrays.asList(sourceUser, null, createAnotherUser());
        List<JQuickTargetUser> targetList = JQuickBeanCopyUtils.copyToList(sourceList, JQuickTargetUser::new);
        assertEquals(3, targetList.size());
        assertNotNull(targetList.get(0));
        assertNull(targetList.get(1));
        assertNotNull(targetList.get(2));
    }

    @Test
    void testMergeNonNull_Normal() {
        JQuickTargetUser target = new JQuickTargetUser();
        target.setId(100L);
        target.setName("原有名称");
        target.setAge(30);

        JQuickSourceUser source = new JQuickSourceUser();
        source.setName("新名称");
        source.setEmail("new@example.com");
        JQuickBeanCopyUtils.mergeNonNull(source, target);
        assertEquals(100L, target.getId());
        assertEquals("新名称", target.getName());
        assertEquals(30, target.getAge());
        assertEquals("new@example.com", target.getEmail());
    }

    @Test
    void testMergeNonNull_SourceNull() {
        JQuickTargetUser target = new JQuickTargetUser();
        target.setName("原有名称");
        JQuickBeanCopyUtils.mergeNonNull(null, target);
        assertEquals("原有名称", target.getName());
    }

    @Test
    void testMergeNonNull_TargetNull() {
        assertDoesNotThrow(() -> JQuickBeanCopyUtils.mergeNonNull(sourceUser, null));
    }

    @Test
    @DisplayName("测试空对象拷贝")
    void testCopy_EmptyObject() {
        JQuickSourceUser emptySource = new JQuickSourceUser();
        JQuickTargetUser target = new JQuickTargetUser();
        target.setName("原有值");
        JQuickBeanCopyUtils.copy(emptySource, target);
        assertNull(target.getId());
        assertNull(target.getName());
        assertNull(target.getAge());
    }

    @Test
    @DisplayName("测试大量数据拷贝性能")
    void testPerformance_LargeDataCopy() {
        List<JQuickSourceUser> sourceList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            JQuickSourceUser user = new JQuickSourceUser();
            user.setId((long) i);
            user.setName("用户" + i);
            user.setAge(20 + i % 50);
            sourceList.add(user);
        }

        long startTime = System.currentTimeMillis();
        List<JQuickTargetUser> targetList = JQuickBeanCopyUtils.copyToList(sourceList, JQuickTargetUser.class);
        long endTime = System.currentTimeMillis();
        assertNotNull(targetList);
        assertEquals(10000, targetList.size());
        System.out.println("拷贝 10000 条数据耗时: " + (endTime - startTime) + "ms");
    }

    private JQuickSourceUser createAnotherUser() {
        JQuickSourceUser user = new JQuickSourceUser();
        user.setId(2L);
        user.setName("李四");
        user.setAge(30);
        user.setEmail("lisi@example.com");
        user.setActive(false);
        user.setScore(88.0);
        return user;
    }







}

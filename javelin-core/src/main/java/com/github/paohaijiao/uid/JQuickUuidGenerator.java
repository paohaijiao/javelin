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
package com.github.paohaijiao.uid;

/**
 * packageName com.github.paohaijiao.uid
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/1
 */
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UUID生成器 (Java 8版本)
 */
public class JQuickUuidGenerator {

    /**
     * 生成标准UUID（带横线）
     * 格式: 123e4567-e89b-12d3-a456-426614174000
     */
    public static String standardUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成简洁UUID（无横线）
     * 格式: 123e4567e89b12d3a456426614174000
     */
    public static String simpleUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成大写UUID
     * 格式: 123E4567-E89B-12D3-A456-426614174000
     */
    public static String upperUuid() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * 生成数字型UUID（基于时间戳+随机数）
     * 格式: 2024010112345678901234567890
     */
    public static String numericUuid() {
        return System.currentTimeMillis() + String.format("%019d", ThreadLocalRandom.current().nextLong(1000000000000000000L));
    }

    /**
     * 生成短UUID（8位）
     * 格式: 最多8位字符
     */
    public static String shortUuid() {
        String uuid = simpleUuid();
        return uuid.substring(0, 8);
    }

    /**
     * 生成基于时间的UUID (Version 1)
     * 注意：Java默认不支持，这里使用模拟实现
     */
    public static String timeBasedUuid() {
        long timestamp = System.currentTimeMillis();
        long random = ThreadLocalRandom.current().nextLong();
        return String.format("%016x-%016x", timestamp, random);
    }
}
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 雪花算法ID生成器 (Java 8版本)
 * 64位ID结构: 1位符号位 + 41位时间戳 + 10位工作机器ID + 12位序列号
 */
public class JQuickSnowflakeIdGenerator {

    // 起始时间戳 (2024-01-01 00:00:00)
    private final static long START_TIMESTAMP = 1704067200000L;

    // 机器ID所占位数
    private final static long WORKER_ID_BITS = 10L;

    // 序列号所占位数
    private final static long SEQUENCE_BITS = 12L;

    // 机器ID最大值 (1023)
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    // 序列号最大值 (4095)
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 机器ID左移位数
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    // 时间戳左移位数
    private final static long TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;

    private final long workerId;

    private final AtomicLong sequence = new AtomicLong(0);

    private final Lock lock = new ReentrantLock();

    private volatile long lastTimestamp = -1L;

    /**
     * 构造函数
     * @param workerId 工作机器ID (0-1023)
     */
    public JQuickSnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(String.format("WorkerId必须介于0-%d之间", MAX_WORKER_ID));
        }
        this.workerId = workerId;
    }

    /**
     * 生成唯一ID
     */
    public long nextId() {
        lock.lock();
        try {
            long currentTimestamp = System.currentTimeMillis();
            // 时钟回拨处理
            if (currentTimestamp < lastTimestamp) {
                long offset = lastTimestamp - currentTimestamp;
                if (offset <= 5) {
                    // 允许5ms内的时钟回拨，等待时间同步
                    try {
                        Thread.sleep(offset << 1);
                        currentTimestamp = System.currentTimeMillis();
                        if (currentTimestamp < lastTimestamp) {
                            throw new RuntimeException("时钟回拨超过5ms，无法生成ID");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException("线程中断异常", e);
                    }
                } else {
                    throw new RuntimeException(String.format("时钟回拨超过5ms，请检查服务器时间"));
                }
            }

            // 同一毫秒内生成多个ID
            if (currentTimestamp == lastTimestamp) {
                long seq = sequence.incrementAndGet();
                if (seq > MAX_SEQUENCE) {
                    // 当前毫秒序列号用尽，等待下一毫秒
                    currentTimestamp = waitNextMillis(lastTimestamp);
                    sequence.set(0L);
                }
            } else {
                sequence.set(0L);
            }

            lastTimestamp = currentTimestamp;
            // 生成最终ID
            return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 等待下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long currentTimestamp = System.currentTimeMillis();
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = System.currentTimeMillis();
        }
        return currentTimestamp;
    }

    /**
     * 解析ID中的时间戳
     */
    public static long getTimestamp(long id) {
        return START_TIMESTAMP + (id >> TIMESTAMP_SHIFT);
    }

    /**
     * 解析ID中的机器ID
     */
    public static long getWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 解析ID中的序列号
     */
    public static long getSequence(long id) {
        return id & MAX_SEQUENCE;
    }
}

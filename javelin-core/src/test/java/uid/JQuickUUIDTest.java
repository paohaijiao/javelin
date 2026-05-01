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
package uid;

import com.github.paohaijiao.uid.JQuickSnowflakeIdGenerator;
import com.github.paohaijiao.uid.JQuickUuidGenerator;
import org.junit.Before;
import org.junit.Test;


import java.util.HashSet;

/**
 * packageName uid
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/1
 */
public class JQuickUUIDTest {

    private JQuickSnowflakeIdGenerator snowflakeGenerator;

    @Before
    public void setUp() {
        snowflakeGenerator = new JQuickSnowflakeIdGenerator(1);
    }
    @Test
    public void testGenerateSingleLongId() {
        long nextId=snowflakeGenerator.nextId();
        System.out.println("生成的Long ID: " + nextId);
    }
    @Test
    public void testGenerateSingleLongId1() {
        String uuid=JQuickUuidGenerator.standardUuid();
        System.out.println("uuid ID: " + uuid);
        String simpleId=JQuickUuidGenerator.simpleUuid();
        System.out.println("simpleId ID: " + simpleId);
        String upperUuid=JQuickUuidGenerator.upperUuid();
        System.out.println("upperUuid ID: " + upperUuid);
        String numericUuid=JQuickUuidGenerator.numericUuid();
        System.out.println("numericUuid ID: " + numericUuid);
        String shortUuid=JQuickUuidGenerator.shortUuid();
        System.out.println("shortUuid ID: " + shortUuid);
        String timeBasedUuid=JQuickUuidGenerator.timeBasedUuid();
        System.out.println("timeBasedUuid ID: " + timeBasedUuid);
    }
}

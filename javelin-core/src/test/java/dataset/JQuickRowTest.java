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
package dataset;

import com.github.paohaijiao.crypto.exception.CryptoException;
import com.github.paohaijiao.crypto.impl.EccCryptoService;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

/**
 * packageName dataset
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/6/20
 */
public class JQuickRowTest {

    @Test
    public void rows() throws IOException {
        JQuickRow row = new JQuickRow();
        row.put("id", 1001);
        row.put("name", "张三");
        row.put("age", 28);
        row.put("email", "zhangsan@example.com");
        row.put("createTime", new Date());
        row.show();

    }
}

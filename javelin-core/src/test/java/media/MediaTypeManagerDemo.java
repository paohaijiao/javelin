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
package media;


import com.github.paohaijiao.media.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MediaTypeManager 使用示例
 */
public class MediaTypeManagerDemo {
    public static void main(String[] args) {
        MediaTypeManager manager = MediaTypeManager.getInstance();

        System.out.println("========== 1. 基础查询 ==========");
        MediaTypeInfo info = manager.getByExtension("json");
        System.out.println("JSON 类型: " + info.getCode());
        System.out.println("  - 字符集: " + info.getCharset());
        System.out.println("  - 数据类型: " + info.getDataType());
        System.out.println("  - 扩展名: " + info.getExtensions());

        info = manager.getByCode("video/mp4");
        System.out.println("\nMP4 类型: " + info.getCode());
        System.out.println("  - 数据类型: " + info.getDataType());

        String mimeType = manager.getMimeTypeByFileName("document.pdf");
        System.out.println("\ndocument.pdf -> " + mimeType);

        mimeType = manager.getMimeTypeByFileName("style.css");
        System.out.println("style.css -> " + mimeType);

        System.out.println("\n========== 2. 动态添加媒体类型 ==========");
        manager.addMediaType("application/manifest+json", StandardCharsets.UTF_8, JDataType.STRING, "webmanifest", "manifest");
        manager.addMediaType("video/x-matroska", null, JDataType.BYTE_STREAM, "mkv", "mka");

        info = manager.getByExtension("mkv");
        System.out.println("MKV 类型: " + (info != null ? info.getCode() : "未找到"));

        System.out.println("\n========== 3. 更新媒体类型 ==========");
        // 更新字符集
        boolean updated = manager.updateCharset("application/manifest+json", StandardCharsets.UTF_8);
        System.out.println("更新字符集: " + updated);

        // 添加扩展名
        manager.addExtension("application/manifest+json", "webapp");
        info = manager.getByCode("application/manifest+json");
        System.out.println("更新后的扩展名: " + info.getExtensions());

        // 更新数据类型
        manager.updateDataType("application/octet-stream", JDataType.BYTES);
        info = manager.getByCode("application/octet-stream");
        System.out.println("更新后的数据类型: " + info.getDataType());

        System.out.println("\n========== 4. 删除媒体类型 ==========");
        // 删除单个
        boolean removed = manager.removeByCode("application/manifest+json");
        System.out.println("删除 manifest+json: " + removed);

        // 批量删除
        int count = manager.removeByCodes("video/x-matroska", "application/wasm");
        System.out.println("批量删除数量: " + count);

        System.out.println("\n========== 5. 查询和筛选 ==========");
        // 根据数据类型筛选
        List<MediaTypeInfo> stringTypes = manager.getByDataType(JDataType.STRING);
        System.out.println("STRING 类型数量: " + stringTypes.size());
        System.out.println("前5个 STRING 类型:");
        stringTypes.stream().limit(5).forEach(t ->
                System.out.println("  - " + t.getCode())
        );

        // 根据字符集筛选
        List<MediaTypeInfo> utf8Types = manager.getByCharset(StandardCharsets.UTF_8);
        System.out.println("\nUTF-8 类型数量: " + utf8Types.size());

        // 搜索
        List<MediaTypeInfo> searchResults = manager.search("json");
        System.out.println("\n搜索 'json' 结果:");
        searchResults.forEach(t ->
                System.out.println("  - " + t.getCode() + " (扩展名: " + t.getExtensions() + ")")
        );

        System.out.println("\n========== 6. 统计信息 ==========");
        System.out.println("总类型数: " + manager.size());
        System.out.println("总扩展名数: " + manager.extensionSize());
        System.out.println("是否存在 text/html: " + manager.exists("text/html"));
        System.out.println("是否存在 .xyz 扩展名: " + manager.existsExtension("xyz"));

        // 按数据类型统计
        Map<JDataType, Integer> stats = manager.statisticsByDataType();
        System.out.println("\n按数据类型统计:");
        for (Map.Entry<JDataType, Integer> entry : stats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        // 打印详细统计
        manager.printStatistics();

        System.out.println("\n========== 7. 获取所有信息 ==========");
        Set<String> allCodes = manager.getAllCodes();
        System.out.println("所有 MIME 类型（前20个）:");
        allCodes.stream().limit(20).forEach(code ->
                System.out.println("  - " + code)
        );

        Set<String> allExtensions = manager.getAllExtensions();
        System.out.println("\n所有扩展名（前20个）:");
        allExtensions.stream().limit(20).forEach(ext ->
                System.out.println("  - ." + ext)
        );

        System.out.println("\n========== 8. 批量操作 ==========");
        // 批量添加
        List<MediaTypeDefinition> definitions = Arrays.asList(
                new MediaTypeDefinition("application/custom1", JDataType.BYTES, Arrays.asList("c1")),
                new MediaTypeDefinition("application/custom2", JDataType.STRING, Arrays.asList("c2")),
                new MediaTypeDefinition("application/custom3", JDataType.BYTE_STREAM, Arrays.asList("c3"))
        );
        manager.addMediaTypes(definitions);
        System.out.println("批量添加后总数: " + manager.size());

        // 导出定义
        List<MediaTypeDefinition> exported = manager.exportDefinitions();
        System.out.println("导出定义数量: " + exported.size());

        System.out.println("\n========== 9. 文件类型判断示例 ==========");
        String[] testFiles = {
                "document.pdf", "image.jpg", "video.mp4", "audio.mp3",
                "script.js", "data.json", "style.css", "font.ttf",
                "archive.zip", "document.docx", "presentation.pptx"
        };

        System.out.println("文件类型判断:");
        for (String file : testFiles) {
            String mime = manager.getMimeTypeByFileName(file);
            MediaTypeInfo typeInfo = manager.getByFileName(file);
            String dataType = typeInfo != null ? typeInfo.getDataType().name() : "UNKNOWN";
            System.out.printf("  %-20s -> %-35s [%s]%n", file, mime, dataType);
        }

        System.out.println("\n========== 10. SPI 重新加载 ==========");
        manager.reloadProviders();
        System.out.println("SPI 重新加载完成，当前类型数: " + manager.size());
    }
}
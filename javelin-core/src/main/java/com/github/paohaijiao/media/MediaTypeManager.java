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
package com.github.paohaijiao.media;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JLogLevel;
import com.github.paohaijiao.spi.ServiceLoader;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.media
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/4/12
 */
public class MediaTypeManager {

    private static volatile MediaTypeManager instance;

    private final Map<String, MediaTypeInfo> codeMap = new ConcurrentHashMap<>();

    private final Map<String, MediaTypeInfo> extensionMap = new ConcurrentHashMap<>();

    private final List<MediaTypeProvider> mediaTypeProviders = new CopyOnWriteArrayList<>();

    JConsole console = new JConsole();

    private MediaTypeManager() {
        initDefaultMediaTypes();
        List<MediaTypeProvider> providers = ServiceLoader.loadServicesByPriority(MediaTypeProvider.class);
        this.mediaTypeProviders.addAll(providers);
        initMediaTypesFromProviders();
    }

    public static MediaTypeManager getInstance() {
        if (instance == null) {
            synchronized (MediaTypeManager.class) {
                if (instance == null) {
                    instance = new MediaTypeManager();
                }
            }
        }
        return instance;
    }

    /**
     * 从 SPI 提供者加载媒体类型
     */
    private void initMediaTypesFromProviders() {
        for (MediaTypeProvider provider : mediaTypeProviders) {
            try {
                List<MediaTypeDefinition> definitions = provider.provideMediaTypes();
                for (MediaTypeDefinition def : definitions) {
                    register(def.getCode(), def.getCharset(), def.getDataType(), def.getExtensions());
                }
                consoleLog("已加载媒体类型提供者: " + provider.getClass().getName() + "，提供 " + definitions.size() + " 个类型");
            } catch (Exception e) {
                consoleLog("加载媒体类型提供者失败: " + provider.getClass().getName() + ", 错误: " + e.getMessage());
            }
        }
    }

    /**
     * 初始化默认的媒体类型
     */
    private void initDefaultMediaTypes() {
        register("text/plain", StandardCharsets.UTF_8, JDataType.STRING, Arrays.asList("txt", "text", "conf", "def", "list", "log", "in", "ini"));
        register("text/html", StandardCharsets.UTF_8, JDataType.STRING, Arrays.asList("html", "htm"));
        register("text/css", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("css"));
        register("text/csv", StandardCharsets.UTF_8, JDataType.BYTES, Collections.singletonList("csv"));
        register("text/javascript", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("js"));
        register("text/xml", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("xml"));
        register("text/markdown", StandardCharsets.UTF_8, JDataType.STRING, Arrays.asList("md", "markdown"));
        register("application/json", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("json"));
        register("application/xml", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("xml"));
        register("application/pdf", null, JDataType.BYTES, Collections.singletonList("pdf"));
        register("application/zip", null, JDataType.BYTES, Collections.singletonList("zip"));
        register("application/gzip", null, JDataType.BYTES, Collections.singletonList("gz"));
        register("application/octet-stream", null, JDataType.BYTES, Collections.emptyList());
        register("application/javascript", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("js"));
        register("application/xhtml+xml", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("xhtml"));
        register("application/rss+xml", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("rss"));
        register("application/atom+xml", StandardCharsets.UTF_8, JDataType.STRING, Collections.singletonList("atom"));
        register("application/yaml", StandardCharsets.UTF_8, JDataType.STRING, Arrays.asList("yaml", "yml"));
        register("application/protobuf", null, JDataType.BYTES, Arrays.asList("pb", "proto"));
        register("application/msword", null, JDataType.BYTES, Arrays.asList("doc", "dot"));
        register("application/vnd.ms-excel", null, JDataType.BYTES, Arrays.asList("xls", "xlt"));
        register("application/vnd.ms-powerpoint", null, JDataType.BYTES, Arrays.asList("ppt", "pot"));
        register("application/x-www-form-urlencoded", StandardCharsets.UTF_8, JDataType.STRING, Collections.emptyList());
        register("multipart/form-data", StandardCharsets.UTF_8, JDataType.BYTES, Collections.emptyList());
        register("image/jpeg", null, JDataType.BYTES, Arrays.asList("jpeg", "jpg", "jpe"));
        register("image/png", null, JDataType.BYTES, Collections.singletonList("png"));
        register("image/gif", null, JDataType.BYTES, Collections.singletonList("gif"));
        register("image/bmp", null, JDataType.BYTES, Arrays.asList("bmp", "dib"));
        register("image/webp", null, JDataType.BYTES, Collections.singletonList("webp"));
        register("image/svg+xml", StandardCharsets.UTF_8, JDataType.STRING, Arrays.asList("svg", "svgz"));
        register("image/tiff", null, JDataType.BYTES, Arrays.asList("tiff", "tif"));
        register("image/vnd.microsoft.icon", null, JDataType.BYTES, Collections.singletonList("ico"));
        register("image/heic", null, JDataType.BYTES, Arrays.asList("heic", "heif"));
        register("image/avif", null, JDataType.BYTES, Collections.singletonList("avif"));
        register("audio/mpeg", null, JDataType.BYTES, Arrays.asList("mp3", "mpga", "mp2", "mp2a", "m2a", "m3a"));
        register("audio/ogg", null, JDataType.BYTES, Arrays.asList("oga", "ogg", "spx"));
        register("audio/wav", null, JDataType.BYTES, Collections.singletonList("wav"));
        register("audio/webm", null, JDataType.BYTES, Collections.singletonList("weba"));
        register("audio/aac", null, JDataType.BYTES, Collections.singletonList("aac"));
        register("audio/midi", null, JDataType.BYTES, Arrays.asList("mid", "midi", "kar"));
        register("audio/flac", null, JDataType.BYTES, Collections.singletonList("flac"));
        register("audio/x-ms-wma", null, JDataType.BYTES, Collections.singletonList("wma"));
        register("video/mp4", null, JDataType.BYTES, Arrays.asList("mp4", "mp4v", "mpg4"));
        register("video/ogg", null, JDataType.BYTES, Collections.singletonList("ogv"));
        register("video/webm", null, JDataType.BYTES, Collections.singletonList("webm"));
        register("video/x-msvideo", null, JDataType.BYTES, Collections.singletonList("avi"));
        register("video/mpeg", null, JDataType.BYTES, Arrays.asList("mpeg", "mpg", "mpe", "m1v", "m2v"));
        register("video/quicktime", null, JDataType.BYTES, Arrays.asList("qt", "mov"));
        register("video/3gpp", null, JDataType.BYTES, Arrays.asList("3gp", "3gpp"));
        register("video/x-flv", null, JDataType.BYTES, Collections.singletonList("flv"));
        register("video/x-matroska", null, JDataType.BYTES, Arrays.asList("mkv", "mka", "mks"));
        register("video/x-ms-wmv", null, JDataType.BYTES, Collections.singletonList("wmv"));

        register("font/ttf", null, JDataType.BYTES, Collections.singletonList("ttf"));
        register("font/otf", null, JDataType.BYTES, Collections.singletonList("otf"));
        register("font/woff", null, JDataType.BYTES, Collections.singletonList("woff"));
        register("font/woff2", null, JDataType.BYTES, Collections.singletonList("woff2"));
        register("application/vnd.ms-fontobject", null, JDataType.BYTES, Collections.singletonList("eot"));

        register("application/java-archive", null, JDataType.BYTES, Arrays.asList("jar", "war", "ear"));
        register("application/x-tar", null, JDataType.BYTES, Collections.singletonList("tar"));
        register("application/x-7z-compressed", null, JDataType.BYTES, Collections.singletonList("7z"));
        register("application/x-rar-compressed", null, JDataType.BYTES, Collections.singletonList("rar"));
        register("application/x-shockwave-flash", null, JDataType.BYTES, Collections.singletonList("swf"));
        register("application/x-bzip2", null, JDataType.BYTES, Collections.singletonList("bz2"));
    }


    /**
     * 注册媒体类型
     */
    public void register(String code, Charset charset, JDataType dataType, List<String> extensions) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("MediaType code cannot be null or empty");
        }
        if (dataType == null) {
            throw new IllegalArgumentException("dataType cannot be null");
        }
        String normalizedCode = code.trim().toLowerCase();
        MediaTypeInfo info = new MediaTypeInfo(normalizedCode, charset, dataType, extensions);
        codeMap.put(normalizedCode, info);
        if (extensions != null) {
            for (String ext : extensions) {
                if (ext != null && !ext.trim().isEmpty()) {
                    extensionMap.put(ext.trim().toLowerCase(), info);
                }
            }
        }
    }

    /**
     * 添加媒体类型
     */
    public void addMediaType(String code, Charset charset, JDataType dataType, String... extensions) {
        register(code, charset, dataType, extensions != null ? Arrays.asList(extensions) : new ArrayList<>());
    }

    /**
     * 批量添加媒体类型
     */
    public void addMediaTypes(List<MediaTypeDefinition> definitions) {
        for (MediaTypeDefinition def : definitions) {
            register(def.getCode(), def.getCharset(), def.getDataType(), def.getExtensions());
        }
    }

    /**
     * 根据 code 删除媒体类型
     */
    public boolean removeByCode(String code) {
        if (code == null) {
            return false;
        }
        MediaTypeInfo removed = codeMap.remove(code.trim().toLowerCase());
        if (removed != null && removed.getExtensions() != null) {
            for (String ext : removed.getExtensions()) {
                extensionMap.remove(ext.trim().toLowerCase(), removed);
            }
        }
        return removed != null;
    }

    /**
     * 根据扩展名删除媒体类型
     */
    public boolean removeByExtension(String extension) {
        if (extension == null) {
            return false;
        }
        MediaTypeInfo removed = extensionMap.remove(extension.trim().toLowerCase());
        if (removed != null) {
            if (!extensionMap.containsValue(removed)) {
                codeMap.remove(removed.getCode());
            }
        }
        return removed != null;
    }

    /**
     * 批量删除
     */
    public int removeByCodes(String... codes) {
        int count = 0;
        for (String code : codes) {
            if (removeByCode(code)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 根据 code 更新媒体类型
     */
    public boolean updateByCode(String code, Charset charset, JDataType dataType, List<String> extensions) {
        if (code == null || !codeMap.containsKey(code.trim().toLowerCase())) {
            return false;
        }
        removeByCode(code);
        register(code, charset, dataType, extensions);
        return true;
    }

    /**
     * 更新媒体类型的字符集
     */
    public boolean updateCharset(String code, Charset charset) {
        MediaTypeInfo info = getByCode(code);
        if (info == null) {
            return false;
        }
        return updateByCode(code, charset, info.getDataType(), info.getExtensions());
    }

    /**
     * 更新媒体类型的数据类型
     */
    public boolean updateDataType(String code, JDataType dataType) {
        MediaTypeInfo info = getByCode(code);
        if (info == null) {
            return false;
        }
        return updateByCode(code, info.getCharset(), dataType, info.getExtensions());
    }

    /**
     * 添加扩展名到现有类型
     */
    public boolean addExtension(String code, String extension) {
        MediaTypeInfo info = getByCode(code);
        if (info == null) {
            return false;
        }
        List<String> newExtensions = new ArrayList<>(info.getExtensions());
        if (!newExtensions.contains(extension)) {
            newExtensions.add(extension);
            return updateByCode(code, info.getCharset(), info.getDataType(), newExtensions);
        }
        return false;
    }

    /**
     * 移除扩展名
     */
    public boolean removeExtension(String code, String extension) {
        MediaTypeInfo info = getByCode(code);
        if (info == null) {
            return false;
        }
        List<String> newExtensions = new ArrayList<>(info.getExtensions());
        if (newExtensions.remove(extension)) {
            return updateByCode(code, info.getCharset(), info.getDataType(), newExtensions);
        }
        return false;
    }

    /**
     * 根据 code 获取媒体类型信息
     */
    public MediaTypeInfo getByCode(String code) {
        if (code == null) {
            return null;
        }
        return codeMap.get(code.trim().toLowerCase());
    }

    /**
     * 根据扩展名获取媒体类型信息
     */
    public MediaTypeInfo getByExtension(String extension) {
        if (extension == null) {
            return null;
        }
        return extensionMap.get(extension.trim().toLowerCase());
    }

    /**
     * 根据文件名获取媒体类型信息
     */
    public MediaTypeInfo getByFileName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return getByExtension(extension);
    }

    /**
     * 获取文件扩展名对应的 MIME Type
     */
    public String getMimeTypeByFileName(String fileName) {
        MediaTypeInfo info = getByFileName(fileName);
        return info != null ? info.getCode() : "application/octet-stream";
    }

    /**
     * 获取扩展名对应的 MIME Type
     */
    public String getMimeTypeByExtension(String extension) {
        MediaTypeInfo info = getByExtension(extension);
        return info != null ? info.getCode() : "application/octet-stream";
    }

    /**
     * 获取所有已注册的媒体类型
     */
    public List<MediaTypeInfo> getAllMediaTypes() {
        return new ArrayList<>(codeMap.values());
    }

    /**
     * 获取所有已注册的 code
     */
    public Set<String> getAllCodes() {
        return new HashSet<>(codeMap.keySet());
    }

    /**
     * 获取所有已注册的扩展名
     */
    public Set<String> getAllExtensions() {
        return new HashSet<>(extensionMap.keySet());
    }

    /**
     * 根据数据类型筛选
     */
    public List<MediaTypeInfo> getByDataType(JDataType dataType) {
        return codeMap.values().stream()
                .filter(info -> info.getDataType() == dataType)
                .collect(Collectors.toList());
    }

    /**
     * 根据字符集筛选
     */
    public List<MediaTypeInfo> getByCharset(Charset charset) {
        return codeMap.values().stream()
                .filter(info -> {
                    Charset infoCharset = info.getCharset();
                    return (charset == null && infoCharset == null) ||
                            (charset != null && charset.equals(infoCharset));
                })
                .collect(Collectors.toList());
    }

    /**
     * 搜索媒体类型（支持模糊匹配）
     */
    public List<MediaTypeInfo> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMediaTypes();
        }

        String lowerKeyword = keyword.trim().toLowerCase();
        return codeMap.values().stream()
                .filter(info -> info.getCode().contains(lowerKeyword) ||
                        info.getExtensions().stream().anyMatch(ext -> ext.contains(lowerKeyword)))
                .collect(Collectors.toList());
    }

    /**
     * 检查媒体类型是否存在
     */
    public boolean exists(String code) {
        return codeMap.containsKey(code != null ? code.trim().toLowerCase() : null);
    }

    /**
     * 检查扩展名是否存在
     */
    public boolean existsExtension(String extension) {
        return extensionMap.containsKey(extension != null ? extension.trim().toLowerCase() : null);
    }

    /**
     * 获取媒体类型总数
     */
    public int size() {
        return codeMap.size();
    }

    /**
     * 获取扩展名总数
     */
    public int extensionSize() {
        return extensionMap.size();
    }

    /**
     * 按数据类型统计
     */
    public Map<JDataType, Integer> statisticsByDataType() {
        Map<JDataType, Integer> stats = new HashMap<>();
        for (JDataType type : JDataType.values()) {
            stats.put(type, 0);
        }
        for (MediaTypeInfo info : codeMap.values()) {
            JDataType dataType = info.getDataType();
            stats.put(dataType, stats.get(dataType) + 1);
        }
        return stats;
    }

    /**
     * 按字符集统计
     */
    public Map<String, Integer> statisticsByCharset() {
        Map<String, Integer> stats = new HashMap<>();
        for (MediaTypeInfo info : codeMap.values()) {
            String charsetName = info.getCharset() != null ? info.getCharset().name() : "null";
            stats.put(charsetName, stats.getOrDefault(charsetName, 0) + 1);
        }
        return stats;
    }

    /**
     * 按扩展名数量统计
     */
    public Map<Integer, Integer> statisticsByExtensionCount() {
        Map<Integer, Integer> stats = new HashMap<>();
        for (MediaTypeInfo info : codeMap.values()) {
            int count = info.getExtensions().size();
            stats.put(count, stats.getOrDefault(count, 0) + 1);
        }
        return stats;
    }

    /**
     * 获取扩展名最多的媒体类型
     */
    public MediaTypeInfo getMaxExtensionsType() {
        return codeMap.values().stream()
                .max(Comparator.comparingInt(info -> info.getExtensions().size()))
                .orElse(null);
    }

    /**
     * 获取最常用的数据类型
     */
    public JDataType getMostUsedDataType() {
        return statisticsByDataType().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(JDataType.BYTES);
    }

    /**
     * 重新加载 SPI 提供者
     */
    public void reloadProviders() {
        ServiceLoader.reload(MediaTypeProvider.class);
        List<MediaTypeProvider> newProviders = ServiceLoader.loadServicesByPriority(MediaTypeProvider.class);
        mediaTypeProviders.clear();
        mediaTypeProviders.addAll(newProviders);
        codeMap.clear();
        extensionMap.clear();
        initMediaTypesFromProviders();
        initDefaultMediaTypes();
    }

    /**
     * 清空所有媒体类型
     */
    public void clear() {
        codeMap.clear();
        extensionMap.clear();
    }

    /**
     * 导出为定义列表
     */
    public List<MediaTypeDefinition> exportDefinitions() {
        List<MediaTypeDefinition> definitions = new ArrayList<>();
        for (MediaTypeInfo info : codeMap.values()) {
            definitions.add(new MediaTypeDefinition(info.getCode(), info.getCharset(), info.getDataType(), info.getExtensions()));
        }
        return definitions;
    }

    /**
     * 导入定义列表
     */
    public void importDefinitions(List<MediaTypeDefinition> definitions) {
        for (MediaTypeDefinition def : definitions) {
            register(def.getCode(), def.getCharset(), def.getDataType(), def.getExtensions());
        }
    }

    /**
     * 打印所有已注册的媒体类型
     */
    public void printAllMediaTypes() {
        consoleLog("========== 已注册的媒体类型 ==========");
        for (MediaTypeInfo info : codeMap.values()) {
            consoleLog(info.toString());
        }
        consoleLog("总共: " + size() + " 种媒体类型");
        consoleLog("扩展名总数: " + extensionSize());
    }

    /**
     * 打印统计信息
     */
    public void printStatistics() {
        consoleLog("========== 媒体类型统计 ==========");
        consoleLog("总类型数: " + size());
        consoleLog("总扩展名数: " + extensionSize());
        consoleLog("\n按数据类型统计:");
        Map<JDataType, Integer> dataTypeStats = statisticsByDataType();
        for (Map.Entry<JDataType, Integer> entry : dataTypeStats.entrySet()) {
            consoleLog(String.format("  %-12s: %d", entry.getKey().name(), entry.getValue()));
        }
        consoleLog("\n按字符集统计:");
        Map<String, Integer> charsetStats = statisticsByCharset();
        for (Map.Entry<String, Integer> entry : charsetStats.entrySet()) {
            String displayName = "null".equals(entry.getKey()) ? "无字符集" : entry.getKey();
            consoleLog(String.format("  %-12s: %d", displayName, entry.getValue()));
        }
        consoleLog("\n扩展名数量分布:");
        Map<Integer, Integer> extCountStats = statisticsByExtensionCount();
        for (Map.Entry<Integer, Integer> entry : extCountStats.entrySet()) {
            consoleLog(String.format("  扩展名数量 %d: %d 个类型", entry.getKey(), entry.getValue()));
        }
        MediaTypeInfo maxExtType = getMaxExtensionsType();
        if (maxExtType != null) {
            consoleLog("\n扩展名最多的类型:");
            consoleLog(String.format("  %s -> 扩展名: %s", maxExtType.getCode(), maxExtType.getExtensions()));
        }
        consoleLog("\n最常用的数据类型: " + getMostUsedDataType());
    }

    private void consoleLog(String message) {
        console.log(JLogLevel.INFO, "[MediaTypeManager] " + message);
    }
}

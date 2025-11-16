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
package com.github.paohaijiao.base64;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * packageName com.github.paohaijiao.base64
 *
 * @author Martin
 * @version 1.0.0
 * @className JBase64Provider
 * @date 2025/6/28
 * @description
 */
public class JBase64Provider {
    private static final Pattern BASE64_PATTERN = Pattern.compile(
            "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$"
    );
    private static final Pattern BASE64_URL_PATTERN = Pattern.compile(
            "^([A-Za-z0-9-_]{4})*([A-Za-z0-9-_]{3}|[A-Za-z0-9-_]{2})?$"
    );

    public static boolean isBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return BASE64_PATTERN.matcher(str).matches();
    }

    public static boolean isBase64Url(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return BASE64_URL_PATTERN.matcher(str).matches();
    }

    public static String encode(String content) {
        String encode = Base64.getEncoder().encodeToString(content.getBytes());
        return encode;
    }

    public static String decode(String content) {
        byte[] decodedBytes = Base64.getDecoder().decode(content);
        String decode = new String(decodedBytes);
        return decode;
    }

    public static String encode(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("input data require not null");
        }
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decodeByBase64String(String base64String) {
        if (base64String == null) {
            throw new IllegalArgumentException("input data require not null");
        }
        try {
            return Base64.getDecoder().decode(base64String);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid the base 64 data", e);
        }
    }

    public static boolean isValidBase64(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isValidBase64Url(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        try {
            Base64.getUrlDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}

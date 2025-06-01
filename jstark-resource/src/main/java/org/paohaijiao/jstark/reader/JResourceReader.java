package org.paohaijiao.jstark.reader;

import org.paohaijiao.jstark.test.ServerConfig;

import java.io.IOException;
import java.io.InputStream;

/**
 * File Parse to parse the File
 * @param <T>
 */
public interface JResourceReader<T>  {
    /**
     *
     * @param inputStream
     * @param targetClass
     * @return
     * @throws IOException
     */
    T parse(InputStream inputStream, Class<T> targetClass) throws IOException;

    /**
     *
     * @param inputStream
     * @param targetClass
     * @param key
     * @return
     * @throws IOException
     */
    T parse(InputStream inputStream, Class<T> targetClass,String key) throws IOException;

    /**
     *
     * @param filePath
     * @param targetClass
     * @return
     * @throws IOException
     */
    default T parse(String filePath, Class<T> targetClass) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IOException("File not found: " + filePath);
            }
            return parse(is, targetClass);
        }
    }

    /**
     *
     * @param filePath
     * @param targetClass
     * @param key
     * @return
     * @throws IOException
     */
    default T parse(String filePath, Class<T> targetClass,String key) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IOException("File not found: " + filePath);
            }
            return parse(is, targetClass,key);
        }
    }

}

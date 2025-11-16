package com.github.paohaijiao.crypto.impl;

import com.github.paohaijiao.crypto.CryptoService;
import com.github.paohaijiao.crypto.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AesCryptoService implements CryptoService {

    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int KEY_SIZE = 256;

    private static final int GCM_TAG_LENGTH = 128;

    private static final int GCM_IV_LENGTH = 12;

    private final SecretKey secretKey;

    public AesCryptoService() throws CryptoException {
        this.secretKey = generateKey();
    }

    public AesCryptoService(String base64Key) throws CryptoException {
        this.secretKey = loadKey(base64Key);
    }

    public AesCryptoService(byte[] key) {
        this.secretKey = new SecretKeySpec(key, ALGORITHM);
    }

    @Override
    public String encrypt(String data) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            // 初始化加密模式
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            // 加密数据
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            // 组合IV和加密数据
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CryptoException("AES加密失败", e);
        }
    }

    @Override
    public String decrypt(String encryptedData) throws CryptoException {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            // 分离IV和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decryptedData = cipher.doFinal(encryptedBytes);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new CryptoException("AES解密失败", e);
        }
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String getKeyInfo() {
        return String.format("AES-%d Key: %s", KEY_SIZE,
                Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }

    /**
     * 生成新的AES密钥
     */
    private SecretKey generateKey() throws CryptoException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("生成AES密钥失败", e);
        }
    }

    /**
     * 从Base64字符串加载密钥
     */
    private SecretKey loadKey(String base64Key) throws CryptoException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            throw new CryptoException("加载AES密钥失败", e);
        }
    }

    /**
     * 获取Base64编码的密钥（用于保存）
     */
    public String getBase64Key() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}

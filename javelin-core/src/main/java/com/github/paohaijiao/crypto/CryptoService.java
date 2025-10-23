package com.github.paohaijiao.crypto;

import com.github.paohaijiao.crypto.exception.CryptoException;

public interface CryptoService {
    /**
     * 加密数据
     * @param data 原始数据
     * @return 加密后的数据（Base64编码）
     */
    String encrypt(String data) throws CryptoException;

    /**
     * 解密数据
     * @param encryptedData 加密数据（Base64编码）
     * @return 解密后的原始数据
     */
    String decrypt(String encryptedData) throws CryptoException;

    /**
     * 获取加密算法名称
     */
    String getAlgorithm();

    /**
     * 获取密钥信息（用于调试或显示）
     */
    String getKeyInfo();
}

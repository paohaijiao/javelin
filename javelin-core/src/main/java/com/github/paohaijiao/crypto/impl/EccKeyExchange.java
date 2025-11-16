package com.github.paohaijiao.crypto.impl;

import com.github.paohaijiao.crypto.exception.CryptoException;

import javax.crypto.KeyAgreement;
import java.security.*;

public class EccKeyExchange {

    /**
     * 生成ECC密钥对
     */
    public static KeyPair generateKeyPair() throws CryptoException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(256);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new CryptoException("生成ECC密钥对失败", e);
        }
    }

    /**
     * 执行ECDH密钥协商
     */
    public static byte[] performKeyAgreement(PrivateKey privateKey, PublicKey publicKey) throws CryptoException {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
            return keyAgreement.generateSecret();
        } catch (Exception e) {
            throw new CryptoException("ECDH密钥协商失败", e);
        }
    }

    /**
     * 从共享密钥派生AES密钥
     */
    public static javax.crypto.SecretKey deriveAesKey(byte[] sharedSecret) throws CryptoException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyMaterial = digest.digest(sharedSecret);
            return new javax.crypto.spec.SecretKeySpec(keyMaterial, 0, 32, "AES");
        } catch (Exception e) {
            throw new CryptoException("派生AES密钥失败", e);
        }
    }
}

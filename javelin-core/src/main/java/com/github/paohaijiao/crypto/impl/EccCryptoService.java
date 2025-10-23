package com.github.paohaijiao.crypto.impl;
import com.github.paohaijiao.crypto.CryptoService;
import com.github.paohaijiao.crypto.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
public class EccCryptoService implements CryptoService {

    private static final String ALGORITHM = "EC";

    private static final String TRANSFORMATION = "ECIES";

    private static final int KEY_SIZE = 256;

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public EccCryptoService() throws CryptoException {
        KeyPair keyPair = generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public EccCryptoService(String base64PublicKey, String base64PrivateKey) throws CryptoException {
        this.publicKey = loadPublicKey(base64PublicKey);
        this.privateKey = loadPrivateKey(base64PrivateKey);
    }

    public EccCryptoService(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    // 仅用于加密的场景（只有公钥）
    public EccCryptoService(PublicKey publicKey) {
        this.publicKey = publicKey;
        this.privateKey = null;
    }

    // 仅用于解密的场景（只有私钥）
    public EccCryptoService(PrivateKey privateKey) {
        this.publicKey = null;
        this.privateKey = privateKey;
    }

    @Override
    public String encrypt(String data) throws CryptoException {
        if (publicKey == null) {
            throw new CryptoException("公钥未初始化，无法加密");
        }
        try {
            Cipher cipher = Cipher.getInstance("ECIESwithAES-CBC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            return encryptWithEcdh(data);
        }
    }

    @Override
    public String decrypt(String encryptedData) throws CryptoException {
        if (privateKey == null) {
            throw new CryptoException("私钥未初始化，无法解密");
        }

        try {
            Cipher cipher = Cipher.getInstance("ECIESwithAES-CBC");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(encryptedBytes);
            return new String(decryptedData);
        } catch (Exception e) {
            return decryptWithEcdh(encryptedData);
        }
    }

    /**
     * 使用ECDH密钥协商进行加密（备选方案）
     */
    private String encryptWithEcdh(String data) throws CryptoException {
        try {
            // 生成临时密钥对
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(KEY_SIZE);
            KeyPair tempKeyPair = keyGen.generateKeyPair();
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(tempKeyPair.getPrivate());
            keyAgreement.doPhase(publicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            javax.crypto.SecretKey aesKey = deriveAesKey(sharedSecret);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            byte[] iv = cipher.getIV();
            byte[] tempPublicKey = tempKeyPair.getPublic().getEncoded();
            byte[] combined = new byte[tempPublicKey.length + iv.length + encryptedData.length];
            System.arraycopy(tempPublicKey, 0, combined, 0, tempPublicKey.length);
            System.arraycopy(iv, 0, combined, tempPublicKey.length, iv.length);
            System.arraycopy(encryptedData, 0, combined, tempPublicKey.length + iv.length, encryptedData.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CryptoException("ECC加密失败", e);
        }
    }

    /**
     * 使用ECDH密钥协商进行解密（备选方案）
     */
    private String decryptWithEcdh(String encryptedData) throws CryptoException {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            int publicKeyLength = 91;
            int ivLength = 12;
            byte[] tempPublicKeyBytes = new byte[publicKeyLength];
            byte[] iv = new byte[ivLength];
            byte[] encryptedBytes = new byte[combined.length - publicKeyLength - ivLength];
            System.arraycopy(combined, 0, tempPublicKeyBytes, 0, publicKeyLength);
            System.arraycopy(combined, publicKeyLength, iv, 0, ivLength);
            System.arraycopy(combined, publicKeyLength + ivLength, encryptedBytes, 0, encryptedBytes.length);
            PublicKey tempPublicKey = loadPublicKeyFromBytes(tempPublicKeyBytes);
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(tempPublicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            javax.crypto.SecretKey aesKey = deriveAesKey(sharedSecret);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new javax.crypto.spec.GCMParameterSpec(128, iv));
            byte[] decryptedData = cipher.doFinal(encryptedBytes);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new CryptoException("ECC解密失败", e);
        }
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String getKeyInfo() {
        String publicKeyInfo = publicKey != null ? Base64.getEncoder().encodeToString(publicKey.getEncoded()).substring(0, 30) + "..." : "null";
        String privateKeyInfo = privateKey != null ? Base64.getEncoder().encodeToString(privateKey.getEncoded()).substring(0, 30) + "..." : "null";
        return String.format("ECC-%d - Public: %s, Private: %s", KEY_SIZE, publicKeyInfo, privateKeyInfo);
    }

    /**
     * 使用私钥签名
     */
    public String sign(String data) throws CryptoException {
        if (privateKey == null) {
            throw new CryptoException("私钥未初始化，无法签名");
        }
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            byte[] digitalSignature = signature.sign();
            return Base64.getEncoder().encodeToString(digitalSignature);
        } catch (Exception e) {
            throw new CryptoException("ECC签名失败", e);
        }
    }

    /**
     * 使用公钥验证签名
     */
    public boolean verify(String data, String signatureStr) throws CryptoException {
        if (publicKey == null) {
            throw new CryptoException("公钥未初始化，无法验证签名");
        }
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new CryptoException("ECC验证签名失败", e);
        }
    }

    /**
     * 从共享密钥派生AES密钥
     */
    private javax.crypto.SecretKey deriveAesKey(byte[] sharedSecret) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyMaterial = digest.digest(sharedSecret);
        return new javax.crypto.spec.SecretKeySpec(keyMaterial, 0, 16, "AES");
    }

    /**
     * 生成ECC密钥对
     */
    private KeyPair generateKeyPair() throws CryptoException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("生成ECC密钥对失败", e);
        }
    }

    /**
     * 从Base64字符串加载公钥
     */
    private PublicKey loadPublicKey(String base64PublicKey) throws CryptoException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            return loadPublicKeyFromBytes(keyBytes);
        } catch (Exception e) {
            throw new CryptoException("加载ECC公钥失败", e);
        }
    }

    /**
     * 从字节数组加载公钥
     */
    private PublicKey loadPublicKeyFromBytes(byte[] keyBytes) throws CryptoException {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new CryptoException("加载ECC公钥失败", e);
        }
    }

    /**
     * 从Base64字符串加载私钥
     */
    private PrivateKey loadPrivateKey(String base64PrivateKey) throws CryptoException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new CryptoException("加载ECC私钥失败", e);
        }
    }

    /**
     * 获取Base64编码的公钥
     */
    public String getBase64PublicKey() {
        return publicKey != null ? Base64.getEncoder().encodeToString(publicKey.getEncoded()) : null;
    }

    /**
     * 获取Base64编码的私钥
     */
    public String getBase64PrivateKey() {
        return privateKey != null ? Base64.getEncoder().encodeToString(privateKey.getEncoded()) : null;
    }

    /**
     * 获取密钥对
     */
    public KeyPair getKeyPair() {
        return new KeyPair(publicKey, privateKey);
    }
}

package com.github.paohaijiao.crypto.impl;
import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import com.github.paohaijiao.crypto.CryptoService;
import com.github.paohaijiao.crypto.exception.CryptoException;

public class RsaCryptoService implements CryptoService {

    private static final String ALGORITHM = "RSA";

    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static final int KEY_SIZE = 2048;

    private final PublicKey publicKey;

    private final PrivateKey privateKey;

    public RsaCryptoService() throws CryptoException {
        KeyPair keyPair = generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public RsaCryptoService(String base64PublicKey, String base64PrivateKey) throws CryptoException {
        this.publicKey = loadPublicKey(base64PublicKey);
        this.privateKey = loadPrivateKey(base64PrivateKey);
    }

    public RsaCryptoService(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    // 仅用于加密的场景（只有公钥）
    public RsaCryptoService(PublicKey publicKey) {
        this.publicKey = publicKey;
        this.privateKey = null;
    }

    @Override
    public String encrypt(String data) throws CryptoException {
        if (publicKey == null) {
            throw new CryptoException("公钥未初始化，无法加密");
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // RSA加密有长度限制，需要分段加密
            byte[] dataBytes = data.getBytes();
            int maxLength = KEY_SIZE / 8 - 11; // PKCS1Padding的填充长度

            if (dataBytes.length <= maxLength) {
                byte[] encryptedData = cipher.doFinal(dataBytes);
                return Base64.getEncoder().encodeToString(encryptedData);
            } else {
                throw new CryptoException("数据长度超过RSA加密限制");
            }

        } catch (Exception e) {
            throw new CryptoException("RSA加密失败", e);
        }
    }

    @Override
    public String decrypt(String encryptedData) throws CryptoException {
        if (privateKey == null) {
            throw new CryptoException("私钥未初始化，无法解密");
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(encryptedBytes);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new CryptoException("RSA解密失败", e);
        }
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String getKeyInfo() {
        String publicKeyInfo = publicKey != null ?
                Base64.getEncoder().encodeToString(publicKey.getEncoded()).substring(0, 50) + "..." : "null";
        String privateKeyInfo = privateKey != null ?
                Base64.getEncoder().encodeToString(privateKey.getEncoded()).substring(0, 50) + "..." : "null";
        return String.format("RSA-%d - Public: %s, Private: %s", KEY_SIZE, publicKeyInfo, privateKeyInfo);
    }

    /**
     * 使用公钥加密（仅加密场景）
     */
    public String encryptWithPublicKey(String data) throws CryptoException {
        return encrypt(data);
    }

    /**
     * 使用私钥解密（仅加密场景）
     */
    public String decryptWithPrivateKey(String encryptedData) throws CryptoException {
        return decrypt(encryptedData);
    }

    /**
     * 使用私钥签名
     */
    public String sign(String data) throws CryptoException {
        if (privateKey == null) {
            throw new CryptoException("私钥未初始化，无法签名");
        }
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            byte[] digitalSignature = signature.sign();
            return Base64.getEncoder().encodeToString(digitalSignature);
        } catch (Exception e) {
            throw new CryptoException("RSA签名失败", e);
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
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new CryptoException("RSA验证签名失败", e);
        }
    }

    /**
     * 生成RSA密钥对
     */
    private KeyPair generateKeyPair() throws CryptoException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("生成RSA密钥对失败", e);
        }
    }

    /**
     * 从Base64字符串加载公钥
     */
    private PublicKey loadPublicKey(String base64PublicKey) throws CryptoException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new CryptoException("加载RSA公钥失败", e);
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
            throw new CryptoException("加载RSA私钥失败", e);
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
}

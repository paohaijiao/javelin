package crypo;

import com.github.paohaijiao.crypto.exception.CryptoException;
import com.github.paohaijiao.crypto.impl.AesCryptoService;
import com.github.paohaijiao.crypto.impl.EccCryptoService;
import com.github.paohaijiao.crypto.impl.RsaCryptoService;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RsaCryptoServiceTest {
    @Test
    public void encry() throws IOException {
        try {
            // 对称加密示例
            System.out.println("=== 对称加密（AES）示例 ===");
            AesCryptoService aesService = new AesCryptoService();
            System.out.println("AES密钥信息: " + aesService.getKeyInfo());
            String originalText = "Hello, AES Encryption!";
            String aesEncrypted = aesService.encrypt(originalText);
            String aesDecrypted = aesService.decrypt(aesEncrypted);
            System.out.println("原始文本: " + originalText);
            System.out.println("AES加密后: " + aesEncrypted);
            System.out.println("AES解密后: " + aesDecrypted);
            System.out.println("加解密结果: " + originalText.equals(aesDecrypted));
            System.out.println("\n=== 非对称加密（RSA）示例 ===");
            RsaCryptoService rsaService = new RsaCryptoService();
            System.out.println("RSA密钥信息: " + rsaService.getKeyInfo());
            String rsaOriginalText = "Hello, RSA Encryption!";
            String rsaEncrypted = rsaService.encrypt(rsaOriginalText);
            String rsaDecrypted = rsaService.decrypt(rsaEncrypted);
            System.out.println("原始文本: " + rsaOriginalText);
            System.out.println("RSA加密后: " + rsaEncrypted);
            System.out.println("RSA解密后: " + rsaDecrypted);
            System.out.println("加解密结果: " + rsaOriginalText.equals(rsaDecrypted));
            System.out.println("\n=== 数字签名示例 ===");
            String dataToSign = "Important data to sign";
            String signature = rsaService.sign(dataToSign);
            boolean isValid = rsaService.verify(dataToSign, signature);
            System.out.println("待签名数据: " + dataToSign);
            System.out.println("数字签名: " + signature);
            System.out.println("签名验证结果: " + isValid);
            System.out.println("\n=== 密钥保存和加载示例 ===");
            String aesKey = aesService.getBase64Key();
            String rsaPublicKey = rsaService.getBase64PublicKey();
            String rsaPrivateKey = rsaService.getBase64PrivateKey();
            AesCryptoService newAesService = new AesCryptoService(aesKey);
            RsaCryptoService newRsaService = new RsaCryptoService(rsaPublicKey, rsaPrivateKey);
            String testText = "Test key persistence";
            String encryptedWithNewAes = newAesService.encrypt(testText);
            String decryptedWithNewAes = newAesService.decrypt(encryptedWithNewAes);
            System.out.println("密钥持久化测试: " + testText.equals(decryptedWithNewAes));
        } catch (CryptoException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void mixCryptoTest() throws IOException {
        try {
            RsaCryptoService rsaService = new RsaCryptoService();
            AesCryptoService aesService = new AesCryptoService();
            String originalData = "这是一段需要加密的重要数据";
            String encryptedData = aesService.encrypt(originalData);
            String encryptedAesKey = rsaService.encrypt(aesService.getBase64Key());
            System.out.println("混合加密结果:");
            System.out.println("加密后的数据: " + encryptedData);
            System.out.println("加密后的AES密钥: " + encryptedAesKey);
            String decryptedAesKey = rsaService.decrypt(encryptedAesKey);
            AesCryptoService decryptionAesService = new AesCryptoService(decryptedAesKey);
            String decryptedData = decryptionAesService.decrypt(encryptedData);
            System.out.println("\n解密结果: " + decryptedData);
            System.out.println("加解密成功: " + originalData.equals(decryptedData));

        } catch (CryptoException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void eccTest() throws IOException {
        try {
            System.out.println("=== ECC椭圆曲线加密示例 ===");
            EccCryptoService eccService = new EccCryptoService();
            System.out.println("ECC密钥信息: " + eccService.getKeyInfo());
            String originalText = "Hello, ECC Encryption!";
            String eccEncrypted = eccService.encrypt(originalText);
            String eccDecrypted = eccService.decrypt(eccEncrypted);
            System.out.println("原始文本: " + originalText);
            System.out.println("ECC加密后: " + eccEncrypted);
            System.out.println("ECC解密后: " + eccDecrypted);
            System.out.println("加解密结果: " + originalText.equals(eccDecrypted));
            System.out.println("\n=== ECC数字签名示例 ===");
            String dataToSign = "Important ECC signed data";
            String eccSignature = eccService.sign(dataToSign);
            boolean eccSignatureValid = eccService.verify(dataToSign, eccSignature);
            System.out.println("待签名数据: " + dataToSign);
            System.out.println("ECC签名: " + eccSignature);
            System.out.println("签名验证结果: " + eccSignatureValid);

        } catch (CryptoException e) {
            e.printStackTrace();
        }

    }
}

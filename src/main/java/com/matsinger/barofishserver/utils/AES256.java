package com.matsinger.barofishserver.utils;

import com.matsinger.barofishserver.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AES256 {

    @Value("${utils.aes256.secret-key}")
    private String key;

    public String encrypt(String plaintext) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new BusinessException("카드 정보를 인코딩하는데 실패했습니다.");
        }
    }

    public String decrypt(String ciphertext) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
            byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new BusinessException("카드 정보를 디코딩하는데 실패했습니다.");
        }
    }
}

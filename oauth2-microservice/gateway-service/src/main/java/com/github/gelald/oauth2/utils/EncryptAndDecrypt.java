package com.github.gelald.oauth2.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

public class EncryptAndDecrypt {
    private final static String KEY = "Joy-youth-youth-";

    /**
     * 将16进制转换为二进制
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            int r = high * 16 + low;
            // 转换成2进制
            result[i] = (byte) r;
        }
        return result;
    }

    /**
     * 加密
     */
    public static String encryptAES2(String data) {
        try {
            // 构建密钥
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // "算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder buf = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                buf.append(String.format("%02x", b & 0xff));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     */
    public static String decryptAES2(String content) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // "算法/模式/补码方式"
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] bytes = parseHexStr2Byte(content);
            assert bytes != null;
            byte[] result = cipher.doFinal(bytes);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

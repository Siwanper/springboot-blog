package com.swp.springboot.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

/**
 * 描述:
 * 工具类
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-10-26 9:32 AM
 */
public class Tools {
    private static final Random random = new Random();
    /**
     * AES 对称加密解密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String enAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrytedBytes = cipher.doFinal(data.getBytes());
        return new BASE64Encoder().encode(encrytedBytes);
    }

    public static String deAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] cipherTextBytes = new BASE64Decoder().decodeBuffer(data);
        byte[] decValue = cipher.doFinal(cipherTextBytes);
        return new String(decValue);
    }

    /**
     * 判断字符串是否为数字和有正确的值
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        // Pattern pattern=Pattern.compile("[0-9]*");
        // return pattern.matcher(str).matches();
        if (null != str && 0 != str.trim().length() && str.matches("\\d*")) {
            return true;
        }

        return false;
    }

    public static int rand(int min, int max) {
        return random.nextInt(max) % (max - min + 1) + min;
    }
}

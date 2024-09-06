package com.example.miniobackend.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import java.util.Random;

public class HelperUtil {

    public static String md5(String str) {
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        return md5.digestHex(str);
    }

    /**
     * 返回随机字符串
     *
     * @param length 要生成的长度
     * @return String
     */
    public static String randomString(int length) {
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int strLength = str.length();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(strLength);
            stringBuffer.append(str.charAt(index));
        }
        return stringBuffer.toString();
    }

}

package org.code4j.jproxy.crypyo;/**
 * Description : MD5Util
 * Created by YangZH on 16-5-25
 *  下午2:25
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Description : MD5Util
 * Created by YangZH on 16-5-25
 * 下午2:25
 */

public class MD5Util {

    public static byte[] getMD5(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return digest.digest(string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSHA(String string){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return Arrays.toString(digest.digest(string.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

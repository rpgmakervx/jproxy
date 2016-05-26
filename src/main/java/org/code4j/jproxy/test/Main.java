package org.code4j.jproxy.test;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午2:28
 */

import org.code4j.jproxy.server.ProxyServer;

/**
 * Description :
 * Created by YangZH on 16-5-25
 * 下午2:28
 */

public class Main {

    public static void main(String[] args) {
//        System.out.println(Arrays.toString(MD5Util.getMD5("x")));
//        System.out.println(MD5Util.getMD5("x").length*8);
//        System.out.println(Arrays.toString(new String(MD5Util.getMD5("x")).getBytes()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ProxyServer("www.baidu.com",80).startup(9524);
            }
        }).start();
    }

}

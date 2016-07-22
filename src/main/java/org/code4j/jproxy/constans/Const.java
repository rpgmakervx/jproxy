package org.code4j.jproxy.constans;/**
 * Description : 
 * Created by YangZH on 16-6-13
 *  下午10:06
 */

import org.code4j.jproxy.util.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Description :
 * Created by YangZH on 16-6-13
 * 下午10:06
 */

public class Const {
//    public static final String PROTOCOL = "protocol";
    public static final String PROTOCOLNAME = "http";
    public static final String PROXY_PASS = "proxy_pass";
    public static final String LOCALHOST = "localhost";
    public static final String LISTEN = "listen";
    public static final String WEIGHT = "weight";
    public static final String HOST = "host";
    public static final String PORT = "port";

    public static final String PROXY_HOST;
    static {
        //PROTOCOLNAME = Configuration.getStringValue(PROTOCOL); 可配置的协议
        String proxy_host = Configuration.getStringValue(LOCALHOST);
        if (proxy_host.startsWith(PROTOCOLNAME)){
            try {
                proxy_host = new URL(proxy_host).getHost();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        PROXY_HOST = proxy_host;
    }

}

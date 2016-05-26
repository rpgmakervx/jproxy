package org.code4j.jproxy.proxy;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午7:45
 */

/**
 * Description :
 * Created by YangZH on 16-5-25
 * 下午7:45
 *
 * 多线程启动代理客户端。每一条客户端链接都对应一条线程
 */

public class ProxyAccessorLauncher implements Runnable{

    private ProxyAccessor proxyAccessor;

    public ProxyAccessorLauncher(ProxyAccessor proxyAccessor) {
        this.proxyAccessor = proxyAccessor;
    }

    @Override
    public void run() {
        proxyAccessor.connect();
    }
}

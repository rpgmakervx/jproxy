package org.code4j.jproxy.proxy.pool;/**
 * Description : LauncherPool
 * Created by YangZH on 16-5-25
 *  下午8:02
 */

import org.code4j.jproxy.proxy.ProxyAccessorLauncher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description : LauncherPool
 * Created by YangZH on 16-5-25
 * 下午8:02
 */

public class LauncherPool {

    private ExecutorService pool;


    public LauncherPool() {
        pool = Executors.newCachedThreadPool();
    }

    public void exec(ProxyAccessorLauncher launcher){
        pool.submit(launcher);
    }
}

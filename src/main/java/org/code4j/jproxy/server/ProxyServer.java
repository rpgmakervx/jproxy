package org.code4j.jproxy.server;/**
 * Description : server
 * Created by YangZH on 16-5-25
 *  上午8:14
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.code4j.jproxy.handler.ServerChildHandler;

import java.net.InetSocketAddress;

/**
 * Description : server
 * Created by YangZH on 16-5-25
 * 上午8:14
 */

public class ProxyServer {

    public String remoteHost;

    public int remotePort;

    public ProxyServer(String hostname, int port) {
        this.remoteHost = hostname;
        this.remotePort = port;
    }

    public void startup(int port){
        System.out.println("正在启动服务。。。");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        ChannelFuture f = null;
        try {
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChildHandler(new InetSocketAddress(remoteHost, remotePort)))
                    .option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            f = b.bind(port).sync();
            System.out.println("服务已启动");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}

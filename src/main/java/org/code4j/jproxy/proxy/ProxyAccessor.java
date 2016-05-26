package org.code4j.jproxy.proxy;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午7:26
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.code4j.jproxy.handler.ProxyAccessorChildHandler;

import java.net.InetSocketAddress;

/**
 * Description :
 * Created by YangZH on 16-5-25
 * 下午7:26
 */

public class ProxyAccessor {

    private String remoteHost;

    private int remotePort;
    private ChannelFuture future;
    private ProxyAccessorChildHandler handler;

    public ProxyAccessor(String remoteHost, int remotePort, ProxyAccessorChildHandler handler) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.handler = handler;
    }

    public ProxyAccessor(InetSocketAddress address,ProxyAccessorChildHandler handler){
        this.handler = handler;
        this.remoteHost = address.getHostName();
        this.remotePort = address.getPort();
    }

    public void connect(){
        System.out.println("代理访问端正在链接远程服务器.....");
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(group).channel(NioSocketChannel.class).handler(handler)
                    .option(ChannelOption.TCP_NODELAY, true);
            future = b.connect(remoteHost, remotePort).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public void sendRequest(Object msg){
        System.out.println("向远程服务器 "+remoteHost+":"+remotePort+" 转发数据");
        future.channel().writeAndFlush(msg);
        System.out.println("sendRequest 中 代理服务的channelId: "+future.channel().id()+"\n");
    }
}

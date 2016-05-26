package org.code4j.jproxy.handler;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午10:52
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.code4j.jproxy.handler.http.HttpProxyAccessorHandler;

/**
 * Description :
 * Created by YangZH on 16-5-25
 * 下午10:52
 */

public class ProxyAccessorChildHandler extends ChannelInitializer<SocketChannel> {
    private ChannelHandlerContext ctx;

    public ProxyAccessorChildHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(new HttpProxyAccessorHandler(ctx));
    }
}

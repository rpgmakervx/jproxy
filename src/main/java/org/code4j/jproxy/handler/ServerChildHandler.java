package org.code4j.jproxy.handler;/**
 * Description : ChildHandler
 * Created by YangZH on 16-5-25
 *  上午9:13
 */

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.code4j.jproxy.handler.http.CSSFilterHandler;
import org.code4j.jproxy.handler.http.HttpServerHandler;

import java.net.InetSocketAddress;


/**
 * Description : ChildHandler
 * Created by YangZH on 16-5-25
 * 上午9:13
 */

public class ServerChildHandler extends ChannelInitializer<SocketChannel> {

    private InetSocketAddress address;

    public ServerChildHandler(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder",new HttpRequestDecoder());
        pipeline.addLast("encoder",new HttpResponseEncoder());
        pipeline.addLast("aggregator",new HttpObjectAggregator(1024000));
        pipeline.addLast("txthandler",new HttpServerHandler(address));
        pipeline.addLast("csshandler",new CSSFilterHandler(address));
//        pipeline.addLast("handler",new PrivateMessageHandler(address));
    }
}

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
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.code4j.jproxy.handler.http.ImageHandler;


/**
 * Description : ChildHandler
 * Created by YangZH on 16-5-25
 * 上午9:13
 */

public class ServerChildHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder",new HttpRequestDecoder());
        pipeline.addLast("encoder",new HttpResponseEncoder());
        pipeline.addLast("json_decoder",new JsonObjectDecoder());
        pipeline.addLast("aggregator",new HttpObjectAggregator(1024000));
        pipeline.addLast("handler",new ImageHandler());
//        pipeline.addLast("css_handler",new CSSFilterHandler(address));
//        pipeline.addLast("handler",new VPNHandler(address));
    }
}

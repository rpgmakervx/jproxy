package org.code4j.jproxy.handler.http.test;/**
 * Description : 
 * Created by YangZH on 16-6-4
 *  上午1:24
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Description :
 * Created by YangZH on 16-6-4
 * 上午1:24
 */

public class Handler1 extends SimpleChannelInboundHandler<HttpRequest> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        System.out.println("handler1并不打算处理数据 "+request.method());
        ctx.fireChannelRead(request);
    }
}

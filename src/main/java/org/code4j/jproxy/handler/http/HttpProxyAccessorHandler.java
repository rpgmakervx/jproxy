package org.code4j.jproxy.handler.http;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午7:39
 */

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Description :
 * Created by code4j on 16-5-25
 * 下午7:39
 *
 * 处理访问完远程服务器后，将数据返还给客户端
 */

public class HttpProxyAccessorHandler extends ChannelHandlerAdapter {

    private ChannelHandlerContext ctx;

    public HttpProxyAccessorHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("代理服务器链接到 目标服务器");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("得到代理服务的响应 "+(msg instanceof HttpResponse));
        System.out.println("-------------------------");
        System.out.println(msg);
        System.out.println("-------------------------");
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

package org.code4j.jproxy.handler.http;/**
 * Description : 
 * Created by YangZH on 16-5-26
 *  下午5:56
 */

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * Description :
 * Created by YangZH on 16-5-26
 * 下午5:56
 */

public class ImageFilterHandler extends ChannelHandlerAdapter {

    private InetSocketAddress address;

    public ImageFilterHandler(InetSocketAddress address) {
        this.address = address;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

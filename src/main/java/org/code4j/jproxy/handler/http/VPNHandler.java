package org.code4j.jproxy.handler.http;/**
 * Description : PrivateMessageHandler
 * Created by YangZH on 16-5-27
 *  下午4:43
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

/**
 * Description : PrivateMessageHandler
 * Created by YangZH on 16-5-27
 * 下午4:43
 */

public class VPNHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = Logger.getLogger(VPNHandler.class);
    private InetSocketAddress address;
    private String host;
    public VPNHandler(InetSocketAddress address){
        this.address = address;
        host = "http://"+address.getHostName()+":"+address.getPort();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }


    private void response(ChannelHandlerContext ctx,byte[] contents,String contentType) throws UnsupportedEncodingException {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.wrappedBuffer(contents));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes() + "");
        ctx.channel().writeAndFlush(response);
        ctx.close();
    }
}

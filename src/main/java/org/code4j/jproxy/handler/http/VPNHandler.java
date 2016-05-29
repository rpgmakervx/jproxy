package org.code4j.jproxy.handler.http;/**
 * Description : PrivateMessageHandler
 * Created by YangZH on 16-5-27
 *  下午4:43
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URL;

/**
 * Description : PrivateMessageHandler
 * Created by YangZH on 16-5-27
 * 下午4:43
 */

public class VPNHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private Logger logger = Logger.getLogger(VPNHandler.class);
    private InetSocketAddress address;
    private String host;
    public VPNHandler(InetSocketAddress address){
        this.address = address;
        host = "http://"+address.getHostName()+":"+address.getPort();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        URL url = new URL(host+request.uri());
        logger.debug("url : "+url.toString()+"  uri : "+request.uri());
//        ProxyClient client = new ProxyClient(url.toString());
        CharSequence authentication = request.headers().get("authentication");
        System.out.println("can in? "+authentication);
        if (authentication!=null&&"passby".equals(authentication.toString())){
            response(ctx,"200 ok!!!".getBytes(), WebUtil.TEXT_PLAIN);
        }else{
            response(ctx,"403 fail!!!".getBytes(), WebUtil.TEXT_PLAIN);
        }
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

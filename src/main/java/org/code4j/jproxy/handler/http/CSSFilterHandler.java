package org.code4j.jproxy.handler.http;/**
 * Description : CSSFilterHandler
 * Created by YangZH on 16-5-26
 *  下午5:01
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.util.DiskUtil;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description : CSSFilterHandler
 * Created by YangZH on 16-5-26
 * 下午5:01
 */

public class CSSFilterHandler extends SimpleChannelInboundHandler{

    private InetSocketAddress address;
    public CSSFilterHandler(InetSocketAddress address){
        this.address = address;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        Pattern css_pattern = Pattern.compile(".*\\.("+ WebUtil.CSS_FILE+")");
        System.out.println("css filter: "+msg.getClass().getName());
        if (msg instanceof DefaultHttpRequest){
            DefaultHttpRequest request = (DefaultHttpRequest) msg;
            if (css_pattern.matcher(request.uri()).matches()){
                System.out.println("包含css 文件");
                URL url = new URL("http://"+address.getHostName()+":"+address.getPort()+request.uri());
                ProxyClient client = new ProxyClient(url.toString());
                String css = client.fetchText(HttpMethod.GET);
                fetchAllResource(WebUtil.fetchImageFromString(url.toString(),css),request.uri(),ctx);
                byte[] bytes = client.fetchText(HttpMethod.GET).getBytes();
                response(ctx, bytes);
                DiskUtil.saveToDisk(address.getHostName(), request.uri(), bytes);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("CSS 解析异常");
        cause.printStackTrace();
    }

    public void response(ChannelHandlerContext ctx,byte[] contents) throws UnsupportedEncodingException {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.wrappedBuffer(contents));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes() + "");
        ctx.channel().writeAndFlush(response);
    }

    public void fetchAllResource(List<String> urls,String uri,ChannelHandlerContext ctx){
        for (String url:urls){
            ProxyClient client = new ProxyClient(url);
            byte[] bytes = client.fetchImage(HttpMethod.GET);
            try {
                response(ctx, bytes);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            DiskUtil.saveToDisk(address.getHostName(),uri, bytes);
        }
    }
}

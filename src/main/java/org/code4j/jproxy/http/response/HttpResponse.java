package org.code4j.jproxy.http.response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.code4j.jproxy.http.head.Method;

/**
 * Description :
 * Created by YangZH on 16-6-17
 * 上午1:08
 */

public class HttpResponse implements Response{
    private DefaultFullHttpResponse response;
    private ChannelHandlerContext ctx;
    private int statusCode;

    private Method method;
    @Override
    public void addHeader(String key,String value){
        response.headers().add(key,value);
    }
    @Override
    public void setContentType(String contentType){
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,contentType);
    }

    public void setCharacterEncoding(String encoding){
    }

    @Override
    public void write(Object msg){
        ctx.writeAndFlush(msg);
    }

    @Override
    public void writeAndClose(Object msg) {
        ctx.writeAndFlush(msg);
        close();
    }
    @Override
    public void close(){
        ctx.close();
    }
}

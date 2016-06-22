package org.code4j.jproxy.http.response.factory;/**
 * Description : 
 * Created by YangZH on 16-6-17
 *  上午1:10
 */

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.util.Date;

/**
 * Description :
 * Created by YangZH on 16-6-17
 * 上午1:10
 */

public class HttpResponseFactory {

    public static FullHttpResponse getResponse(String content,HttpResponseStatus status){
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
            response.setStatus(status);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
            response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            response.headers().set(HttpHeaderNames.DATE,new Date());
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                    response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION,  HttpHeaderValues.KEEP_ALIVE);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

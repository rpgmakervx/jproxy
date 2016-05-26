package org.code4j.jproxy.client;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午10:48
 */

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;

import java.util.Arrays;

/**
 * Description :
 * Created by YangZH on 16-5-25
 * 下午10:48
 */

public class ClientHandler extends ChannelHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("和服务器建立链接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到消息："+msg.getClass().getName());
        if (msg instanceof HttpContent){
            HttpContent response = (HttpContent)msg;
            System.out.println("isFailure : "+response.decoderResult().isFailure());
            System.out.println("content : \n"+ Arrays.toString(response.content().array()));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

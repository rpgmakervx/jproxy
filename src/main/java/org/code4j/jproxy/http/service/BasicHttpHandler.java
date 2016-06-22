package org.code4j.jproxy.http.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.log4j.Logger;
import org.code4j.codecat.api.response.factory.HttpResponseFactory;
import org.code4j.codecat.commons.constants.Const;
import org.code4j.codecat.commons.util.JedisUtil;

import java.io.File;

/**
 * Description :
 * Created by code4j on 16-6-16
 * 下午7:46
 *
 * 这个类继承了netty的业务handler,封装这个类是避免用户直接接触netty,
 * 转而用codecat的api使用服务更加“简单” ( <— <— )| 但愿我能封装的好点
 */

public abstract class BasicHttpHandler extends ChannelInboundHandlerAdapter{

    private Logger logger = Logger.getLogger(BasicHttpHandler.class);

    /**
     * 提供给用户使用的业务方法
     * @param msg
     */
    public abstract Object service(Object msg);

    @Override
    public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        String uri = getURI(request.uri());
        if (request.uri().equals(File.separator)){
            String result = String.valueOf(service(request.uri()));
            responseTo(ctx, result, HttpResponseStatus.OK);
        }
        String root = getRoot(request.uri());
        logger.info("hash key? "+JedisUtil.hasKey(root));
        if (!JedisUtil.hasKey(root)){
            responseTo(ctx, Const.NOTFOUNG+"<p align='center'>no suitable jar!!</p>", HttpResponseStatus.NOT_FOUND);
        }
        if (this.getClass().isAnnotationPresent(Path.class)){
            Path path = this.getClass().getAnnotation(Path.class);
            System.out.println();
            if (uri.equals(path.value())){
                String result = String.valueOf(service(request.uri()));
                responseTo(ctx, result, HttpResponseStatus.OK);
            }else if (ctx.pipeline().last() == this){
                responseTo(ctx, Const.NOTFOUNG+"<p align='center'>no suitable app!!</p>", HttpResponseStatus.NOT_FOUND);
            }else{
                ctx.fireChannelRead(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public final void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush(HttpResponseFactory.getResponse(
                        "<p>"+cause.getMessage()+"</p>", HttpResponseStatus.INTERNAL_SERVER_ERROR));
        ctx.close();
        cause.printStackTrace();
    }


    private void responseTo(ChannelHandlerContext ctx,String content, HttpResponseStatus status){
        FullHttpResponse response =  HttpResponseFactory.getResponse(content, status);
        ctx.writeAndFlush(response);
        ctx.close();
    }

    private String getURI(String url){
        if (url.equals(File.separator)){
            return File.separator;
        }
        String[] path_segement = url.split(File.separator);
        String root_path = path_segement[1];
        return url.substring(root_path.lastIndexOf(root_path) + root_path.length()+1);
    }


    private String getRoot(String url){
        if (url == null||url.equals(File.separator)||url.isEmpty()){
            return "/";
        }
        String[] path_segement = url.split(File.separator);
        return path_segement[1];
    }
}

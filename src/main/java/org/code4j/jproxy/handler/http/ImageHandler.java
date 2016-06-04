package org.code4j.jproxy.handler.http;/**
 * Description : SocksServerHandler
 * Created by YangZH on 16-5-25
 *  上午9:18
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.server.IPSelector;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description : SocksServerHandler
 * Created by YangZH on 16-5-25
 * 上午9:18
 */

public class ImageHandler extends ChannelInboundHandlerAdapter{

    private InetSocketAddress address;

    /**
     * 每次请求都重新获取一次地址
     */
    public void fetchInetAddress(){
        this.address = IPSelector.weight();
        System.out.println("新获取的地址-->  "+address.getHostName()+":"+address.getPort());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        messageReceived(ctx,msg);
    }

    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest request = (HttpRequest) msg;
        if (request.method().equals(HttpMethod.GET)){
            Pattern pattern = Pattern.compile(".+\\.("+ WebUtil.IMAGE+").*");
            String context = "";
            byte[] bytes = null;
            CloseableHttpResponse response = null;
            //读取图片
            if (pattern.matcher(request.uri()).matches()){
                fetchInetAddress();
                ProxyClient client = new ProxyClient(address,WebUtil.ROOT.equals(request.uri())?"":request.uri());
                //在这里强转类型，如果使用了聚合器，就会被阻塞
                System.out.println("读取到图片 " + request.uri());
                response = client.fetchImage();
                bytes = client.getResponseBytes(response);
                response(ctx, bytes, response.getAllHeaders());
            }else{
//                ReferenceCountUtil.retain(request);
                ctx.fireChannelRead(request);
            }
        }else{
//            ReferenceCountUtil.retain(request);
            ctx.fireChannelRead(request);
        }
//        System.out.println("ImageHandler并不想处理");
//        ctx.fireChannelRead(request);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void response(ChannelHandlerContext ctx,byte[] contents,Header[] headers) throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(contents, 0, contents.length);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,byteBuf);
        System.out.println("response header ---------------");
        for (Header header:headers){
            response.headers().set(header.getName(),header.getValue());
            System.out.println(header.getName()+"::"+header.getValue());
        }
        System.out.println("end header ---------------");
        ctx.channel().writeAndFlush(response);
        ctx.close();
    }
    private void fetchImageResource(List<String> uris,ChannelHandlerContext ctx,HttpHeaders headers){
        for (String uri:uris){
            ProxyClient client;
            CloseableHttpResponse response = null;
            if (uri.contains(WebUtil.HTTP)||uri.contains(WebUtil.HTTPS)){
                uri = uri.replace(WebUtil.HTTP,"").replace(WebUtil.HTTPS,"");
                String host = uri.split("/")[0];
                uri = uri.replace(host,"");
                client = new ProxyClient(new InetSocketAddress(host,80),uri);
            }else{
                client = new ProxyClient(address,uri);
            }
            response = client.fetchImage(headers);
            byte[] bytes = client.getResponseBytes(response);
            try {
                response(ctx, bytes, response.getAllHeaders());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将CSS中的图片读取出来
     * @param context   CSS文件内容
     * @param request   请求对象
     * @param ctx       上下文
     */
    private void fetchResource(String context,HttpRequest request,ChannelHandlerContext ctx){
        Pattern css_pattern = Pattern.compile(".+\\.(" + WebUtil.CSS_FILE + ").*");
        if (css_pattern.matcher(request.uri()).matches()){
            fetchImageResource(WebUtil.fetchImageFromString(context),ctx,request.headers());
        }
    }



}

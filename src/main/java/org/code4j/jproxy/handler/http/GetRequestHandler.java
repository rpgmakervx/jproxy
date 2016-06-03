package org.code4j.jproxy.handler.http;/**
 * Description : CSSFilterHandler
 * Created by YangZH on 16-5-26
 *  下午5:01
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.server.IPSelector;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Description : CSSFilterHandler
 * Created by YangZH on 16-5-26
 * 下午5:01
 */

public class GetRequestHandler extends SimpleChannelInboundHandler<HttpRequest>{
    private InetSocketAddress address;

    public GetRequestHandler(boolean autoRelease) {
        super(autoRelease);
    }

    /**
     * 每次请求都重新获取一次地址
     */
    public void fetchInetAddress(){
        this.address = IPSelector.filter();
        System.out.println("新获取的地址-->  "+address.getHostName()+":"+address.getPort());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        String context = "";
        byte[] bytes = null;
        CloseableHttpResponse response = null;
        boolean isGet = request.method().equals(HttpMethod.GET);
        boolean isJSON = "application/json".equals(request.headers().get("Content-Type"));
        System.out.println("isGet  "+isGet+"  isJSON  "+isJSON);
        if (isGet){
            fetchInetAddress();
            ProxyClient client = new ProxyClient(address,WebUtil.ROOT.equals(request.uri())?"":request.uri());
            if (isJSON){
                System.out.println("GET 业务请求");
                response = client.fetchText(request.headers());
                context = client.getResponse(response);
                //redis缓存
                bytes = context.getBytes();
                response(ctx, bytes, response.getAllHeaders());
            }else{
                System.out.println("GET 页面请求");
                response = client.fetchText(request.headers());
                context = client.getResponse(response);
                //CDN缓存
                bytes = context.getBytes();
                response(ctx, bytes, response.getAllHeaders());
            }
        }else{
            System.out.println("非GET请求或JSON类型  "+request.uri());
            ReferenceCountUtil.retain(request);
            ctx.fireChannelRead(request);
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

}

package org.code4j.jproxy.handler.http;/**
 * Description : SocksServerHandler
 * Created by YangZH on 16-5-25
 *  上午9:18
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.server.IPSelector;
import org.code4j.jproxy.util.DiskUtil;
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

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private InetSocketAddress address;

    /**
     * 每次请求都重新获取一次地址
     */
    public void fetchInetAddress(){
        this.address = IPSelector.filter();
        System.out.println("新获取的地址-->  "+address.getHostName()+":"+address.getPort());
    }
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        fetchInetAddress();
        //在这里强转类型，如果使用了聚合器，就会被阻塞
        ProxyClient client = new ProxyClient(IPSelector.filter(),WebUtil.ROOT.equals(request.uri())?"":request.uri());
        Pattern pattern = Pattern.compile(".+\\.("+ WebUtil.IMAGE+").*");
        byte[] bytes;
        //读取图片
        if (pattern.matcher(request.uri()).matches()){
            System.out.println("读取到图片 " + request.uri());
            bytes = client.fetchImage(request.method(),request.headers());
            response(ctx, bytes, WebUtil.IMAGE_PNG);
        }else {
            //读取文本
            System.out.println("读取到文本 " + request.uri());
            String context = client.fetchText(request.method(),request.headers());
            //同时检查文本如果是CSS，再读取CSS中的图片
            fetchResource(context, request, ctx);
            bytes = context.getBytes();
            response(ctx, bytes, WebUtil.TEXT_HTML);
        }
        DiskUtil.saveToDisk(address.getHostName(), request.uri(), bytes);
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

    private void response(ChannelHandlerContext ctx,byte[] contents,String contentType) throws UnsupportedEncodingException {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.wrappedBuffer(contents));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes() + "");
        ctx.channel().writeAndFlush(response);
        ctx.close();
    }
    private void fetchImageResource(List<String> uris,HttpHeaders headers){
        for (String uri:uris){
            ProxyClient client;
            if (uri.contains(WebUtil.HTTP)||uri.contains(WebUtil.HTTPS)){
                uri = uri.replace(WebUtil.HTTP,"").replace(WebUtil.HTTPS,"");
                System.out.println("uri --> "+uri);
                String host = uri.split("/")[0];
                uri = uri.replace(host,"");
                System.out.println("host --> "+host);
                client = new ProxyClient(new InetSocketAddress(host,80),uri);
            }else{
                client = new ProxyClient(address,uri);
            }
            byte[] bytes = client.fetchImage(HttpMethod.GET,headers);
//            if (!ImageUtil.isImage(bytes)){
//                ctx.close();
//                return;
//            }
            DiskUtil.saveToDisk(address.getHostName(),uri, bytes);
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
            System.out.println("包含css 文件 "+request.uri());
            byte[] bytes = context.getBytes();
            fetchImageResource(WebUtil.fetchImageFromString(context),request.headers());
            try {
                response(ctx, bytes, WebUtil.TEXT_CSS);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

//    private void fetchResource(String context,HttpRequest request,ChannelHandlerContext ctx){
//        Pattern css_pattern = Pattern.compile(".+\\.(" + WebUtil.CSS_FILE + ").*");
//        System.out.println("包含css 文件 "+request.uri()+"  "+css_pattern.matcher(context).matches());
//        if (css_pattern.matcher(context).matches()){
//            byte[] bytes = context.getBytes();
//            fetchImageResource(WebUtil.fetchImageFromString(context),request.headers());
//            try {
//                response(ctx, bytes, WebUtil.TEXT_CSS);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    //62 line
//        System.out.println("tomcat 的响应："+response);
//        if (msg instanceof HttpContent){
//            System.out.println("-------------------------");
//            System.out.println(msg);
//            System.out.println("-------------------------");
//            ProxyAccessor accessor = new ProxyAccessor(address,new ProxyAccessorChildHandler(ctx));
//            ProxyAccessorLauncher launcher = new ProxyAccessorLauncher(accessor);
//            LauncherPool pool = new LauncherPool();
//            pool.exec(launcher);
//            accessor.connect();
//            accessor.sendRequest(msg);
//        }
}

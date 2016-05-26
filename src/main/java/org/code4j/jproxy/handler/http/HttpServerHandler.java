package org.code4j.jproxy.handler.http;/**
 * Description : SocksServerHandler
 * Created by YangZH on 16-5-25
 *  上午9:18
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.util.DiskUtil;
import org.code4j.jproxy.util.ImageUtil;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description : SocksServerHandler
 * Created by YangZH on 16-5-25
 * 上午9:18
 */

public class HttpServerHandler extends ChannelHandlerAdapter {

    private InetSocketAddress address;
    private String host;
    public HttpServerHandler(InetSocketAddress address){
        this.address = address;
        host = "http://"+address.getHostName()+":"+address.getPort();
    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
//        System.out.println("创建了一个链接");
////        String res = "<h1>Welcome to Code4J's Home!!</h1>";
////        response(ctx, res);
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //在这里强转类型，如果使用了聚合器，就会被阻塞
        DefaultHttpRequest request = (DefaultHttpRequest) msg;
        URL url = new URL(host+request.uri());
        System.out.println("url : "+url.toString()+"  uri : "+request.uri());
        ProxyClient client = new ProxyClient(url.toString());
        System.out.println("打开链接");
        Pattern pattern = Pattern.compile(".*\\."+WebUtil.IMAGE);
        Pattern css_pattern = Pattern.compile(".*\\.("+ WebUtil.CSS_FILE+")");
        System.out.println("正则分析结束"+pattern.matcher(request.uri()).matches());
        byte[] bytes = null;
        if (pattern.matcher(request.uri()).matches()){
            System.out.println("读取到图片");
            bytes = client.fetchImage(HttpMethod.GET);
            if (!ImageUtil.isImage(bytes)){
                ctx.close();
                return;
            }
            response(ctx, bytes, WebUtil.IMAGE_PNG);
        }else {
            System.out.println("读取到文本");
            if (css_pattern.matcher(request.uri()).matches()){
                System.out.println("包含css 文件");
                String css = client.fetchText(HttpMethod.GET);
                bytes = css.getBytes();
                fetchImageResource(WebUtil.fetchImageFromString(host, css), request.uri(), ctx);
                response(ctx, bytes, WebUtil.TEXT_CSS);
            }else{
                bytes = client.fetchText(HttpMethod.GET).getBytes();
                response(ctx, bytes, WebUtil.TEXT_HTML);
            }
//            bytes = client.fetchText().getBytes();
//            response(ctx, bytes, WebUtil.TEXT_HTML);
        }
        DiskUtil.saveToDisk(address.getHostName() ,request.uri(), bytes);
        ctx.close();

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private void response(ChannelHandlerContext ctx,byte[] contents,String contentType) throws UnsupportedEncodingException {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.wrappedBuffer(contents));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                response.content().readableBytes() + "");
        ctx.channel().writeAndFlush(response);
    }
    private void fetchImageResource(List<String> urls,String uri,ChannelHandlerContext ctx){
        for (String url:urls){
            System.out.println("新的图片路径： "+url);
            ProxyClient client = new ProxyClient(url);
            byte[] bytes = client.fetchImage(HttpMethod.GET);
            if (!ImageUtil.isImage(bytes)){
                ctx.close();
                return;
            }
            System.out.println("读取到CSS中的图片");
            DiskUtil.saveToDisk(address.getHostName(),uri, bytes);
        }
    }
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

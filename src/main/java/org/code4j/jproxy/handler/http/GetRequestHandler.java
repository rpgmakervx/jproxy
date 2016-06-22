package org.code4j.jproxy.handler.http;/**
 * Description : CSSFilterHandler
 * Created by YangZH on 16-5-26
 *  下午5:01
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.dao.RequestDataDao;
import org.code4j.jproxy.server.IPSelector;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description : CSSFilterHandler
 * Created by YangZH on 16-5-26
 * 下午5:01
 */

public class GetRequestHandler extends ChannelInboundHandlerAdapter {
    private InetSocketAddress address;
    private RequestDataDao dao = new RequestDataDao();
    private ExecutorService threadPool = Executors.newCachedThreadPool();

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
        threadPool.submit(new Task(ctx,msg));
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
    private void response(ChannelHandlerContext ctx,byte[] contents) throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(contents, 0, contents.length);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,byteBuf);
        ctx.channel().writeAndFlush(response);
        ctx.close();
    }

    class Task implements Runnable{
        Object msg;
        ChannelHandlerContext ctx;

        public Task( ChannelHandlerContext ctx,Object msg) {
            this.msg = msg;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            String context = "";
            byte[] bytes = null;
            CloseableHttpResponse response = null;
            HttpRequest request = (HttpRequest) msg;
            boolean isGet = request.method().equals(HttpMethod.GET);
            boolean isJSON = "application/json".equals(request.headers().get("Content-Type"));
            try {
                if (isGet){
                    fetchInetAddress();
                    ProxyClient client = new ProxyClient(address, WebUtil.ROOT.equals(request.uri())?"":request.uri());
                    if (isJSON){
                        System.out.println("GET 业务请求");
                        //redis缓存
                        String cache = dao.get(request.uri(),"");
                        if (cache == null ||cache.isEmpty()){
                            response = client.fetchText(request.headers());
                            context = client.getResponse(response);
                            dao.save(request.uri(),"",context);
                            bytes = context.getBytes();
                            response(ctx, bytes, response.getAllHeaders());
                        }else{
                            response(ctx, cache.getBytes());
                        }
                    }else{
                        response = client.fetchText(request.headers());
                        context = client.getResponse(response);
                        //CDN缓存
                        bytes = context.getBytes();
                        response(ctx, bytes, response.getAllHeaders());
                    }
                }else{
                    System.out.println("非GET请求或JSON类型  "+request.uri());
                    ctx.fireChannelRead(request);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

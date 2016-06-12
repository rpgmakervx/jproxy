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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Description : SocksServerHandler
 * Created by YangZH on 16-5-25
 * 上午9:18
 */

public class ImageHandler extends ChannelInboundHandlerAdapter{

    private InetSocketAddress address;
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

    class Task implements Runnable{

        Object msg;
        ChannelHandlerContext ctx;

        public Task( ChannelHandlerContext ctx,Object msg) {
            this.msg = msg;
            this.ctx = ctx;
        }
        @Override
        public void run() {
            HttpRequest request = (HttpRequest) msg;
            try {
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
                        ctx.fireChannelRead(request);
                    }
                }else{
                    ctx.fireChannelRead(request);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}

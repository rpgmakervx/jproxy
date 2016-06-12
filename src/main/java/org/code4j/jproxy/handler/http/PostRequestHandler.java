package org.code4j.jproxy.handler.http;/**
 * Description : 
 * Created by YangZH on 16-6-3
 *  下午10:23
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.dao.RequestDataDao;
import org.code4j.jproxy.server.IPSelector;
import org.code4j.jproxy.util.JSONUtil;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description :
 * Created by YangZH on 16-6-3
 * 下午10:23
 */

public class PostRequestHandler extends ChannelInboundHandlerAdapter {

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

    //    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        threadPool.submit(new Task(ctx,msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
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
        System.out.println("没有请求头，回写数据");
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
            HttpRequest request = (HttpRequest)msg;
            try {
                if (request.method().equals(HttpMethod.POST)){
                    fetchInetAddress();
                    CloseableHttpResponse response = null;
                    ProxyClient client = new ProxyClient(address, WebUtil.ROOT.equals(request.uri())?"":request.uri());
                    byte[] bytes = null;
                    System.out.println("POST 请求");
                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
                    if (decoder.isMultipart()){
                        StringBuffer sb = new StringBuffer();
                        try{
                            String paramstr = null;
                            List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
                            // 读取从客户端传过来的参数
                            int index = 0;
                            for (InterfaceHttpData data : postList) {
                                String name = data.getName();
                                String value = null;
                                if (InterfaceHttpData.HttpDataType.Attribute == data.getHttpDataType()) {
                                    MemoryAttribute attribute = (MemoryAttribute) data;
                                    attribute.setCharset(CharsetUtil.UTF_8);
                                    value = attribute.getValue();
                                    sb.append(name).append("=").append(value);
                                    if (!(index == postList.size()-1)){
                                        sb.append("&");
                                    }
                                }
                            }
                            paramstr = sb.toString();
                            //redis先查询，命中就不请求了。
                            String cache = dao.get(request.uri(),paramstr);
                            if (cache == null ||cache.isEmpty()){
                                response = client.postMultipartEntityRequest(JSONUtil.requestParam(paramstr), request.headers());
                                String responseStr = client.getResponse(response);
                                bytes = responseStr.getBytes();
                                response(ctx, bytes, response.getAllHeaders());
                            }else{
                                response(ctx, cache.getBytes());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if (request instanceof HttpContent) {
                        HttpContent httpContent = (HttpContent) request;
                        ByteBuf content = httpContent.content();
                        String message = content.toString(io.netty.util.CharsetUtil.UTF_8);
                        if (JSONUtil.isJson(message)){
                            System.out.println("json 数据");
                            String cache = dao.get(request.uri(),message);
                            if (cache == null ||cache.isEmpty()){
                                System.out.println("cache并没有命中！");
                                response = client.postJsonRequest(message, request.headers());
                                String responseStr = client.getResponse(response);
                                dao.save(request.uri(),message,responseStr);
                                bytes = responseStr.getBytes();
                                response(ctx, bytes, response.getAllHeaders());
                            }else{
                                System.out.println("cache命中！");
                                response(ctx, cache.getBytes());
                            }
                        }else{
                            System.out.println("key-value 数据");
                            String cache = dao.get(request.uri(),message);
                            if (cache == null ||cache.isEmpty()){
                                System.out.println("cache并没有命中！");
                                response = client.postEntityRequest(JSONUtil.requestParam(message), request.headers());
                                String responseStr = client.getResponse(response);
                                dao.save(request.uri(),message,responseStr);
                                bytes = responseStr.getBytes();
                                response(ctx, bytes, response.getAllHeaders());
                            }else {
                                System.out.println("cache命中！");
                                response(ctx, cache.getBytes());
                            }
                        }
                    }
                }else{
                    System.out.println("不是post请求");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

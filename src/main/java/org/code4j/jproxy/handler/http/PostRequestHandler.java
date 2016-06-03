package org.code4j.jproxy.handler.http;/**
 * Description : 
 * Created by YangZH on 16-6-3
 *  下午10:23
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.server.IPSelector;
import org.code4j.jproxy.util.JSONUtil;
import org.code4j.jproxy.util.WebUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Description :
 * Created by YangZH on 16-6-3
 * 下午10:23
 */

public class PostRequestHandler extends SimpleChannelInboundHandler<HttpRequest> {

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
        if (request.method().equals(HttpMethod.POST)){
            fetchInetAddress();
            CloseableHttpResponse response = null;
            ProxyClient client = new ProxyClient(address, WebUtil.ROOT.equals(request.uri())?"":request.uri());
            String context = "";
            byte[] bytes = null;
            System.out.println("POST 请求");
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            if (decoder.isMultipart()){
                StringBuffer sb = new StringBuffer();
                try{
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
                    response = client.postMultipartEntityRequest(JSONUtil.requestParam(sb.toString()), request.headers());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if (request instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) request;
                ByteBuf content = httpContent.content();
                String message = content.toString(io.netty.util.CharsetUtil.UTF_8);
                if (JSONUtil.isJson(message)){
                    System.out.println("json 数据");
                    JSONUtil.strToMap(message);
                    response = client.postJsonRequest(message, request.headers());
                }else{
                    System.out.println("key-value 数据");
                    response = client.postEntityRequest(JSONUtil.requestParam(message), request.headers());
                }
            }
            String responseStr = client.getResponse(response);
            bytes = responseStr.getBytes();
            response(ctx, bytes, response.getAllHeaders());
        }else{
            System.out.println("不是post请求");
        }
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
}

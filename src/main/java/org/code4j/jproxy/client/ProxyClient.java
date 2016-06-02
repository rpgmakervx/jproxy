package org.code4j.jproxy.client;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午10:47
 */

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description :
 * Created by YangZH on 16-5-25
 * 下午10:47
 */

public class ProxyClient {


    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httpPost;
    HttpGet httpGet;
    HttpHost httpHost;
    String host;
    String HOST;
    public ProxyClient(InetSocketAddress address,String uri){
        this.host = HOST = address.getHostName();
        boolean isLocal = host.equals("localhost");
        httpHost = new HttpHost(host,address.getPort());
        String url = "http://";
        if (isLocal){
            url += host+":"+address.getPort()+uri;
            httpPost = new HttpPost(url);
            httpGet = new HttpGet(url);
            HOST = host+":"+address.getPort();
        }else if (!host.contains("http://")){
            url += host+":"+address.getPort()+uri;
            httpPost = new HttpPost(url);
            httpGet = new HttpGet(url);
        }else{
            url = "http://"+host+":"+address.getPort()+uri;
            httpPost = new HttpPost(host+address.getPort()+uri);
            httpGet = new HttpGet(host+address.getPort()+uri);
        }
        System.out.println("client url : "+url);
    }

    public void setUrl(String url){
        httpPost = new HttpPost(url);
        httpGet = new HttpGet(url);
    }
    //    private ChannelFuture future;
//
//    public void connect(String remoteHost,int remotePort){
//        System.out.println("代理访问端正在链接远程服务器.....");
//        EventLoopGroup group = new NioEventLoopGroup();
//        Bootstrap b = new Bootstrap();
//        try {
//            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                    socketChannel.pipeline().addLast(new HttpResponseEncoder());
//                    socketChannel.pipeline().addLast(new HttpRequestDecoder());
//                    socketChannel.pipeline().addLast(new ClientHandler());
//                }
//            }).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_BACKLOG, 128);
//            future = b.connect(remoteHost, remotePort).sync();
//            sendRequest();
//            sendRequest();
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//            group.shutdownGracefully();
//        }
//    }

    public CloseableHttpResponse fetchText(HttpMethod method,HttpHeaders headers){
        String body = "";
        setHeader(httpGet,headers);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpHost,httpGet);
            System.out.println(httpGet.getURI()+" response code : "+response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public CloseableHttpResponse fetchImage(HttpMethod method,HttpHeaders headers){
        byte[] body = {};
        CloseableHttpResponse response = null;
        setHeader(httpGet,headers);
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private List<NameValuePair> setRequestData(Map<String,String> params){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String,String> entry:params.entrySet()){
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        return nameValuePairs;
    }
    private void setHeader(HttpRequestBase httpRequest,HttpHeaders headers){
        for (CharSequence name:headers.names()){
            if ("Host".equalsIgnoreCase(name.toString())){
                httpRequest.setHeader(name.toString(), HOST);
                System.out.println(name.toString() + ":" + HOST);
            } else if (!name.toString().equals("Content-Length")){
                httpRequest.setHeader(name.toString(), headers.get(name).toString());
                System.out.println(name.toString()+":"+headers.get(name).toString());
            }
        }
    }

    public CloseableHttpResponse postJsonRequest(String json,HttpHeaders headers){
        setHeader(httpPost,headers);
        StringEntity s = null;
        CloseableHttpResponse response = null;
        try {
            s = new StringEntity(json);
            httpPost.setEntity(s);
            response = httpclient.execute(httpPost);
            System.out.println(httpPost.getURI() + " response code : " + response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public CloseableHttpResponse postEntityRequest(Map<String,String> param,HttpHeaders headers){
        setHeader(httpPost,headers);
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(setRequestData(param),"UTF-8"));
            response = httpclient.execute(httpPost);
            System.out.println(httpPost.getURI() + " response code : " + response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getResponse(CloseableHttpResponse response){
        String body = "";
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            try {
                body = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                //释放链接
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return body;
    }

    public byte[] getResponseBytes(CloseableHttpResponse response){
        byte[] body = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            try {
                body = EntityUtils.toByteArray(entity);
                EntityUtils.consume(entity);
                //释放链接
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return body;
    }
//    public void sendRequest(){
//        System.out.println("客户端发送消息");
//        URI uri = null;
//        try {
//            uri = new URI("http://127.0.0.1:9524");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        String msg = "Are you ok?";
//        DefaultFullHttpRequest request = null;
//        try {
//            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
//                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        // 构建http请求
//        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
//        request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html");
//        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes()+"");
//        this.future.channel().writeAndFlush(request);
//    }

    public static void main(String[] args) throws URISyntaxException, IOException {
//        System.out.println(new ProxyClient(new InetSocketAddress("localhost",8080),"/dobehub/user/login").fetchText(HttpMethod.GET));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("localhost:8080");
    }
}

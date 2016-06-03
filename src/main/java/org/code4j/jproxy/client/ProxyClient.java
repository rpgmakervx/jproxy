package org.code4j.jproxy.client;/**
 * Description : 
 * Created by YangZH on 16-5-25
 *  下午10:47
 */

import io.netty.handler.codec.http.HttpHeaders;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.code4j.jproxy.util.DiskUtil;

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

    public CloseableHttpResponse fetchText(){
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            System.out.println(httpGet.getURI()+" response code : "+response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public CloseableHttpResponse fetchImage(){
        CloseableHttpResponse response = null;
        httpGet.setHeader("Content-Type","image/png");
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public CloseableHttpResponse fetchText(HttpHeaders headers){
        setHeader(httpGet,headers);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            System.out.println(httpGet.getURI()+" response code : "+response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public CloseableHttpResponse fetchImage(HttpHeaders headers){
        CloseableHttpResponse response = null;
        setHeader(httpGet,headers);
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private List<NameValuePair> setRequestData(Map<String,Object> params){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String,Object> entry:params.entrySet()){
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
        }
        return nameValuePairs;
    }

    private HttpEntity setMultipartEntity(Map<String,Object> param){
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (Map.Entry<String,Object> entry:param.entrySet()){
            Object value = entry.getValue();
            if (value instanceof String){
                StringBody sb = new StringBody((String) entry.getValue(), ContentType.MULTIPART_FORM_DATA);
                builder.addPart(entry.getKey(),sb);
            }else if (value instanceof Byte[]){
                builder.addBinaryBody(entry.getKey(),(byte[])value);
            }
        }
        return builder.build();
    }
    private void setHeader(HttpRequestBase httpRequest,HttpHeaders headers){
        System.out.println("REQUEST header ---------------");
        for (CharSequence name:headers.names()){
            String n = name.toString();
            boolean exclusive = n.equalsIgnoreCase("Content-Length")||n.equalsIgnoreCase("Referer")
                    ||n.equalsIgnoreCase("If-Modified-Since")||n.equalsIgnoreCase("If-None-Match");
            if ("Host".equalsIgnoreCase(name.toString())){
                httpRequest.setHeader(name.toString(), HOST);
                System.out.println(name.toString() + ":" + headers.get(name).toString());
            } else if (!exclusive){
                httpRequest.setHeader(name.toString(), headers.get(name).toString());
                System.out.println(name.toString() + ":" + headers.get(name).toString());
            }
        }
        System.out.println("END header ---------------");
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

    public CloseableHttpResponse postMultipartEntityRequest(Map<String,Object> param,HttpHeaders headers){
        setHeader(httpPost,headers);
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(setMultipartEntity(param));
            response = httpclient.execute(httpPost);
            System.out.println(httpPost.getURI() + " response code : " + response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public CloseableHttpResponse postEntityRequest(Map<String, Object> param,HttpHeaders headers){
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

    public static void main(String[] args) throws URISyntaxException, IOException {
        ProxyClient client = new ProxyClient(new InetSocketAddress("localhost",8080),"/tomcat.png");
        DiskUtil.saveToDisk("localhost:8080","/tomcat.png",client.getResponseBytes(client.fetchImage()));
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("localhost:8080");
    }
}

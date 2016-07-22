package org.code4j.jproxy.handler.http;/**
 * Description : 
 * Created by YangZH on 16-7-21
 *  上午10:02
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.code4j.jproxy.constans.Const;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Description :
 * Created by YangZH on 16-7-21
 * 上午10:02
 */
public class LoggerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("首当其冲的handler,收到了一条请求");
        HttpRequest request = (HttpRequest) msg;
        HttpHeaders headers = request.headers();
        Iterator<Map.Entry<String, String>> it = headers.iterator();
        String referername = headers.get("Referer");
        if (referername !=null){
            referername = referername.replace(File.separator,"");
            System.out.println(referername.equals(Const.PROXY_HOST));
            System.out.println("Referer : "+referername);
        }
        System.out.println("--------request headers------- uri:"+request.uri());
        while (it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        System.out.println("--------request headers-------");

        ctx.fireChannelRead(msg);
    }
}

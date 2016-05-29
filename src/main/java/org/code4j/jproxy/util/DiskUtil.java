package org.code4j.jproxy.util;/**
 * Description : 
 * Created by YangZH on 16-5-26
 *  下午3:40
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Description :
 * Created by YangZH on 16-5-26
 * 下午3:40
 */

public class DiskUtil {

    public static void saveToDisk(String host,String uri,byte[] contents){
        System.out.println(uri+" 保存前的URI");
        if (uri.equals(File.separator)){
            uri = File.separator+"index";
        }else if (uri.contains("?")){
            //去掉带？的资源
            String [] sub_uri = uri.split("\\?");
            if (sub_uri.length > 1){
                uri = File.separator+sub_uri[0];
            }
        }else if (uri.length() >= 255){
            //路径过长，去掉uri中？后面的参数，
            if (uri.contains(".")){
                if (uri.contains("?")){
                    uri = uri.substring(0,uri.indexOf("?")-1);
                }
            }else if (uri.contains("?")){
                uri = uri.substring(0,uri.indexOf("?")-1);
            }
        }
        //NIO 磁盘写入
        File file = new File(WebUtil.WEB_ROOT+host+uri);
        FileOutputStream fos = null;
        try {
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            if (!file.exists()){
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            FileChannel inChannel = fos.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(contents);
            inChannel.write(byteBuffer);
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

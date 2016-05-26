package org.code4j.jproxy.util;/**
 * Description : 
 * Created by YangZH on 16-5-26
 *  下午3:40
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Description :
 * Created by YangZH on 16-5-26
 * 下午3:40
 */

public class DiskUtil {

    public static void saveToDisk(String host,String uri,byte[] contents){
        if (uri.equals(File.separator)){
            uri = File.separator+"index";
        }
        File file = new File(WebUtil.WEB_ROOT+host+uri);
        try {
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            if (!file.exists()){
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(contents);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

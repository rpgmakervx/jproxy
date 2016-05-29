package org.code4j.jproxy.util;/**
 * Description : 
 * Created by YangZH on 16-5-26
 *  下午2:50
 */

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description :
 * Created by YangZH on 16-5-26
 * 下午2:50
 */

public class WebUtil {

    public static final String WEB_ROOT = System.getProperty("user.dir")+ File.separator+"cache/";

    public static final String INDEX = "index";
    public static final String CSS_FILE = "css";
    public static final String JS_FILE = "js";
    public static final String IMAGE = "(png|ico|gif|jpg|jpeg|bmp|swf|swf)";

    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_CSS = "text/css";
    public static final String TEXT_HTML = "text/html";
    public static final String IMAGE_PNG = "image/png";
    public static final String INNER_IMAGE = "url\\(.*\\.(png|ico|jpg|jpeg|bmp|swf|swf)\\)";

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    public static final String ROOT = "/";

    public static List<String> fetchImageFromString(String content){
        System.out.println("在CSS中寻找图片资源");
        Pattern pattern = Pattern.compile(INNER_IMAGE);
        Matcher matcher = pattern.matcher(content);
        List<String> uris = new CopyOnWriteArrayList<String>();
        while(matcher.find()){
            System.out.println("找到一个图片");
            String path = matcher.group().replace("url(","").replace(")", "");
            System.out.println("path ---> : "+path);
            uris.add(File.separator+path);
        }
        return uris;
    }
}

/**
 * Description : 
 * Created by YangZH on 16-5-26
 *  下午2:16
 */

import io.netty.handler.codec.http.HttpMethod;
import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.util.ImageUtil;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Description :
 * Created by YangZH on 16-5-26
 * 下午2:16
 */

public class ProxyTest {

    @Test
    public void testParrtern(){
        Pattern pattern = Pattern.compile(".*\\.(png|ico|jpg|jpeg|bmp|swf|swf)");
        System.out.println(pattern.matcher("http://125.100.158/favor.png").matches());
    }

    @Test
    public void testImageAccess() throws Exception {
        ProxyClient client = new ProxyClient("http://127.0.0.1:8080/tomcat.png");
        File file = new File(System.getProperty("user.dir")+File.separator+"cache"+ File.separator+"tomcat.png");
        if (!file.exists()){
            System.out.println(file.getAbsolutePath());
            file.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(client.fetchImage(HttpMethod.GET));
//
        bos.flush();
        System.out.println("is image? "+ ImageUtil.isImage(client.fetchImage(HttpMethod.GET)));
    }

    @Test
    public void testPath(){
        System.out.println(System.getProperty("user.dir")+"cache");
        System.out.println(getClass().getClassLoader().getResource("txt"));
    }

    @Test
    public void testSaveFile() throws IOException {
        File file = new File("/home/code4j/newfile");
        file.createNewFile();
    }
}

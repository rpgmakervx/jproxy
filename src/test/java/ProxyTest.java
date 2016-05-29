/**
 * Description : 
 * Created by YangZH on 16-5-26
 *  下午2:16
 */

import org.code4j.jproxy.client.ProxyClient;
import org.code4j.jproxy.server.IPSelector;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description :
 * Created by YangZH on 16-5-26
 * 下午2:16
 */

public class ProxyTest {

    @Test
    public void testParrtern(){
        Pattern pattern = Pattern.compile("url\\(.*\\.(png|ico|jpg|jpeg|bmp|swf|swf)\\)");
//        Pattern pattern = Pattern.compile("(url\\()(http://).*?(?:jpg|png|gif)(?=\\))");
        Matcher matcher = pattern.matcher("background: #fff url(bg-upper.png) repeat-x top left;");
        System.out.println(matcher.matches());
        while (matcher.find()){
            System.out.println(matcher.group());
        }
    }

    @Test
    public void testImageAccess() throws Exception {
        ProxyClient client = new ProxyClient(new InetSocketAddress("127.0.0.1",8080),"/tomcat.css");
        File file = new File(System.getProperty("user.dir")+File.separator+"cache"+ File.separator+"tomcat.css");
        if (!file.exists()){
            System.out.println(file.getAbsolutePath());
            file.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//        bos.write(client.fetchImage(HttpMethod.GET));
//
        bos.flush();
//        System.out.println("is image? "+ ImageUtil.isImage(client.fetchImage(HttpMethod.GET)));
    }

    @Test
    public void testPath(){
        System.out.println(System.getProperty("user.dir")+"cache");
        System.out.println(getClass().getClassLoader().getResource("txt"));
    }

    @Test
    public void testSaveFile() throws IOException {
//        Pattern css_pattern = Pattern.compile(".+\\.(css).*");
//        System.out.println(css_pattern.matcher("<link href=\"tomcat.css\" rel=\"stylesheet\" type=\"text/css\" />").matches());
        Pattern pattern = Pattern.compile("url\\(.*\\.(png|ico|jpg|jpeg|bmp|swf|swf)\\)");
//        pattern.matcher("")
    }

    @Test
    public void testConfig() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/config.json");
        System.out.println(is);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String str = "";
        while ((str = br.readLine()) != null){
            sb.append(str);
        }
        System.out.println(sb);
        int weight = (int) Math.rint(Float.valueOf("2.1"));
        System.out.println();
    }

    @Test
    public void testLoadBalance(){
        InetSocketAddress address = IPSelector.filter();
        System.out.println(address.getHostName()+":"+address.getPort());
    }
}

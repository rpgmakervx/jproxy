package org.code4j.jproxy.http.response;/**
 * Description : 
 * Created by YangZH on 16-6-21
 *  上午12:59
 */

/**
 * Description :
 * Created by YangZH on 16-6-21
 * 上午12:59
 */

public interface Response {

    public void addHeader(String key, String value);

    public void setContentType(String contentType);

    public void write(Object msg);

    public void writeAndClose(Object msg);
    public void close();


}

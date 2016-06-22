package org.code4j.jproxy.http.session;

/**
 * Description :
 * Created by YangZH on 16-6-16
 * 下午8:22
 */

public interface IHttpSession {

    public void setAttribute(String key, String value);

    public Object getAttribute(String key);

    public void removeAttribute(String key);

    public void removeAllAttribute();
}

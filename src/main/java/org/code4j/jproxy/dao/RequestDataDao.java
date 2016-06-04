package org.code4j.jproxy.dao;/**
 * Description : 
 * Created by YangZH on 16-6-4
 *  下午8:42
 */

import org.code4j.jproxy.crypt.EncryptUtil;
import org.code4j.jproxy.util.JedisUtil;

/**
 * Description :
 * Created by YangZH on 16-6-4
 * 下午8:42
 */

public class RequestDataDao {

    public void save(String url,String param,String data){
        String key = EncryptUtil.hash(url+param,EncryptUtil.SHA1);
        JedisUtil.set(key,data);
        System.out.println("保存完，key="+key+" , value="+data);
    }

    private boolean isHit(String key){
        String value = JedisUtil.get(key);
        if (value == null||value.isEmpty()){
            return false;
        }
        return true;
    }
    public boolean isHit(String url,String param){
        String key = EncryptUtil.hash(url+param,EncryptUtil.SHA1);
        String value = JedisUtil.get(key);
        if (value == null||value.isEmpty()){
            return false;
        }
        return true;
    }

    public String get(String url,String param){
        String key = EncryptUtil.hash(url+param,EncryptUtil.SHA1);
        return JedisUtil.get(key);
    }
}

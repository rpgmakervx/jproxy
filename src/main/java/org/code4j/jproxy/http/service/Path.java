package org.code4j.jproxy.http.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description :
 * Created by YangZH on 16-6-17
 * 下午12:11
 */
// 使用JDK的元数据Annotation：Retention
@Retention(RetentionPolicy.RUNTIME)
// 使用JDK的元数据Annotation：Target
@Target(ElementType.TYPE)
public @interface Path {
    String value() default "/";
}

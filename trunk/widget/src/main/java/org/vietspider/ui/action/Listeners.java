package org.vietspider.ui.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 27, 2006  
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Listeners {
  Class<?>[] value() default void.class ;
}

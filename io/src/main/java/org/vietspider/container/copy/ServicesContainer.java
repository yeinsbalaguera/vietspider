/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.container.copy;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.container.copy.ServiceConfig.ServiceType;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 3, 2007
 */
public class ServicesContainer {

  private static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

  /*public static <T> void put(Class<T> clazz) { put(clazz.getName(), clazz); }

  public static <T> void put(String id, Class<T> clazz) {
    try {
      map.put(id, create(clazz));
    }catch (Exception e) {
      throw new RuntimeException(e);
    }
  }*/

  public static <T> T get(Class<T> clazz) { return get(clazz.getName(), clazz); }

  public static <T> T get(String id, Class<T> clazz) {
    ServiceType type = ServiceType.INSTANCE;
    ServiceConfig config = clazz.getAnnotation(ServiceConfig.class);
    if(config != null) type = config.type();
    return get(type, id, clazz);
  }
  
  public static void put(String id, Object instance) { map.put(id, instance); }

  @SuppressWarnings("unchecked")
  public static <T> T get(ServiceType type, String id, Class<T> clazz) {
    if(type == ServiceType.INSTANCE) {
      try {
        return create(clazz);
      }catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    if(type == ServiceType.SINGLE_FINAL) {
      Object service = map.get(id);
      if(service != null) return clazz.cast(service);
      try{        
        service = create(clazz);
        map.put(id, service);
        return clazz.cast(service);
      }catch (Exception e) {
        System.err.println("service " + service + " with id "+ id);
        throw new RuntimeException(e);
      }
    }

//  if(type == ServiceType.SOFT_REFERENCE) { 
    ThreadSoftReference<T> thread = (ThreadSoftReference<T>)map.get(clazz);
    if(thread == null) {
      thread = new ThreadSoftReference<T>(clazz);
      map.put(id, thread);
    }
    try {
      return clazz.cast(thread.getRef());
    }catch (Exception e) {
      map.remove(id);
      throw new RuntimeException(e);
    }
    /*}

    if(type == ServiceType.LAZY_FINAL) return clazz.cast(map.get(id));

    return null;*/
  }

  private static <T> T create(Class<T> clazz) throws Exception {
    Constructor<?> [] constructors = clazz.getDeclaredConstructors();
    java.util.Arrays.sort(constructors, new Comparator<Constructor<?>>() {
      public int compare(Constructor<?> cons1, Constructor<?> cons2) {
        return cons1.getParameterTypes().length - cons2.getParameterTypes().length;
      }
    });
    if(constructors.length < 1) throw new Exception("Not constructor in class "+clazz);
    Constructor<?> constructor = constructors[0];
    Class<?> [] classes = constructors[0].getParameterTypes();
    if(classes.length < 1) return clazz.newInstance();
    Object [] objs = new Object[classes.length];
    for(int i = 0; i< classes.length; i++){
      objs[i] = get(classes[i]);
    }
    constructor.setAccessible(true);
    return clazz.cast(constructor.newInstance(objs));
  }

  private static class ThreadSoftReference<T> extends ThreadLocal<SoftReference<T>> {

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    private ThreadSoftReference(Class<?> clazz) { this.clazz = (Class<T>)clazz; }  

    private T getRef() throws Exception {
      SoftReference<T> sr = get();
      if (sr == null || sr.get() == null) {
        sr = new SoftReference<T>(create(clazz));
        set(sr);
      }
      return sr.get();
    }

  }

}

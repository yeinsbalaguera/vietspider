/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.startup;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 28, 2007  
 */
public class StartServer {
 
  public StartServer() throws Exception {  
//    System.setProperty("vietspider.path", "D:\\VietSpider build 11");
//    
//    new Thread() {
//      public void run() {
        try {
          ClassLoaderCreator creator = new ClassLoaderCreator();
          URLClassLoader loader = creator.createLoader(true);
          Class<?> clazz  = loader.loadClass("org.vietspider.VietSpider");
          Method method = clazz.getDeclaredMethod("currentInstance", new Class[]{});
          System.setProperty("vietspider.server.icon", "false");
          method.invoke(null, new Object[]{});
          System.setProperty("vietspider.client.icon", "true");
        } catch (Exception e) {
          e.printStackTrace();
        }
//      }
//    }.start();
    
    Set<String> values = new HashSet<String>();
    
    int index = 1;
    while(true) {
      String key  = "vietspider.data.path."+String.valueOf(index);
      final String value = System.getProperty(key);
      if(value == null || value.trim().length() < 1) break;
      File file = new File(value);
      values.add(file.getAbsolutePath());
      index++;
    }
    
    ClassLoaderCreator creator = new ClassLoaderCreator();
    String appPath  = creator.lookupPath();
    if(appPath != null) {
      File file = new File(appPath);
      file = new File(file.getAbsolutePath());
      File [] files = file.getParentFile().listFiles();
      for(int i = 0; i < files.length; i++) {
        if(files[i].isFile()) continue;
        if(files[i].getName().startsWith("data_")) {
          values.add(files[i].getAbsolutePath());
        }
      }
    }
    
   Iterator<String> iterator = values.iterator();
    while(iterator.hasNext()) {
      final String value = iterator.next();
      new Thread() {
        public void run() {
          try {
//            System.setProperty("vietspider.path", "D:\\VietSpider build 11");
            ClassLoaderCreator creator_ = new ClassLoaderCreator();
            URLClassLoader loader = creator_.createLoader(false);
            Class<?> clazz  = loader.loadClass("org.vietspider.VietSpider");
            Method method = clazz.getDeclaredMethod(
                "currentInstance", new Class[]{ClassLoader.class, String.class});
            method.invoke(null, new Object[]{loader, value});
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }.start();
    }
  }
  
  public static void main(String[] args) throws Exception {
    new StartServer();
  }
  
}

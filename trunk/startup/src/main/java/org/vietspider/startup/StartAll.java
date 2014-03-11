/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.startup;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 28, 2007
 */
public class StartAll {

  public Object instance;

  public StartAll() throws Exception {  
    ClassLoaderCreator creator = new ClassLoaderCreator();
    URLClassLoader loader = creator.createLoader(true);
    Class<?> main  = null;
    try {
      Class<?> clazz  = loader.loadClass("org.vietspider.VietSpider");
      Method method = clazz.getDeclaredMethod("currentInstance", new Class[]{});
      System.setProperty("vietspider.server.icon", "false");
      method.invoke(null, new Object[]{});
      System.setProperty("vietspider.client.icon", "true");
    } catch (Exception e) {
      System.err.println(e.toString());
    }
    main  = loader.loadClass("org.vietspider.gui.workspace.VietSpiderClient");
    main.newInstance();
  }

  public static void main(String[] args) {    
    try{      
      new StartAll();
    }catch(Exception exp){
      exp.printStackTrace();
    }
    System.exit(0);  
  }

}

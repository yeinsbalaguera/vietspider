import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.vietspider.startup.ClassLoaderCreator;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 26, 2008  
 */
public class rvietspider {
  
  public Object instance;

  public rvietspider(String[] args) throws Exception {  
    ClassLoaderCreator creator = new ClassLoaderCreator();
    URLClassLoader loader = creator.createLoader(true);
    try {
      Class<?> clazz  = loader.loadClass("org.vietspider.client.ClientConsole");
      Method method = clazz.getDeclaredMethod("start", Object[].class);
      if(args == null) {
        method.invoke(null, new Object[]{});
      } else {
        method.invoke(null, new Object[]{args});
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {    
    try {      
      new rvietspider(args);
    }catch(Exception exp){
      exp.printStackTrace();
    }
    System.exit(0);  
  }
}
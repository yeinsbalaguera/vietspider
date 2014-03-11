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
public class vshell {
  
  public Object instance;

  public vshell(String[] args) throws Exception {  
    ClassLoaderCreator creator = new ClassLoaderCreator();
    URLClassLoader loader = creator.createLoader(true);
    try {
      Class<?> clazz  = loader.loadClass("org.vietspider.app.client.cmd.Shell");
      Method method = clazz.getDeclaredMethod("main", String[].class);
      if(args == null) {
        method.invoke(null, new Object[]{});
      } else {
        method.invoke(null, new Object[]{args});
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {    
    try {      
      new vshell(args);
    }catch(Exception exp){
      exp.printStackTrace();
    }
    System.exit(0);  
  }
}
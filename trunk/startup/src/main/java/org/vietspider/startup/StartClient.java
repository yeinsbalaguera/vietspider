/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.startup;

import java.net.URLClassLoader;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 28, 2007
 */
public class StartClient {

  public Object instance;

  public StartClient() throws Exception {  
    ClassLoaderCreator creator = new ClassLoaderCreator();
    URLClassLoader loader = creator.createLoader(true);
    
    System.setProperty("vietspider.client.icon", "false");
    Class<?> main  = loader.loadClass("org.vietspider.gui.workspace.VietSpiderClient");
    main.newInstance();
  }
  
  public static void main(String[] args) {    
    try{      
      new StartClient();
    }catch(Exception exp){
      exp.printStackTrace();
    }
    System.exit(0);  
  }
  
}

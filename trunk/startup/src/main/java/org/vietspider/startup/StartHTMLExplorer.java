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
public class StartHTMLExplorer {

  public Object instance;

  public StartHTMLExplorer() throws Exception {  
    ClassLoaderCreator creator = new ClassLoaderCreator();
    URLClassLoader loader = creator.createLoader(true);
    Class<?> main = loader.loadClass("org.vietspider.ui.htmlexplorer.HTMLExplorerMain");
    main.newInstance();
  }

  public static void main(String[] args) {    
    try{      
      new StartHTMLExplorer();
    }catch(Exception exp){
      exp.printStackTrace();
    }
    System.exit(0);  
  }

}

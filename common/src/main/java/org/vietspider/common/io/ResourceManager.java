/***************************************************************************
 * Copyright 2001-2005 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.common.io;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * @since Jul 12, 2005
 */
public class ResourceManager {

  protected ResourceBundle resources;
  
  public ResourceManager(){
  }

  public ResourceManager(Class<?> clazz, Properties properties, String name) {
    loadResources(clazz, properties, name);
  }
  
  protected void loadResources(Class<?> clazz, Properties properties, String name) {
    ClassLoader loader = clazz.getClassLoader() ;
    String locale = null;
    try {      
      locale = properties.get("locale").toString();
      if( locale == null || locale.trim().length() == 0) locale = "vn";
    } catch( Exception exp){
      LogService.getInstance().setThrowable(null, exp);
      locale = "vn";
    }    
    resources = ResourceBundle.getBundle("resources.i18n."+locale+"."+name, Locale.ENGLISH, loader);
  }
  
  
  public String getLabel(String name){
    try {
      return resources.getString(name);
    } catch( Exception exp){
      LogService.getInstance().setMessage(exp, name);
      return name;
    }
  }
  
}

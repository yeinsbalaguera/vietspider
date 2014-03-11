/***************************************************************************
 * Copyright 2001-2005 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * @since Jul 12, 2005
 */
public class ResourceManager2 {

  protected ResourceBundle resources;
  
  public ResourceManager2(){
  }

  public ResourceManager2(Class<?> clazz, Properties properties, String name)  {
    try {
      loadResources(clazz, properties, name, true);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(null, e);
    }
  }
  
  protected void loadResources(Class<?> clazz, Properties properties, String name, boolean reload) throws Exception {
    ClassLoader loader = clazz.getClassLoader() ;
    String locale = null;
    try {      
      if(properties != null && properties.get("locale") != null) {
        locale = properties.get("locale").toString();
      } else {
        locale = "vn";
      }
      if( locale == null || locale.trim().length() == 0) locale = "vn";
      locale = locale.trim();
    } catch( Exception exp){
      LogService.getInstance().setThrowable(null, exp);
      locale = "vn";
    }    
    
    File file = UtilFile.getFolder("client/resources/i18n/"+locale);
    file  = new File(file, name+".properties");
    if(file.exists()) {
      try {
        resources = new PropertyResourceBundle(file);
      } catch (Exception exp) {
        LogService.getInstance().setThrowable(null, exp);
      }
    } 
    
    if(resources == null) {
      try {
        resources = ResourceBundle.getBundle("resources.i18n."+locale+"."+name, Locale.ENGLISH, loader);
      } catch (Exception e) {
        if(!reload) throw e;
        file = UtilFile.getFolder("client/resources/i18n/vn");
        file  = new File(file, name+".properties");
        if(!file.exists()) throw e;
        try {
          resources = new PropertyResourceBundle(file);
        } catch (Exception exp) {
          LogService.getInstance().setThrowable(null, exp);
        }
      }
    }
  }
  
  public String getLabel(String name){
    try {
      return resources.getString(name);
    } catch( Exception exp){
//      LogService.getInstance().setMessage(exp, name);
      return name;
    }
  }
  
  public String getRawLabel(String name){
    try {
      return resources.getString(name);
    } catch( Exception exp){
//      LogService.getInstance().setMessage(exp, name);
      return null;
    }
  }
  
}

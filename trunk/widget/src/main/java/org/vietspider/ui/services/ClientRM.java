/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.services;

import java.io.File;
import java.net.URL;
import java.util.ListResourceBundle;
import java.util.Properties;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.PropertyResourceBundle;
import org.vietspider.common.io.ResourceManager2;
import org.vietspider.common.io.UtilFile;
import org.vietspider.ui.widget.ApplicationFactory;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 16, 2008  
 */
public class ClientRM extends ResourceManager2 {
  
  public ClientRM(ListResourceBundle resourceBundle ) {
    this.resources = resourceBundle;
  }
  
  public ClientRM(String name) {
    this(null, name);
  }
  
  public ClientRM(Class<?> clazz, String name) {
    PropertiesFile propFile = new PropertiesFile(true);
    Properties properties = new Properties();
    try {
      properties = propFile.load(UtilFile.getFile("client", "config.properties"));
      String locale = null;
      if(properties != null && properties.get("locale") != null) {
        locale = properties.get("locale").toString();
      } else {
        locale = "vn";
      }
      
      File file = new File(UtilFile.getFolder("client/resources/i18n/"+locale), name+".properties");
      if(file.exists() && file.length() > 0) {
        resources = new PropertyResourceBundle(file);
        return;
      }
      
      file = UtilFile.getFolder("client/resources/i18n/"+locale);
      file  = new File(file, name+".properties");
      if(!file.exists() && clazz != null) {
        URL url = clazz.getResource(name+".properties");
        if(url != null) {
          resources = new PropertyResourceBundle(new File(url.toURI()));
          return;
        }
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    try {
      loadResources(ApplicationFactory.class, properties, name, true);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  

}

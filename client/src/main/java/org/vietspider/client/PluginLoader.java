/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class PluginLoader {
  
  private StringBuilder error = new StringBuilder();
  
  public PluginLoader() {
  }
  
  public ClientPlugin[] createPlugins() {
    File file  = new File(UtilFile.getFolder("client"), "plugin.config");
    if(file == null || !file.exists()) return new ClientPlugin[0];
    String value = null;
    try {
      byte [] bytes = RWData.getInstance().load(file);
      value = new String(bytes);
    } catch (Exception e) {
      if(error.length() > 0) error.append('\n');
      error.append(e.toString());
    }
    
    if(value == null) return new ClientPlugin[0];
    String [] elements = value.split("\n");
    List<ClientPlugin> list = new ArrayList<ClientPlugin>(); 
    for(String pluginClass : elements) {
      
      if(pluginClass == null 
          || pluginClass.trim().isEmpty()
          || pluginClass.charAt(0) == '#') continue;
      try {
        Class<?> clazz = Class.forName(pluginClass.trim());
        list.add((ClientPlugin)clazz.newInstance());
      } catch (Exception e) {
        if(error.length() > 0) error.append('\n');
        error.append(e.toString());
      }
    }
    return list.toArray(new ClientPlugin[list.size()]);
  }

  public String getError() { return error.toString(); }
  
}

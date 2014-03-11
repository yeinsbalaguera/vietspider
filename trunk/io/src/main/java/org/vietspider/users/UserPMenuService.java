/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.users;

import java.io.File;

import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 16, 2008  
 */
public class UserPMenuService {
  
  private static volatile UserPMenuService INSTANCE;
  
  public static synchronized UserPMenuService getInstance() {
    if(INSTANCE == null) INSTANCE = new UserPMenuService();
    return INSTANCE;
  }
  
  private InmemoryCache<String, String[]> pmenuCaches;
  
  public UserPMenuService() {
    pmenuCaches  = new InmemoryCache<String, String[]>("pmenus", 15);
  }
  
  public String[] listPMenu(String username) {
    String[] values = pmenuCaches.getCachedObject(username);
    if(values != null) return values;
    values = loadPMenu(username);
    pmenuCaches.putCachedObject(username, values);
    return values;
  }
  
  private String [] loadPMenu(String username) {
    File file = UtilFile.getFile("/system/user/pmenu/", username);
    if(!file.exists() || file.length() < 1) return new String[0];
    try {
      byte [] bytes = RWData.getInstance().load(file);
      return new String(bytes, Application.CHARSET).split("\n");
    } catch (Exception e) {
      return new String[0];
    }
  }
  
  public void savePMenu(String username, String value) throws Exception {
    File file = UtilFile.getFile("/system/user/pmenu/", username);
    RWData.getInstance().append(file, value.getBytes(Application.CHARSET));
  }
  
  public void deletePMenu(String username, String value) {
    String [] deletes = value.split("\n");
    StringBuilder builder = new StringBuilder();
    File file = UtilFile.getFile("/system/user/pmenu/", username);
    String [] values = loadPMenu(username);
    for(int i = 0; i < values.length; i++) {
      boolean add = true;
      for(int j = 0; j < deletes.length; j++) {
        if(values[i].equals(deletes[i])) {
          add = false;
          break;
        }
      }
      if(!add)  continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(values[i]);
    }
    file.delete();
    try {
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
}

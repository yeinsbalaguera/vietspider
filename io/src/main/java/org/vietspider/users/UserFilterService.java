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
import org.vietspider.common.text.NameConverter;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.user.Filter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 16, 2008  
 */
public class UserFilterService {
  
  private static volatile UserFilterService INSTANCE;
  
  public static synchronized  UserFilterService getInstance() {
    if(INSTANCE == null) INSTANCE = new UserFilterService();
    return INSTANCE;
  }
  
  private InmemoryCache<String, String[]> filterCaches;
  
  public UserFilterService() {
    filterCaches  = new InmemoryCache<String, String[]>("filters", 15);
  }
  
  public String[] listFilters(String username) {
    String[] values = filterCaches.getCachedObject(username);
    if(values != null) return values;
    values = loadFilters(username);
    filterCaches.putCachedObject(username, values);
    return values;
  }
  
  private String [] loadFilters(String username) {
    File folder = UtilFile.getFolder("/system/user/filter/"+username+"/");
    File [] files = folder.listFiles();
    if(files == null || files.length < 1) return new String [] {};
    String [] values = new String[files.length];
    NameConverter converter = new NameConverter();
    for(int i = 0; i < values.length; i++) {
      values[i] = converter.decode(files[i].getName());
    }
    return values;
  }
  public void saveFilter(String username, String name, String value) throws Exception {
    name = name.replace('-', ' ');
    saveFilter(username, new Filter(name, value, Filter.CONTENT));
  }
  
  public void saveFilter(String username, Filter filter) throws Exception {
    filter.setName(filter.getName().replace('-', ' '));
    File file = getFile(username, filter.getName());
    String xml = Object2XML.getInstance().toXMLDocument(filter).getTextValue();
    RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    filterCaches.remove(username);
  }
  
  public Filter loadFilter(String username, String name) {
    name = name.replace('-', ' ');
    File file = getFile(username, name);
    try {
      byte [] bytes = RWData.getInstance().load(file);
      return XML2Object.getInstance().toObject(Filter.class, bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }
  
  public void deleteFilter(String username, String name, String item) throws Exception {
    Filter filter = UserFilterService.getInstance().loadFilter(username, name);
    if(filter.getType() != Filter.DOMAIN || filter.getFilter() == null) return;
    String [] elements = filter.getFilter().split("\n");
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
      if(elements[i].equals(item)) continue;
      builder.append(elements[i]).append('\n');
    }
    filter.setFilter(builder.toString().trim());
    saveFilter(username, filter);
  }
  
  public void deleteFilter(String username, String name) {
    name = name.replace('-', ' ');
    File file = getFile(username, name);
    file.delete();
    filterCaches.remove(username);
  }
  
  private File getFile(String username, String name) {
    File folder = UtilFile.getFolder("/system/user/filter/"+username+"/");
    NameConverter converter = new NameConverter();
    return new File(folder, NameConverter.encode(name));
  }
}

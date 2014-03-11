/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.users;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.user.Group;
import org.vietspider.user.User;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 26, 2007  
 */
public class Organization {
  
//  private  InmemoryCache<String, User> userCaches;
//  private  InmemoryCache<String, Group> groupCaches;
  
  private static volatile Organization INSTANCE;
  
  public static synchronized  Organization getInstance() {
    if(INSTANCE == null) INSTANCE = new Organization();
    return INSTANCE;
  }
  
  public Organization() {
//    userCaches = new InmemoryCache<String, User>("user", 15);
//    userCaches.setLiveTime(30*60);
//    
//    groupCaches = new InmemoryCache<String, Group>("group", 5);
//    groupCaches.setLiveTime(60*60);
    
    initDB();
  }
  
  public int login(String username, String password) {
    if(username == null || username.trim().isEmpty()) return -2;
    User user = loadInstance("user", User.class, username);
    if(user == null) return -2;
    if(password == null) return -1;
    if(!user.getPassword().equals(password)) return -1;
    return user.getPermission();
  }
  
  public String[] listGroups() {
    File folder = UtilFile.getFolder("/system/user/group/");
    File [] files = folder.listFiles();
    if(files == null || files.length < 1) return new String[]{};
    String [] groups = new String[files.length];
    for(int i = 0; i < groups.length; i++) {
      groups[i] = files[i].getName();
    }
    return groups;
  }
  
  public <T> T loadInstance(String folder, Class<T> clazz, String name) {
    File file = UtilFile.getFolder("/system/user/"+folder+"/");
    file = new File(file, name);
    if(!file.exists()) return null;

    try {
      String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
      XMLDocument document = XMLParser.createDocument(xml, null);
      return XML2Object.getInstance().toObject(clazz, document);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("USER", e);
      return null;
    }
  }
  
  private void initDB() {
    File file = UtilFile.getFolder("/system/user/user/");
    if(file.exists() && file.list() != null && file.list().length > 0) {
      File [] files = file.listFiles();
      if(Application.LICENSE != Install.ENTERPRISE 
          && Application.LICENSE != Install.SEARCH_SYSTEM) {
        for(int i = 15; i < files.length; i++) {
          files[i].delete();
        }
      }
      return;
    }
    initUser(file);
    file = UtilFile.getFolder("/system/user/group/");
    initGroup(file);
  }
  
  private void initGroup(File folder) {
    Group group = new Group();
    group.setGroupName("admin");
    List<String> users = new ArrayList<String>();
    users.add("vietspider");
    group.setUsers(users);
    save(new File(folder, group.getGroupName()), group);
    
    group = new Group();
    group.setGroupName("guest");
    users = new ArrayList<String>();
    users.add("guest");
    List<String> categories = new ArrayList<String>();
    categories.add("*");
    group.setWorkingCategories(categories);
    save(new File(folder, group.getGroupName()), group);
  }
  
  private void initUser(File folder) {
    User user = new User();
    user.setUserName("vietspider");
    user.setEmail("vietspider@vietspider");
    user.setFullName("VietSpider");
    user.setPassword("vietspider");
    user.setPermission(User.ROLE_ADMIN);
    ArrayList<String> groups = new ArrayList<String>();
    groups.add("admin");
    user.setGroups(groups);
    save(new File(folder, user.getUserName()), user);
    
    user = new User();
    user.setUserName("guest");
    user.setEmail("guest@vietspider");
    user.setFullName("Guest");
    user.setPassword("guest");
    user.setPermission(User.DATA_READ_ONLY);
    groups = new ArrayList<String>();
    groups.add("guest");
    user.setGroups(groups);
    save(new File(folder, user.getUserName()), user);
  }
  
  private void save(File file, Object obj) {
    try {
      String xml = Object2XML.getInstance().toXMLDocument(obj).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      RWData.getInstance().save(file, bytes);
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
}

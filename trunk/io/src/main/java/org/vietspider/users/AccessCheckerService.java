/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.users;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.io.UtilFile;
import org.vietspider.user.AccessChecker;
import org.vietspider.user.AdminAccessChecker;
import org.vietspider.user.Group;
import org.vietspider.user.GuestAccessChecker;
import org.vietspider.user.MergeAccessChecker;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2008  
 */
public class AccessCheckerService {
  
  private static volatile AccessCheckerService INSTANCE;
  
  public synchronized  static AccessCheckerService getInstance() {
    if(INSTANCE == null) INSTANCE = new AccessCheckerService();
    return INSTANCE;
  }
  
  private  InmemoryCache<String, AccessChecker> checkerCaches;

  public AccessCheckerService() {
    checkerCaches = new InmemoryCache<String, AccessChecker>("checkers", 15);
    checkerCaches.setLiveTime(30*60);
  }
  
  public AccessChecker getAccessChecker(String username) {
    AccessChecker accessChecker = checkerCaches.getCachedObject(username);
    if(accessChecker != null) return accessChecker;
    accessChecker = createChecker(username);
    checkerCaches.putCachedObject(username, accessChecker);
    return accessChecker;
  }
  
  private AccessChecker createChecker(String username) {
    Organization org = Organization.getInstance();
    
    if(username == null || username.equals("guest")) {
      return createGuestAccessChecker();
    }
    
    User user = org.loadInstance("user", User.class, username);
    if(user == null) return createGuestAccessChecker();
    List<String> groupNames = user.getGroups();
    Set<String> categories = new HashSet<String>();
    
    for(String groupName : groupNames) {
      if(groupName.equals("admin")) return new AdminAccessChecker();
      Group group = org.loadInstance("group", Group.class, groupName); 
      List<String> groupCategories = group.getWorkingCategories();
      for(String category : groupCategories) {
        if(category == null || category.trim().isEmpty()) continue;
        categories.add(category);
      }
    }
    
    String [] arrCategories = categories.toArray(new String[categories.size()]);
    File file = UtilFile.getFolder("system/user/user/");
    file = new File(file, "guest");
    if(!file.exists() || file.length() < 1) return new AccessChecker(arrCategories);
    GuestAccessChecker guestAccessChecker = (GuestAccessChecker) getAccessChecker("guest");
    return new MergeAccessChecker(new AccessChecker(arrCategories), guestAccessChecker);
  }
  
  public GuestAccessChecker createGuestAccessChecker() {
    ConcurrentSkipListSet<String> list = new ConcurrentSkipListSet<String>();
    
    Organization organization = Organization.getInstance();
    
    String [] groupNames = organization.listGroups();
    for(String groupName: groupNames) {
      if(groupName.equals("guest")) continue;
      Group group = organization.loadInstance("group", Group.class, groupName); 
      if(group == null) continue;
      List<String> groupCategories = group.getWorkingCategories();
      for(int i = 0; i < groupCategories.size(); i++) {
        list.add(groupCategories.get(i));
      }
    }
    
    return new GuestAccessChecker(list.toArray(new String[list.size()]));
  }
  
}

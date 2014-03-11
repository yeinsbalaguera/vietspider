/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.user;

import java.io.Serializable;

import org.vietspider.model.Track;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2008  
 */
@SuppressWarnings("serial")
public class AccessChecker implements Serializable {
  
  protected String [] categories;
  
  public AccessChecker() {    
  }
  
  public AccessChecker(String [] categories){
    this.categories = categories;
  }
  
  public void computeMenu(Track menuInfo, boolean edit) { 
//    List<TrackId> categorieInfos = menuInfo.getTrackIds(TrackId.CATEGORY);
//    List<TrackId> newCategorieInfos = new ArrayList<TrackId>();
//    for(int i = 0; i < categorieInfos.size(); i++) {
//      if(!isPermitAccess(categorieInfos.get(i).getName(), edit)) continue;
//      newCategorieInfos.add(categorieInfos.get(i));;
//    }
//    menuInfo.setCategories(newCategorieInfos);
  }
  
  @SuppressWarnings("unused")
  public boolean isPermitAccess(String value, boolean edit) {
    if(categories == null) return true;
    for(String category : categories) {
      if(category == null || (category = category.trim()).isEmpty()) continue;
      if(category.length() == 1 && category.charAt(0) == '*') return true;      
      if(value.startsWith(category)) return true;
    }
    return false;
  }
  
  public boolean isPermitGroup(String value) {
    if(categories == null) return true;
    for(String category : categories) {
      if(category == null || category.trim().isEmpty()) continue;
      if(category.length() == 1 && category.charAt(0) == '*') return true;
      if(category.startsWith(value)) return true;
    }
    return false;
  }

  public String[] getCategories() { return categories; }

  public void setCategories(String[] categories) { this.categories = categories; }
  
}

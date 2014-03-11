/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 27, 2007  
 */
@NodeMap("menu-info")
public class MenuInfo {
  
  @NodesMap(value="categories", item="item")
  private List<CategoryInfo> categories = new ArrayList<CategoryInfo>();

  public List<CategoryInfo> getCategories() { return categories; }
  public void setCategories(List<CategoryInfo> categories) { this.categories = categories; }
  
  public void add(String value, int visit, int data,
      long link, /*long downloaded, */long  lastAccess) {
    int idx = value.lastIndexOf('.');
    if(idx < 1) return;
    String category  = value.substring(0, idx);
    String source = value.substring(idx+1, value.length());
    add(category, source, visit, data, link, /*downloaded,*/ lastAccess);
  }
  
  private void add(String category, String name, int visit,
      int data, long link,/* long downloaded,*/ long lastAccess) {
    for(CategoryInfo categoryInfo  : categories) {
      if(categoryInfo.getCategory().equals(category)) {
        categoryInfo.add(name, visit, data, link, /*downloaded,*/ lastAccess);
        return ;
      }
    }
    CategoryInfo newCategory = new CategoryInfo(category);
    newCategory.add(name, visit, data, link, /*downloaded, */lastAccess);
    categories.add(newCategory);
  }
  
  public int getTotalData() {
    int total = 0; 
    for(CategoryInfo category : categories) {
      total += category.getTotalData();
    }
    return total;
  }
  
  public CategoryInfo getCategoryInfo(String category) {
    for(CategoryInfo categoryInfo  : categories) {
      if(categoryInfo.getCategory().equals(category)) return categoryInfo;
    }
    return null;
  }
  
  public MenuInfo clone() {
    MenuInfo menuInfo = new MenuInfo();
    List<CategoryInfo> list = new ArrayList<CategoryInfo>();
    for(int i = 0; i < categories.size(); i++) {
      list.add(categories.get(i).clone());
    }
    menuInfo.setCategories(list);
    return  menuInfo;
  }

}

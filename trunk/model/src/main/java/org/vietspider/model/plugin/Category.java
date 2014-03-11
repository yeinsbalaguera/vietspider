/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 20, 2010  
 */
@NodeMap("category")
public class Category {
  
  @NodeMap("category-id")
  private String categoryId;
  @GetterMap("category-id")
  public String getCategoryId() { return categoryId; }
  @SetterMap("category-id")
  public void setCategoryId(String categoryid) { this.categoryId = categoryid; }
  
  @NodeMap("category-name")
  private String categoryName;
  @GetterMap("category-name")
  public String getCategoryName() { return categoryName; }
  @SetterMap("category-name")
  public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
  
  @NodesMap(value = "sub-categories", item="item")
  private List<Category> subCategories = new ArrayList<Category>();
  @GetterMap("sub-categories")
  public List<Category> getSubCategories() { return subCategories; }
  @SetterMap("sub-categories")
  public void setSubCategories(List<Category> categories) { this.subCategories = categories; }
}

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.abix;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2008  
 */
@NodeMap("drupal-categories")
public class DrupalCategoriesConfig {
  
  @NodesMap(value = "categories", item= "item")
  private DrupalCategory [] categories;
  
  public DrupalCategoriesConfig() {
    categories = new DrupalCategory[] {
       new DrupalCategory("category_1", "category_name_1")
    };
  }
  
  public DrupalCategoriesConfig(DrupalCategory [] categories) {
    this.categories = categories;
  }

  public DrupalCategory[] getCategories() {  return categories; }

  public void setCategories(DrupalCategory[] categories) { this.categories = categories; }
  
  @NodeMap("drupal-category")
  public static class DrupalCategory {
    
    @NodeMap("id")
    private String id;
    @NodeMap("name")
    private String name;
    
    public DrupalCategory() {
    }
    
    public DrupalCategory(String id, String name) {
      this.id = id;
      this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
  }

}

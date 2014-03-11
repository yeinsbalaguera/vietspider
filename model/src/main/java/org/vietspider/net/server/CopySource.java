/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.server;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2007  
 */
@NodeMap("copy_sources")
public class CopySource {

  @NodeMap("src_group")
  private String srcGroup;
  
  @NodeMap("des_group")
  private String desGroup;
  
  @NodeMap("src_category")
  private String srcCategory;
  
  @NodeMap("is_cut")
  private boolean delete = false;
  
  @NodesMap(value = "src_names", item = "name")
  private String [] srcNames;
  
  @NodesMap(value = "des_categories", item = "category")
  private String [] desCategories;

  public String getSrcCategory() { return srcCategory; }

  public void setSrcCategory(String srcCategory) { this.srcCategory = srcCategory; }

  public String[] getSrcNames() { return srcNames; }

  public void setSrcNames(String[] srcNames) { this.srcNames = srcNames; }

  public String [] getDesCategories() { return desCategories; }

  public void setDesCategories(String [] desCategories) { this.desCategories = desCategories; }
  
  public String getSrcGroup() { return srcGroup; }

  public void setSrcGroup(String srcGroup) { this.srcGroup = srcGroup; }

  public String getDesGroup() { return desGroup; }

  public void setDesGroup(String desGroup) { this.desGroup = desGroup; }

  public boolean isDelete() { return delete; }

  public void setDelete(boolean del) { this.delete = del; }
}

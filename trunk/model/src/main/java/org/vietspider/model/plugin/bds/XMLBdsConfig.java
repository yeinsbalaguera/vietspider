/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin.bds;

import org.vietspider.model.plugin.PluginConfig;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 8, 2008  
 */

@NodeMap("bds-config")
public class XMLBdsConfig extends PluginConfig {
  
  @NodeMap("homepage")
  private String homepage = "";
  //"http://localhost/joomla/";
  public String getHomepage() { return homepage; }
  public void setHomepage(String homepage) { this.homepage = homepage; }

  @NodeMap("charset")
  private String charset = "utf-8";
  public String getCharset() { return charset; }
  public void setCharset(String charset) { this.charset = charset; }
  
  @NodeMap("auto")
  private boolean auto = false;
  @GetterMap("auto")
  public boolean isAuto() { return auto; }
  @SetterMap("auto")
  public void setAuto(boolean value) { this.auto = value; }
 
  @NodeMap("upload-image")
  private boolean uploadImage = false;
  @GetterMap("upload-image")
  public boolean isUploadImage() { return uploadImage; }
  @SetterMap("upload-image")
  public void setUploadImage(boolean value) { this.uploadImage = value; }
  
  @NodeMap("alert-message")
  private boolean alertMessage = true;
  @GetterMap("alert-message")
  public boolean isAlertMessage() { return alertMessage; }
  @SetterMap("alert-message")
  public void setAlertMessage(boolean value) {alertMessage = value;}

  
  @NodeMap("login-address")
  private String loginAddress = "";
  //"http://localhost/joomla/administrator/index.php?option=com_login";
  @GetterMap("login-address")
  public String getLoginAddress() { return loginAddress; }
  @SetterMap("login-address")
  public void setLoginAddress(String urlLogin) { this.loginAddress = urlLogin; }
  
  @NodeMap("login-username")
  private String username = "admin";
  @GetterMap("login-username")
  public String getUsername() { return username; }
  @SetterMap("login-username")
  public void setUsername(String username) { this.username = username; }

  @NodeMap("login-password")
  private String password = "1234";
  @GetterMap("login-password")
  public String getPassword() { return password; }
  @SetterMap("login-password")
  public void setPassword(String password) { this.password = password; }
  
  @NodesMap("categories")
  private Category[] categories;
  @GetterMap("categories")
  public Category[] getCategories(){ return this.categories; }
  @SetterMap("categories")
  public void setCategories(Category [] value){ this.categories = value; }
  
  @NodesMap("regions")
  private Region[] regions;
  @GetterMap("regions")
  public Region[] getRegions(){ return this.regions; }
  @SetterMap("regions")
  public void setRegions(Region  [] value){ this.regions = value; }
  
  @NodeMap("category")
  public static class Category {
    
//    @NodeMap("section-id")
//    private String sectionId;
//    @GetterMap("section-id")
//    public String getSectionId() { return sectionId; }
//    @SetterMap("section-id")
//    public void setSectionId(String sectionid) { this.sectionId = sectionid; }
   
    @NodeMap("category-name")
    private String categoryName;
    @GetterMap("category-name")
    public String getCategoryName() { return categoryName; }
    @SetterMap("category-name")
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    @NodeMap("category-id")
    private String categoryId;
    @GetterMap("category-id")
    public String getCategoryId() { return categoryId; }
    @SetterMap("category-id")
    public void setCategoryId(String categoryid) { this.categoryId = categoryid; }
  }
  
  @NodeMap("region")
  public static class Region {
    
    @NodeMap("region-name")
    private String regionName;
    @GetterMap("region-name")
    public String getRegionName() { return regionName; }
    @SetterMap("region-name")
    public void setRegionName(String name) { this.regionName = name; }
    
    @NodeMap("region-id")
    private String regionId;
    @GetterMap("region-id")
    public String getRegionId() { return regionId; }
    @SetterMap("region-id")
    public void setRegionId(String id) { this.regionId = id; }
  }
  
}

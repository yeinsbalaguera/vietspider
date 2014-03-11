/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin.nukeviet;

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

@NodeMap("nukeviet-config")
public class XMLNukeVietConfig extends PluginConfig {
  
  @NodeMap("homepage")
  private String homepage = "http://localhost/admin/";
  public String getHomepage() { return homepage; }
  public void setHomepage(String homepage) { this.homepage = homepage; }
  
  @NodeMap("login")
  private String loginAddress = "http://localhost/admin/";
  public String getLoginAddress() { return loginAddress; }
  public void setLoginAddress(String login) { this.loginAddress = login; }
  
  @NodeMap("upload-image")
  private boolean uploadImage = true;
  @GetterMap("upload-image")
  public boolean isUploadImage() { return uploadImage; }
  @SetterMap("upload-image")
  public void setUploadImage(boolean value) { this.uploadImage = value; }
  
  @NodeMap("published")
  private boolean published = false;
  @GetterMap("published")
  public boolean isPublished() { return published; }
  @SetterMap("published")
  public void setPublished(boolean isPublished) { this.published = isPublished; }

  @NodeMap("charset")
  private String charset = "utf-8";
  public String getCharset() { return charset; }
  public void setCharset(String charset) { this.charset = charset; }
  
  @NodeMap("boundary")
  private String boundary = "---------------------------281452328116827";
  public String getBoundary() { return boundary; }
  public void setBoundary(String boundary) { this.boundary = boundary; }

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

  @NodeMap("post-address")
  private String postAddress = "http://localhost/admin/index.php?nv=news&op=content";
  @GetterMap("post-address")
  public String getPostAddress() { return postAddress; }
  @SetterMap("post-address")
  public void setPostAddress(String postURL) { this.postAddress = postURL;  }
  
  @NodeMap("image-upload-address")
  private String imageUploadAddress = "";
  @GetterMap("image-upload-address")
  public String getImageUploadAddress() { return imageUploadAddress; }
  @SetterMap("image-upload-address")
  public void setImageUploadAddress(String address) {imageUploadAddress = address;}
  
  @NodeMap("image-position")
  private String imagePosition = "0";
  @GetterMap("image-position")
  public String getImagePosition() { return imagePosition; }
  @SetterMap("image-position")
  public void setImagePosition(String value) { this.imagePosition= value;}
  
  @NodeMap("meta-image-width")
  private String metaImageWidth = "-1";
  @GetterMap("meta-image-width")
  public String getMetaImageWidth() { return metaImageWidth;  }
  @SetterMap("meta-image-width")
  public void setMetaImageWidth(String metaImageWidth) { this.metaImageWidth = metaImageWidth; }
  
  @NodesMap("categories")
  private Category[] categories;
  @GetterMap("categories")
  public Category[] getCategories(){ return this.categories; }
  @SetterMap("categories")
  public void setCategories(Category [] value){ this.categories = value; }
  
  @NodeMap("link-to-source")
  private String linkToSource = "";
  @GetterMap("link-to-source")
  public String getLinkToSource() { return linkToSource; }
  @SetterMap("link-to-source")
  public void setLinkToSource(String value) { linkToSource = value;}
  
  @NodeMap("alert-message")
  private boolean alertMessage = true;
  @GetterMap("alert-message")
  public boolean isAlertMessage() { return alertMessage; }
  @SetterMap("alert-message")
  public void setAlertMessage(boolean value) {alertMessage = value;}
  
  @NodeMap("auto-sync")
  private boolean autoSync = true;
  @GetterMap("auto-sync")
  public boolean isAutoSync() { return autoSync; }
  @SetterMap("auto-sync")
  public void setAutoSync(boolean value) { autoSync = value; }
  
  @NodeMap("category")
  public static class Category {
    
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
    
  }
  
}

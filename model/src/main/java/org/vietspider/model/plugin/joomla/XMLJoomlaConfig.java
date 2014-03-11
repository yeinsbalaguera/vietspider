/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin.joomla;

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

@NodeMap("joomla-config")
public class XMLJoomlaConfig extends PluginConfig {
  
  @NodeMap("homepage")
  private String homepage = "";
  //"http://localhost/joomla/";
  public String getHomepage() { return homepage; }
  public void setHomepage(String homepage) { this.homepage = homepage; }

  @NodeMap("charset")
  private String charset = "utf-8";
  public String getCharset() { return charset; }
  public void setCharset(String charset) { this.charset = charset; }

  @NodeMap("login-address")
  private String loginAddress = "";
  //"http://localhost/joomla/administrator/index.php?option=com_login";
  @GetterMap("login-address")
  public String getLoginAddress() { return loginAddress; }
  @SetterMap("login-address")
  public void setLoginAddress(String urlLogin) { this.loginAddress = urlLogin; }
  
  @NodeMap("image-width")
  private String imageWidth = "-1";
  @GetterMap("image-width")
  public String getImageWidth() { return imageWidth;  }
  @SetterMap("image-width")
  public void setImageWidth(String width) { this.imageWidth = width; }
  
  @NodeMap("published")
  private boolean published = false;
  @GetterMap("published")
  public boolean isPublished() { return published; }
  @SetterMap("published")
  public void setPublished(boolean isPublished) { this.published = isPublished; }
  
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
  
  @NodeMap("featured")
  private boolean featured = false;
  @GetterMap("featured")
  public boolean isFeatured() { return featured; }
  @SetterMap("featured")
  public void setFeatured(boolean isFeatured) { this.featured = isFeatured; }
  
  @NodeMap("alert-message")
  private boolean alertMessage = true;
  @GetterMap("alert-message")
  public boolean isAlertMessage() { return alertMessage; }
  @SetterMap("alert-message")
  public void setAlertMessage(boolean value) {alertMessage = value;}
  
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
  private String postAddress = "";
  //"http://localhost/joomla/administrator/index.php?option=com_content&task=add";
  @GetterMap("post-address")
  public String getPostAddress() { return postAddress; }
  @SetterMap("post-address")
  public void setPostAddress(String postURL) { this.postAddress = postURL;  }
  
  @NodeMap("image-folder")
  private String imageFolder = "";
  //"http://localhost/joomla/administrator/index.php?option=com_content&task=add";
  @GetterMap("image-folder")
  public String getImageFolder() { return imageFolder; }
  @SetterMap("image-folder")
  public void setImageFolder(String folder) { this.imageFolder = folder;  }
  
  @NodeMap("uri-image-folder")
  private String uriImageFolder = "";
  //"http://localhost/joomla/administrator/index.php?option=com_content&task=add";
  @GetterMap("uri-image-folder")
  public String getUriImageFolder() { return uriImageFolder; }
  @SetterMap("uri-image-folder")
  public void setUriImageFolder(String uri) { this.uriImageFolder = uri;  }
  
//  @NodeMap("image-upload-address")
//  private String imageUploadAddress = "";
//  // "http://localhost/joomla/administrator/index.php?option=com_media&folder=stories";
//  @GetterMap("image-upload-address")
//  public String getImageUploadAddress() { return imageUploadAddress; }
//  @SetterMap("image-upload-address")
//  public void setImageUploadAddress(String address) { imageUploadAddress = address;}
//  
//  @NodeMap("image-path")
//  private String imagePath = "images/";
//  // "//images/stories";
//  @GetterMap("image-path")
//  public String getImagePath() { return imagePath; }
//  @SetterMap("image-path")
//  public void setImagePath(String path) {imagePath = path;}
  
  @NodeMap("link-to-source")
  private String linkToSource = "";
  @GetterMap("link-to-source")
  public String getLinkToSource() { return linkToSource; }
  @SetterMap("link-to-source")
  public void setLinkToSource(String value) { linkToSource = value;}
  
  @NodeMap("text-style")
  private String textStyle = "";
  @GetterMap("text-style")
  public String getTextStyle() { return textStyle; }
  @SetterMap("text-style")
  public void setTextStyle(String textStyle) { this.textStyle = textStyle; }

  @NodesMap("categories")
  private Category[] categories;
  @GetterMap("categories")
  public Category[] getCategories(){ return this.categories; }
  @SetterMap("categories")
  public void setCategories(Category [] value){ this.categories = value; }
  
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
  
}

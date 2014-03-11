/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin.vbulletin;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.model.plugin.Category;
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

@NodeMap("vbulletin-config")
public class XMLVBulletinConfig extends PluginConfig {
  
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
  
//  @NodeMap("meta-image-width")
//  private String metaImageWidth = "-1";
//  @GetterMap("meta-image-width")
//  public String getMetaImageWidth() { return metaImageWidth;  }
//  @SetterMap("meta-image-width")
//  public void setMetaImageWidth(String metaImageWidth) { this.metaImageWidth = metaImageWidth; }
  
//  @NodeMap("published")
//  private boolean published = false;
//  @GetterMap("published")
//  public boolean isPublished() { return published; }
//  @SetterMap("published")
//  public void setPublished(boolean isPublished) { this.published = isPublished; }
  
  @NodeMap("auto")
  private boolean auto = false;
  @GetterMap("auto")
  public boolean isAuto() { return auto; }
  @SetterMap("auto")
  public void setAuto(boolean value) { this.auto = value; }
 
//  public boolean isUploadImage(){ 
//    return imageUploadAddress != null && !imageUploadAddress.trim().isEmpty(); 
//  }
  
//  @NodeMap("frontpage")
//  private boolean frontpage = false;
//  @GetterMap("frontpage")
//  public boolean isFrontpage() { return frontpage; }
//  @SetterMap("frontpage")
//  public void setFrontpage(boolean isFrontpage) { this.frontpage = isFrontpage; }
  
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
  
//  @NodeMap("image-upload-address")
//  private String imageUploadAddress = "";
//  // "http://localhost/joomla/administrator/index.php?option=com_media&folder=stories";
//  @GetterMap("image-upload-address")
//  public String getImageUploadAddress() { return imageUploadAddress; }
//  @SetterMap("image-upload-address")
//  public void setImageUploadAddress(String address) {imageUploadAddress = address;}
  
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
  public String getLinkToSource() {
    if(linkToSource == null) return "";
    return linkToSource; 
  }
  @SetterMap("link-to-source")
  public void setLinkToSource(String value) { linkToSource = value;}
  
  @NodeMap("text-style")
  private String textStyle = "";
  @GetterMap("text-style")
  public String getTextStyle() { return textStyle; }
  @SetterMap("text-style")
  public void setTextStyle(String textStyle) { this.textStyle = textStyle; }

  @NodesMap("categories")
  private List<Category> categories = new ArrayList<Category>();
  @GetterMap("categories")
  public List<Category> getCategories(){ return this.categories; }
  @SetterMap("categories")
  public void setCategories(List<Category> value){ this.categories = value; }
  
}

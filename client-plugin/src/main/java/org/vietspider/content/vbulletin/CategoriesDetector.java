/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.vbulletin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.browser.HttpSessionUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.content.cms.CommonCategoriesDetector;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.model.plugin.Category;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
public class CategoriesDetector  extends CommonCategoriesDetector {

  private List<Category> categories = new ArrayList<Category>();

  public CategoriesDetector(String homepage, 
      String loginURL, String username, String password, String charset) {
    super(homepage, loginURL, username, password, charset);
  }
  
  public void detect(String url) throws Exception {
    categories = detectSingle(url);
//    for(int i = 0; i < categories.size(); i++) {
//      detectSubCategories(url, categories.get(i));
//    }
  }
  
//  private void detectSubCategories(String url, Category category) throws Exception {
//    StringBuilder builder = new StringBuilder(url);
//    builder.append("forumdisplay.php?f=").append(category.getCategoryId()); 
//    List<Category> subCategories = detectSingle(builder.toString());
//    if(subCategories.size() < 1) return;
//    category.setSubCategories(subCategories);
//    for(int i = 0; i < subCategories.size(); i++) {
//      detectSubCategories(url, subCategories.get(i));
//    }
//  }

  private List<Category> detectSingle(String url)  throws Exception {
    List<Category> list = new ArrayList<Category>();
//    System.out.println(" homepage "+ url);
//    System.out.println(" login  "+loginURL );
//    System.out.println(username+ " : "+ password);
    webClient.setURL(homepage, new URL(homepage));
    
    HttpMethodHandler handler = new HttpMethodHandler(webClient);
    HttpSessionUtils httpSession = new HttpSessionUtils(handler, "Error");
    StringBuilder builder = new StringBuilder(loginURL).append('\n');
    builder.append(username).append(':').append(password);
    boolean login = httpSession.login(builder.toString(), charset, new URL(homepage), homepage);
    if(!login) throw new Exception("Cann't login to website!");

    HTMLDocument document = new HTMLParser2().createDocument(get(url), charset);
//    System.out.println(document.getTextValue());
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.A)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("href");
      if(attribute == null) continue;
      String link  = attribute.getValue();
//      System.out.println(link + " : " + link.indexOf("forumdisplay.php?f="));
      if(link.indexOf("forumdisplay.php?f=") < 0) continue; 
      detectNode(list, node, link);
    }
    
    return list;
  }

  private void detectNode(List<Category> list, HTMLNode node, String link) {
    String id = getId(link);
    if(id == null || "-1".equals(id) 
        || contains(categories, id) || contains(list, id)) return;

    RefsDecoder decoder = new RefsDecoder();
    String name  = null;

    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      name = node.getTextValue();
      
      break;
    }
    
    if(name  == null || 
        ("Main Category".equals(name) && "1".equals(id))) return;
    
    Category category = new Category();
    name = new String(decoder.decode(name.toCharArray()));
    category.setCategoryName(name);
    category.setCategoryId(id);
    list.add(category);
  }

  private String getId(String link) {
    int start = link.indexOf("f=");
    if(start < 0) return null;
    int end  = link.indexOf("&", start);
    if(end < 0) end  = link.length();
    if(start+2 >= end) return null;
    String id = link.substring(start+2, end);
    if(id.trim().isEmpty()) return null;
    return id;
  }

  public List<Category> getCategories() { return categories; }

  public void setCategories(List<Category> categories) { this.categories = categories; }

  private boolean contains(List<Category>list, String id) {
    for(int i = 0; i < list.size(); i++) {
      Category category = list.get(i);
      if(category.getCategoryId().equals(id)) return true;
      if(contains(category.getSubCategories(), id)) return true; 
    }
    return false;
  }
}

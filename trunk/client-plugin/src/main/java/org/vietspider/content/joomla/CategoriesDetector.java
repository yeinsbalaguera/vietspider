/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.joomla;

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
import org.vietspider.model.plugin.joomla.XMLJoomlaConfig.Category;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
public class CategoriesDetector extends CommonCategoriesDetector {

  private List<Category> categories = new ArrayList<Category>();

  public CategoriesDetector(String homepage, 
      String loginURL, String username, String password, String charset) {
    super(homepage, loginURL, username, password, charset);
  }

  public void detect(String url) throws Exception {
//    System.out.println(" homepage "+ homepage);
//    System.out.println(" login  "+loginURL );
//    System.out.println(" cateURL "+ cateURL);
//    System.out.println(username+ " : "+ password);
    webClient.setURL(homepage, new URL(homepage));
    
    HttpMethodHandler handler = new HttpMethodHandler(webClient);
    HttpSessionUtils httpSession = new HttpSessionUtils(handler, "Error");
    StringBuilder builder = new StringBuilder(loginURL).append('\n');
    builder.append(username).append(':').append(password);
    boolean login = httpSession.login(builder.toString(), charset, new URL(homepage), homepage);
    if(!login) throw new Exception("Cann't login to website!");

    HTMLDocument document = new HTMLParser2().createDocument(get(url), charset);
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.A)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("href");
      if(attribute == null) continue;
      String link  = attribute.getValue();
      if(link.indexOf("com_categories") < 0/* 
          || link.indexOf("section") < 0*/) continue; 
      detectNode(node, link);
    }
  }

  private void detectNode(HTMLNode node, String link) {
    String id = getId(link);
//    System.out.println(" be e "+ id);
    if(id == null) return;
    for(int i = 0; i < categories.size(); i++) {
      if(id.equals(categories.get(i).getCategoryId())) return;
    }

    if(node.getChildren().size() < 1) return;
    String name = node.getChild(0).getTextValue().trim();
    if(name.isEmpty()) return;

    RefsDecoder decoder = new RefsDecoder();
//    HTMLNode parent = upParent(node);
//    if(parent == null) return;

//    String sectionId = null;
    /*NodeIterator iterator = parent.iterator();
    while(iterator.hasNext()) {
      node = iterator.next();
      if(!node.isNode(Name.A)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("href");
      if(attribute == null) continue;
      link  = attribute.getValue();
//      if(link.indexOf("com_sections") < 0) continue; 
//      sectionId = getId(link);
      break;
    }*/
//    if(sectionId == null) return;
    Category category = new Category();
    name = new String(decoder.decode(name.toCharArray()));
    category.setCategoryName(name);
    category.setCategoryId(id);
    
//    System.out.println(" thay co "+ category.getCategoryName()+ " : " + category.getCategoryId());
//    category.setSectionId(sectionId);
    categories.add(category);
  }

  private String getId(String link) {
//    System.out.println(" chay vao link "+ link);
    int start = link.indexOf("id");
    if(start < 0) return null;
    int end  = link.indexOf("&", start);
    if(end == -1) end  = link.length();
    int idx = link.indexOf('=', start+1);
    if(idx > -1) start = idx+1; else start += 3;
    if(start >= end) return null;
    String id = link.substring(start, end);
    if(id.trim().isEmpty()) return null;
//    System.out.println(" co duoc id "+ id);
    return id;
  }

 /* private HTMLNode upParent(HTMLNode node) {
    HTMLNode parent  = node.getParent();
    while(parent != null) {
      if(parent.isNode(Name.TR)) return parent;
      parent = parent.getParent();
    }
    return null;
  }*/

  public List<Category> getCategories() { return categories; }

  public void setCategories(List<Category> categories) { this.categories = categories; }

}

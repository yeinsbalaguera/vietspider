/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nukeviet;

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
import org.vietspider.model.plugin.nukeviet.XMLNukeVietConfig.Category;
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
    
    RefsDecoder decoder = new RefsDecoder();

    HTMLDocument document = new HTMLParser2().createDocument(get(url), charset);
//    System.out.println(document.getTextValue());
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.INPUT)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("name");
      if(attribute == null 
          || !"catids[]".equals(attribute.getValue())) continue;
      attribute = attributes.get("value");
      if(attribute == null) continue;
      String id  = attribute.getValue();
      if(id == null || id.trim().isEmpty()) continue;
      HTMLNode parent = node.getParent();
      if(parent.getChildren() == null 
          || parent.getChildren().size() < 2) continue;
      int idx = parent.indexOfChild(node);
      String name = parent.getChild(idx+1).getTextValue();
      
      Category category = new Category();
      name = new String(decoder.decode(name.toCharArray()));
      category.setCategoryName(name);
      category.setCategoryId(id);
      categories.add(category);
    }
  }
  
  public List<Category> getCategories() { return categories; }

  public void setCategories(List<Category> categories) { this.categories = categories; }

}

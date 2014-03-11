/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.drupal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.browser.HttpSessionUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.content.cms.CommonCategoriesDetector;
import org.vietspider.content.drupal.XMLDrupalConfig.Category;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
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
    url = url.trim();
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
//    System.out.println(" thay co "+ login);

    HTMLDocument document = new HTMLParser2().createDocument(get(url), charset);
//    System.out.println(document.getTextValue());
    NodeIterator iterator = document.getRoot().iterator();
    RefsDecoder decoder = new RefsDecoder();
    
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.A)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("href");
      if(attribute == null) continue;
      String attrValue  = attribute.getValue();
      int idx = attrValue.indexOf("node/add/");
      if(idx < 0) continue;
      String id = attrValue.substring(idx + "node/add/".length());
      
      boolean add = true;
      for(int k = 0;  k < categories.size(); k++) {
        if(categories.get(k).getCategoryId().equals(id)) {
          add = false;
          break;
        }
      }
      if(!add) continue;
      
      if(node.getChildren().size() < 1) continue;
      String name = node.getChild(0).getTextValue().trim();
      if(name.isEmpty()) continue;
      name = new String(decoder.decode(name.toCharArray()));

      Category category = new Category();
      category.setCategoryId(id);
      category.setCategoryName(name);
      categories.add(category);
    }
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

}

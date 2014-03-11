/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.bds;

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
import org.vietspider.model.plugin.bds.XMLBdsConfig.Category;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Region;
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
  private List<Region> regions = new ArrayList<Region>();

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
    System.out.println(" chuan bi dang nhap ");
    boolean login = httpSession.login(builder.toString(), charset, new URL(homepage), homepage);
    System.out.println(" dang nhap " + login);
    if(!login) throw new Exception("Cann't login to website!");

    HTMLDocument document = new HTMLParser2().createDocument(get(url), charset);
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.OPTION)) continue;
      detectNode(node);
    }
  }

  private void detectNode(HTMLNode node) {
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("value");
    if(attribute == null) return;
    String id = attribute.getValue();
    
    if(node.getChildren().size() < 1) return;
    String name = node.getChild(0).getTextValue().trim();
    if(name.isEmpty()) return;

    if("nhucau".equals(findParent(node)) ) {
      Category category = new Category();
      category.setCategoryName(name);
      category.setCategoryId(id);

//      System.out.println(" thay co category "+ category.getCategoryName()+ " : " + category.getCategoryId());
      //    category.setSectionId(sectionId);
      categories.add(category);
    } else if("noidang".equals(findParent(node)) ) {
      Region region = new Region();
      region.setRegionName(name);
      region.setRegionId(id);
//      System.out.println(" thay co region "+ region.getRegionId()+ " : " + region.getRegionName());
      regions.add(region);
    }
  }
  
  private String findParent(HTMLNode node) {
    HTMLNode parent  = node.getParent();
    while(parent != null) {
      if(!parent.isNode(Name.SELECT))  {
        parent = parent.getParent();
        continue;
      }
      Attributes attributes = parent.getAttributes(); 
      Attribute attribute = attributes.get("id");
      if(attribute == null) {
        parent = parent.getParent();
        continue;
      }
//      System.out.println(attribute.getName() + " : "+ attribute.getValue());
      return attribute.getValue(); 
    }
    return null;
  }

  public List<Region> getRegions() { return regions; }

  public List<Category> getCategories() { return categories; }

}

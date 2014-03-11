package org.vietspider.content.drupal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Aug 13, 2008
 */

public class DrupalSyncArticlePluginBak extends ClientPlugin {

	private String label;
//	private String confirm;
	private int type = CONTENT;
	
	private SinglePostSelector selector;
	private MultiPostSelector multiPostSelector;
	
	private Browser browser;
	private ProgressAdapter domainListener;

	public DrupalSyncArticlePluginBak() {
		ClientRM resources = new ClientRM("DrupalSyncArticle");
		label = resources.getLabel(DrupalSyncArticlePluginBak.class.getName() + ".itemSendContent");
//		confirm = resources.getLabel(getClass().getName() + ".msgAlertSend");
		enable = true;
		
		domainListener = new ProgressAdapter(){   
      @SuppressWarnings("unused")
      public void changed(ProgressEvent event){  
      }
      @SuppressWarnings("unused")
      public void completed(ProgressEvent event){
        if(browser == null) return;
        searchArticles();
        browser.removeProgressListener(domainListener);
      }
    };     
	}

	public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @Override
  public boolean isValidType(int type_) { 
    this.type = type_;
    if(type == CONTENT) return true;
    return type == DOMAIN;  
  }

	@Override
	public void invoke(Object... objects) {
	  if(type == CONTENT) {
      invokeContent(objects);
    } else {
      invokeDomain(objects);
    }
	}
	
	public void invokeDomain(Object... objects) {
    if (!enable || values == null || values.length < 1) return;
    browser = (Browser) objects[1];
    searchArticles();
  }
  
  void searchArticles() {
    if(browser == null);
    String html = browser.getText();

    List<String> ids = new ArrayList<String>();
    List<String> names = new ArrayList<String>();
    List<String> categories = new ArrayList<String>();
    
    try {
      HTMLDocument document = new HTMLParser2().createDocument(html);
      NodeIterator iterator = document.getRoot().iterator();
      
     HTMLNode htmlNode = null;
      while(iterator.hasNext()) {
        if(htmlNode == null) htmlNode = iterator.next();
        HTMLNode node = htmlNode;
        htmlNode = null;
        if(!node.isNode(Name.A)) continue;
        Attributes attributes = node.getAttributes(); 
        
        Attribute attribute = attributes.get("class");
        if(attribute == null || attribute.getValue() == null) continue;
        String attrValue = attribute.getValue();
        if("meta_title_synchronized".equalsIgnoreCase(attrValue)) continue;
        if(!attrValue.toLowerCase().startsWith("meta_title")) continue;
        
        attribute = attributes.get("href");
        if(attribute == null) continue;
        String href = attribute.getValue();
        String id = href.substring(href.lastIndexOf('/')+1);
        if(node.getChildren()== null || node.getChildren().size() < 1) continue;
        String title = node.getChild(0).getTextValue();
        if(id.trim().isEmpty() || title.trim().isEmpty()) continue;
        
        
        ids.add(id);
        names.add(title);
        String category = "";
        
        while(iterator.hasNext()) {
          HTMLNode cnode = iterator.next();
          if(cnode.isNode(Name.A)) {
            htmlNode = cnode;
            break;
          }
          
          if(!cnode.isNode(Name.TD)) continue;
          attributes = cnode.getAttributes(); 
          attribute = attributes.get("class");
          if(attribute == null || attribute.getValue() == null) continue;
            
          if(!"updated_time_local".equalsIgnoreCase(attribute.getValue()))  continue;
          if(cnode.getChildren()== null || cnode.getChildren().size() < 1) continue;
          category = cnode.getChild(0).getTextValue();
          int idx = category.indexOf('/');
          if(idx > 0) category = category.substring(0, idx);
          category = category.trim();
          idx = category.indexOf('.');
          if(idx > 0) category = category.substring(idx+1);
          break;
        }
        categories.add(category);
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(browser, e);
    }
    
    if(ids.size() < 1 || names.size() != ids.size()) return;
  
    if(multiPostSelector == null) {
      multiPostSelector = new MultiPostSelector(browser.getShell());
//      multiPostSelector.setPlugin(this);
    }
    String [] _ids = ids.toArray(new String[ids.size()]);
    String [] _names = names.toArray(new String[names.size()]);
    String [] _categories = categories.toArray(new String[categories.size()]);
    multiPostSelector.setData(_ids, _names, _categories);
  }
	
	private void invokeContent(Object... objects) {
    if (!enable || values == null || values.length < 1) return;
    final Control control = (Control) objects[0];
    if(selector == null || selector.isDestroy()) {
      if(selector != null) selector.dispose();
      selector = new SinglePostSelector(control.getShell());
      selector.setMetaId(values[0]);
    } else {
      selector.setMetaId(values[0]);
      selector.show();
    }
  }
	
	void nextPage() {
    if(browser == null) return;
    String url = browser.getUrl();
    int index = url.indexOf("DOMAIN/");
    if(index < 1) return;
    int start = index + "DOMAIN/".length();
    int end = url.indexOf('/', start);
    if(end < 1) return;
    String pageValue = url.substring(start, end);
    int page = -1;
    try {
      page = Integer.parseInt(pageValue);
    }catch (Exception e) {
    }
    if(page < 0) return;
    url = url.substring(0, start) + String.valueOf(page+1)+url.substring(end);
    browser.setUrl(url);
    browser.addProgressListener(domainListener);
  }

	public boolean isSetup() { return true; }
	
	public void invokeSetup(Object...objects) {
    if(objects == null || objects.length < 1) return;
    
    final Control link = (Control) objects[0];
    new DrupalSetup(link.getShell());
  }

}
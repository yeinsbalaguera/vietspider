/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Properties;

import org.eclipse.swt.browser.Browser;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public class DeleteDomainPlugin extends AdminDataPlugin {

  private String label;

  protected Browser browser;

  public DeleteDomainPlugin() {
    ClientRM resources = new ClientRM("Plugin");
    label = resources.getLabel(getClass().getName()+".itemDeleteDomain");
//    confirm = resources.getLabel(getClass().getName()+".msgAlertDeleteDomain");
  }

  @Override
  public boolean isValidType(int type) { return type == DOMAIN; }

  public String getConfirmMessage() {
    return null;
//    if(values == null || values.length < 1) return null;
//    return confirm.replaceAll("\\{1\\}", values[0]); 
  }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;

    browser = (Browser) objects[1];

    searchArticles(values[0]);
  }

  protected void searchArticles(String domain) {
    if(browser == null);
    String html = browser.getText();

    List<String> ids = new ArrayList<String>();
    List<String> names = new ArrayList<String>();
//    List<String> categories = new ArrayList<String>();

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
//        String category = "";
//        
//        while(iterator.hasNext()) {
//          HTMLNode cnode = iterator.next();
//          if(cnode.isNode(Name.A)) {
//            htmlNode = cnode;
//            break;
//          }
//          
//          if(!cnode.isNode(Name.TD)) continue;
//          attributes = cnode.getAttributes(); 
//          attribute = attributes.get("class");
//          if(attribute == null || attribute.getValue() == null) continue;
//            
//          if(!"updated_time_local".equalsIgnoreCase(attribute.getValue()))  continue;
//          if(cnode.getChildren()== null || cnode.getChildren().size() < 1) continue;
//          category = cnode.getChild(0).getTextValue();
//          int idx = category.indexOf('/');
//          if(idx > 0) category = category.substring(0, idx);
//          category = category.trim();
//          idx = category.indexOf('.');
//          if(idx > 0) category = category.substring(idx+1);
//          break;
//        }
//        categories.add(category);
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(browser, e);
    }

    if(names.size() != ids.size()) return;

    String [] _ids = ids.toArray(new String[ids.size()]);
    String [] _names = names.toArray(new String[names.size()]);
//    String [] _categories = categories.toArray(new String[categories.size()]);

    new DeleteContentsDialog(browser, domain, _ids, _names);
  }

  private static ClientRM clientRM;

  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;

    clientRM = new ClientRM(new ListResourceBundle() {
      protected Object[][] getContents() {
        PropertiesFile propFile = new PropertiesFile(true);
        String locale = "en";
        Properties properties = new Properties();
        try {
          properties = propFile.load(UtilFile.getFile("client", "config.properties"));
          if(properties != null && properties.get("locale") != null) {
            locale = properties.get("locale").toString();
          } else {
            locale = "vn";
          }
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }

        String packageName  = "org.vietspider.content.";
        if("en".equals(locale)) {
          return new String[][] {
              {packageName + "DeleteContentsDialog.title",  "Delete Data"},
              {"itemDeleteTooltip", "Are you sure you want delete this content?"},
              {packageName + "DeleteContentsDialog.butOk", "Delete Selected Content(s)"},
              {packageName + "DeleteContentsDialog.butClose", "Close"},
              {packageName + "DeleteContentsDialog.butDeletDomain", "Delete this Domain"},
              {packageName + "VBulletinSetup.lblCharset", "Charset:"},
              {packageName + "VBulletinSetup.lblPostAddress", "New Thread Page:"},
          };
        } 
        return new String[][] {
            {packageName + "DeleteContentsDialog.title",  "Xóa nội dung"},
            {"itemDeleteTooltip", "Xóa nội dung này?"},
            {packageName + "DeleteContentsDialog.butOk", "Xóa nội dung đã chọn"},
            {packageName + "DeleteContentsDialog.butClose", "Đóng"},
            {packageName + "DeleteContentsDialog.butDeletDomain", "Xóa toàn bộ chuyên mục"},
            {packageName + "VBulletinSetup.lblCharset", "Charset:"},
            {packageName + "VBulletinSetup.lblPostAddress", "New Thread Page:"},
        };
      }
    });
    return clientRM;
  }


}
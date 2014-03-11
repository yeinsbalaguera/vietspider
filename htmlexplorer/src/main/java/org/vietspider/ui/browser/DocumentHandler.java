/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.browser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.mozilla.interfaces.nsIDOMDocumentFragment;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMRange;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISelection;
import org.mozilla.interfaces.nsIWebBrowser;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.BrowserMenu;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 25, 2009  
 */
public class DocumentHandler {

  private nsIDOMElement element;
  private Browser browser;

  public DocumentHandler(Browser browser, nsIDOMEvent event) {
    this.browser = browser;
    this.element = (nsIDOMElement)event.getTarget ().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
  }

  String getSelectedText() {
    nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
    nsIDOMWindow dw = webBrowser.getContentDOMWindow();
    nsISelection selection = dw.getSelection();

    StringBuilder builder = new StringBuilder();
    for(int i = 0;  i<  selection.getRangeCount(); i++) {
      nsIDOMRange idomRange = selection.getRangeAt(i);
      nsIDOMDocumentFragment fragment = idomRange.cloneContents();

      nsIDOMNodeList nodeList = fragment.getChildNodes();
      for(int k = 0;  k < nodeList.getLength(); k++) {
        buildTextNode(builder, nodeList.item(k));
      }
    }
    String text = builder.toString().trim();
    if(text.length() > 0) return text;

    nsIDOMNode node = element.getFirstChild();
    while(node != null) {
      builder.append(node.getNodeValue());
      node = node.getNextSibling();
    }
    return builder.toString();
  }

  String getSelectedHTML() {
    nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
    nsIDOMWindow dw = webBrowser.getContentDOMWindow();
    nsISelection selection = dw.getSelection();

    StringBuilder builder = new StringBuilder();
    for(int i = 0;  i<  selection.getRangeCount(); i++) {
      nsIDOMRange idomRange = selection.getRangeAt(i);
      nsIDOMDocumentFragment fragment = idomRange.cloneContents();

      nsIDOMNodeList nodeList = fragment.getChildNodes();
      for(int k = 0;  k < nodeList.getLength(); k++) {
        buildHTMLNode(0, builder, nodeList.item(k));
      }
    }
    String text = builder.toString().trim();
    if(text.length() > 0) return text;

    buildHTMLNode(0, builder, element);
    return builder.toString();
  }

  File getHTMLFile(int code) {
    File file = UtilFile.getFolder("track/temp/");
    file  = new File(file, String.valueOf(code)+".html");
    try {
      RWData.getInstance().save(file, getSelectedHTML().getBytes("utf-8"));
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(browser.getShell(), e);
    } 
    return file;
  }

  File getDocumentFile(int code) {
    File file = UtilFile.getFolder("track/temp/");
    file  = new File(file, String.valueOf(code)+".html");
    try {
      RWData.getInstance().save(file, browser.getText().getBytes("utf-8"));
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(browser.getShell(), e);
    } 
    return file;
  }


  private void buildTextNode(StringBuilder builder, nsIDOMNode node) {
    builder.append('\n');
    if(node.getNodeName().charAt(0) == '#') {
      if(node.getNodeValue().trim().isEmpty()) return;
      builder.append(node.getNodeValue().trim());
    } else {
      nsIDOMNodeList children = node.getChildNodes();
      if(children != null) {
        for(long i = 0; i < children.getLength(); i++) {
          buildTextNode(builder, children.item(i));
        }
      }
    }
  }

  private void buildHTMLNode(int tab, StringBuilder builder, nsIDOMNode node) {
    builder.append('\n');
    if(node.getNodeName().charAt(0) == '#') {
      if(node.getNodeValue().trim().isEmpty()) return;
      for(int i = 0; i < tab; i++) {
        builder.append(' ');
      }
      builder.append(node.getNodeValue().trim());
    } else {
      for(int i = 0; i < tab; i++) {
        builder.append(' ');
      }
      builder.append('<').append(node.getNodeName());
      nsIDOMNamedNodeMap attributes = node.getAttributes();
      if(attributes != null)  {
        for(long i = 0; i < attributes.getLength(); i++) {
          nsIDOMNode attr = attributes.item(i);
          builder.append(' ').append(attr.getNodeName());
          builder.append('=').append('\"').append(attr.getNodeValue()).append('\"');
        }
      }
      builder.append('>');

      nsIDOMNodeList children = node.getChildNodes();
      if(children != null) {
        for(long i = 0; i < children.getLength(); i++) {
          buildHTMLNode(tab + 2, builder, children.item(i));
        }
      }

      builder.append('\n');
      for(int i = 0; i < tab; i++) {
        builder.append(' ');
      }
      builder.append('<').append('/').append(node.getNodeName()).append('>');
    }
  }

  String getSelectedLinkAsText() {
    StringBuilder builder = new StringBuilder();
    String[] links = getSelectedLink();
    for(int i = 0; i < links.length; i++) {
      if(builder.length() >  0) builder.append('\n');
      builder.append(links[i]);
    }
    return builder.toString();
  }

  public String[] getSelectedLink() {
    nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
    nsIDOMWindow dw = webBrowser.getContentDOMWindow();
    nsISelection selection = dw.getSelection();

    List<String> list  = new ArrayList<String>();
    
    for(int i = 0;  i<  selection.getRangeCount(); i++) {
      nsIDOMRange idomRange = selection.getRangeAt(i);
//      System.out.println("idomrange "+idomRange);
      nsIDOMDocumentFragment fragment = idomRange.cloneContents();

      searchLink(list, fragment.getChildNodes());
    }
    
    String home = browser.getUrl();
    URLUtils urlUtils = new URLUtils();
    URL url = null;
    try {
      url = new URL(home);
    } catch (Exception e) {
    }

    
    if(list.size() > 0)  {
      for(int i = 0; i < list.size(); i++) {
        list.set(i, urlUtils.createURL(url, list.get(i)));
      }
      return list.toArray(new String[list.size()]);
    }

    String link  = BrowserMenu.getLink(element);
    if(link == null) return new String[0];
    link = urlUtils.createURL(url, link);
    return new String[]{link};
  }
  
  private void searchLink(List<String> list, nsIDOMNodeList nodeList) {
    for(int k = 0;  k < nodeList.getLength(); k++) {
      String l = BrowserMenu.getLink(nodeList.item(k));
      if(l != null) list.add(l);
      searchLink(list, nodeList.item(k).getChildNodes());
    }
  }
  
}

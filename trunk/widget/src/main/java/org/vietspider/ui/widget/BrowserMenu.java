/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Menu;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 25, 2009  
 */
public abstract class BrowserMenu {

  public static String getLink(nsIDOMNode node) {
    String link  = getAttribute(node.getAttributes(), "href");
    if(link != null) return link;
    link  = getAttribute(node.getAttributes(), "onclick");
    if(link != null) return link;
    return getAttribute(node.getAttributes(), "src");
  }
  
  public static String getAttribute(nsIDOMNamedNodeMap map, String name) {
    if(map == null) return null;
    for(long i = 0; i < map.getLength(); i++) {
      nsIDOMNode node = map.item(i);
      if(!name.equalsIgnoreCase(node.getNodeName())) continue;
      String link  = node.getNodeValue();
      if(link == null 
          || (link = link.trim()).isEmpty() || "#".equals(link)) continue;
      return link;
    }
    return null;
  }
  
  public abstract Menu createMenu(Browser browser, nsIDOMEvent event);

  protected Menu menu;

  public Menu getMenu() { return menu; }

  public void dispose() { menu.dispose(); }
  
  protected void putClipBroard(String text) {
    if(text == null || text.isEmpty()) return;
    Clipboard cb = new Clipboard(menu.getDisplay());
    TextTransfer textTransfer = TextTransfer.getInstance();
    cb.setContents(new Object[]{text}, new Transfer[]{textTransfer});
  }
  
}

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 24, 2009  
 */
public class MozillaBrowserMenu {
  
  public static void createMenu(final Browser browser) {
    
    browser.addProgressListener (new ProgressAdapter () {
      @SuppressWarnings("unused")
      public void completed (ProgressEvent event) {
        nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
        if(webBrowser == null) return;
        nsIDOMWindow domWindow = webBrowser.getContentDOMWindow ();
        if(domWindow == null) return;
        nsIDOMEventTarget target = (nsIDOMEventTarget)domWindow.queryInterface (nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
        nsIDOMEventListener listener = new nsIDOMEventListener () {
          public nsISupports queryInterface (String uuid) {
            if (uuid.equals (nsIDOMEventListener.NS_IDOMEVENTLISTENER_IID) ||
              uuid.equals (nsIDOMEventListener.NS_ISUPPORTS_IID)) {
                return this;
            }
            return null;
          }
          public void handleEvent (nsIDOMEvent devent) {
            nsIDOMElement element = (nsIDOMElement)devent.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
            String text =  element.getFirstChild().getNodeValue();
            if(text == null || text.trim().isEmpty() || "null".equalsIgnoreCase(text)) return;
//            System.out.println(text);
//            search(text.trim());
         }
        };
        target.addEventListener ("click", listener, false);
      }
    });
  }
}

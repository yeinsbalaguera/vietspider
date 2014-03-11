package org.vietspider.ui.htmlexplorer;

import org.vietspider.html.HTMLDocument;
import org.vietspider.ui.action.Event;

public class HTMLExplorerEvent extends Event {
  
   private String[] items;
   private String url ;
   private HTMLDocument doc;   
   private String charset;
   
   public HTMLExplorerEvent() {
     eventType = EVENT_CANCEL;
   }
       
   
   public HTMLExplorerEvent(String[] a, String u, HTMLDocument d, String c){
     items = a;
     url = u;
     doc = d;
     charset = c;
   }
  
   public String[] getPath()  { return items; }
   public String getUrl(){ return this.url; }
   
   public String getCharset(){ return charset; }
   public HTMLDocument getDocument(){ return this.doc; }

}

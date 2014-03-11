/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.net.URL;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.URLWidget;
import org.vietspider.gui.web.FastWebClient2;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2011  
 */
public class AutoCompleteFromHomePage {
  
  protected Text txtName;
  protected URLWidget txtHome;
  protected Combo cboCharset;
  protected FastWebClient2 webClient;
  
  private String charset;
  
  public AutoCompleteFromHomePage(Text txtName,
      URLWidget txtHome, Combo cboCharset, FastWebClient2 webClient) {
    this.txtName = txtName;
    this.txtHome = txtHome;
    this.cboCharset = cboCharset;
    this.webClient = webClient;
  }
  
  public String complete() {
    if(txtName.getText().trim().length() > 0) return null;
    Worker worker = new Worker() {
      
      private String address;
      
      public void before() {
        address = txtHome.getText();
      }
      
      public void execute() {
        try {
          webClient.setURL(null, new URL(address));
          byte[] bytes = webClient.loadContent(null, address);
          HTMLParserDetector detector = new HTMLParserDetector();
          charset = detector.detectCharset(bytes);
        } catch (Exception e) {
        }
      }
      
      
      public void after() {
        if(cboCharset.isDisposed()) return;
        if(charset != null) {
          String[] items = cboCharset.getItems();
          for(int i = 0; i < items.length; i++) {
            if(items[i].equalsIgnoreCase(charset)) {
              cboCharset.select(i);
              return;
            }
          }
          cboCharset.add(charset);
          cboCharset.select(cboCharset.getItemCount()-1);
        }
      }
      
      @Override
      public void abort() {
        webClient.abort(address);
      }
    };
    
    ThreadExecutor executor = new ThreadExecutor(worker, txtName);
    executor.start();
   
    try {
      URL url = new URL(txtHome.getText());
      String home = url.getHost();
      if(home.startsWith("www")) home = home.substring(home.indexOf(".")+1);
      if(home.indexOf(".") > -1) home = home.replace('.', ' ');
      if(home.length() < 2) return charset;
      char c = home.charAt(0);
      if(!Character.isUpperCase(c)) {
        home = String.valueOf(Character.toUpperCase(c)) + home.substring(1);
      }
      txtName.setText(home);
    } catch(Exception exp) {}
    
    return charset;
  }

}

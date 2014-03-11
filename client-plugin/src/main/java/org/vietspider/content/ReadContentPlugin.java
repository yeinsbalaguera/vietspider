/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import org.eclipse.swt.browser.Browser;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 12, 2008  
 */
public class ReadContentPlugin extends ClientPlugin {
  
  private String lastData = null;
  
  public ReadContentPlugin () {
  }

  @Override
  public boolean isValidType(int type) { return type == CONTENT; }
  
  public boolean isInvisible() { return true; }
  @Override
  public void invoke(Object... objects) {
    Browser browser = (Browser) objects[0];
    
    String title  =  browser.getToolTipText();
    String id = browser.getUrl();
    id = id.substring(id.lastIndexOf('/')+1);
    String user = ClientConnector2.currentInstance().getUserHeader().getValue();
    
    String value = user + " read.article '"+ title +"' article.id=" + id ;
    if(value.equals(lastData)) return;
    
//    System.out.println("Read: id:" + id);
    lastData = value;
    
    Worker excutor = new Worker() {
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          DataClientHandler.getInstance().saveAction(lastData);
        } catch (Exception e) {
          ClientLog.getInstance().setMessage(null, e);
        }
      }

      public void after() {}
    };
    new ThreadExecutor(excutor, browser).start();
  }

}

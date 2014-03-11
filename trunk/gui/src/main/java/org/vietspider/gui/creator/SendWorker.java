/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2008  
 */
public abstract class SendWorker {
  
  protected String group;
  protected String category;
  protected String [] names;
  
  protected Source source;
  
  protected ClientConnector2 connector = new ClientConnector2();
  
  public SendWorker(Source source) {
    this.source = source;
  }
  
  public SendWorker( String group, String category, String...names) {
    this.group = group;
    this.category = category;
    this.names = names;
  }
  
  public void abort() { connector.abort(); }
  
  public abstract String send(String host, String port, String username, String password); 
  

}

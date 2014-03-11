/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.notifier.notifier;

/**
 * @author Emil Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Jul 2, 2009
 */
public class NotificationEvent {

  public final static short TITLE     = 0;

  public final static short MESSAGE      = 1;
  
  public final static short CLOSE      = -1;

  private short              component = 0;
  
  private String title = null;
  private String message = null;

  

  private String             id;

  public NotificationEvent(String value) {
    this.id = value;
  }

  public String getId() { return id; }
  
  public short getComponent() { return component; }
  public void setComponent(short component) { this.component = component; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getMessage() { return message; }
  public void setMessage(String text) { this.message = text; }
  
  
}

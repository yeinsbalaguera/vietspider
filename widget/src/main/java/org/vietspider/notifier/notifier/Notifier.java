/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.notifier.notifier;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 6, 2009  
 */
public class Notifier {
  
  private Shell parent;
  private int displayTime = 9000;
  private String title;
  private String message;
  private String id;
  private Image icon;
  private NotificationListener listener;
  
  public Notifier(Shell parent, String id) {
    this.parent = parent;
    this.id = id;
  }

  public Shell getParent() { return parent;    }
  public void setParent(Shell parent) { this.parent = parent; }

  public int getDisplayTime() { return displayTime; }
  public void setDisplayTime(int displayTime) { this.displayTime = displayTime; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public Image getIcon() { return icon; }
  public void setIcon(Image icon) { this.icon = icon; }

  public NotificationListener getListener() { return listener; }
  public void setListener(NotificationListener listener) { this.listener = listener; }
  
}

package org.vietspider.ui.action;

import java.util.List;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 27, 2006  
 */
public interface ActionListener {
  
  public void addAction(Object action);
  
  @Listeners()
  public List<Object> getActions() ;
  
  public void removeAction(Object action);
  
}

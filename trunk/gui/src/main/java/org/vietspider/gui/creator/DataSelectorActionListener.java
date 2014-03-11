package org.vietspider.gui.creator;

import java.util.LinkedList;
import java.util.List;

import org.vietspider.ui.action.ActionListener;
import org.vietspider.ui.action.Listeners;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 27, 2006  
 */
public abstract class DataSelectorActionListener implements ActionListener {
  
  private List<Object> actions = new LinkedList<Object>();
  
  public DataSelectorActionListener() {
  }
  
  public void addAction(Object action){ 
    actions.add(action); 
  }
  
  @Listeners()
  public List<Object> getActions() {  return actions; }
  
  public void removeAction(Object action){
    actions.remove(action);
  }
  
}

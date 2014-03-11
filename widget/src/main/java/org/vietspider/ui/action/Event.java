package org.vietspider.ui.action;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.List;
/**
 * Author : Nhu Dinh Thuan
 *         nhudinhthuan@yahoo.com
 * Sep 27, 2006  
 */
public class Event implements EventListener {

  
  public final static int EVENT_OK = 1;
  public final static int EVENT_CANCEL = 0;
  
  private Class<?> type;
  
  protected int eventType = EVENT_OK;

  public void fire(Object t) throws Exception {
    List<Object> actions = null;
    try {
      actions = Services.ACTION_GETTER.getActions(t, type);
    } catch(Exception exp) { 
      
    }    
    
    if(actions == null) return;
    for(Object action : actions){      
      fireAction(action);
    }
  }  

  private void fireAction(Object action) throws Exception  {
    Method [] methods = action.getClass().getDeclaredMethods();   
    for(Method method : methods){     
      if(method.getAnnotation(Executor.class) == null) continue;
      method.setAccessible(true);
      method.invoke(action, this);      
    }
  }

  public void setType(Class<?> type) { this.type = type; }

  public int getEventType() { return eventType; }

  public void setEventType(int eventType) { this.eventType = eventType; } 
}

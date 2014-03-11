/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.ui.action;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 27, 2006
 */
public final class ActionGetter {
  
  @SuppressWarnings("unchecked")
  List<Object> getActions(Object t, Class type) throws Exception {    
    Method [] methods = t.getClass().getMethods();
    Method method = null;
    for(Method ele : methods){
      if(ele.getAnnotation(Listeners.class) == null) continue;
      method = ele;
      break;
    }    
    if(method != null) {
      method.setAccessible(true);
      List<Object> actions = (List<Object>) method.invoke(t);
      if(type == null) return actions;
      Iterator<Object> iter = actions.iterator();
      List<Object> newActions = new LinkedList<Object>();
      while(iter.hasNext()){
        Object obj = iter.next();
        if(type.isInstance(obj)) newActions.add(obj); 
      }      
      return newActions;
    }
    
    List<Object> actions = new LinkedList<Object>();
    Listeners listeners = t.getClass().getAnnotation(Listeners.class);
    if(listeners == null) return actions;
    Class [] classes = listeners.value();
    for(Class ele : classes){
      if(type != null && type != ele) continue;
      actions.add(ele.newInstance());
    }
    return actions;
  }
  
}

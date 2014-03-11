/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2008  
 */
public class SynchronizedContentFilterPlugin extends UnreadContentFilterPlugin {

  public SynchronizedContentFilterPlugin() {
    filter = "/filter:2";
    ClientRM resources = new ClientRM("Plugin");
    Class<?>  clazz = SynchronizedContentFilterPlugin.class;
    label = resources.getLabel(clazz.getName()+".label");
  }
}

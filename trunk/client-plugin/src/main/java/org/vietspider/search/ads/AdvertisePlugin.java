package org.vietspider.search.ads;

import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;
import org.vietspider.ui.services.ClientRM;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Aug 13, 2008
 */

public class AdvertisePlugin extends ClientPlugin {

	private String label;
//	private String confirm;
	private ClientRM resources;
	
	public AdvertisePlugin() {
	  resources = new ClientRM(getClass(), "Advertise");
	 label = resources.getLabel(getClass().getName() + ".label");
		enable = true;
		
	}

	public String getConfirmMessage() {
//	  if(type  == CONTENT) {
//	    if(Application.LICENSE != Install.PERSONAL)  return null;
//	    return ; 
//	  }
	  return null; 
	}

  public String getLabel() { return label; }

  @Override
  @SuppressWarnings("unused")
  public boolean isValidType(int type_) {
    return false; 
  }
  
	@Override
	@SuppressWarnings("unused")
	public void invoke(Object... objects) {
	 
	}
	
	public boolean isSetup() { return true; }

  public void invokeSetup(Object...objects) {
    if(objects == null || objects.length < 1) return;
    
    final Control link = (Control) objects[0];
    new AdvertiseSetup(link.getShell());
  }
	
}
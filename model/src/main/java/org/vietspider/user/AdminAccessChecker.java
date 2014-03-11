/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.user;

import org.vietspider.model.Track;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2008  
 */
@SuppressWarnings("serial")
public class AdminAccessChecker extends AccessChecker {

  public AdminAccessChecker() {    
  }
  
  @SuppressWarnings("unused")
  public void computeMenu(Track menuInfo) { return; }
  
  @SuppressWarnings("unused")
  public boolean isPermitAccess(String value) { return true; }
  
  @SuppressWarnings("unused")
  public boolean isPermitGroup(String value) { return true; }
}

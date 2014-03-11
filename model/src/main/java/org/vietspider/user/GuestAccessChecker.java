/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.user;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2008  
 */
@SuppressWarnings("serial")
public class GuestAccessChecker extends AccessChecker {
  
  public GuestAccessChecker() {
  }
  
  public GuestAccessChecker(String [] categories){
    super(categories);
  }
  
  public boolean isPermitAccess(String value, boolean edit) {
    if(edit) return false;
    return super.isPermitAccess(value, edit);
  }
  
  @SuppressWarnings("unused")
  public boolean isPermitGroup(String value) { return false; }
  
}

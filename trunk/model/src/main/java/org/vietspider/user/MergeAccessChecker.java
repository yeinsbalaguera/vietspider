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
public class MergeAccessChecker extends AccessChecker {
  
  private AccessChecker accessChecker;
  private GuestAccessChecker guestAccessChecker;
  
  public MergeAccessChecker(AccessChecker accessChecker, GuestAccessChecker guestAccessChecker) {
    this.accessChecker = accessChecker;
    this.guestAccessChecker = guestAccessChecker;
  }
  
  @SuppressWarnings("unused")
  public boolean isPermitAccess(String value, boolean edit) {
    if(accessChecker.isPermitAccess(value, edit)) return true;
    if(edit) return false;    
    if(guestAccessChecker.isPermitAccess(value, edit)) return true;
    return false;
  }
  
  public boolean isPermitGroup(String value) {
    if(accessChecker.isPermitGroup(value)) return true;
    return false;
  }
  
}

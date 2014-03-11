/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2008  
 */
public abstract class ClientPlugin {
  
   public final static int  APPLICATION = -1;
  
   public final static int  DOMAIN = 0;
   public final static int  CONTENT = 1;
   
   public final static int  LOG = 2;
   
   protected String [] values;
   protected boolean enable = true;
   
   public abstract boolean isValidType(int type);
   
   public String getLabel() { return "plugin"; }
   
   public String getConfirmMessage() { return null; }
   
   public void setValues(String...values) { this.values = values;}
   
  public abstract void invoke(Object...objects);
   
   @SuppressWarnings("unused")
   public void syncEnable(Object...objects) { }
   
   public boolean isSetup() { return false; }
   
   @SuppressWarnings("unused")
   public void saveSetup(Object...objects) { }
   
   @SuppressWarnings("unused")
   public void invokeSetup(Object...objects) { }
   
   public void resetSetup() { }

   public boolean isInvisible() { return false; }

}

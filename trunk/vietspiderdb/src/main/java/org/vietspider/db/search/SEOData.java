/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.search;

import java.util.Calendar;

import org.vietspider.common.io.MD5Hash;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.Object2XML;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2010  
 */
@NodeMap("seo")
public class SEOData {
  
  public final static short DELETE = 1000;
  public final static short EMAIL = 0;
  public final static short PHONE = 1;
  
  @NodeMap("type")
  private short type = EMAIL;
  @NodeMap("data")
  private String data;
  
  @NodeMap("update")
  private long update = -1;
  
  private byte[] key;
  
  public SEOData() {
  }
  
  public SEOData(String data, short type) {
    this.data = data;
    this.type  = type;
  }
  
  public short getType() { return type; }
  public void setType(short type) { this.type = type; }
  
  public String getData() { return data; }
  public void setData(String data) { this.data = data; }
  
  public long getUpdate() { return update;  }
  public void setUpdate(long update) {
    this.update = update;
  }

  public boolean isNeedProcess() {
    return System.currentTimeMillis() - update <= 24*60*60*1000l;
  }
  
  public boolean needUpdate() {
//    System.out.println(data + " : " + update);
    if(update < 1) return true;
    Calendar updateCalendar = Calendar.getInstance();
    updateCalendar.setTimeInMillis(update);

    Calendar currentCalendar = Calendar.getInstance();
    
//    System.out.println("data: " + data+ " update " +updateCalendar.get(Calendar.MONTH)
//        + ": current " +   currentCalendar.get(Calendar.MONTH)
//        + ": update date: " + updateCalendar.get(Calendar.DAY_OF_MONTH) 
//        + ": current date: " +  currentCalendar.get(Calendar.DAY_OF_MONTH) + " = " 
//        + (updateCalendar.get(Calendar.MONTH) ==  currentCalendar.get(Calendar.MONTH)
//            && updateCalendar.get(Calendar.DAY_OF_MONTH) ==  currentCalendar.get(Calendar.DAY_OF_MONTH)));

    if(updateCalendar.get(Calendar.MONTH) ==  currentCalendar.get(Calendar.MONTH)
        && updateCalendar.get(Calendar.DAY_OF_MONTH) ==  currentCalendar.get(Calendar.DAY_OF_MONTH)) {
      return false;
    }

    return true;
  }

  public byte[] getKey() {
    if(key != null) return key;
    MD5Hash md5Hash = MD5Hash.digest(data.toLowerCase());
    key  = md5Hash.getDigest();
    return key;
  }

  public void setKey(byte[] key) {
    this.key = key;
  }
  
  void updateTime() {
    Calendar currentCalendar = Calendar.getInstance();
    update = currentCalendar.getTimeInMillis();
  }
  
  public String toXML() throws Exception {
    return Object2XML.getInstance().toXMLDocument(this).getTextValue();
  }
  

  
}

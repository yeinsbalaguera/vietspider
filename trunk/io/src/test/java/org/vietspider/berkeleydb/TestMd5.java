/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.berkeleydb;

import org.vietspider.common.io.MD5Hash;
import org.vietspider.link.V_URL;

import com.sun.org.apache.bcel.internal.generic.NEW;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2009  
 */
public class TestMd5 {
  public static void main(String[] args) throws Exception {
    
    String url = "http://www20.24h.com.vn/news/detail/55/228541";
    V_URL v_url = new V_URL(url);
    MD5Hash mdHash1 = MD5Hash.digest(v_url.toNormalize());
    
    v_url = new V_URL("http://vnexpress.net/GL/Xa-hoi/2009/07/3BA11C4E/?SFDasd=89&g=fdskfdsf", "g=");
    System.out.println(v_url.toNormalize());
    
    url = "http://www16.24h.com.vn/news/detail/55/228541";
    v_url = new V_URL(url);
    MD5Hash mdHash2 = MD5Hash.digest(v_url.toNormalize());
    System.out.println(mdHash1.equals(mdHash2));
    
    url = "SADMIN";
    mdHash2 = MD5Hash.digest(url);
    System.out.println(mdHash2.toString());
    
    
    url = "http://www.datvietflower.com/?page=1&l=2&1&l=2&p=2&1&l=2&p=1&1&l=2&p=2&1&l=2&p=3&1&l=2&p=2&1&l=2&p=2&1&l=2&p=2&1&l=2&p=3";
    v_url = new V_URL(url, null);
    System.out.println(v_url.toNormalize());
    
    
    
  }
}

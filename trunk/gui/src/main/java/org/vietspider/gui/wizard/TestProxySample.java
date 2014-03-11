package org.vietspider.gui.wizard;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class TestProxySample {

  public static void main(String[] args) throws Throwable {
    System.setProperty("java.net.useSystemProxies", "true");
    List<Proxy> pl = ProxySelector.getDefault().select(new URI("http://ihned.cz/"));
    System.out.println(ProxySelector.getDefault());
    for (Proxy p : pl) {
      System.out.println(p.address());
      System.out.println(p.type());
      System.out.println("=====");
      System.out.println(p);
    }
  }
}

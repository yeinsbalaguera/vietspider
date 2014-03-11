/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.httpserver;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public class GetLocalHostName {

  public static void main(String args[]) throws Exception {

    System.out.println("đã có sổ hồng nhà có gác lửng".length());
//    Enumeration<NetworkInterface> nets = 
//      NetworkInterface.getNetworkInterfaces();
//
//    for (NetworkInterface netint : Collections.list(nets)) {
//      System.out.println("\nInterface Name : " + netint.getDisplayName());
//
//      Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
//
//      for (InetAddress inetAddress : Collections.list(inetAddresses)) {
//        System.out.println("Local host Name\t: " + inetAddress.getHostName());
//        System.out.println("Host Address\t: " + inetAddress.getHostAddress());
//      }
//    }
//    System.out.println(InetAddress.getLocalHost().getHostName());
  }
}
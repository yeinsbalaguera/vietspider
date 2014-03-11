/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 11, 2008  
 */
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPAdress {
 public void  getInterfaces (){
      try {
         Enumeration e = NetworkInterface.getNetworkInterfaces();

         while(e.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e.nextElement();
            System.out.println("Net interface: "+ni.getName());

            Enumeration e2 = ni.getInetAddresses();

            while (e2.hasMoreElements()){
               InetAddress ip = (InetAddress) e2.nextElement();
               System.out.println(ip.getHostAddress());
               System.out.println("IP address: "+ ip.toString());
            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
    IPAdress ip = new IPAdress();
    ip.getInterfaces();
   }
}

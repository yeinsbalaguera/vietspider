/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 21, 2008  
 */
public class TestMd5Encoder {
  
  private static String hexMd5(String password) throws Exception {
//  System.out.println("===== > "+ password);
    /*MessageDigest algorithm = MessageDigest.getInstance("MD5");
    algorithm.reset();
    algorithm.update(password.getBytes());
    byte messageDigest[] = algorithm.digest();

    StringBuffer hexString = new StringBuffer();
    for (int i=0;i<messageDigest.length;i++) {
      hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
    }
    return hexString.toString();*/
    
    String hashword = null;
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(password.getBytes());
      BigInteger hash = new BigInteger(1, md5.digest());
      hashword = hash.toString(16);
      return pad(hashword, 32, '0');
    } catch (NoSuchAlgorithmException nsae) {

    }
    return hashword;
  }
  
  private static String pad(String s, int length, char pad) {
    StringBuffer buffer = new StringBuffer(s);
    while (buffer.length() < length) {
      buffer.insert(0, pad);
    }
    return buffer.toString();
  }

  public static void main(String[] args)  throws Exception {
     System.out.println(hexMd5("13gh67"));
  }
}

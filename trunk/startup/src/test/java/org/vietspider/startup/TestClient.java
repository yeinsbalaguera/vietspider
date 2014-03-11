package org.vietspider.startup;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
public class TestClient {

  public static void main(String[] args) {
    try {
      Class<?> clazz = Class.forName("org.vietspider.startup.StartClient");

//    File file  = new File("D:\\java\\test\\data3");
      File file  = new File("D:\\java\\vietspider3\\workspace\\test\\vsnews\\data\\");

      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");

      clazz.newInstance();
    }catch (Exception e) {
      e.printStackTrace();
    }      
  }

}

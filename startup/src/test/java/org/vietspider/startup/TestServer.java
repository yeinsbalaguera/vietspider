package org.vietspider.startup;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
public class TestServer {
  
  public static void main(String[] args) {
    try {
      Class<?> clazz = Class.forName("org.vietspider.startup.StartServer");
      
      File file = new File(clazz.getResource("/").toURI());
      String path = file.getCanonicalPath()+File.separator+".."+File.separator+"..";
      path += File.separator+"src"+File.separator+"test"+File.separator+"data";
      file  = new File(path);
      
      file = new File("D:\\Releases\\search\\vssearch\\data\\");
      
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
//      System.setProperty("vietspider.data.path.1", "D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data_1\\");
//      System.setProperty("vietspider.test", "true");
      
      clazz.newInstance();
    }catch (Exception e) {
      e.printStackTrace();
    }      
  }
  
}

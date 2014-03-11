package org.vietspider.startup;

import java.io.File;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 18, 2007  
 */
public class TestHTMLExplorer {

  public static void main(String[] args) {
    try {
      Class<?> clazz = Class.forName("org.vietspider.startup.StartHTMLExplorer");

      File file  = new File("D:\\java\\vietspider3\\workspace\\test\\vsnews\\data\\");

      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
//      System.setProperty("org.eclipse.swt.browser.XULRunnerPath", "D:\\VietSpider Build 19\\xulrunner");
      
      clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

package org.vietspider.startup;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.vietspider.price.export.PriceIndexExporter;
import org.vietspider.price.index.AutoPriceIndexing;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
public class TestCrawler {

  public static void main(String[] args) {
    try {
      Class<?> clazz = Class.forName("org.vietspider.startup.StartAll");

      File file  = new File("D:\\java\\vietspider3\\workspace\\test\\vscrawler\\data\\");
      
//      System.out.println(file.getCanonicalPath());
      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
//      new AutoPriceIndexing();
      
//      try {
//        new PriceIndexExporter().exportToFile("hà nội", "1,1");
//      } catch (Throwable e) {
//        LogService.getInstance().setThrowable(e);
//      }
      
      clazz.newInstance();
      
    }catch (Exception e) {
      e.printStackTrace();
    }      
  }

}

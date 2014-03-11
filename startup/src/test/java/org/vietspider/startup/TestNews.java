package org.vietspider.startup;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
public class TestNews {

  public static void main(String[] args) {
    try {
      Class<?> clazz = Class.forName("org.vietspider.startup.StartAll");

      File file  = new File("E:\\Program\\VietSpider3_19 XML_Windows_Pro\\data");
//      File file  = new File("D:\\Program\\VietSpiderBuild19\\data\\");
//      File file  = new File("F:\\Bakup\\codes\\vietspider3\\test\\news\\data\\");
//      File file = new File("D:\\Program\\VietSpider3_19_News_Vi_Linux\\data\\");
//      File file = new File("D:\\Releases\\Releases\\VietSpider3_19_XML_Windows\\data\\");
//      File file = new File("D:\\Releases\\Releases\\VietSpider3_17_1_XML_Win64\\data\\");
//      File file = new File("D:\\VietSpider Build 19\\data\\");
//      File file  = new File("D:\\Releases\\Joomla_Demo\\VietSpider3_18_XML_Solr\\data\\");
//      File file  = new File("D:\\Releases\\Releases\\VietSpider3_18_1_News_Vi_Windows\\data");
      
//      System.out.println(file.getCanonicalPath());
      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }      
  }

}

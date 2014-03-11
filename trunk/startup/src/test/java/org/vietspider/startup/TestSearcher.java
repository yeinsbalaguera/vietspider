package org.vietspider.startup;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
public class TestSearcher {

  public static void main(String[] args) {
    try {
      Class<?> clazz = Class.forName("org.vietspider.startup.StartAll");

//      File file = new File(clazz.getResource("/").toURI());
//      String path = file.getCanonicalPath()+File.separator+".."+File.separator+"..";
//      path += File.separator+"src"+File.separator+"test"+File.separator+"data";
//      file  = new File(path);
      
      File file  = new File("D:\\java\\vietspider3\\workspace\\test\\vssearcher\\data\\");
      
//      file  = new File("D:\\Releases\\VietStock\\VietSpiderServerBuild17\\data\\");
      
//      file  = new File("D:\\VietSpider Build 17\\data");
//      file = new File("D:\\Releases\\TinhVan\\BoTC\\VietSpiderServerBuild17\\data\\");
//      file = new File("F:\\Projects\\VietStock\\VietSpider3Build13Server\\data\\");
//      file = new File("D:\\Releases\\search\\vssearch\\data\\");
//      file  = new File("D:\\Releases\\vssearch\\vs_nik_client\\data\\");

//      System.out.println(file.getCanonicalPath());
      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
//      System.setProperty("vietspider.data.path.1", "D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data_1\\");
//      System.setProperty("vietspider.test", "true");

      clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }      
  }

}

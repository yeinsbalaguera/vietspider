package org.vietspider.crawler.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 29, 2006
 */
public class Native2ASCII {

  /**
   * @param args
   */
  public static void main(String[] args) {
    try{     
      File f = new File("D:\\java\\test\\name.txt");
      if(!f.exists()) f.createNewFile();
      Properties p = new Properties();    
      FileInputStream input = new FileInputStream( f);
      p.load(input);
      String [] value = {
          "Trang nội dung không khới với Dạng trang nội dung!",
          "Quét từ trang",
          "Thêm trang bắt đầu quét từ nó",
          "Xong và Quét"
          
          
         
      };
      for(int i=0; i<value.length; i++){
        p.setProperty(String.valueOf(i), value[i]);        
      }
      FileOutputStream output = new FileOutputStream( f);
      p.store(output, "/* properties config by veis */");
      output.flush();
      output.close(); 
      
      String time = "04\\10\\2006 12:03:25.0";
      if(time.length() > 19) time  = time.substring(0, 19);
      System.out.println(time.indexOf('\\'));
      System.out.println(time);
    }catch(Exception exp){
      exp.printStackTrace();
    }

  }

}

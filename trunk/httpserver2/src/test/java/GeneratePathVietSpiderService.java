import java.io.File;

/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 6, 2010  
 */
public class GeneratePathVietSpiderService {
  public static void main(String[] args) {
    File file = new File("D:\\Releases\\search\\vssearch\\lib\\") ;
    File [] files = file.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) continue;
      System.out.println("wrapper.java.classpath."+(i+1)+"=..\\"+files[i].getName());
    }
  }
} 

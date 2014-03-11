/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.unit;

import java.io.File;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 10, 2006
 */
public class TestBedVietnamet {

  public static void main(String[] args) throws Exception {
    File file = new File("E:\\Temp\\error_html\\blogbk.html");
    long start = System.currentTimeMillis();
    HTMLNode node = HTMLParser.createDocument(file, "utf-8").getRoot();
    long end = System.currentTimeMillis();
    System.out.println(end - start);
    System.out.println(node.getTextValue());
    
  /*  FileInputStream input = new FileInputStream(file);
    FileChannel fchan = input.getChannel();
    long fsize = fchan.size();       
    System.out.println(fsize);
//    ByteBuffer.allocateDirect((int)fsize)
    ByteBuffer buff =  ByteBuffer.allocateDirect((int)fsize); 
      //ByteBuffer.allocate((int)fsize);        
    fchan.read(buff);
    buff.rewind();
    byte[] data = buff.array();      
    buff.clear();      
    fchan.close();        
    input.close();     
    System.out.println(new String(data, "UTF-8"));*/
  }

}

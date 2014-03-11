/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import java.io.File;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 2, 2008  
 */
public class TestHTMLRef {
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\Document\\VietSpider\\Build11\\VietSpider3Build11_Help\\vn\\html\\Menu.html");
    byte []  bytes  = RWData.getInstance().load(file);
    RefsDecoder encoder = new RefsDecoder();
    String value  = new String(bytes, "utf-8");
    value = new String(encoder.decode(value.toCharArray()));
    
    File file2  = new File("D:\\Document\\VietSpider\\Build11\\VietSpider3Build11_Help\\vn\\html\\Menu2.html");
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    writer.save(file2, value.getBytes("utf-8"));
    
//    String value =  "&#109;&#97;&#105;&#108;&#116;&#111;&#58;&#120;&#105;&#99;&#104;&#113;&#117;&#121;&#64;&#104;&#111;&#116;&#109;&#97;&#105;&#108;&#46;&#99;&#111;&#109;";
//    System.out.println(new String(encoder.decode(value.toCharArray())));

  }
}

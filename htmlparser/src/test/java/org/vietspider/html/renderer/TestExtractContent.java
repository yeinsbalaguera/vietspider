/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 16, 2009  
 */
public class TestExtractContent {
  
  static ContentRegionSearcher2 contentRegionSearcher2 = new ContentRegionSearcher2();  
  
  private static void process(File file, boolean single) throws Exception {
    System.out.println(" chuan bi ================= >"+ file.getAbsolutePath());
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    process(document, single, file.getName());
  }
  
  private static void process(HTMLDocument document, 
      boolean single, String name) throws Exception {
    String url = "http://ebo299.wordpress.com/2008/12/29/d%e1%ba%b9p-lung-linh-nh%e1%bb%afng-kho%e1%ba%a3nh-kh%e1%ba%afc-an-m%e1%bb%abng-chi%e1%ba%bfn-th%e1%ba%afng/";
    HTMLNode node = contentRegionSearcher2.extractContent(document, url, true);
    
    File file;
    if(single) {
      file  = new File("F:\\Temp2\\web\\output\\a.html");
    } else {
      file  = new File("F:\\Temp2\\web\\output\\"+name+".html");
    }
    
    byte [] bytes = node.getTextValue().getBytes(Application.CHARSET);
    org.vietspider.common.io.RWData.getInstance().save(file, bytes);
    
  }
  
  public static void main(String[] args) throws Exception {
    UtilFile.deleteFolder(new File("F:\\Temp2\\web\\output\\"), false);
    File file = new File("F:\\Temp2\\web\\output\\");
    file.mkdirs();
    
    String [] name = {
        /*"laodong1.htm"*/ /*"vnexpress2.htm"*/ /*"vietnamnet.htm"*/ /*"thanhnien.htm"*/ 
        /*"dantri.htm"*/ /*"vnmedia.htm"*/ /*"kenh14_1.htm"*/ /*"autocad.htm"*/ 
        /*"vneconomy.htm"*/ /*"wordpress10.htm"*/ /*"bocsaiwordpress.htm"*/ 
        /*"wordpress12.htm"*/ /*"sky1.htm"*/ /*"laodong1.htm"*/ /*"vnba.htm"*/
        /*"vnweblogs.htm"*/ /*"sky1.htm"*/ /*"sky2.htm"*/ /*"blogger4.htm"*/
        "ngoisaoblog8.htm"
        };
    file  = new File("F:\\Temp2\\web\\"+name[0]);
//    process(file, true);
    
    File folder  = new File("F:\\Temp2\\web\\");
//    File folder = new File("F:\\Temp2\\web\\site\\");
    
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].getName().endsWith(".htm") 
          || files[i].getName().endsWith(".html")) {
        process(files[i], false);
      }
    }
    
//    String address  = "http://kentduc.sky.vn/archives/11";
//    java.net.URL url = new java.net.URL(address);
//    HTMLDocument document = HTMLParser.createDocument(url.openStream(),"utf-8");
//    process(document, true, address);
    
  }
  
}

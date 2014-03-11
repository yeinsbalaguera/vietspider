/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.regex;

import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 5, 2011  
 */
public class TestURLParser {
  public static void main(String[] args) {
    //  String url  = "http://www.devdaily.com/blog/post/java/java-program-download-parse-contents-of-url";
    //  String url  = "http://www.devdaily.com";
    //  String url  = "http://www.devdaily.com/";
    //  String url = "http://chodua.com/abc/blog_detail.asp?id=21733&page=3";
    //  String url = "http://chodua.com/abc/blog_detail.asp?id=21733&page=3#sssddsds";
    //  String url  =  "http://www.fsoft.com.vn/Lists/General%20Discussion/Flat.aspx?RootFolder=%2fLists%2fGeneral%20Discussion%2f%c4%90%e1%bb%81%20ngh%e1%bb%8b%20b%e1%bb%8f%20ch%c4%83n%20mail%20%40fpt%2eedu%2evn&FolderCTID=0x01200200D5150BE569F10B4FA22EA5A11D6AA5FD";

//    String url = "toggle_info(\'*\','*')";
//    String url = "toggle_info(\'*\' , '*')";
//    String url = "toggle_info('../ajax/product_detail.php?divid=df&product_id=35sdfsdf','23432')";
//    String url = "toggle_info('../ajax/product_detail.php?divid=df&product_id=35sdfsdf','23432)";
    
//    String url = "toggle_info('../ajax/product_detail.php?divid=df&product_id=35sdfsdf' , 23432)";
    
    String url = "toggle_info(../ajax/product_detail.php?divid=df&product_id=35sdfsdf , '23432')";

    URIElement elements = URIParser.parse(url);
    List<Element> list = elements.getElements();
    for(int i = 0; i < list.size(); i++) {
      System.out.println(list.get(i).getType() + " : " + list.get(i).getValue());
    }
  }
}

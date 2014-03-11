/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import static org.vietspider.link.pattern.LinkPatternFactory.createPatterns;

import org.vietspider.link.pattern.LinkPatterns;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2008  
 */
public class TestLinkPatterns {
  
  public static void main(String[] args) throws Exception {
    String [] elements = {
//        "http://blog.360.yahoo.com/blog-*"
//        "http://*.vnweblogs.com/post/*/*#comments",
//        "http://*.vnweblogs.com"
//        "http://forum.vannho.info/showthread.php/*.html"
//        "http://forum.thainguyen.edu.vn/Default.aspx?g=posts&t=*"
        "http://www2.thanhniennews.com/*/?catid=*&newsid=*"
    };
    
    LinkPatterns linkPatterns = createPatterns(LinkPatterns.class, elements);
    
//    String url = "http://phamtran.vnweblogs.com/post/5666/5678#comments";
//    String url = "http://blog.360.yahoo.com/blog-I9JQvW42d6_WVFief4WhA4roKkQZ";
//    String url  = "http://bantroikhoa7.vnweblogs.com/";
//    String url  = "http://forum.vannho.info/showthread.php/showthread.php/showthread.php/showthread.php/showthread.php/showthread.php/showthread.php/nh-c-theo-5187.html";
//    String url = "http://203.162.71.77:100/vn/print/4249/index.aspx";
//    String url = "http://forum.thainguyen.edu.vn/Default.aspx?g=login&ReturnUrl=%2fDefault.aspx%3fg%3dlogin%26ReturnUrl%3d%252fDefault.aspx%253fg%253dlogin%2526ReturnUrl%253d%25252fDefault.aspx%25253fg%25253dlogin%252526ReturnUrl%25253d%2525252fDefault.aspx%2525253fg%2525253dlogin%25252526ReturnUrl%2525253d%252525252fDefault.aspx%252525253fg%252525253dlogin%2525252526ReturnUrl%252525253d%25252525252fDefault.aspx%25252525253fg%25252525253dlogin%252525252526ReturnUrl%25252525253d%2525252525252fDefault.aspx%2525252525253fg%2525252525253dlogin%25252525252526ReturnUrl%2525252525253d%252525252525252fDefault.aspx%252525252525253fg%252525252525253dlogin%2525252525252526ReturnUrl%252525252525253d%25252525252525252fDefault.aspx%25252525252525253fg%25252525252525253dlogin%252525252525252526ReturnUrl%25252525252525253d%2525252525252525252fDefault.aspx%2525252525252525253fg%2525252525252525253dlogin%25252525252525252526ReturnUrl%2525252525252525253d%252525252525252525252fDefault.aspx%252525252525252525253fg%252525252525252525253dprofile%2525252525252525252526u%252525252525252525253d127";
    String url = "http://thanhniennews.com/sports/?catid=5&newsid=39159";
    
    System.out.println(" bi length " + url.length());
    System.out.println(linkPatterns.match(url));
  }
  
}

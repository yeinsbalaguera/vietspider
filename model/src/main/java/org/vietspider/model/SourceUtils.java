/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2009  
 */
public class SourceUtils {
  
  public static String getCodeName(Source source) {
    String codeName  = source.getCodeSite();
    if(codeName != null) return codeName;
    String [] home = source.getHome();
    if(home == null || home.length < 1) {
      source.setCodeSite("null");
      return "null";
    }
    codeName = getCodeName(home[0]);
    source.setCodeSite(codeName);
    return codeName;
  }
  
  public static String getCodeName(String home) {
    String string = home.toLowerCase().trim() ;
    int idx = string.indexOf("://") ;
    if(idx > 0) {
      string = string.substring(idx + 3, string.length());
    }
    
    idx = string.indexOf('/') ;
    if(idx > 0) string = string.substring(0, idx) ;

    if(string.startsWith("www")) {
      if(string.length() > 3 && string.charAt(3) != '.') {
        idx = string.indexOf('.') ;
        string = "www." + string.substring(idx + 1, string.length()) ;
      }
    } else {
      string = "www." + string ;
    }
   
    StringBuilder builder = new StringBuilder();
    idx = 0; 
    while(idx < string.length()) {
      char c = string.charAt(idx);
      if(Character.isLetterOrDigit(c) || c == '.') {
        builder.append(c);  
      } else {
        builder.append('_');
      }
      idx++;
    }
    return builder.toString();
  }
  
  
  public static String getFileName(String home) {
    String string = home.toLowerCase().trim() ;
    int idx = string.indexOf("://") ;
    if(idx > 0) {
      string = string.substring(idx + 3, string.length());
    }
    
    idx = string.indexOf('/') ;
    if(idx > 0) string = string.substring(0, idx) ;

    if(string.startsWith("www")) {
      if(string.length() > 3 && string.charAt(3) != '.') {
        idx = string.indexOf('.') ;
        string = "www." + string.substring(idx + 1, string.length()) ;
      }
    } else {
      string = "www." + string ;
    }
   
    StringBuilder builder = new StringBuilder();
    idx = 0; 
    while(idx < string.length()) {
      char c = string.charAt(idx);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);  
      } else {
        builder.append(' ');
      }
      idx++;
    }
    return builder.toString();
  }
  
  /*public static void main(String[] args) {
    Source source = new Source();
    source.setHome(new String[]{"http://sourceforge.net:9090/project/stats/detail.php?group_id=158429&ugn=binhgiang&type=prdownload"});
    System.out.println(getCodeName(source));
  }*/
}

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.js;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 7, 2008  
 */
public class JsUtils {

  public static String [] getParams(String jsFunction) {
    List<String> list = new ArrayList<String>();
    int i = 0;

    while(i < jsFunction.length()) {
      char c = jsFunction.charAt(i);
      i++;
      if(c == '(' || c == '=') break;
    }

    while(i < jsFunction.length()) {
      char c = jsFunction.charAt(i);

      while(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        i++;
        if(i >= jsFunction.length()) break;
        c = jsFunction.charAt(i);
      }

      if(c == ',') {
        i++;
        continue;
      } else if (c  == ')' && jsFunction.charAt(i-1) != '\\' ) {
        break;
      }

      while(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        i++;
        if(i >= jsFunction.length()) break;
        c = jsFunction.charAt(i);
      }

      if(c == '\'') {
        i++;
        int start = i;
        while(i < jsFunction.length()) {
          c = jsFunction.charAt(i);
          if( (c == '\'' && jsFunction.charAt(i-1) != '\\')
              || (c  == ')' && jsFunction.charAt(i-1) != '\\')) break;
          i++;
        }
        if(i <= start) {
          list.add("");
        } else {
          list.add(jsFunction.substring(start, i).trim());
        }
        i++;
        continue;
      } 

      if(c == '"') {
        i++;
        int start = i;
        while(i < jsFunction.length()) {
          c = jsFunction.charAt(i);
          if( (c == '"' && jsFunction.charAt(i-1) != '\\')
              || (c  == ')' && jsFunction.charAt(i-1) != '\\')) break;
          i++;
        }
        if(i <= start) {
          list.add("");
        } else {
          list.add(jsFunction.substring(start, i).trim());
        }
        i++;
        continue;
      } 

      int start = i;
      i++;
      while(i < jsFunction.length()) {
        c = jsFunction.charAt(i);
        if((c == ',' && jsFunction.charAt(i-1) != '\\')
            || (c  == ')' && jsFunction.charAt(i-1) != '\\') ) break;
        i++;
      }

      if(i <= start) {
        list.add("");
      } else if( start < Math.min(i, jsFunction.length())){
        list.add(jsFunction.substring(start, Math.min(i, jsFunction.length())).trim());
      }

    }

    return list.toArray(new String[list.size()]);
  }

//  public static void main(String[] args) throws Exception {
//    String aaa = "200905181719249.1.108896_14113.rar";
//    String value = "javascript:popupPageMail('/gdtLive/Khoi-chuc-nang/Mail-popup?contentId=115584&amp;location=tct&amp;sendSiteNodeId=100074')";
//    String [] params = JsUtils.getParams(value);
//    for(String param : params ){
//      System.out.println(param);
//    }
//  }
//StringBuilder builder = new StringBuilder("  <div class=\"bText\">");
//builder.append("\n\t\t<script language=\"javascript\"> show_postcontent('a aaaa bbdd\n");
//builder.append("<font face=\"Ariral\"> sdfdshfjdsfds </font> xcvxcvcxdf 333223 ');</script>\n\t");
//builder.append("<a id=\"feedbacks\"></a><a id=\"comments\"></a><a id=\"trackbacks\">");
//builder.append("</a><a id=\"pingbacks\"></a> \n\t");
//builder.append("<h4>Địa chỉ gửi link liên kết đến bài viết này</h4>");

//List<String> jsScripts = new ArrayList<String>();
//jsScripts.add("show_postcontent");

//HTMLDocument document = HTMLParser.createDocument(builder.toString());
//JsScriptHandler.updateDocument(document, jsScripts);
//System.out.println(document.getTextValue());
//}
}

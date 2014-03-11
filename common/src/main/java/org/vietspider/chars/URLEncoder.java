/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.chars;

import java.net.URLDecoder;
import java.util.Arrays;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 13, 2008  
 */
public class URLEncoder {
  
  private final static int PATH = 0;
  private final static int PARAM_NAME = 1;
  private final static int PARAM_VALUE = 1;
  
  private final static String[] hex = {
    "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
    "%08", "%09", "%0A", "%0B", "%0C", "%0D", "%0E", "%0F",
    "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
    "%18", "%19", "%1A", "%1B", "%1C", "%1D", "%1E", "%1F",
    "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
    "%28", "%29", "%2A", "%2B", "%2C", "%2D", "%2E", "%2F",
    "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
    "%38", "%39", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F",
    "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
    "%48", "%49", "%4A", "%4B", "%4C", "%4D", "%4E", "%4F",
    "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
    "%58", "%59", "%5A", "%5B", "%5C", "%5D", "%5E", "%5F",
    "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
    "%68", "%69", "%6A", "%6B", "%6C", "%6D", "%6E", "%6F",
    "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
    "%78", "%79", "%7A", "%7B", "%7C", "%7D", "%7E", "%7F",
    "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
    "%88", "%89", "%8A", "%8B", "%8C", "%8D", "%8E", "%8F",
    "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
    "%98", "%99", "%9A", "%9B", "%9C", "%9D", "%9E", "%9F",
    "%A0", "%A1", "%A2", "%A3", "%A4", "%A5", "%A6", "%A7",
    "%A8", "%A9", "%AA", "%AB", "%AC", "%AD", "%AE", "%AF",
    "%B0", "%B1", "%B2", "%B3", "%B4", "%B5", "%B6", "%B7",
    "%B8", "%B9", "%BA", "%BB", "%BC", "%BD", "%BE", "%BF",
    "%C0", "%C1", "%C2", "%C3", "%C4", "%C5", "%C6", "%C7",
    "%C8", "%C9", "%CA", "%CB", "%CC", "%CD", "%CE", "%CF",
    "%D0", "%D1", "%D2", "%D3", "%D4", "%D5", "%D6", "%D7",
    "%D8", "%D9", "%DA", "%DB", "%DC", "%DD", "%DE", "%DF",
    "%E0", "%E1", "%E2", "%E3", "%E4", "%E5", "%E6", "%E7",
    "%E8", "%E9", "%EA", "%EB", "%EC", "%ED", "%EE", "%EF",
    "%F0", "%F1", "%F2", "%F3", "%F4", "%F5", "%F6", "%F7",
    "%F8", "%F9", "%FA", "%FB", "%FC", "%FD", "%FE", "%FF"
  };
  
  
  private final static String WHITE_CHAR_VALUE = "%20";
  
  public String encode(String url) {
    if(url.indexOf('%') > -1) {
      try {
        url = URLDecoder.decode(url, Application.CHARSET);
      } catch (Exception e) {
      }
    }
    
    int type  = PARAM_VALUE;
    int index = 0;
    int length  = url.length();
    while(index < length) {
      char c = url.charAt(index);
      if(c == '/' || c == '\\') {
        type = PATH;
        break;
      } else if(c == '?') {
        type = PATH;
        break;
      } else if(c == '&') {
        type  = PARAM_NAME;
        break;
      }
      index++;
    }
    
    index = 0;
    while(index < length) {
      char c = url.charAt(index);
      if((Character.isSpaceChar(c) || Character.isWhitespace(c))) {
        index++;
        continue;
      }
      break;
    }
    
    StringBuilder builder = new StringBuilder(url.length() + 50);
    boolean ref = false;
    while(index < length) {
      char c = url.charAt(index);

      if(c == '\\')  c = '/';
      if(c == '&' 
        && index + 4 < length) {
        char c1 = url.charAt(index + 1);
        char c2 = url.charAt(index + 2);
        char c3 = url.charAt(index + 3);
        char c4 = url.charAt(index + 4);
        if( (c1 == 'a' || c1 == 'A') &&
            (c2 == 'm' || c2 == 'M') &&
            (c3 == 'p' || c3 == 'P') &&
            (c4 == ';') )  index += 4;  
      }
      
      if(type == PATH && c == '?') {
        type = PARAM_NAME;
        builder.append(c);
      } else if(type == PARAM_NAME && c == '=') {
        type = PARAM_VALUE;
        builder.append(c);
      } else if(type == PARAM_VALUE && c == '&') {
        type = PARAM_NAME;
        builder.append(c);
      } else if (c == '\'' || c == '\"') {  
        encode(c, builder);
      } else if(c == '#') {
        if(type == PARAM_VALUE || ref) {
          builder.append("%23");
        } else {
          ref = true;
          builder.append(c);
        }
      } else {
        if(type == PARAM_VALUE) {
          encode(c, builder);
        } else if(type == PATH) {
          if((Character.isSpaceChar(c) || Character.isWhitespace(c))) {
//            if(start < i) {
//              builder.append(address.subSequence(start, i));
//              start = i+1;
//            }
            
            if(index > 0 
                && Arrays.binarySearch(URLUtils.URICS, url.charAt(index-1)) > -1) {
              index++;
              continue;
            }
            
            while(index+1 < url.length()) {
              char c1 = url.charAt(index+1);
              if(Character.isSpaceChar(c1) 
                    || Character.isWhitespace(c1)) {
                index++;
                continue;
              }
              break;
            }
            
            if(index+1 < url.length() 
                && Arrays.binarySearch(URLUtils.URICS, url.charAt(index+1)) > -1) {
              index++;
              continue;
            }
            
            if(index < url.length() - 1) builder.append(WHITE_CHAR_VALUE);
            
          } else if(Character.isLetter(c)){
            encode(c, builder);
          } else {
            if(Arrays.binarySearch(URLUtils.URICS, c) > -1) {
              builder.append(c);
            } else {
              encode(c, builder);
            }
          }
        } else {
          builder.append(c);
        }
      }
      index++;
    }
    
//    System.out.println(url);
//    System.out.println(builder+"\n");
    return builder.toString();
  }
  
  private void encode(char c, StringBuilder builder) {  
//    try {
//      builder.append(java.net.URLEncoder.encode(new String(new char[]{c}), "utf-8"));
//    } catch (Exception e) {
//    }
    if ('A' <= c && c <= 'Z') {   // 'A'..'Z'
      builder.append(c);
    } else if ('a' <= c && c <= 'z') {  // 'a'..'z'
      builder.append(c);
    } else if ('0' <= c && c <= '9') {  // '0'..'9'
      builder.append(c);
    } else if (c == ' ') {     // space
      builder.append(WHITE_CHAR_VALUE);
    } else if (c == '-' || c == '_'   // unreserved
                || c == '.' || c == '!' 
                || c == '@' || c == '#'
                || c == '+' || c == '/'
                || c == ',' || c == '*'
        /*|| ch == '~'*/ 
          /*|| ch == '\''*/ /*|| ch == '('*/
            /*|| ch == ')'*/) {
      builder.append(c);
    } else if (c <= 0x007f) {    // other ASCII
      builder.append(hex[c]);
    } else if (c <= 0x07FF) {    // non-ASCII <= 0x7FF
      builder.append(hex[0xc0 | (c >> 6)]);
      builder.append(hex[0x80 | (c & 0x3F)]);
    } else {          // 0x7FF < ch <= 0xFFFF
      builder.append(hex[0xe0 | (c >> 12)]);
      builder.append(hex[0x80 | ((c >> 6) & 0x3F)]);
      builder.append(hex[0x80 | (c & 0x3F)]);
    }
  }
  
}

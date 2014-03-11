/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 20, 2009  
 */
public class CommonMapper {

  //  protected RefsDecoder decoder = new RefsDecoder();

  protected static void encode(StringBuilder builder, String value) {
    if(value == null) { 
      builder.append('~');
      return;
    }
    int idx = 0;
    int length  = value.length();
    while (idx < length) {
      char c = value.charAt(idx);
      if(c == ',') {
        builder.append("&#44;");
      } else if(c == ':') {
        builder.append("&#58;");
      } else if(c == '[') {
        builder.append("&#91;");
      } else if(c == ']') {
        builder.append("&#93;");
      } else {
        builder.append(c);
      }
      idx++;
    }
  }

  public static String decode(String text) {
    if(text == null) return null;
    int length = text.length();
    StringBuilder builder = new StringBuilder(length);
    int index = 0;
    while(index < length) {
      char c = text.charAt(index);
      if(c == '&' && index <= length - 5) {
        if(text.charAt(index+1) == '#'
          && Character.isDigit(text.charAt(index+2))
          && Character.isDigit(text.charAt(index+3))
          && text.charAt(index+4) == ';'
        ) {

          int value  = Integer.parseInt(text.substring(index+2, index+4));
          if(value == 44) {
            builder.append(',');
            index += 5;
            continue;
          } else if(value == 58) {
            builder.append(':');
            index += 5;
            continue;
          } else if(value == 91) {
            builder.append('[');
            index += 5;
            continue;
          } else if(value == 93) {
            builder.append(']');
            index += 5;
            continue;
          }
        }

      } 
      builder.append(c);
      index++;
    }
    return builder.toString();
    //    if(text == null || text.length() < 2) return text;
    //    char [] chars = text.toCharArray();
    //    chars = decoder.decode(chars);
    //    return new String(chars);
  }

 /* public static void main(String[] args) {
    CommonMapper mapper = new CommonMapper();
    String text = "[http://www.tuoitre.com.vn/Tianyon/I[ndex].aspx?ArticleID=341690&ChannelID=193]";
    StringBuilder builder = new StringBuilder();
    mapper.encode(builder, text);
    System.out.println(builder);

    String text2 = mapper.decode(builder.toString());
    System.out.println(text2);

  }
*/
}

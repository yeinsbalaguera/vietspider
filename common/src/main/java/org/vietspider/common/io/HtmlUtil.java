package org.vietspider.common.io;

/**
 * Author : Tuan Nguyen
 */
public class HtmlUtil {
  final static public String escDoubleQuote(String s) {
    if(s == null || s.length() == 0) return s ;
    return s.replaceAll("\"", "&quot;") ;
  }
  
  final static public String removeTag(String text, boolean markBlock) { 
    char[] buf = text.toCharArray() ;
    StringBuilder b = new StringBuilder(text.length()) ;
    int idx = 0 ;
    while(idx < buf.length) {
      if(buf[idx] == '<') {
        int endTagPos = 0 ;
        if(idx + 2 < buf.length && buf[idx + 1] == '!' && buf[idx + 2] == '-') {
          endTagPos = endTagPosition(buf, idx, 3000) ;
        } else {
          endTagPos = endTagPosition(buf, idx, 300) ;
        }
        if(endTagPos > 0) {
          if(markBlock) {
            String tag = new String(buf, idx, endTagPos - idx) ;
            if(tag.startsWith("<p") || tag.startsWith("<P") ||
               tag.startsWith("<br") || tag.startsWith("<BR") ||
               tag.startsWith("<tr") || tag.startsWith("<TR") ||
               tag.startsWith("<li") || tag.startsWith("<LI")) {
              b.append(".\n\n") ;
            }
          }
          idx = endTagPos + 1 ;
        } else {
          b.append(buf[idx]) ;
          idx++ ;
        }
      } else {
        b.append(buf[idx]) ;
        idx++ ;
      }
    }
    return b.toString() ; 
  }
  
  private static int endTagPosition(char[] buf, int pos, int maxLookup) {
    if(pos + 1 == buf.length) return -1 ;
    char nextChar = buf[pos + 1] ;
    if(nextChar == '!' || nextChar == '/' || Character.isLetter(nextChar)) {
      int limit = pos + maxLookup ;
      if(limit > buf.length) limit = buf.length ;
      for(int i = pos; i < limit; i++) {
        if(buf[i] == '>') return i ;
      }
    }
    return -1 ;
  }
}

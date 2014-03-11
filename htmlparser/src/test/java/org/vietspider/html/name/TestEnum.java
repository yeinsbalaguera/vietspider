/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.name;

import java.util.HashMap;

import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2009  
 */
public class TestEnum {
  
  private static Name name(String value) {
    switch (value.charAt(0)) {
    case 'A':
      if(value.length() == 1) return Name.A;
      switch (value.charAt(1)) {
      case 'B'://"ABBR"
        if(value.length() != 4) return null;
        if(value.charAt(2) == 'B' && value.charAt(3) == 'R') return Name.ABBR;
        return null;
      case 'C': //"ACRONYM"
        if(value.length() != 7) return null;
        if(value.charAt(2) == 'R'
          && value.charAt(3) == 'O'
            && value.charAt(3) == 'N'
              && value.charAt(3) == 'Y'
                && value.charAt(3) == 'M') return Name.ACRONYM;
        return null;
      case 'D': //"ADDRESS"
        if(value.length() != 7) return null;
        if(value.charAt(2) == 'D'
          && value.charAt(3) == 'R'
            && value.charAt(3) == 'E'
              && value.charAt(3) == 'S'
                && value.charAt(3) == 'S') return Name.ADDRESS;
        return null;
      case 'P': //"APPLET"
        if(value.length() != 6) return null;
        if(value.charAt(2) == 'P'
          && value.charAt(3) == 'L'
            && value.charAt(3) == 'E'
              && value.charAt(3) == 't') return Name.APPLET;
      case 'R': //"AREA"
        if(value.length() != 4) return null;
        if(value.charAt(2) == 'E'
          && value.charAt(3) == 'A') return Name.AREA;
        return null;
      case 'N': //"ANY"
        if(value.length() != 3) return null;
        if(value.charAt(2) == 'Y') return Name.ANY;
        return null;
      default:
        return null;
      }
      
    // "B", "BASE", "BASEFONT", "BDO", "BIG", "BLOCKQUOTE", "BODY", "BR", "BUTTON", 
    // "BAD_CONTENT", 
    case 'B':
      if(value.length() == 1) return Name.B;
      switch (value.charAt(1)) {
      case 'A'://"ABBR"
        if(value.length() < 3) return null;
        switch (value.charAt(2)) {
        case 'S':
          if(value.length() == 4
            && value.charAt(3) == 'E') return Name.BASE;
          if(value.length() == 8
              && value.charAt(3) == 'E'
                && value.charAt(4) == 'F'
                  && value.charAt(5) == 'O'
                    && value.charAt(6) == 'N'
                      && value.charAt(7) == 'T'
                ) return Name.BASEFONT;
          return null;
        case 'D'://"BAD_CONTENT"
          if(value.length() == 8
              && value.charAt(3) == 'E'
                && value.charAt(4) == 'F'
                  && value.charAt(5) == 'O'
                    && value.charAt(6) == 'N'
                      && value.charAt(7) == 'T'
                ) return Name.BASEFONT;
          return null;
        default:
          return null;
        }
      case 'D': //"ACRONYM"
        if(value.length() != 7) return null;
        if(value.charAt(2) == 'R'
          && value.charAt(3) == 'O'
            && value.charAt(3) == 'N'
              && value.charAt(3) == 'Y'
                && value.charAt(3) == 'M') return Name.ACRONYM;
        return null;
      case 'I': //"ADDRESS"
        if(value.length() != 7) return null;
        if(value.charAt(2) == 'D'
          && value.charAt(3) == 'R'
            && value.charAt(3) == 'E'
              && value.charAt(3) == 'S'
                && value.charAt(3) == 'S') return Name.ADDRESS;
        return null;
      case 'O': //"APPLET"
        if(value.length() != 6) return null;
        if(value.charAt(2) == 'P'
          && value.charAt(3) == 'L'
            && value.charAt(3) == 'E'
              && value.charAt(3) == 't') return Name.APPLET;
      case 'R': //"AREA"
        if(value.length() != 4) return null;
        if(value.charAt(2) == 'E'
          && value.charAt(3) == 'A') return Name.AREA;
        return null;
      case 'U': //"ANY"
        if(value.length() != 3) return null;
        if(value.charAt(2) == 'Y') return Name.ANY;
        return null;
      default:
        return null;
      }
    default:
      return null;
    }
  }
  
  public static void main(String[] args) {
    String [] values = {
        "A", "ABBR", "ACRONYM", "ADDRESS", "APPLET", "AREA",
        "ANY", //code
        "B", "BASE", "BASEFONT", "BDO", "BIG", "BLOCKQUOTE", "BODY", "BR", "BUTTON", 
        "BAD_CONTENT", //code
        "CAPTION", "CENTER", "CITE", "CODE", "COL", "COLGROUP", 
        "CONTENT", "COMMENT", "CODE_CONTENT", //code
        "DD", "DEL", "DFN", "DIR", "DIV", "DL", "DT",
        "DOCTYPE",//code
        "EM", "EMBED",
        "FIELDSET", "FONT", "FORM", "FRAME", "FRAMESET", 
        "H1", "H2", "H3", "H4", "H5", "H6", "HEAD", "HR", "HTML", 
        "I", "IFRAME", "IMG", "INPUT", "INS", "ISINDEX",
        "IRNOGE",//code
        "KBD", 
        "LABEL", "LEGEND", "LI", "LINK", 
        "MAP", "MARQUEE", "MENU", "META", 
        "NOBR", "NOFRAMES", "NOSCRIPT", 
        "OBJECT", "OL", "OPTGROUP", "OPTION", 
        "P", "PARAM", "PRE", 
        "Q", 
        "S", "SAMP", "SCRIPT", "SELECT", "SMALL", "SPAN", "STRIKE", "STRONG", "STYLE", "SUB", "SUP", 
        "TABLE", "TBODY", "TD", "TEXTAREA", "TFOOT", "TH", "THEAD", "TITLE", "TR", "TT", 
        "U", "UL", 
        "UNKNOWN",//code
        "VAR",
        "XXX"//code
    };
    
//    TIntObjectHashMap<Name> maps = new TIntObjectHashMap<Name>();
//    for(int i = 0; i < values.length; i++) {
//      maps.put(values[i].hashCode(), Name.valueOf(values[i]));
//    }
    
    HashMap<String, Name> maps = new HashMap<String, Name>();
    for(int i = 0; i < values.length; i++) {
      maps.put(values[i].intern(), Name.valueOf(values[i]));
    }
    
    int size = 10000000;
    long total1 = 0;
    long total2 = 0;
    long total3 = 0;
    
    long start = 0;
    long end = 0;
    
    NameNodes.valueOf("A");
    
    for(int i = 0; i < size; i++) {
      int index = (int)(Math.random()*values.length);
      
      start = System.currentTimeMillis();
      Name name2 = Name.valueOf(values[index]);
      end = System.currentTimeMillis();
      total2 += end - start; 
      
      start = System.currentTimeMillis();
      Name name1 = maps.get(values[index]);
      end = System.currentTimeMillis();
      total1 += end - start; 
      
      start = System.currentTimeMillis();
      Name name3 = NameNodes.valueOf(values[index]);
      end = System.currentTimeMillis();
      total3 += end - start; 
    }
    
    System.out.println(" total 1 "+ total1 + ", total 2 "+ total2 + ", total 3 "+ total3);
  }
  
}

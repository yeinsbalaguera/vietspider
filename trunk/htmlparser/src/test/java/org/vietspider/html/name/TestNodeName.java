/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.name;

import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2009  
 */
public class TestNodeName {
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
    
    for(int i = 0; i < values.length; i++) {
      Name name = NameNodes.valueOf(values[i]);
      if(name == null) {
        System.err.println(" Error "+ values[i]);
        System.exit(0);
      }
    }
  }
}

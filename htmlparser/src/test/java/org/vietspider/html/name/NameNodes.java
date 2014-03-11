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
public class NameNodes {
  
  private static  NameNode [] nodes = {
      // "A", "ABBR", "ACRONYM", "ADDRESS", "APPLET", "AREA", ->"ANY", 
      new NameNode(Name.A, 0,
          new NameNode(Name.ABBR, 1),
          new NameNode(Name.ACRONYM, 1),
          new NameNode(Name.ADDRESS, 1),
          new NameNode(Name.APPLET, 1),
          new NameNode(Name.AREA, 1),
          new NameNode(Name.ANY, 1)
      ),
      // "B", "BASE", "BASEFONT", "BDO", "BIG", "BLOCKQUOTE", "BODY", "BR", "BUTTON", ->"BAD_CONTENT",
      new NameNode(Name.B, 0, 
          new NameNode(new char[]{'B', 'A'}, 1,
              new NameNode(new char[]{'B', 'A', 'S'}, 2, 
                  new NameNode(Name.BASE, 3, new NameNode(Name.BASEFONT, 4))
              ),
              new NameNode(Name.BAD_CONTENT, 2)
          ),
          new NameNode(Name.BDO, 1),
          new NameNode(Name.BIG, 1),
          new NameNode(Name.BLOCKQUOTE, 1),
          new NameNode(Name.BODY, 1),
          new NameNode(Name.BR, 1),
          new NameNode(Name.BUTTON, 1)
      ),
      //"CAPTION", "CENTER", "CITE", "CODE", "COL", "COLGROUP", 
      //"CONTENT", "COMMENT", "CODE_CONTENT", //code
      new NameNode(new char[]{'C'}, 0, 
          new NameNode(Name.CAPTION, 1),
          new NameNode(Name.CENTER, 1),
          new NameNode(Name.CITE, 1),
          new NameNode(new char[]{'C', 'O'}, 1,
              new NameNode(new char[]{'C', 'O', 'D'}, 2, 
                  new NameNode(Name.CODE, 3, new NameNode(Name.CODE_CONTENT, 4))
              ),
              new NameNode(Name.COL, 2, new NameNode(Name.COLGROUP, 3)), 
              new NameNode(Name.COMMENT, 2),
              new NameNode(Name.CONTENT, 2)
          )
      ),
      // "DD", "DEL", "DFN", "DIR", "DIV", "DL", "DT",
      //"DOCTYPE",//code
      new NameNode(new char[]{'D'}, 0, 
          new NameNode(Name.DD, 1),
          new NameNode(Name.DEL, 1),
          new NameNode(Name.DFN, 1),
          new NameNode(new char[]{'D', 'I'}, 1,
              new NameNode(Name.DIR, 2),
              new NameNode(Name.DIV, 2)
          ),
          new NameNode(Name.DL, 1),
          new NameNode(Name.DOCTYPE, 1),
          new NameNode(Name.DT, 1)
      ),
      //"EM", "EMBED",
      new NameNode(new char[]{'E'}, 0, 
          new NameNode(Name.EM, 1, new NameNode(Name.EMBED, 2))
      ),
      //"FIELDSET", "FONT", "FORM", "FRAME", "FRAMESET", 
      new NameNode(new char[]{'F'}, 0, 
          new NameNode(Name.FIELDSET, 1),
          new NameNode(new char[]{'F', 'O'}, 1,
              new NameNode(Name.FONT, 2),
              new NameNode(Name.FORM, 2)
          ),
          new NameNode(new char[]{'F', 'R'}, 1,
              new NameNode(new char[]{'F', 'R', 'A'}, 2,
                  new NameNode(new char[]{'F', 'R', 'A', 'M'}, 3,
                  new NameNode(Name.FRAME, 4, new NameNode(Name.FRAMESET, 5)))
              )
          )
      ),
      //"H1", "H2", "H3", "H4", "H5", "H6", "HEAD", "HR", "HTML",
      new NameNode(new char[]{'H'}, 0, 
          new NameNode(Name.H1, 1),
          new NameNode(Name.H2, 1),
          new NameNode(Name.H3, 1),
          new NameNode(Name.H4, 1),
          new NameNode(Name.H5, 1),
          new NameNode(Name.H6, 1),
          new NameNode(Name.HEAD, 1),
          new NameNode(Name.HR, 1),
          new NameNode(Name.HTML, 1)
      ),
     // "I", "IFRAME", "IMG", "INPUT", "INS", "ISINDEX",
     // "IRNOGE",//code
      new NameNode(Name.I, 0, 
          new NameNode(Name.IFRAME, 1),
          new NameNode(Name.IMG, 1),
          new NameNode(new char[]{'I', 'N'}, 1,
              new NameNode(Name.INPUT, 2),
              new NameNode(Name.INS, 2)
          ),
          new NameNode(Name.IRNOGE, 1),
          new NameNode(Name.ISINDEX, 1)
      ),
      //"KBD", 
      new NameNode(new char[]{'K'}, 0, 
          new NameNode(Name.KBD, 1)
      ),
      //"LABEL", "LEGEND", "LI", "LINK",
      new NameNode(new char[]{'L'}, 0, 
          new NameNode(Name.LABEL, 1),
          new NameNode(Name.LEGEND, 1),
          new NameNode(Name.LI, 1, new NameNode(Name.LINK, 2))
      ),
      //"MAP", "MARQUEE", "MENU", "META", 
      new NameNode(new char[]{'M'}, 0,
          new NameNode(new char[]{'M', 'A'}, 1,
              new NameNode(Name.MAP, 2),
              new NameNode(Name.MARQUEE, 2)
          ),
          new NameNode(new char[]{'M', 'E'}, 1,
              new NameNode(Name.MENU, 2),
              new NameNode(Name.META, 2)
          )
      ),
      //"NOBR", "NOFRAMES", "NOSCRIPT", 
      new NameNode(new char[]{'N'}, 0, 
          new NameNode(new char[]{'N', 'O'}, 1,
              new NameNode(Name.NOBR, 2),
              new NameNode(Name.NOFRAMES, 2),
              new NameNode(Name.NOSCRIPT, 2)
          )
      ),
      //"OBJECT", "OL", "OPTGROUP", "OPTION",
      new NameNode(new char[]{'O'}, 0, 
          new NameNode(Name.OBJECT, 1),
          new NameNode(Name.OL, 1),
          new NameNode(new char[]{'O', 'P'}, 1,
              new NameNode(new char[]{'O', 'P', 'T'}, 2,
                  new NameNode(Name.OPTGROUP, 3),
                  new NameNode(Name.OPTION, 3)
              )
          )
      ),
      //"P", "PARAM", "PRE", 
      new NameNode(Name.P, 0, 
          new NameNode(Name.PARAM, 1),
          new NameNode(Name.PRE, 1)
      ),
      //"Q", 
      new NameNode(Name.Q, 0),
      //"S", "SAMP", "SCRIPT", "SELECT", "SMALL", "SPAN", "STRIKE", "STRONG", "STYLE", "SUB", "SUP",
     new NameNode(Name.S, 0, 
          new NameNode(Name.SAMP, 1),
          new NameNode(Name.SCRIPT, 1),
          new NameNode(Name.SELECT, 1),
          new NameNode(Name.SMALL, 1),
          new NameNode(Name.SPAN, 1),
          new NameNode(new char[]{'S', 'T'}, 1,
              new NameNode(new char[]{'S', 'T', 'R'}, 2,
                  new NameNode(Name.STRIKE, 3),
                  new NameNode(Name.STRONG, 3)
              ),
              new NameNode(Name.STYLE, 2)
          ),
          new NameNode(new char[]{'S', 'U'}, 1,
              new NameNode(Name.SUB, 2),
              new NameNode(Name.SUP, 2)
          )
      ),
      //"TABLE", "TBODY", "TD", "TEXTAREA", "TFOOT", "TH", "THEAD", "TITLE", "TR", "TT", 
      new NameNode(new char[]{'T'}, 0, 
          new NameNode(Name.TABLE, 1),
          new NameNode(Name.TBODY, 1),
          new NameNode(Name.TD, 1),
          new NameNode(Name.TEXTAREA, 1),
          new NameNode(Name.TFOOT, 1),
          new NameNode(Name.TH, 1, new NameNode(Name.THEAD, 2)),
          new NameNode(Name.TITLE, 1),
          new NameNode(Name.TR, 1),
          new NameNode(Name.TT, 1)
      ),
      //"U", "UL", 
      //"UNKNOWN",//code
     new NameNode(Name.U, 0, 
          new NameNode(Name.UL, 1),
          new NameNode(Name.UNKNOWN, 1)
      ),
      //"VAR",
      new NameNode(new char[]{'V'}, 0, 
          new NameNode(Name.VAR, 1)
      ),
      //"XXX"//code
      new NameNode(new char[]{'X'}, 0, 
          new NameNode(Name.XXX, 1)
      )
  };
  
  private static HashMap<Character, NameNode> maps = new HashMap<Character, NameNode>();
  
  static  {
    for(int i = 0; i < nodes.length; i++) {
      maps.put(nodes[i].key(), nodes[i]);
    }
  }
  
  public static Name valueOf(String value) {
    NameNode nameNode = maps.get(value.charAt(0));
    if(nameNode == null) return null;
    return nameNode.valueOf(0, value);
    
   /* NameNode [] nameNodes = nodes;
    Name name = null;
    int index = 0;
    while(nameNodes.length > 0) {
      if(index >=  value.length()) break;
      NameNode nameNode = null;
      for(int i = 0; i < nameNodes.length; i++) {
        if(nameNodes[i].key() != value.charAt(index)) continue;
        nameNode = nameNodes[i];
        break;
      }
      if(nameNode == null) return null;
      index++;
      name = nameNode.getName();
      nameNodes = nameNode.getNodes();
    }*/
    
//    System.out.println(index+ " : "+ value.length());
//    return name;
    
   /* for(int i = 0; i < nodes.length; i++) {
      if(nodes[i].key() != value.charAt(0)) continue;
//      System.out.println("======================="+nodes[i].key()+"=============================");
      return nodes[i].valueOf(0, value);
//      System.out.println(nodes[i].key() + " : " +  name);
//      if(name == null) continue;
//      if(name ==  Name.NO_NAME) return null;
//      return name;
    }
    return null;*/
    
  }
  
  public static void main(String[] args) {
    System.out.println(NameNodes.valueOf("FRAMESET"));
//    System.out.println("=======================nodes[i].key()=============================");
    System.out.println(NameNodes.valueOf("FONT"));
  } 
  
}

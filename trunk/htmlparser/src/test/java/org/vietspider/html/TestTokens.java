/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.vietspider.html.parser.CharsToken;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.ParserService;
import org.vietspider.html.parser.TokenCreator;
import org.vietspider.token.TokenParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 12, 2006
 */
public class TestTokens {
  
  private static ByteArrayOutputStream loadInputStream(InputStream input) throws Exception {
    BufferedInputStream buffer = new BufferedInputStream(input);    
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] data  = new byte[buffer.available()];      
    int available = -1;
    while( (available = buffer.read(data)) > -1){
      output.write(data, 0, available);
    }   
    return output;
  }
  
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\error\\error.5410362.html");
    FileInputStream inputStream = new FileInputStream(file);
    String text = new String(loadInputStream(inputStream).toByteArray(), "utf-8");
    CharsToken tokens = new CharsToken();
    ParserService parserService = new ParserService();
    TokenParser tokenParser = new TokenParser();
    TokenCreator tokenCreator = new TokenCreator(parserService, tokens);
    tokenParser.createBeans(tokens, text.toCharArray(), tokenCreator);
    
//    HTMLDocument document = HTMLParser.createDocument(tokens);
    
//    int start = -1;
//    int total = 0;
//    Iterator<NodeImpl> iterator = tokens.iterator();
//    while(iterator.hasNext()){
//      NodeImpl node = iterator.next();
////      if(!node.isNode(Name.STYLE)) {
////        if(start == 0) start = 1;
////        else start = -1;
////      } else {
////        if(start == -1) start = 0; else start = 2;
////      }
////      if(start == -1) continue;
//      String value = new String(node.getValue());
//      System.out.println("----------------------");
//      System.out.println(value +" : "+ node.getType() + " : " + node.isOpen());
//      total++;
//    }
//    System.out.println(total);
//    
    HTMLDocument document = new HTMLParser2().createDocument(tokens);
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
//      System.out.println(new String(node.getValue()));
    }
    
//    System.out.println(document.getRoot().getTextValue());
    
    System.out.println("\n\n#################################################################\n\n");
//    iterator = tokens.iterator();
//    total  = 0;
//    while(iterator.hasNext()){
//      NodeImpl node = iterator.next();
//      if(!node.isNode(Name.STYLE)) {
//        if(start == 0) start = 1;
//        else start = -1;
//      } else {
//        if(start == -1) start = 0; else start = 2;
//      }
//      if(start == -1) continue;
//      String value = new String(node.getValue());
//      System.out.println("----------------------");
//      System.out.println(value +" : "+ node.getType() + " : " + node.isOpen());
//      total++;
//    }
    
//    System.out.println(" tinh co "+ total);
    
  }
}

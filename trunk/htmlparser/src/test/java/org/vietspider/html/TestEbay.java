/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.vietspider.html.parser.CharsToken;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.parser.ParserService;
import org.vietspider.html.parser.TokenCreator;
import org.vietspider.token.TokenParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 10, 2008  
 */
public class TestEbay {
  
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
  
  private static void testTokens (File file) throws Exception  {
    FileInputStream inputStream = new FileInputStream(file);
    String text = new String(loadInputStream(inputStream).toByteArray(), "utf-8");
    CharsToken tokens = new CharsToken();
    TokenParser tokenParser = new TokenParser();
    ParserService parserService = new ParserService();
    TokenCreator tokenCreator = new TokenCreator(parserService, tokens);
    tokenParser.createBeans(tokens, text.toCharArray(), tokenCreator);
    int start = -1;
    while(tokens.hasNext()){
      NodeImpl node = tokens.pop();
      if(!node.isNode(Name.STYLE)) {
        if(start == 0) start = 1;
        else start = -1;
      } else {
        if(start == -1) start = 0; else start = 2;
      }
      if(start == -1) continue;
      System.out.println(node.getTextValue());
//      String value = new String(node.getValue());
      System.out.println("----------------------");
//      System.out.println(value +" : "+ node.getType());
    }
  }
  
  public static void main(String[] args) throws Exception  {
    File file = new File("F:\\Temp2\\TestHTMLParser\\bug_thptphumy.html");
    testTokens(file);
    
//    HTMLNode node = HTMLParser.createDocument(file,"utf-8").getRoot();
//    System.out.println(node.getTextValue());
  }
}

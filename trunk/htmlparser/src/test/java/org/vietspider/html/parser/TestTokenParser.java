/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.parser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLToken;
import org.vietspider.token.TokenParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 10, 2009  
 */
public class TestTokenParser {
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
    File file = new File("D:\\Temp\\b.html");
    FileInputStream inputStream = new FileInputStream(file);
    String text = new String(loadInputStream(inputStream).toByteArray(), "utf-8");
    XMLToken tokens = new XMLToken();
    TokenParser tokenParser = new TokenParser();
    tokenParser.createBeans(tokens, text.toCharArray());
    int counter =  0;
    while(tokens.hasNext()){
      XMLNode node = tokens.pop();
//      if(node.getType() != TypeToken.DOCTYPE &&
//          node.getType() != TypeToken.CONTENT) continue;
      String value = new String(node.getValue());
      System.out.println("====================================================");
      System.out.println(value);
      counter++;
      if(counter > 100) break;
    }
  }
}

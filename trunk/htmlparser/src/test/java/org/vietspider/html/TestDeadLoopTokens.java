/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.vietspider.html.parser.CharsToken;
import org.vietspider.html.parser.ParserService;
import org.vietspider.html.parser.TokenCreator;
import org.vietspider.token.TokenParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 7, 2011  
 */
public class TestDeadLoopTokens {

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
    File file = new File("D:\\java\\test\\error\\");
    File [] files = file.listFiles();
    for(File f : files) {
      System.out.println(" parse " + f.getName() );
      FileInputStream inputStream = new FileInputStream(f);
      String text = new String(loadInputStream(inputStream).toByteArray(), "utf-8");
      CharsToken tokens = new CharsToken();
      ParserService parserService = new ParserService();
      TokenParser tokenParser = new TokenParser();
      TokenCreator tokenCreator = new TokenCreator(parserService, tokens);
      tokenParser.createBeans(tokens, text.toCharArray(), tokenCreator);
    }
  }
}

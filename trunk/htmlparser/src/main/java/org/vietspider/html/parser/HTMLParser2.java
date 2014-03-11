/*
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2006, 7:48 PM
 */

package org.vietspider.html.parser;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsDecoder;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.token.TypeToken;
/**
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public class HTMLParser2 {
  
  public final static HTMLNode clone(HTMLNode node)  {
    NodeImpl nodeImpl = (NodeImpl)node;
    int type = nodeImpl.getType();
    if(type == TypeToken.CONTENT 
        || type == TypeToken.COMMENT || type == TypeToken.CODE_CONTENT) {
      return new NodeImpl(nodeImpl.getValue(), nodeImpl.getName());
    }
    return new NodeImpl(nodeImpl.getValue(), nodeImpl.getName(), nodeImpl.getType());
  }
  
  public List<NodeImpl> createTokens(char[] data) throws Exception {
    CharsToken tokens = new CharsToken();
    ParserService parserService = new ParserService();
    TokenCreator tokenCreator = new TokenCreator(parserService, tokens);
    parserService.getTokenParser().createBeans(tokens, data, tokenCreator);
    List<NodeImpl> list = new ArrayList<NodeImpl>();
    while(tokens.hasNext()){    
      list.add(tokens.pop()); 
    }
    return list;
  }
  
  public HTMLDocument createDocument(char[] data) throws Exception { 
    CharsToken tokens = new CharsToken();
    ParserService parserService = new ParserService();
    TokenCreator tokenCreator = new TokenCreator(parserService, tokens);
    parserService.getTokenParser().createBeans(tokens, data, tokenCreator);
    parserService.parse(tokens, tokens.getDocument());
    return tokens.getDocument();
  }
  
  public HTMLDocument createDocument(CharsToken tokens) throws Exception { 
    ParserService parserService = new ParserService();
    parserService.parse(tokens, tokens.getDocument());
    return tokens.getDocument();
  }
  
  public HTMLDocument createDocument(List<NodeImpl> list) throws Exception { 
    CharsToken tokens = new CharsToken();
    for(int i = 0; i < list.size(); i++) {
      tokens.push(list.get(i));
    }
    new ParserService().parse(tokens, tokens.getDocument());
    return tokens.getDocument();
  }

  public  HTMLDocument createDocument(String text) throws Exception { 
    return createDocument(text.toCharArray());
  }

  public HTMLDocument createDocument(byte[] data, String charset) throws Exception {
    if(charset == null) {
      HTMLParserDetector parserDetector = new HTMLParserDetector();
      return parserDetector.createDocument(data);
    }
    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    return createDocument(chars);
  }  

  public  HTMLDocument createDocument(InputStream input, String charset) throws Exception {
    return createDocument(RWData.getInstance().loadInputStream(input).toByteArray(), charset);  
  }

  public HTMLDocument createDocument(File file, String charset) throws Exception {
    return createDocument(RWData.getInstance().load(file), charset);
  }

}
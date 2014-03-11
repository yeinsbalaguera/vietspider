/*
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2006, 7:48 PM
 */

package org.vietspider.html.parser;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.jchardet.nsDetector;
import org.vietspider.chars.jchardet.nsICharsetDetectionObserver;
import org.vietspider.chars.jchardet.nsPSMDetector;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
/**
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
@Deprecated()
public final class HTMLParser {

  private static String charset_ = null;
  
  @Deprecated()
  public static HTMLNode clone(HTMLNode node){
//    NodeImpl nodeImpl = (NodeImpl)node;
//    int type = nodeImpl.getType();
//    if(type == TypeToken.CONTENT 
//        || type == TypeToken.COMMENT || type == TypeToken.CODE_CONTENT) {
//      return new NodeImpl(nodeImpl.getValue(), nodeImpl.getName());
//    }
//    return new NodeImpl(nodeImpl.getValue(), nodeImpl.getName(), nodeImpl.getType());
    return HTMLParser2.clone(node);
  }
  
  @Deprecated()
  public static synchronized List<NodeImpl> createTokens(char[] data) throws Exception {
    return new HTMLParser2().createTokens(data);
//    CharsToken tokens = new CharsToken();
//    ParserService.getTokenParser().createBeans(tokens, data, new TokenCreator(tokens));
//    List<NodeImpl> list = new ArrayList<NodeImpl>();
//    while(tokens.hasNext()){    
//      list.add(tokens.pop()); 
//    }
//    return list;
  }
  
  @Deprecated()
  public static synchronized HTMLDocument createDocument(char[] data) throws Exception {
    return new HTMLParser2().createDocument(data);
//    CharsToken tokens = new CharsToken();
//    ParserService.getTokenParser().createBeans(tokens, data, new TokenCreator(tokens));
//    ParserService.parse(tokens, tokens.getDocument());
//    return tokens.getDocument();
  }
  
  @Deprecated()
  public static HTMLDocument createDocument(CharsToken tokens) throws Exception {
    return new HTMLParser2().createDocument(tokens);
//    ParserService.parse(tokens, tokens.getDocument());
//    return tokens.getDocument();
  }
  
  @Deprecated()
  public static HTMLDocument createDocument(List<NodeImpl> list) throws Exception { 
   /* CharsToken tokens = new CharsToken();
    for(int i = 0; i < list.size(); i++) {
      tokens.push(list.get(i));
    }
    ParserService.parse(tokens, tokens.getDocument());
    return tokens.getDocument();*/
    return  new HTMLParser2().createDocument(list);
  }

  public static HTMLDocument createDocument(String text) throws Exception { 
    return createDocument(text.toCharArray());
  }

  public static HTMLDocument createDocument(byte[] data, String charset) throws Exception {
    if(charset == null) charset = detect(data);
    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    return createDocument(chars);
  }  

  @Deprecated()
  public static HTMLDocument createDocument(InputStream input, String charset) throws Exception {
    return createDocument(RWData.getInstance().loadInputStream(input).toByteArray(), charset);  
  }

  @Deprecated()
  public static HTMLDocument createDocument(File file, String charset) throws Exception {
    return createDocument(RWData.getInstance().load(file), charset);
  }  

  @Deprecated()
  public static String detect(byte [] buf){
    nsDetector det = new nsDetector(nsPSMDetector.ALL) ;
    charset_ = null;
    det.Init(new nsICharsetDetectionObserver() {
      @Override
      public void Notify(String charset) {
        charset_ = charset;
        
      }
    });

    boolean isAscii = true ;
    int len = buf.length;
    
    isAscii = det.isAscii(buf, len);   
    if (!isAscii) det.DoIt(buf, len, false);
    det.DataEnd();
    
    if (isAscii) charset_ = "ASCII";
    return charset_;
  }
}
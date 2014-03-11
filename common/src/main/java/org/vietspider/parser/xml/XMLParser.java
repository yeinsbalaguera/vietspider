/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.parser.xml;

import java.io.File;
import java.io.InputStream;

import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.XMLDataDecoder;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.token.TokenParser;
import org.vietspider.token.TypeToken;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLParser {
  
//  private final static String READER_ID = "XMLParserReader";
  
//  private static ThreadSoftRef<TokenParser> tokenParser = new ThreadSoftRef<TokenParser>(TokenParser.class);
  
  public static XMLDocument createDocument(char[] data, XMLDataDecoder decoder) throws Exception { 
    XMLNode root = new XMLNode("document".toCharArray(), "document", TypeToken.TAG);
    XMLDocument document = new XMLDocument(root);
    XMLToken tokens = new XMLToken(decoder);
    document.setXmlType(tokens.getXmlType());
    new TokenParser().createBeans(tokens, data);
    DOMParser.parse(tokens, root);
    return document;
  }
  
  public static XMLDocument createDocument(String text, XMLDataDecoder decoder) throws Exception {     
    return createDocument(text.toCharArray(), decoder);
  }
  
  public static XMLDocument createDocument(byte[] data, String charset, XMLDataDecoder decoder) throws Exception {   
    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    return createDocument(chars, decoder);
  }  
  
  public static XMLDocument createDocument(InputStream input, String charset, XMLDataDecoder decoder) throws Exception {
    return createDocument(RWData.getInstance().loadInputStream(input).toByteArray(), charset, decoder);  
  }
  
  public static XMLDocument createDocument(File file, String charset, XMLDataDecoder decoder) throws Exception {
    return createDocument(RWData.getInstance().load(file), charset, decoder);
  } 
 
}

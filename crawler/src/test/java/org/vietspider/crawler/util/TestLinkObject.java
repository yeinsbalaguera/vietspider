/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.vietspider.crawl.link.Link;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 26, 2008  
 */
public class TestLinkObject {
  
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
  
  public static void main(String[] args)  throws Exception {
    Link link  = new Link("http://forums.sun.com/thread.jspa?threadID=5300999", null);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    
    File file = new File("F:\\Temp2\\TestHTMLParser\\bug_thptphumy1.html");
    FileInputStream inputStream = new FileInputStream(file);
    String text = new String(loadInputStream(inputStream).toByteArray(), "utf-8");
    List<NodeImpl> list = new HTMLParser2().createTokens(text.toCharArray());
    link.setTokens(list);
    link.setSourceFullName("source");
    link.getDocument();
    System.out.println(" link tokens " + link.getTokens().size());
    
    objectOutputStream.writeObject(link);
    objectOutputStream.close();
    
    byte [] bytes = byteArrayOutputStream.toByteArray();
    System.out.println("brefore zip " + bytes.length);
    
    byte [] newBytes = new GZipIO2().zip(bytes);
    System.out.println("zip bytes "+ newBytes.length);
    
    bytes = new GZipIO2().unzip(newBytes);
    System.out.println("after zip " + bytes.length);

    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
    link  = (Link)objectInputStream.readObject();
    System.out.println(" link tokens " + link.getTokens().size());
  }
}

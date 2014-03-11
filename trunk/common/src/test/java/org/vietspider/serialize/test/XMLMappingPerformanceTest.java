/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.Unknown2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.serialize.XML2Unknown;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2011  
 */
public class XMLMappingPerformanceTest {
  
  private static void deserilizable(List<byte[]> bytes) {
    for(int i = 0; i < bytes.size(); i++) {
      ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes.get(i));
      ObjectInputStream objectInputStream = null;
      try {
        objectInputStream = new ObjectInputStream(byteInputStream);
        Article article = (Article)objectInputStream.readObject();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if(byteInputStream != null) byteInputStream.close();
        } catch (Exception e) {
        }
        try {
          if(objectInputStream != null)  objectInputStream.close();
        } catch (Exception e) {
        }
      } 
    }
  }
  
  private static void toObject(List<String> xmls)  {
    for(int i = 0; i < xmls.size(); i++) {
      try {
        Article article = XML2Object.getInstance().toObject(Article.class, xmls.get(i));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  private static void toObject2(List<String> xmls)  {
    for(int i = 0; i < xmls.size(); i++) {
      try {
        Article article = XML2Unknown.getInstance().toObject(Article.class, xmls.get(i));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void serilizable(Article [] articles, List<byte[]> bytes) {
    for(int i = 0; i < articles.length; i++) {
      ByteArrayOutputStream bytesObject = new ByteArrayOutputStream();
      ObjectOutputStream objOutput = null;
      try {
        objOutput = new ObjectOutputStream(bytesObject);
        objOutput.writeObject(articles[i]);
        objOutput.flush();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        try {
          if(objOutput != null) objOutput.close();
        } catch (Exception e) {
        }
        try {
          if(bytesObject != null) bytesObject.close();
        } catch (Exception e) {
        }
      }

      bytes.add(bytesObject.toByteArray());
    }
  }

  private static void toXML(Article [] articles, List<String> xmls) {
    for(int i = 0; i < articles.length; i++) {
      try {
        xmls.add(Object2XML.getInstance().toXMLDocument(articles[i]).getTextValue());
//        Object2XML.getInstance().toXMLDocument(articles[i]);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  private static void toXML2(Article [] articles, List<String> xmls) {
    for(int i = 0; i < articles.length; i++) {
      try {
        xmls.add(Unknown2XML.getInstance().toXMLDocument(articles[i]).getTextValue());
//        Object2XML.getInstance().toXMLDocument(articles[i]);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    Article [] articles = new Article[50000];
    for(int i = 0; i < articles.length; i++) {
      articles[i] = XMLMappingTest.createBean();
    }

    List<byte[]> bytes = new ArrayList<byte[]>();
    List<String> xmls = new ArrayList<String>();
    List<String> xmls2 = new ArrayList<String>();
    
//    try {
//      Thread.sleep(2*60*1000);
//    } catch (Exception e) {
//    }

    long start ;
    long end;
    
    start = System.currentTimeMillis();
    toXML(articles, xmls);
    end = System.currentTimeMillis();
    System.out.println(" xmls 1 " +  (end - start));
    
    start = System.currentTimeMillis();
    toXML2(articles, xmls2);
    end = System.currentTimeMillis();
    System.out.println(" xmls 2 " +  (end - start));
    
    start = System.currentTimeMillis();
    serilizable(articles, bytes);
    end = System.currentTimeMillis();
    System.out.println(" seril  " +  (end - start));
    
    start = System.currentTimeMillis();
    deserilizable(bytes);
    end = System.currentTimeMillis();
    System.out.println(" deseri " +  (end - start));
    
    start = System.currentTimeMillis();
    toObject(xmls);
    end = System.currentTimeMillis();
    System.out.println(" obj 1  " +  (end - start));
    
    start = System.currentTimeMillis();
    toObject2(xmls2);
    end = System.currentTimeMillis();
    System.out.println(" obj 2  " +  (end - start));
    
    System.exit(1);

  }

}

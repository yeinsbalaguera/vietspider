/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class TestClassifiedFolder {
  
  private static File folder;
  private static Tags tags;
  
  protected static void setUp() throws Exception {
    Document.print = true;
    tags = new Tags(new File("D:\\Program\\vsnews\\data\\sources\\type\\tags\\"), "bat-dong-san");
    tags.setMinScore(0);
    tags.setMinRate(0.1);
    tags.setMinDefaultRate(1.0);
    folder = new File("D:\\Temp\\classified\\test4\\");
  }
  
  private static void tag(File file) throws Exception {
    String value  = new String(RWData.getInstance().load(file), Application.CHARSET);
    Document document = new Document(value);
    document.setId(file.getName());
    String tag = tags.tag(document);
    value = value.trim();
    int idx = value.indexOf('\n');
    if(idx > 0) value = value.substring(0, idx);
//    if(tag != null && tag.equals("bat-dong-san")) {
    if(tag == null ) {
      System.out.println(tag + "  :  "+ file.getName() + "  : " + value);
    }
  }
  

  public static void main(String[] args) throws Exception {
    setUp();
    Document.print = false;
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      tag(files[i]);
    }
  }

}

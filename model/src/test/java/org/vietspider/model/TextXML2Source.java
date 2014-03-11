/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.File;

import org.vietspider.chars.CharsUtil;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2009  
 */
public class TextXML2Source {

  private static File getFile(String group, String category, String name) {
    NameConverter converter = new NameConverter();
    group = NameConverter.encode(CharsUtil.cutAndTrim(group));
    category = NameConverter.encode(CharsUtil.cutAndTrim(category));
    name  = category+"."+NameConverter.encode(CharsUtil.cutAndTrim(name));

    File folder = UtilFile.getFolderNotCreate("sources/sources/"+group+"/"+category+"/");
    if(folder == null || !folder.exists()) return null;
    File file = new File(folder, name);
    if(!file.exists()) return null;
    return file;
  }

  private static long testFolder1(File file) {
    if(file.isFile()) {
      try {
        if(file.getName().indexOf(".v.") > -1) return 0;
//        file = getFile("XML", "Company", "BBB");
        byte[] bytes = RWData.getInstance().load(file);
        System.out.println(file.getAbsolutePath() + "  testing ...");
        XMLDocument document = XMLParser.createDocument(bytes, "utf-8", null);
        long start =  System.currentTimeMillis();
        Source source1 = XML2Object.getInstance().toObject(Source.class, document);
        long end =  System.currentTimeMillis();
        
        start =  System.currentTimeMillis();
        XML2Source xmlSource = new XML2Source();
        Source source2 = new Source();
        xmlSource.invoke(document, source2);
        end =  System.currentTimeMillis();
        
        String xml1  = Object2XML.getInstance().toXMLDocument(source1).getTextValue();
        String xml2  = Object2XML.getInstance().toXMLDocument(source2).getTextValue();

        if(!SourceComparator.equals(source1, source2) || !xml1.equals(xml2) ) {
          System.out.println(source1.getFullName());
          System.out.println(source2.getFullName());
          
          System.out.println(xml1);
          System.out.println("==============================================================");
          System.out.println(xml2);
          System.exit(0);
        }
        
        return end - start;
      } catch (Exception e) {
        return 0;
      }
    }
    File [] files = file.listFiles();
    if(files == null) return 0;
    long total = 0;
    for(int i = 0; i < files.length; i++) {
      total += testFolder1(files[i]);
    }
    return total;
  }

  private static long testFolder2(File file) {
    if(file.isFile()) {
      try {
        file = getFile("XML", "Company", "BBB");
        byte[] bytes = RWData.getInstance().load(file);
        XMLDocument document = XMLParser.createDocument(bytes, "utf-8", null);

        long start =  System.currentTimeMillis();
        XML2Source xmlSource = new XML2Source();
        Source source2 = new Source();
        xmlSource.invoke(document, source2);
        long end =  System.currentTimeMillis();

        return end - start;
      } catch (Exception e) {
        return 0;
      }
    }
    File [] files = file.listFiles();
    if(files == null) return 0;
    long total = 0;
    for(int i = 0; i < files.length; i++) {
      total += testFolder2(files[i]);
    }
    return total;
  }

  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
//    long total2 = testFolder2(folder);
    
    File folder = UtilFile.getFolder("sources/sources/");
    long total1 = testFolder1(folder);
    
//    File single  = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\sources\\sources\\FORUM\\Dot Net\\Dot Net.Port80");
//    long total1 = testFolder1(single);
    System.out.println(" testing successfull....");
    
//    System.out.println(total1+ " : "+ total2);

   /* file = getFile("CLASSIFIED", "Rao vặt 4", "Raovatdidong");
    byte[] bytes = reader.load(file);

    XMLDocument document = XMLParser.createDocument(bytes, "utf-8", null);

    Source source1 = XML2Object.getInstance().toObject(Source.class, document);

    XML2Source xmlSource = new XML2Source();
    Source source2 = new Source();
    xmlSource.invoke(document, source2);

    String xml1  = Object2XML.getInstance().toXMLDocument(source1).getTextValue();
    String xml2  = Object2XML.getInstance().toXMLDocument(source2).getTextValue();

    System.out.println(xml1.length() +  " / "+ xml2.length());
    System.out.println(SourceComparator.equals(source1, source2));
    
    File newFile = new File("D:\\Temp\\source.txt");
    org.vietspider.common.io.RWData.getInstance().save(newFile, xml2.getBytes("utf-8"));*/

  }

}

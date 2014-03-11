/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;

import org.vietspider.chars.TextSpliter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 6, 2011  
 */
public class TestDictExtractor  {
  public static void main(String[] args) throws Exception {
    //    File file = new File("D:\\Temp\\tp\\vv30K.dict");
    //    FileInputStream fileInput = new FileInputStream(file);
    //    InputStreamReader inputReader = new InputStreamReader(fileInput, Charset.forName("utf-8"));
    //    LineNumberReader reader = new LineNumberReader(inputReader);
    //    String line = reader.readLine();
    //    int counter = 0;
    //    Set<String> list = new TreeSet<String>();
    //    while(line != null) {
    //      if(line.length() > 0 && line.charAt(0) == '@') {
    //        list.add(line.substring(1));
    //        counter++;
    //      }
    //      line = reader.readLine();
    //    }
    //    System.out.println(counter);


    //    File file = new File("D:\\Temp\\tp\\baambo.txt");
    //    FileInputStream fileInput = new FileInputStream(file);
    //    InputStreamReader inputReader = new InputStreamReader(fileInput, Charset.forName("utf-8"));
    //    LineNumberReader reader = new LineNumberReader(inputReader);
    //    String line = reader.readLine();
    //    int counter = 0;
    //    Set<String> list = new TreeSet<String>();
    //    while(line != null) {
    //      if(line.indexOf('\t') > -1) {
    //        String [] elements = line.split("\t");
    //        counter += elements.length;
    //        for(String ele : elements) {
    //          ele = ele.trim();
    //          
    //          if(isUpperCase(ele)) {
    //            list.add(ele);
    //          } else {
    //           list.add(ele.toLowerCase());
    //          }
    //        }
    ////        System.out.println(line);
    //      }
    //      line = reader.readLine();
    //    }
    //    System.out.println(counter);

//    File file = new File("D:\\Temp\\tp\\wiktionary.txt");
//    Set<String> list = new TreeSet<String>();
//    String text = new String(RWData.getInstance().load(file), "utf-8");
//    String [] elements = text.split("\n");
//    for(String line : elements) {
//      line  = line.trim();
//      if(line.length() < 2) continue;
//      if(line.endsWith(" tiếp")) {
//        int idx = line.indexOf(' ');
//        if(idx == 1) continue;
//      }
//
//      if(isUpperCase(line)) {
//        list.add(line);
//      } else {
//        list.add(line.toLowerCase());
//      }
//    }
//    System.out.println(list.size());

    // test duoi

//    file = new File("D:\\java\\vietspider\\model\\src\\main"
//        +"\\java\\org\\vietspider\\locale\\vn\\full.vn.dict.cfg");
//    byte [] bytes = RWData.getInstance().load(file);
//    String data = new String(bytes, Application.CHARSET);
//    list.addAll(CharsUtil.split2List(data, ';'));
//    String [] values = list.toArray(new String[0]);
//    for(int i = 0; i < values.length; i++) {
//      values[i] = values[i].trim();
//      if(values[i].length() < 2) continue;
//      if(isUpperCase(values[i])) {
//        list.add(values[i]);
//      } else {
//        list.add(values[i].toLowerCase());
//      }
//    }
//    Arrays.sort(values, new VNIgnoreCaseComparator());
//    System.out.println(list.size());
//
//    file = new File("D:\\Temp\\tp\\full.vn.dict.cfg");
//    StringBuilder builder = new StringBuilder();
//    for(int i = 0; i < values.length; i++) {
//      if(builder.length() > 0) builder.append(';');
//      builder.append(values[i]);
//    }
//    org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    
    
    
    
    
    File file = new File("D:\\java\\test\\vsnews\\data\\system\\dictionary\\vn\\temp.txt");
    String text = new String(RWData.getInstance().load(file), "utf-8");
//    System.out.println(text);
    TextSpliter spliter = new TextSpliter();
    String [] elements = spliter.toArray(text, '\n');
    StringBuilder builder = new StringBuilder();
    for(String line : elements) {
      line  = line.trim();
      if(line.length() < 1) continue;
//      System.out.println(line);
      if(builder.length() > 0) builder.append(';');
      builder.append(line);
    }
    System.out.println(builder);
  }

  private static boolean isUpperCase(String text) {
    if(!Character.isUpperCase(text.charAt(0))) return false;
    int idx = text.indexOf(' ');
    if(idx < 0) {
      if(text.length() > 1 && Character.isUpperCase(text.charAt(1))) return true;
      return false;
    }
    if(idx < text.length()-1 
        && Character.isUpperCase(text.charAt(idx+1))) return true; 
    return false;
  }
}

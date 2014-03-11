/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import org.vietspider.db.dict.WordIndex2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class TestWordNet2 {
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    WordIndex2 wordIndex2 = new WordIndex2(0);
    
    wordIndex2.add("chia");
    wordIndex2.add("chia sẻ");
    wordIndex2.add("làn da dầu");
    wordIndex2.add("làn da");
    wordIndex2.add("cơ thể");
    
    
    System.out.println(wordIndex2.contains("chia"));
    System.out.println(wordIndex2.contains("chia sẻ"));
    System.out.println(wordIndex2.contains("cơ thể"));
    System.out.println(wordIndex2.contains("cơ"));
    System.out.println(wordIndex2.contains("cơ cấu"));
    System.out.println(wordIndex2.contains("cơ thể khỏe"));
    /*System.out.println(wordIndex.contains("chia"));
    System.out.println(wordIndex.contains("chia sẻ"));
    System.out.println(wordIndex.contains("cơ thể"));
    
    wordIndex.remove("cơ thể");
    System.out.println(wordIndex.contains("cơ thể"));
    
    String word = "chuyên nghiệp"; 
    System.out.println(wordIndex.contains(word));
    wordIndex.add(word);
    System.out.println(wordIndex.contains(word));
    wordIndex.remove(word);
    System.out.println(wordIndex.contains(word));
    
    */
    System.out.println("==========================");
    System.out.println(wordIndex2.toString());
    
    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    DataOutputStream outputStream = new DataOutputStream(byteOutput);
    wordIndex2.write(outputStream);
    
    byte [] bytes = byteOutput.toByteArray();
    System.out.println(" hihi "+ bytes.length);
    
    ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
    DataInputStream inputStream = new DataInputStream(byteInput);
    
    WordIndex2 _wordIndex2 = WordIndex2.readObject(inputStream, 0, bytes.length);
    System.out.println(_wordIndex2.toString());
    
    
  }
}

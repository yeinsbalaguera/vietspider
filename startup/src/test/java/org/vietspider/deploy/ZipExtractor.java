package org.vietspider.deploy;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 2, 2009  
 */
public class ZipExtractor {

  private static final int BUFFER = 2048;

  private static byte data[] = new byte[BUFFER]; 

  public static void extract(File input, String output) throws Exception {
    FileInputStream fileInput = new FileInputStream(input);    
    ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(fileInput));
    
    ZipEntry entry;
    int total =  0;   
    try {
      while((entry = zipInput.getNextEntry()) != null) {      
        if(entry.isDirectory()) {
          createFile(new File(output+entry.getName()),true);
        } else {       
          try {
            total += extractFile(zipInput, entry, output);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      zipInput.close();
    } finally {
      zipInput.close();
      fileInput.close();
    }
  }

  private static int extractFile(ZipInputStream zipInput, ZipEntry entry, String output) throws Exception {
    FileInputStream inputStream = null;
    FileOutputStream fileOuput = null;
    BufferedOutputStream dest = null;

    int count; 
    int total = 0;
    try {

      File f = null;
      if(output != null) {
        f = new File(output+entry.getName());
      } else {
        f = new File(entry.getName());
      }

      byte [] oldData = new byte[0];
      if(!f.exists()) {
        createFile(f, false);
      } else {
        inputStream = new FileInputStream(f);
        oldData = new byte[(int)f.length()];
        inputStream.read(oldData);
        inputStream.close();
      }

      fileOuput = new FileOutputStream(f);       
      dest = new BufferedOutputStream(fileOuput, BUFFER);      
      dest.write(oldData);

      while ((count = zipInput.read(data, 0, BUFFER)) != -1) {        
        dest.write(data, 0, count);
        total += count;
      }        
      dest.close();
    } finally {
      if(inputStream != null) inputStream.close();
      if(fileOuput != null) fileOuput.close();
      if(dest != null) dest.close();
    }

    return total;
  }

  private static File createFile(File file, boolean folder) throws Exception {
    if(file.getParentFile() != null) createFile( file.getParentFile(), true);
    if(file.exists()) return file;
    if(file.isDirectory() || folder) file.mkdir();
    else file.createNewFile();
    return file;
  }
}

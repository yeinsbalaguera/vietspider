/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2008  
 */
/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2008  
 */
public class ZipIO {
  
//  public byte[] zip (byte [] bytes) throws Exception {
//    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
//    BytesOutputStream outputStream = new BytesOutputStream();
//    zip(outputStream, inputStream, "data");
//    return outputStream.toByteArray();
//  }
  
  public void zip(OutputStream outputStream, InputStream inputStream, String entryName) throws Exception {
    ZipOutputStream zipOutput = null;
    try {
      zipOutput = new ZipOutputStream(outputStream);
      ZipEntry entry = new ZipEntry(entryName);    
      zipOutput.putNextEntry(entry);
      int read = -1;
      int BUFFER = 2*1024;
      byte data[] = new byte[BUFFER];
      while ((read = inputStream.read(data, 0, BUFFER)) != -1) {
        zipOutput.write(data, 0, read);
      }
      zipOutput.closeEntry();    
      zipOutput.close();
    } finally {
      try {
        if(zipOutput != null) zipOutput.close();
      } catch (Exception e) {
      }
      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
      }
    }
  }
  
//  public byte[] unzip(byte [] bytes) throws Exception {
//    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
//    BytesOutputStream outputStream = new BytesOutputStream();
//    unzip(inputStream, outputStream, null);
//    return outputStream.toByteArray();
//  }

  public void unzip(InputStream inputStream, OutputStream outputStream, ZipProgress progress) throws Exception {
    ZipInputStream zipInput = null;
    try {
      zipInput = new ZipInputStream(inputStream);
      int read = -1;
      byte [] bytes = new byte[4*1024];  
      while(zipInput.getNextEntry() != null) {      
        BufferedOutputStream dest = new BufferedOutputStream(outputStream, bytes.length);      
        while ((read = zipInput.read(bytes, 0, bytes.length)) != -1) {
          dest.write(bytes, 0, read);
          if(progress != null) progress.setValue(read);
        }        
        dest.close();
      }
      zipInput.close();
    } finally {
      try {
        if(zipInput != null) zipInput.close();
      } catch (Exception e) {
      }
      
      try {
        if(outputStream != null) outputStream.close();
      } catch (Exception e) {
      }
    }
  }
  
//  public void unzip(InputStream inputStream, FileOutputStream outputStream, ZipProgress progress) throws Exception {
//    ZipInputStream zipInput = null;
//    try {
//      zipInput = new ZipInputStream(inputStream);
//      int read = -1;
//      byte [] bytes = new byte[4*1024];  
//      while(zipInput.getNextEntry() != null) {      
//        BufferedOutputStream dest = new BufferedOutputStream(outputStream, bytes.length);      
//        while ((read = zipInput.read(bytes, 0, bytes.length)) != -1) {        
//          dest.write(bytes, 0, read);
//          if(progress != null) progress.setValue(read);
//        }        
//        dest.close();
//      }
//      zipInput.close();
//    } finally {
//      try {
//        if(zipInput != null) zipInput.close();
//      } catch (Exception e) {
//      }
//      
//      try {
//        if(outputStream != null) outputStream.close();
//      } catch (Exception e) {
//      }
//    }
//  }
  
  public static interface ZipProgress {
    
    public void setValue(int value);
    
  }
}

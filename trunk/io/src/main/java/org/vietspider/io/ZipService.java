/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Author : Nhu Dinh Thuan
 * nhudinhthuan@yahoo.com
 * Feb 06, 2006
 * Version 1.4
 */
public class ZipService {

  private final int BUFFER = 2048;

  private byte data[] = new byte[BUFFER]; 

  public void addToArchive(File input, File output, boolean containParent) throws Exception{    
    ByteArrayOutputStream byteOutput = addToArchive(input, containParent);
    output = createFile(output, false);
    FileOutputStream fileOutput = null;
    try {
      fileOutput = new FileOutputStream(output);
      byteOutput.writeTo(fileOutput);
      byteOutput.close();
    } finally {
      if(fileOutput != null) fileOutput.close();
    }
  }

  public ByteArrayOutputStream addToArchive(File input, boolean containParent)throws Exception {
    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    addToArchive(input, byteOutput, containParent).close();
    return byteOutput;
  }

  public ZipOutputStream addToArchive(File input, OutputStream output, boolean containParent)throws Exception{ 
    String path = input.getAbsolutePath();
    ZipOutputStream zipOutput = new ZipOutputStream(output);

    List<File> list = listFile(input);    
    if(input.isDirectory()) list.remove(input);
    if( list == null || list.size() < 1){
      return zipOutput;
    }
    int index = 0;
    for (File f : list) {      
      String filePath = f.getAbsolutePath();
      if (filePath.startsWith(path)){
        if(containParent && input.isDirectory())
          filePath = input.getName() + File.separator + filePath.substring(path.length()+1);          
        else if(input.isDirectory())
          filePath = filePath.substring(path.length()+1);
        else
          filePath = input.getName();
      }
      if(f.isFile()){
        FileInputStream fileInput = new FileInputStream(f);
        BufferedInputStream bufInput = new BufferedInputStream(fileInput, BUFFER);
        addToArchive(zipOutput, bufInput, filePath);
        bufInput.close();
        fileInput.close();
      } else {
        filePath += "/";
        addToArchive(zipOutput, (BufferedInputStream)null, filePath);
      }      
      index++;       
    }    
    return zipOutput;
  }  

  public ByteArrayOutputStream addToArchive(InputStream input, String entryName)throws Exception{    
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ZipOutputStream zipOutput = new ZipOutputStream(output);
    addToArchive(zipOutput, input, entryName);
    zipOutput.close();    
    return output;
  }

  public ZipOutputStream addToArchive(
      ZipOutputStream zipOutput, InputStream input, String entryName)throws Exception{    
    ZipEntry entry = new ZipEntry(entryName);    
    zipOutput.putNextEntry(entry);    
    if(input != null){    
      int count;
      while ((count = input.read(data, 0, BUFFER)) != -1) zipOutput.write(data, 0, count);  
    }    
    zipOutput.closeEntry();    
    return zipOutput;
  }

  public ZipOutputStream addToArchive(
      ZipOutputStream zipOutput, byte[] d, String entryName)throws Exception{    
    ZipEntry entry = new ZipEntry(entryName);    
    zipOutput.putNextEntry(entry);    
    if(d != null && d.length > 0) zipOutput.write(d);     
    zipOutput.closeEntry();    
    return zipOutput;
  }

  public void extractFromArchive(File input, String output) throws Exception {
    BufferedOutputStream dest = null;
    FileInputStream fileInput = new FileInputStream(input);    
    ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(fileInput));
    ZipEntry entry;
    int count;    
    FileOutputStream fileOuput = null;
    FileInputStream inputStream = null;
    int total =  0;   
    try {
      while((entry = zipInput.getNextEntry()) != null) {      
        if(entry.isDirectory())
          createFile(new File(output+entry.getName()),true);
        else {       
          File f = null;
          if(output != null)
            f = new File(output+entry.getName());
          else
            f = new File(entry.getName());
          byte [] oldData = new byte[0];
          if(!f.exists())
            createFile(f, false);
          else {
            if(inputStream != null) inputStream.close();
            inputStream = new FileInputStream(f);
            oldData = new byte[(int)f.length()];
            inputStream.read(oldData);
            inputStream.close();
          }
          if(fileOuput != null) fileInput.close();
          fileOuput = new FileOutputStream(f);       
          dest = new BufferedOutputStream(fileOuput, BUFFER);      
          dest.write(oldData);
          while ((count = zipInput.read(data, 0, BUFFER)) != -1) {        
            dest.write(data, 0, count);
            total += count;
          }        
          dest.close();
        }
      }
      zipInput.close();
    } finally {
      if(inputStream != null) inputStream.close();
      if(fileOuput != null) fileInput.close();
    }
  }

  private File createFile(File file, boolean folder) throws Exception {
    if(file.getParentFile() != null) createFile( file.getParentFile(), true);
    if(file.exists()) return file;
    if(file.isDirectory() || folder) file.mkdir();
    else file.createNewFile();
    return file;
  }

  private List<File> listFile(File dir) {
    final List <File> list = new ArrayList<File>();    
    if(dir.isFile()) {
      list.add(dir);
      return list;
    }
    dir.listFiles(new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) list.addAll(listFile(f));
        list.add(f);
        return true;
      }
    });
    return list;
  }
}
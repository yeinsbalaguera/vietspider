/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 14, 2007  
 */
public class ApplicationFileHandler extends CommonHandler {

  @SuppressWarnings("unused")
  public void execute(final HttpRequest request, final HttpResponse response,
                      final HttpContext context, OutputStream output) throws Exception  {
    
    Header header = request.getFirstHeader("action");
    String action = header.getValue();
    
    if("save".equals(action)) {
      java.io.File file = getFile(request, true);
      
      byte[] requestBytes = getRequestData(request);
      
      byte [] fileBytes = RWData.getInstance().load(file);
      if(compareBytes(fileBytes, requestBytes)) {
        output.write("no save".getBytes());
        return;
      }
      
      header = request.getFirstHeader("versioning");
      if(header != null && "true".equals(header.getValue())) {
        output.write(verioning(file).getBytes());
      }
      logAction(request, action);
      RWData.getInstance().save(file, requestBytes);
      return ;
    } 
    
    if("save.file".equals(action)) {
      java.io.File file = getFile(request, true);
      
      byte[] requestBytes = getRequestData(request);
     
      logAction(request, action);
      RWData.getInstance().save(file, requestBytes);
      return ;
    } 
    
    if("delete".equals(action)) {
      java.io.File file = getFile(request, false);
      
      if(file == null) {
        output.write("-1".getBytes());
        return;
      }
      
      logAction(request, action);
      if(file.isDirectory()) deleteFolder(file); else file.delete();
      output.write("1".getBytes());
      return;
    } 
    
    if("create.folder".equals(action)) {
      Header fileHeader = request.getFirstHeader("file");
      if(header == null) {
        output.write("-1".getBytes());
        return ;
      }
      
      String fileName = fileHeader.getValue();
      File file = UtilFile.getFolder(fileName);
      if(file.isFile() && file.length() < 1) {
        file.delete();
        UtilFile.getFolder(fileName);
      }
      
      logAction(request, action);
      output.write("1".getBytes());
      return;
    }

    if("load.file".equals(action)) {
      java.io.File file = getFile(request, false);
      if(file == null)  {
        output.write("-1".getBytes());
        return;
      }
      
      FileEntity fileEntity = new FileEntity(file, "xml/text");
      response.setEntity(fileEntity);
      response.addHeader(new BasicHeader("last-modified", String.valueOf(file.lastModified())));
//      response.addHeader(new BasicHeader("Content-Length", String.valueOf(file.length())));
//      output.write(RWData.getInstance().load(file));        
      return;
    }
    
    if("read.file".equals(action)) {
      java.io.File file = getFile(request, false);
      if(file == null)  {
        output.write("".getBytes());
        return;
      }
      
      output.write(RWData.getInstance().load(file));        
      return;
    }
    
    if("load.file.by.gzip".equals(action)) {
      java.io.File file = getFile(request, false);
      if(file == null)  {
        output.write("-1".getBytes());
        return;
      }
      
      if(file.length() > 500*1024*1024l) {
        FileEntity fileEntity = new FileEntity(file, "xml/text");
        response.setEntity(fileEntity);
        response.addHeader(new BasicHeader("last-modified", String.valueOf(file.lastModified())));
        return;
      }
      
      try {
        FileInputStream input  = new FileInputStream(file);
        new GZipIO().zip(input, output);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      return;
    }

    if("list.folder".equals(action)) {
      java.io.File file = getFile(request, true);
      if(file == null || !file.exists()) {
        output.write("".getBytes());
        return;
      }
      int minSize = -1;
      Header sizeHeader = request.getFirstHeader("min.size");
      if(sizeHeader != null) {
        try {
          minSize = Integer.parseInt(sizeHeader.getValue());
        } catch (Exception e) {
        }
      }
      
      byte [] bytes = getRequestData(request);
      CommonFileFilter fileFilter = null;
      try {
        if(bytes != null && bytes.length > 0) {
          ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(bytes));
          fileFilter = (CommonFileFilter) objectInput.readObject();
          objectInput.close();
        }
      } catch (Exception e) {
        logAction(request, action);
        LogService.getInstance().setThrowable("SERVER", e);
      }

      File [] files = UtilFile.listFiles(file, fileFilter);
      if(files == null)  {
        output.write("".getBytes());
        return;
      }
      
      StringBuilder builder = new StringBuilder();
      for(File ele : files) {
//        System.out.println(ele.getName() + " : " + ele.length() + "  : "+ minSize);
        if(ele.length() > minSize) {
          builder.append(ele.getName()).append('\n');
        }
      }
      
//      System.out.println("======================== \n"+ builder);
      
      try {
        output.write(builder.toString().getBytes());
      } catch (Exception e) {
        logAction(request, action);
        LogService.getInstance().setMessage("SERVER", e, null);
      }
      return;
    }
    
    if("list.version".equals(action)) {
      java.io.File file = getFile(request, true);
      int i = 1;
      while(i <= MAX_VERISON) {
        String name  = file.getName()+".v."+String.valueOf(i);
        File vfile = new File(file.getParentFile(), name);
        if(vfile.exists()) output.write((String.valueOf(i)+ "\n").getBytes());
        i++;
      }
    }
    
    if("sources.move.dustbin".equals(action)) {
      File file = getFile(request, false);
      if(file == null || !file.exists())  {
        output.write("-1".getBytes());
        return;
      }
      
      if(file.isDirectory()) {
        File [] files = file.listFiles();
        for(File ele : files) moveToDustbin(ele);
      } else {
        deleteVersion(file);
        moveToDustbin(file); 
      }
      boolean delete = file.delete();
      if(!delete) RWData.getInstance().save(file, new byte[0]);
    }
  }

  private void deleteFolder(File file) {
    File [] list = file.listFiles();
    for(File ele : list) {
      if(ele.isDirectory()) {
        deleteFolder(ele); 
      } else ele.delete();
    }
    file.delete();
  }
  
  private void logAction(HttpRequest request, String action) throws Exception {
    Header userHeader = request.getFirstHeader("username");
    Header fileHeader = request.getFirstHeader("file");
    if(userHeader == null  || fileHeader == null) throw new InvalidActionHandler();

    String message = NameConverter.decode(fileHeader.getValue());
    super.logAction(userHeader.getValue(), action, message);
  }
  
  private final static int MAX_VERISON = 10;
  
  private void moveToDustbin(File file) throws Exception {
    if(file == null || !file.exists() || file.isDirectory())  return;
    
    String group = file.getParentFile().getParentFile().getName();
    String category = group + "_" +  file.getParentFile().getName();
    
    String folder = "sources/sources/DUSTBIN/"+category+"/";
    File newFile = new File(UtilFile.getFolder(folder), group+"_"+file.getName());
    RWData.getInstance().copy(file, newFile);
    file.setWritable(true);
    file.delete();
    
  }
  
  private void deleteVersion(File file) {
    int i = MAX_VERISON;
    while(i > 0) {
      String name  = file.getName()+".v."+String.valueOf(i);
      File vfile = new File(file.getParentFile(), name);
      if(vfile.exists()) vfile.delete();
      i--;
    }
  }
  
  private String verioning(File file) {
    if(!file.exists() || file.length() < 1) return "unable to verioning " + file.getName();
    
    int i = MAX_VERISON;
    
    while(i > 0) {
      String name  = file.getName()+".v."+String.valueOf(i);
      File vfile = new File(file.getParentFile(), name);
      if(vfile.exists()) break;
      i--;
    }
    
//    System.out.println(" i = " + i  );
//    File currentVersion =  new File(file.getParentFile(), file.getName()+".v."+String.valueOf(i));
//    System.out.println(" CurrentFile: " + currentVersion.getName());
    
    if(i == MAX_VERISON) i--;
    String result = "Oldest verion " + (i+1);
    while(i > 0) {
      String name  = file.getName()+".v."+String.valueOf(i+1);
      File nextFile = new File(file.getParentFile(), name);
      
      try {
        if(!nextFile.exists()) nextFile.createNewFile();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        i--;
        continue;
      }
      
      name  = file.getName()+".v."+String.valueOf(i);
      File prevFile = new File(file.getParentFile(), name);
      if(!prevFile.exists()) break;
      
      try {
        RWData.getInstance().copy(prevFile, nextFile);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      i--;
    }
    
    File vFile = new File(file.getParentFile(), file.getName()+".v.1");
    try {
      RWData.getInstance().copy(file, vFile);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return e.getMessage();
    }
    return result;
  }
  
  private boolean compareBytes(byte [] bytes1, byte[] bytes2){
    if(bytes1 == bytes2) return true;
    if(bytes1 == null || bytes2 == null) return false;
    if(bytes1.length != bytes2.length) return false;
    for(int i = 0; i < bytes1.length; i++) {
      if(bytes1[i] != bytes2[i]) return false;
    }
    return true;
  }
  
}


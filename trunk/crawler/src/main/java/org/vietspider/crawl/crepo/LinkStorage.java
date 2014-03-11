/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.crepo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.LinkCreator;
import org.vietspider.crawl.plugin.ProcessPlugin;
import org.vietspider.link.pattern.LinkPatterns;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 26, 2009  
 */
class LinkStorage {
  
  private int maxFile = 500;
  static int MAX_SIZE = 5000;

  private String name;

  protected volatile CopyOnWriteArrayList<Link> temp = new CopyOnWriteArrayList<Link>();

  LinkStorage(String name) {
    this.name = NameConverter.encode(name);
    
    if(Application.LICENSE == Install.PERSONAL) {
      MAX_SIZE = 500;
    } else  if(Application.LICENSE == Install.PROFESSIONAL) {
      MAX_SIZE = 1000;
    } else  if(Application.LICENSE == Install.ENTERPRISE) {
      MAX_SIZE = 3000;
    } else {
      MAX_SIZE = 5000;
    }
    
    //for test
//    maxFile = 50;
//    MAX_SIZE = 50;
  }

  void add(final ProcessPlugin processor, Link value) {
    if(!value.isData()) return;
    if(temp.size() >= maxFile) {
      new Thread(){
        public void run() {
          store1(processor, maxFile);
        }
      }.start();
    }
    temp.add(value);
  }


  int load(Source source, ConcurrentLinkedQueue<Link> visitQueue,
      ConcurrentLinkedQueue<Link> dataQueue, int max) throws Throwable {
    File folder = UtilFile.getFolder("track/link2/");
    File [] files = UtilFile.listModifiedFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isFile() && f.getName().startsWith(name);
      }
    });

    int counter = 0;
    int fileIndex  = files.length-1;
    LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
    while(counter < max) {
      if(fileIndex < 0) break;
      File file  = files[fileIndex];
      byte []  bytes = RWData.getInstance().load(file);
      file.delete();

      ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
      DataInputStream buffered = new DataInputStream(byteInput);
      try {
        while(true) { 
          try {
            int length = buffered.readInt();
            bytes = new byte[length];
            buffered.read(bytes, 0, length); 
            
            Link link = toData(bytes);
            String address = link.getAddress();
            
            LinkPatterns dataTempl = linkCreator.getDataPatterns();
            LinkPatterns visitTempl = linkCreator.getVisitPatterns();
            
//            System.out.println(" thuan test "+ dataTempl + "  : "+ visitTempl);

            if(source.getDepth() > 1) {
              link.setIsLink(visitTempl == null || visitTempl.match(address));
            } else {
              link.setIsLink(false);
            }
            link.setIsData(dataTempl == null || dataTempl.match(address));
            //    System.out.println(link.getAddress());
            if(!link.isLink() && !link.isData()) continue;
//            System.out.println("is data "+link.isData()+ ", is link "+ link.isLink());
            
            link.setSourceFullName(source.getFullName());
            
            if(link.isData()) {
              dataQueue.add(link);
            } else {
              visitQueue.add(link);
            }
            //          System.out.println(" === > "+ data.getContentIndex().getId());
            counter++;
          } catch (EOFException e) {
            break;
          }
        }
      } finally {
        buffered.close();
      }
      fileIndex--;
      //      System.out.println(" doc xong duoc "+ idx);
    }
    return counter;
  }
  
  private Link toData(byte[] bytes) throws Throwable {
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = null;
    try {
      objectInputStream = new ObjectInputStream(byteInputStream);
      return (Link)objectInputStream.readObject();
    } catch (StackOverflowError e) {
      LogService.getInstance().setMessage("LINK STORAGE - LOAD", new Exception(e), e.toString() );
      return null;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return null;
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

  synchronized void store1(ProcessPlugin processor, int min) {
    if(temp.size() < 1) return;
    ByteArrayOutputStream bytesOutput =  new ByteArrayOutputStream(10*1024*1024);
    DataOutputStream buffered = new DataOutputStream(bytesOutput);
    Iterator<Link> iterator = temp.iterator();
    int counter = 0;
//    System.out.println(" truoc khi save con "+ temp.size());
    while(iterator.hasNext()) {
      Link link = iterator.next();
      
      if(processor != null && processor.checkDownloaded(link, false)) {
        temp.remove(link);
//        System.out.println(" da thay download roi "+ link.getAddress());
        continue;
      }
      
      writeBuffer(buffered, link);
      temp.remove(link);
      counter++;
      if(counter >= maxFile) break;
    }
    
//    System.out.println(" sau khi save con "+ temp.size());

    byte [] bytes = bytesOutput.toByteArray();
    if(bytes.length < 10) return;

    File file = searchNewFile();
    try {
      RWData.getInstance().save(file, bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    if(temp.size() > min) store1(processor, min);
  }

  private void writeBuffer(DataOutputStream buffered, Link link) {
    //  System.out.println("luc save "+ data.getContentIndex());
    ByteArrayOutputStream bytesObject = new ByteArrayOutputStream();
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(bytesObject);
      out.writeObject(link);
      out.flush();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    } finally {
      try {
        if(bytesObject != null) bytesObject.close();
      } catch (Exception e) {
      }
      try {
        if(out != null) out.close();
      } catch (Exception e) {
      }
    }

    byte [] bytes = bytesObject.toByteArray();
    try {
      buffered.writeInt(bytes.length);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }

    try {
      buffered.write(bytes, 0, bytes.length);
      buffered.flush();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private File searchNewFile() {
    File folder = UtilFile.getFolder("track/link2/");
    int index = 0;
    File file = new File(folder, name + "." + String.valueOf(index));
    while(file.exists()) {
      index++;
      file = new File(folder, name + "." + String.valueOf(index));
    }
    return file;
  }
  
  int totalFile() {
    File folder = UtilFile.getFolder("track/link2/");
    File [] files = UtilFile.listModifiedFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isFile() && f.getName().startsWith(name);
      }
    });
    return files.length;
  }
  


}

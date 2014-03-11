/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Iterator;

import org.vietspider.bean.ArticleIndex;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.solr2.common.TempQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 9, 2011  
 */
public class ArticleIndexTempIndexs extends TempQueue<ArticleIndex> {

  public ArticleIndexTempIndexs(){
    super("content/solr2/temp/");
  }

  ArticleIndex loadTemp(long id) {
    Iterator<ArticleIndex> iterator = queue.iterator();
    while(iterator.hasNext()) {
      ArticleIndex temp = iterator.next();
      if(temp.getId() == id) return temp;
    }
    return null;
  }
  
  public void load() {
    File [] files = UtilFile.listModifiedFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isFile();
      }
    });

    if(files == null || files.length < 1) return;

    for(int i = 0; i < files.length; i++) {
      try {
        byte []  bytes = RWData.getInstance().load(files[i]);

        ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
        DataInputStream buffered = new DataInputStream(byteInput);
        try {
          while(true) { 
            try {
              int length = buffered.readInt();
              bytes = new byte[length];
              buffered.read(bytes, 0, length);
              try {
                ArticleIndex data = toData1(bytes);
                queue.add(data);
                counter.incrementAndGet();
              } catch (Exception e) {
                try {
                  ArticleIndexWrapper data = toData1(bytes);
//                  System.out.println(" hiihihihi "+ data.hashCode());
                  queue.add(data.index);
                  counter.incrementAndGet();
                } catch (Exception e1) {
                }
              }
            } catch (EOFException e) {
              break;
            }
          }
        } finally {
          buffered.close();
        }
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        files[i].delete();
      }
      //      System.out.println(" doc xong duoc "+ idx);
    }
  }
  
  @SuppressWarnings("unchecked")
  private <E> E toData1(byte[] bytes) throws Throwable {
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = null;
    try {
      objectInputStream = new ObjectInputStream(byteInputStream);
      return (E)objectInputStream.readObject();
    } catch (StackOverflowError e) {
      LogService.getInstance().setMessage("QUEUE.TEMP - LOAD", new Exception(e), e.toString() );
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

  public static class ArticleIndexWrapper implements Serializable {

    private final static long serialVersionUID = 1l;

    private ArticleIndex index;

    public ArticleIndexWrapper(ArticleIndex index) {
      this.index = index;
    }

    ArticleIndex get() { return index; } 

    boolean isOk() { return true; }    

  }

}

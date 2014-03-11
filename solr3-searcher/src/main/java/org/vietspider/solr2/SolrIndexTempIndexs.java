/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Iterator;

import org.vietspider.bean.SolrIndex;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.solr2.common.TempQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 9, 2011  
 */
public class SolrIndexTempIndexs extends TempQueue<SolrIndex> {

  public SolrIndexTempIndexs(){
    super("content/solr2/temp/");
  }

  SolrIndex loadTemp(long id) {
    Iterator<SolrIndex> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SolrIndex temp = iterator.next();
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
                SolrIndex data = toData1(bytes);
                queue.add(data);
                counter.incrementAndGet();
              } catch (Exception e) {
                try {
                  SolrIndexWrapper data = toData1(bytes);
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

  /*void searchByPhone(Collection<String> set, List<String> phones) {
    Iterator<SolrIndexWrapper> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SolrIndexWrapper temp = iterator.next();
      for(int i = 0; i < phones.size(); i++) {
        if(!temp.index.getPhones().contains(phones.get(i))) continue;
        String id = String.valueOf(temp.index.getId());
        if(!set.contains(id)) set.add(id);
        break;
      }
    }
  }

  void searchByPhone(Collection<String> set, String phone) {
    Iterator<SolrIndexWrapper> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SolrIndexWrapper temp = iterator.next();
      if(temp.index.getPhones().contains(phone)) {
        set.add(String.valueOf(temp.index.getId()));
      }
    }
  }

  void searchByEmail(Collection<String> set, List<String> emails) {
    Iterator<SolrIndexWrapper> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SolrIndexWrapper temp = iterator.next();
      for(int i = 0; i < emails.size(); i++) {
        if(!temp.index.getEmails().contains(emails.get(i))) continue;
        String id = String.valueOf(temp.index.getId());
        if(!set.contains(id)) set.add(id);
        break;
      }
    }
  }*/

//  boolean delete(String meta) {
//    long id = -1;
//    try {
//      id = Long.parseLong(meta);
//    } catch (Exception e) {
//      return false;
//    }
//    
//    Iterator<SolrIndexWrapper> iterator = queue.iterator();
//    while(iterator.hasNext()) {
//      SolrIndexWrapper temp = iterator.next();
//      if(temp.index.getId() != id) continue;
//      iterator.remove();
//      return true;
//    }
//    return false;
//  }

 /* void index(String meta) {
    long id = -1;
    try {
      id = Long.parseLong(meta);
    } catch (Exception e) {
      return;
    }

    Iterator<SolrIndexWrapper> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SolrIndexWrapper temp = iterator.next();
      if(temp.index.getId() != id) continue;
      temp.status = SolrIndexWrapper.OK;
      return ;
    }
  }

  void owner(String meta) {
    long id = -1;
    try {
      id = Long.parseLong(meta);
    } catch (Exception e) {
      return;
    }

    Iterator<SolrIndexWrapper> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SolrIndexWrapper temp = iterator.next();
      if(temp.index.getId() != id) continue;
      temp.index.setOwner(true);
      temp.status = SolrIndexWrapper.OK;
      LogService.getInstance().setMessage(null, " thay co cai owner "+ temp.index.getId());
      return ;
    }
  }
*/
//  Iterator<SolrIndexWrapper> iterator() { return queue.iterator(); }
//
  public static class SolrIndexWrapper implements Serializable {

    private final static long serialVersionUID = 1l;

//    private final static short WAIT = 0;
//    private final static short OK = 1;

//    private short status = WAIT;
//    private long time = System.currentTimeMillis();

    private SolrIndex index;

    public SolrIndexWrapper(SolrIndex index) {
      this.index = index;
    }

    SolrIndex get() { return index; } 

    boolean isOk() {
      return true;
//      if(status == OK) return true;
//      return System.currentTimeMillis() - time >= 30*60*1000l;
    }    

  }

}

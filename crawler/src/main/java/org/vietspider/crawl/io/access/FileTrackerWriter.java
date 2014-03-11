/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.RandomAccessFile;
import java.util.Iterator;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 28, 2008  
 */
class FileTrackerWriter extends TrackerWriter {
  
  void save() {
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "rwd");
      random.seek(0);
      
      int [] keys = new int[codes.size()];
      long [] values = new long[codes.size()];
      Iterator<Integer> iterator = codes.keySet().iterator();
      int index = 0;
      while(iterator.hasNext()) {
        keys[index] = iterator.next();
        values[index] = codes.get(keys[index]);
        iterator.remove();
        index++;
      }
      
      int position = 0;
      while(position < random.length() - 12) {
        try {
          int key = random.readInt();
          long start = random.getFilePointer();
          random.readLong();
          for(int i = 0; i < index; i++) {
            if(keys[i] == -1 || keys[i] != key) continue;
            random.seek(start);
//            System.out.println(" update by writer "+ key + " : "+ value.getTime());
            random.writeLong(values[i]);
            keys[i] = -1;
            values[i] = -1;
            break;
          }
        } catch (Exception e) {
          break;
        }
      }
      
      for(int i = 0; i < index; i++) {
        if(keys[i] == -1) continue;
        try {
          random.seek(random.length());
//          System.out.println(" write  "+ value.getKey() + " : "+ value.getTime());
          random.writeInt(keys[i]);
          random.writeLong(values[i]);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}

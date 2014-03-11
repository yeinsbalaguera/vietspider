/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Iterator;

import org.vietspider.common.io.LogService;
import org.vietspider.common.util.Queue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
public final class FilePostWriter extends PostWriter<Integer> {

  void save() {
    RandomAccessFile random = null;
    Queue<Long> expireCodes = new Queue<Long>();
    try {
      random  = new RandomAccessFile(file, "rwd");
      random.seek(0);

      Calendar calendar = Calendar.getInstance();
      long currentTime = calendar.getTimeInMillis();
      
      long length = file.length();
      while(true) {
        try {
          long startKey = random.getFilePointer();
          if(startKey >= length) break;  
          int key = random.readInt();
          
          long startPost = random.getFilePointer();
          if(startPost + 4  >= length) break;
          random.readInt();
          
          long startTime = random.getFilePointer();
          if(startTime + 8  >= length) break;
          long time = random.readLong();
          
          Integer value = data.get(key);
          
          if (value != null && value.intValue() > -1) {
            long startLast = random.getFilePointer();
            //write new post value
            random.seek(startPost);
            random.writeInt(value);
            //write new update time value
            random.seek(startTime);
            random.writeLong(currentTime);
            //seek current file pointer
            random.seek(startLast);
            data.remove(key);
          } else if(currentTime - time >= PostTracker.EXPIRE) {
            expireCodes.push(startKey);
          }
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          break;
        }
      }

      Iterator<Integer> iterator = data.keySet().iterator();
      while(expireCodes.hasNext() && iterator.hasNext()) {
        long start = expireCodes.pop();
        try {
          random.seek(start);
          int key = iterator.next();
          random.writeInt(key);
          random.writeInt(data.get(key));
          random.writeLong(Calendar.getInstance().getTimeInMillis());
          iterator.remove();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          e.printStackTrace();
          break;
        }      
      }

      while(iterator.hasNext()) {
        try {
          random.seek(random.length());
          int key = iterator.next();
          random.writeInt(key);
          random.writeInt(data.get(key));
          random.writeLong(Calendar.getInstance().getTimeInMillis());
          iterator.remove();
        } catch (Exception e) {
          break;
        }      
      }
      random.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

}

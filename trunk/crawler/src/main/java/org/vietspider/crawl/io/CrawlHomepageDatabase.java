/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.db.url.HomepageDatabase;
import org.vietspider.db.url.UrlDatabase;
import org.vietspider.db.url.UrlDatabases;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 27, 2009  
 */
public class CrawlHomepageDatabase extends HomepageDatabase {

  private File fileIndex;

  private long timeout = 0;
  private SimpleDateFormat dateFormat;

  public CrawlHomepageDatabase(String _name, long timeout) {
    super(_name, false);
    if(!exists) return;
    fileIndex = new File(folder, this.name + ".url.idx");
    dateFormat = new SimpleDateFormat("yyMMddHHmm");
    this.timeout = timeout;
  }

  public int loadCrawlList(List<String> urls, int page) {
    if(page < 1) page = 1;
//    System.out.println("== > da call vao day "+ page);

    UrlDatabase database = UrlDatabases.getInstance().getDatabase(folder);
    if(page > database.getTotalPage(100)) page = 1;

//    List<String> urls = new Arr/ayList<String>();
    List<MD5Hash> entries = new ArrayList<MD5Hash>();
    page = loadPageByAsc(entries, page);
    for(int i = 0; i < entries.size(); i++) {
      String url  = database.search(entries.get(i));
//      System.out.println("=== > "+ url);
      if(url != null) urls.add(url);
    }
    return page;
  }

  public int loadPageByAsc(List<MD5Hash> entries, int page){
//    List<MD5Hash> entries = new ArrayList<MD5Hash>();
    RandomAccessFile random =null;
    try {
      random = new RandomAccessFile(fileIndex, "rws");
      long length = random.length();
      int entrySize = MD5Hash.DATA_LENGTH + 4;
      long start = (page - 1) * entrySize * 100;
      if (start < 0) return 1;

      while (true){
        start += entrySize;
        if (start >= length) break;
        random.seek(start);

        byte [] bytes = new byte[MD5Hash.DATA_LENGTH];
        int st = -1;
        try {
          random.readFully(bytes);
          st = random.readInt();
        } catch (EOFException ex) {
          LogService.getInstance().setThrowable(ex);
          break;
        }

        MD5Hash entry = new MD5Hash();
        entry.setDigest(bytes);

        try {
          Date date = dateFormat.parse(String.valueOf(st));
          if(Calendar.getInstance().getTimeInMillis() - date.getTime() >= timeout) {
            entries.add(entry);
            updateTime(random, start);
          }
        } catch (Exception e) {
          updateTime(random, start);
        }

        if (entries.size()>= 100) break;
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally{
      try {
        if (random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    return page+1;
  }

  private void updateTime(RandomAccessFile random, long start) {
    try {
      random.seek(start + MD5Hash.DATA_LENGTH);
      String value = dateFormat.format(Calendar.getInstance().getTime());
      random.writeInt(Integer.parseInt(value));
    } catch (Exception e3) {
      LogService.getInstance().setThrowable(e3);
    }
  }

}

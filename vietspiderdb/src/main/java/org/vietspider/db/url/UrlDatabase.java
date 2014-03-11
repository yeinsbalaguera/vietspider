/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.db.url;

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.HttpURL;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.db.HashStringDatabase;
import org.vietspider.db.Md5HashEntry;
import org.vietspider.db.VDatabase;
import org.vietspider.paging.PageIO;
import org.vietspider.paging.PageIOs;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */

public class UrlDatabase extends HashStringDatabase implements VDatabase {

  public UrlDatabase(File folder, long cachedSize) throws Exception {
    super(folder, "url", cachedSize > 0 ? cachedSize : 1*1024*1024l);
  }

  public void save(MD5Hash key, String value) throws Throwable {
    if(isClose) return;
    lastAccess = System.currentTimeMillis();
    if(map.containsKey(key.getDigest())) return;
    map.put(key.getDigest(), value);
    PageIOs.getInstance().write(fileIndex, new Md5HashEntry(key));
    counter++;
    if(counter < 50) return;
    db.sync();
    counter = 0;
  }

  public void save(String url) throws Throwable {
    if(isClose) return;
    MD5Hash md5Hash = MD5Hash.digest(new HttpURL(url).getNormalizeURL());
    save(md5Hash, url);
  }

  public void remove(String url) throws Throwable {
    remove(MD5Hash.digest(new HttpURL(url).getNormalizeURL()));
  }
  
  public List<String> loadUrlByPage(int page, int pageSize) throws Throwable {
    PageIO<Md5HashEntry> pageIO = getPageIO();
    List<Md5HashEntry> md5Hashs = pageIO.loadPageByAsc(page, pageSize, null);

    List<String> list = new ArrayList<String>();
    for(int i = 0; i < md5Hashs.size(); i++) {
      MD5Hash key = md5Hashs.get(i).getMd5Hash();
      String url = search(key);
      if(url != null) list.add(url);
    }
    return list;
  }

  
  public void export(OutputStream output)  throws Throwable {
    OutputStreamWriter streamWriter = null;
    BufferedWriter bufferedWriter = null;
    try {
      streamWriter = new OutputStreamWriter(output, Application.CHARSET);
      bufferedWriter = new BufferedWriter(streamWriter);
      int total = getTotalPage(1000);
      int page  = 1;
      while(page <= total) {
        List<String> list = loadUrlByPage(page, 1000);
        for(int i = 0; i < list.size(); i++) {
          if(page != 1 || i != 0) bufferedWriter.newLine();
          bufferedWriter.write(list.get(i));  
        }
        bufferedWriter.flush();
        page++;
      }
    } finally {
      try {
        if(bufferedWriter != null) bufferedWriter.close();
      } catch (Exception e) {
      }
      
      try {
        if(streamWriter != null) streamWriter.close();
      } catch (Exception e) {
      }
      
      try {
        output.close();
      } catch (Exception e) {
      }
    }
  }
  
}


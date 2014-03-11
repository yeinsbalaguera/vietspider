/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.db.website;

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.vietspider.bean.website.Website;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.HashStringDatabase;
import org.vietspider.db.Md5HashEntry;
import org.vietspider.db.VDatabase;
import org.vietspider.paging.Entry;
import org.vietspider.paging.EntryFilter;
import org.vietspider.paging.PageIO;
import org.vietspider.paging.PageIOs;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */

class WebsiteDatabase extends HashStringDatabase implements VDatabase {
  
  private String path;
  
  WebsiteDatabase(String path/*, short type, String language*/) throws Exception {
    super(UtilFile.getFolder(path), "website", 1*1024*1024l);
    this.path = path;
    
    generateFileIndex();
  }
  
  String getPath() { return path; }
  
  private void save(MD5Hash key, String value) throws Throwable {
    if(isClose) return;
    lastAccess = System.currentTimeMillis();
    if(!map.containsKey(key.getDigest())) {
      Md5HashEntry entry = new Md5HashEntry(key);
      entry.setStatus(1);
      PageIOs.getInstance().write(fileIndex, entry);
    }
    map.put(key.getDigest(), value);
    counter++;
    if(counter < 50) return;
    db.sync();
    counter = 0;
  }

  void save(Website website) throws Throwable {
    if(isClose || website == null) return;
    website.setPath(path);
//    System.out.println(" save website "+ website.getAddress());
    MD5Hash md5Hash = MD5Hash.digest(website.getHost().toLowerCase());
    save(md5Hash, Object2XML.getInstance().toXMLDocument(website).getTextValue());
  }
  
  final Website searchWebsite(MD5Hash key) throws Exception {
    String xml = search(key);
    if(xml == null || xml.trim().isEmpty()) return null;
    Website website = XML2Object.getInstance().toObject(Website.class, xml);
    website.setPath(path);
    return website;
  }
  
  private void generateFileIndex() {
    if(fileIndex.exists() && fileIndex.length() > 0) return;
    new Thread() {
      public void run() {
        Iterator<byte[]> iterator = map.keySet().iterator();
        while(iterator.hasNext()) {
          byte [] bytes = iterator.next();
          Md5HashEntry entry = new Md5HashEntry(new MD5Hash(bytes));
          entry.setStatus(1);
          PageIOs.getInstance().write(fileIndex, entry);
        }
      }
    }.start();
  }
  
  public int getTotalPage(int pageSize) {
    PageIO<Md5HashEntry> pageIO = getPageIO();
    return pageIO.getTotalPage(pageSize);
  }
  
  List<Website> loadUrlByPage(int page, int pageSize) throws Throwable {
    PageIO<Md5HashEntry> pageIO = getPageIO();
    WebsiteEntryFilter filter =  new WebsiteEntryFilter();
    
    List<Md5HashEntry> md5Hashs = pageIO.loadPageByAsc(page, pageSize, filter);

    List<Website> list = new ArrayList<Website>();
    for(int i = 0; i < md5Hashs.size(); i++) {
      MD5Hash key = md5Hashs.get(i).getMd5Hash();
      String xml = search(key);
      if(xml == null || xml.trim().isEmpty()) continue;
      try {
        Website website = XML2Object.getInstance().toObject(Website.class, xml);
        String host = Website.toHost(website.getAddress());
        if(host == null) {
          remove(key);
          Md5HashEntry entry = new Md5HashEntry(key);
          entry.setStatus(Md5HashEntry.DELETE);
          PageIOs.getInstance().write(fileIndex, entry);
        } else {
          website.setHash(key.toString());
          list.add(website);
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }    
    
    if(filter.getDelete() >= 10) {
      pageIO.optimize(-1, new PageIO.OptimizePlugin() {
        public boolean isRemove(byte[] bytes) {
          if(isClose) return false;
          return !map.containsKey(bytes);
        }
      });
    }
    return list;
  }
  
  SortedMap<byte[], String> getMap() { return map; }
  
  
  private static class WebsiteEntryFilter implements  EntryFilter {
    
    private int delete = 0;
    
    public int getDelete() { return delete; }

    public boolean validate(Entry entry) {
      if(entry.getStatus() == -1) {
        delete++;
        return false;
      }
      return true;
    }
  }
}


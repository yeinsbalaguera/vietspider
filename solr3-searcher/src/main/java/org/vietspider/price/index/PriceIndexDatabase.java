/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.index;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.content.CommonDatabase;
import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 15, 2011  
 */
public class PriceIndexDatabase {
  
  private File folder;
  
  private PriceIndexMapper mapper = new PriceIndexMapper();
  
  private Map<String, CommonDatabase> databases = new HashMap<String, CommonDatabase>();
  
  protected volatile long lastAccess = System.currentTimeMillis();
  private boolean isClose = false;
  
  public PriceIndexDatabase(String city) {
    String name  = VietnameseConverter.toAlias(city);
    folder = UtilFile.getFolder("content/solr2/price_index/" + name);
  }
  
  public void save(String action, PriceIndex index) {
    try {
      CommonDatabase database = getDatabase(action);
      long id  = Long.parseLong(index.getId());
//      System.out.println(" chay thu cai ni 0 "+ id + " : "+ index);
      if(database.contains(id)) return;
      String text = mapper.toText(index);
      database.save(id, text.getBytes(Application.CHARSET));
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public long getLastAccess() { return lastAccess; }

  public void refreshLastAccess() { lastAccess = System.currentTimeMillis(); }
  
  public boolean isClose() { return isClose; }

  public synchronized CommonDatabase getDatabase(String action) throws Exception {
    refreshLastAccess();
    CommonDatabase database = databases.get(action);
    if(database != null && !database.isClose()) return database;
    String name  = action.replace(',', '_');
    File file  = new File(folder, name + "/");
    file.mkdirs();
    database = new CommonDatabase(file, name, 1024*1024l, false);
    databases.put(action, database);
    return database;
  }
  
  public void close() {
    isClose = true;
    Iterator<Map.Entry<String, CommonDatabase>> iterator = databases.entrySet().iterator();
    while(iterator.hasNext()) {
      CommonDatabase tracker = iterator.next().getValue();
      tracker.close();
      iterator.remove();
    }
  }

}

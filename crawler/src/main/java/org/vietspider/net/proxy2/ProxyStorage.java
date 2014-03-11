/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.proxy2;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2011  
 */
class ProxyStorage {

  private static ProxyStorage INSTANCE;

  synchronized final static ProxyStorage getInstance() {
    if(INSTANCE != null) return INSTANCE;
    INSTANCE = new ProxyStorage();
    return INSTANCE;
  }

  private RecordManager recman;
  private PrimaryStoreMap<Long, String> main;

  //  private final static int MAX_RECORD = 1000;
  private int counter;
  private boolean error = false;
  //  private SecondaryTreeMap<String, Long, String> valueIndex;

  public ProxyStorage() {
    open();

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Proxy Database";}

      public void execute() {
        try {
          commit(0);
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
        try {
          close();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    });

    //    valueIndex = main.secondaryTreeMap("valueIndex",
    //        new SecondaryKeyExtractor<String, Long, String>() {
    //      public String extractSecondaryKey(Long key, String value) {
    //        return value;
    //      }         
    //    });
  }

  private void open() {
    File folder = new File(UtilFile.getFolder("system/proxy/temp/"), "data");
    try {
      recman = RecordManagerFactory.createRecordManager(folder.getAbsolutePath());
      //class jdbm.helper.PrimaryStoreMapImpl
      main = recman.storeMap("proxy");
    } catch (Exception e) {
      error = true;
      LogService.getInstance().setThrowable(e);
    }
  }

  public long size() { return main.size(); }

  public void write(String value) {
    int idx = value.indexOf(':');
    int port = Integer.parseInt(value.substring(idx+1));
    if(port == 80 || port == 81) {
      save(value);
      return;
    }

//    if(port < 100) {
//      value = value.substring(0, idx);
//      save(value + ":80");
//      save(value + ":81");
//      save(value + ":808");
//      save(value + ":443");
//
//      save(value + ":3124");
//      save(value + ":3127");
//      save(value + ":3128");
//      save(value + ":3129");
//
//      save(value + ":8000");
//      save(value + ":8080");
//      save(value + ":8081");
//      save(value + ":8082");
//      save(value + ":8085");
//      save(value + ":8123");
//      save(value + ":9090");
//      return;
//    }

    save(value);

  }

  private void save(String value)  {
    if(error) return;

    try {
      boolean _contains = main.containsValue(value);
      if(_contains) {
//        System.out.println("exist " + value);
        return;
      }
//      System.out.println(" add "+ value);
      main.putValue(value);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    commit(500);
  }

  private void commit(int size) {
    if(error) return;
    counter++;
    if(counter < size) return;
    counter = 0; 
    try {
      recman.commit();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public List<String> get() {
    List<String> values = new ArrayList<String>();
    if(error) return values;
    try {
      Iterator<Entry<Long, String>> iterator = main.entrySet().iterator();
      while(iterator.hasNext()) {
        Entry<Long, String> entry = iterator.next();
        values.add(entry.getValue());
        iterator.remove();

        try {
          commit(10);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }

        if(values.size() > 100) break;
      }
    } catch (IllegalStateException e) {
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }

    if(values.size() > 0) {
      LogService.getInstance().setMessage(null, "Total of proxies in the temp database is "+ main.size());
    }

    return values;
  }

  public void clean() {
    if(main.size() > 10) return;
    error = true;
    try {
      close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }

    File folder = UtilFile.getFolder("system/proxy/temp/");
    UtilFile.deleteFolder(folder, false);
    open();
    error = false;
  }

  public void close()  throws Exception {
    recman.commit();
    recman.close();
  }


}

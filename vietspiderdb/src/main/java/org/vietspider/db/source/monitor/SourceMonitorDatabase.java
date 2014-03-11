/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.NameConverter;
import org.vietspider.db.JeDatabase;
import org.vietspider.db.VDatabase;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class SourceMonitorDatabase extends JeDatabase implements VDatabase {

  private ClassCatalog tpCatalog;

  protected String name;
  private SortedMap<String, String> map;

  private volatile int counter = 0;

  protected volatile long lastAccess = System.currentTimeMillis();

  public SourceMonitorDatabase(File folder, String name, long cachedSize) throws Exception {
    this.name = name;

    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);
    if(cachedSize > 0) {
      envConfig.setCacheSize(cachedSize);//5*1024*1024
    } else {
      envConfig.setCacheSize(1*1024*1024l);
    }

    this.env = new Environment(folder, envConfig);

    //    Transaction txn = env.beginTransaction(null, null);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, "source-monitor", dbConfig);

    tpCatalog = new StoredClassCatalog(db);

    // use Integer tuple binding for key entries
    StringBinding keyBinding = new StringBinding();
    SerialBinding<String> dataBinding = new SerialBinding<String>(tpCatalog, String.class);
    this.map = new StoredSortedMap<String, String> (db, keyBinding, dataBinding, true);
    //    System.out.println("================= >"+ map.size());
  }

  public void save(SourceLog log) throws Throwable {
    if(isClose || log == null || log.getName() == null) return;
    if(log.getName().trim().isEmpty()) return;
    lastAccess = System.currentTimeMillis();
    map.put(log.getName(), Object2XML.getInstance().toXMLDocument(log).getTextValue());
    
    counter++;
    if(counter < 50) return;
    db.sync();
    counter = 0;
  }

  public SourceLog search(String key) throws Throwable {
    if (isClose) return null;

    lastAccess = System.currentTimeMillis();

    //    System.out.println(" truoc khi search "+ map.size());
    String value  = map.get(key);
    //    System.out.println(" get ra duoc "+ new String(code) + " : "+ value);
    if(value == null || value.trim().isEmpty()) return null;
    return XML2Object.getInstance().toObject(SourceLog.class, value);
  }

  public String getName() { return name; }

  public long lastAccess() { return lastAccess; }

  public void exportDb(File tmpFile) throws Exception {
    if(isClose) return;

    lastAccess = System.currentTimeMillis();
    
    FileOutputStream output = null;
    FileChannel channel = null;
    try {
      output = new FileOutputStream(tmpFile, true);
      channel = output.getChannel();

      Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
      StringBuilder builder = new StringBuilder();
      
      while(iterator.hasNext()) {
        Entry<String, String> entry;
        try {
          entry = iterator.next();
        } catch (Exception e) {
          continue;
        }
        if(entry == null) continue;
        String key = entry.getKey();
        String value = entry.getValue();
        if(key == null || key.trim().isEmpty()) continue;
        if(value == null || value.trim().isEmpty()) continue;
        
        try {
          SourceLog log = XML2Object.getInstance().toObject(SourceLog.class, value);
          
          builder.append(log.getName()).append('/');
          builder.append(log.getCrawlTime()).append('/');
          builder.append(log.getTotalLink()).append('/');
          builder.append(log.getTotalData()).append('/');
          builder.append(lastModified(log.getName())).append('\n');
//          builder.append(log.getLastAccess()).append('\n');

          if(builder.length() > 1000) {
            String text = builder.toString();
            if(text.charAt(text.length()-1) == '\n') {
              text = text.substring(0, text.length()-1);
            }
            byte [] bytes = text.getBytes(Application.CHARSET);
            ByteBuffer buff = ByteBuffer.allocateDirect(bytes.length);
            buff.put(bytes);
            buff.rewind();
            if(channel.isOpen()) channel.write(buff);
            buff.clear();
            
            builder.setLength(0);
            builder.append('\n');
          }
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          continue;
        }
      }
      
      String text = builder.toString();
      if(text.length() > 0 && text.charAt(text.length()-1) == '\n') {
        text = text.substring(0, text.length()-1);
      }
      byte [] bytes = text.getBytes(Application.CHARSET);
      ByteBuffer buff = ByteBuffer.allocateDirect(bytes.length);
      buff.put(bytes);
      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      buff.clear();
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(output != null) output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  private long lastModified(String text) {
    String [] elements =text.split("\\.");
    String group = NameConverter.encode(elements[0]);
    String cate = NameConverter.encode(elements[1]);
    String source = NameConverter.encode(elements[2]);
    source  = cate + "." + NameConverter.encode(source);
    Source model = SourceIO.getInstance().loadSource(group, cate, source);
    if(model == null) {
      LinkLogStorages.getInstance().sourceNull(group + "." + cate + "." + name);
      return -1;
    }
//    System.out.println(" thay co "+ model);
    return model.getLastModified();
  }

  public SortedMap<String, String> getMap() { return map; }
  
  
}


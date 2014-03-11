/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.db.JeDatabase;
import org.vietspider.db.VDatabase;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;
import org.vietspider.serialize.XML2Object;

import com.sleepycat.bind.ByteArrayBinding;
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
public class MultiSourceMonitorDatabase extends JeDatabase implements VDatabase {

  public long lastAccess() { return 0; }

  private SortedMap<String, byte[]> map;

  public MultiSourceMonitorDatabase() throws Exception {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);
    envConfig.setCacheSize(3*1024*1024l);

    File folder = UtilFile.getFolder("content/summary/eid/temp_msdb/");
    this.env = new Environment(folder, envConfig);

    //    Transaction txn = env.beginTransaction(null, null);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, "source-monitor", dbConfig);

    // use Integer tuple binding for key entries
    StringBinding keyBinding = new StringBinding();
    ByteArrayBinding dataBinding = new ByteArrayBinding();
    this.map = new StoredSortedMap<String, byte[]> (db, keyBinding, dataBinding, true);
    //    System.out.println("================= >"+ map.size());
  }

  public void save(MultiSourceLog log) {
    if(isClose || log == null || log.getName() == null) return;
    if(log.getName().trim().isEmpty()) return;

    ByteArrayOutputStream bytesObject =  null;
    try {
      bytesObject = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bytesObject);
      out.writeObject(log);
      out.flush();
      out.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if(bytesObject == null || bytesObject.size() < 1) return;

    map.put(log.getName(), bytesObject.toByteArray());

  }

  public MultiSourceLog search(String key) throws Throwable {
    if (isClose) return null;

    byte[] bytes  = map.get(key);
    if(bytes == null) return null;

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = null;
    objectInputStream = new ObjectInputStream(byteInputStream);
    return (MultiSourceLog)objectInputStream.readObject();
  }

  public void exportDb(File tmpFile) throws Exception {
    if(isClose) return;

    FileOutputStream output = null;
    FileChannel channel = null;
    try {
      output = new FileOutputStream(tmpFile, true);
      channel = output.getChannel();

      Iterator<Map.Entry<String,byte[]>> iterator = map.entrySet().iterator();
      StringBuilder builder = new StringBuilder();

      while(iterator.hasNext()) {
        Entry<String, byte[]> entry;
        try {
          entry = iterator.next();
        } catch (Exception e) {
          continue;
        }

        if(entry == null) continue;
        String key = entry.getKey();
        byte[] value = entry.getValue();
        if(key == null || key.trim().isEmpty()) continue;
        if(value == null) continue;

        try {
          ByteArrayInputStream byteInputStream = new ByteArrayInputStream(value);
          ObjectInputStream objectInputStream = null;
          objectInputStream = new ObjectInputStream(byteInputStream);
          MultiSourceLog log = (MultiSourceLog)objectInputStream.readObject();

          builder.append(log.getName()).append('/');
          builder.append(log.getCrawlTime()).append('/');
          builder.append(log.getTotalLink()).append('/');
          builder.append(log.getTotalData()).append('/');
          builder.append(log.getTotalDownload()).append('/');
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
      LinkLogStorages.getInstance().sourceNull(group+"."+cate+"."+cate);
      return -1;
    }
    //    System.out.println(" thay co "+ model);
    return model.getLastModified();
  }

  public void convertTo(SourceMonitorDatabase database) {
    Iterator<Map.Entry<String,String>> iterator = database.getMap().entrySet().iterator();
    
    while(iterator.hasNext()) {
      Entry<String, String> entry;
      try {
        entry = iterator.next();
      } catch (Exception e) {
        continue;
      }
      if(entry == null) continue;
      String value = entry.getValue();
      if(value == null || value.trim().isEmpty()) continue;

      SourceLog log =  null;
      try {
        log = XML2Object.getInstance().toObject(SourceLog.class, value);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      if(log == null)  continue;
      MultiSourceLog temp = null;
      try {
        temp  = search(log.getName());
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      if(temp == null) temp  = new MultiSourceLog();
      temp.update(log);
      save(temp);
    }
  }

  @Override
  public void close() {
    super.close();
    UtilFile.deleteFolder(UtilFile.getFolder("content/summary/eid/temp_msdb/"));
  }

  
}


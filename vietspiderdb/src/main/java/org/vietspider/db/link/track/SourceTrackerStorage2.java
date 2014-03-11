package org.vietspider.db.link.track;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;

//http://code.google.com/p/jdbm2/

public class SourceTrackerStorage2 {

  private static final long serialVersionUID = 1L;

  private final static int MAX_RECORD = 10;
  private RecordManager recman;
  private PrimaryStoreMap<Long, SourceTracker> main;

  private long lastAccess;
  private SecondaryTreeMap<Integer, Long, SourceTracker> idIndex;
  private SecondaryTreeMap<String, Long, SourceTracker> channelIndex;

  private int counter;
  private String dateFolder;

  public SourceTrackerStorage2(String dateFolder) throws Exception {
    this.dateFolder = dateFolder;
    File file = new File(UtilFile.getFolder("track/logs/sources/" + dateFolder + "/summary"), "data");
    recman = RecordManagerFactory.createRecordManager(file.getAbsolutePath());
    //class jdbm.helper.PrimaryStoreMapImpl
    main = recman.storeMap("source_tracker");
    
//    PrimaryTreeMap<Integer, byte[]> treeMap = recman.treeMap("tete"); 

    idIndex = main.secondaryTreeMap("idIndex",
        new SecondaryKeyExtractor<Integer, Long, SourceTracker>() {
      @SuppressWarnings("unused")
      public Integer extractSecondaryKey(Long key, SourceTracker value) {
        return value.getId();
      }         
    });

    channelIndex = main.secondaryTreeMap("channelIndex",
        new SecondaryKeyExtractor<String, Long, SourceTracker>() {
      @SuppressWarnings("unused")
      public String extractSecondaryKey(Long key, SourceTracker value) {
        return value.getFullName();
      }         
    });
  }

  public long size() { return main.size(); }

  void add(LinkLog log) {
    String fullname = log.getChannel();
    int id = fullname.hashCode();
    SourceTracker tracker = getById(id);
    if(tracker != null) {
      tracker.add(log);
    } else {
      tracker = new SourceTracker(log);
    }
    save(tracker);
  }

  private void save(SourceTracker log) {
    lastAccess = System.currentTimeMillis();

    if(log.getStorageId() != null) {
      main.put(log.getStorageId(), log);
    } else {
      main.putValue(log);
    }
    counter++;
    if(counter < MAX_RECORD) return;
    counter = 0; 
    try {
      recman.commit();
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
  }

  public Collection<SourceTracker> get() {
    lastAccess = System.currentTimeMillis();
    return main.values();
  }

  public List<SourceTracker> get(String channel) {
    lastAccess = System.currentTimeMillis();
    List<SourceTracker> list = new ArrayList<SourceTracker>();
    for(SourceTracker log : channelIndex.getPrimaryValues(channel)){
      list.add(log);
    } 
    return list;
  }

  public SourceTracker getById(int id) {
    lastAccess = System.currentTimeMillis();

    Iterable<Long> iterable = idIndex.get(id);
    if(iterable == null) return null;

    Iterator<Long> keyIterator = iterable.iterator();
    if(keyIterator.hasNext()) {
      Long key = keyIterator.next();
      SourceTracker log = main.get(key);
      log.setStorageId(key);
      return log;
    }

    return null;
  }

  boolean isTimeout() {
    if(System.currentTimeMillis() - lastAccess >= 15*60*1000l) return true;
    return false;
  }

  public void export() {
    File folder = UtilFile.getFolder("track/logs/sources/" + dateFolder);
    File file  = new File(folder, "summary.txt");
    try {
      if(file.exists()) {
        if(System.currentTimeMillis() - file.lastModified() < 15*60*1000l) return;
        file.delete();
      }
      file.createNewFile();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    FileOutputStream output = null;
    FileChannel channel = null;
    try {
      output = new FileOutputStream(file, true);
      channel = output.getChannel();

      StringBuilder builder = new StringBuilder(100);
      Collection<SourceTracker>  list = main.values();
      //      System.out.println(" === > "+list.size());
      for(SourceTracker tracker : list) {
        builder.append(tracker.toString()).append('\n');
        if(builder.length() < 1000) continue;
        byte [] data = builder.toString().getBytes(Application.CHARSET);
        builder.setLength(0);
        ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
        buff.put(data);
        buff.rewind();
        if(channel.isOpen()) channel.write(buff);
        buff.clear(); 
      }

      builder.append("\n\n");
      byte [] data = builder.toString().getBytes(Application.CHARSET);
      ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
      buff.put(data);
      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      buff.clear(); 

    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
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

  public void close() {
    try {
      recman.commit();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    try {
      recman.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}

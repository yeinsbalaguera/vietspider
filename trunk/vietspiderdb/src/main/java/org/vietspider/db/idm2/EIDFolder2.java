/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.idm2;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.cache.InmemoryCache;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Track;
import org.vietspider.paging.Entry;
import org.vietspider.paging.MetaIdEntry;
import org.vietspider.paging.PageIOs;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 12, 2008  
 */
final public class EIDFolder2 {
  
  private final static String EID_FOLDER = "content/summary/eid/";
  
  private final static InmemoryCache<String, Object>
        CACHED = new InmemoryCache<String, Object>("EID", 100); 
  
  static {
    CACHED.setLiveTime(5*60);
  }
  
  static final public File [] getFiles(Domain domain, String metaId) {
    if(metaId.length() < 9) return null;
    String year = metaId.substring(0, 4);
    String month = metaId.substring(4, 6);
    String date = metaId.substring(6, 8);

    String folderName = year + "_" + month + "_" + date;
    final String startWith = folderName + ".";
    File folder = UtilFile.getFolder(EID_FOLDER + folderName+"/");
    
    if(domain == null) {
      return  folder.listFiles(new FileFilter() {
        public boolean accept(File f) {
          return f.getName().startsWith(startWith) && f.isFile();
        }
      });
    }
    
    List<File> files = new ArrayList<File>();
    
    File file  = new File(folder, startWith+"eid");
    files.add(file);
    
    if(domain.getGroup() == null || domain.getGroup().trim().isEmpty()) {
      return files.toArray(new File[files.size()]);
    }
    
    if(domain.getCategory() == null || domain.getCategory().trim().isEmpty()) {
      return files.toArray(new File[files.size()]);
    }
    
    String category = domain.getCategory();
    if(category.indexOf('.') < 0) {
      category = domain.getGroup()+"."  + category; 
    }
    category = NameConverter.encode(category);
    
    file  = new File(folder, startWith+category+".eid");
    files.add(file);
    
    if(domain.getName() == null || domain.getName().trim().isEmpty()) {
      return files.toArray(new File[files.size()]);
    }
    
    String name = NameConverter.encode(domain.getName());
    file  = new File(folder, startWith + category + "." + name + ".eid");
    files.add(file);
    
    return files.toArray(new File[files.size()]);
  }
  
  static final File[] getFileByDates(String date) throws Exception {
    final String startWith = date+".";
    File folder = UtilFile.getFolder(EID_FOLDER + date+"/");
    return folder.listFiles(new FileFilter() {
      public boolean accept(File f) {
        return f.getName().startsWith(startWith) && f.isFile();
      }
    });
  }
  
  final static File getFileName(Domain domain) {
    if(domain.getDate() == null) return null;
    String date = null;
    try {
      Date dateValue = CalendarUtils.getDateFormat().parse(domain.getDate());
      date = CalendarUtils.getFolderFormat().format(dateValue);
    } catch (Exception e) {
      return null;
    }
    if(date == null) return null;
    File folder = UtilFile.getFolder(EID_FOLDER + date + "/");
//    String group = domain.getGroup();
//    System.out.println(group);
//    if(group == null) return  date + ".eid";
    
    if(domain.getCategory() == null) return new File(folder, date + ".eid");
    String category = NameConverter.encode(domain.getCategory());
    
    if(domain.getName() == null) {
      return  new File(folder, date + "."+ category + ".eid");
    }
    String name = NameConverter.encode(domain.getName());
    return new File(folder, date + "." + category + "." + name + ".eid");
  }
  
  public static void delete(String date) {
    try {
      File [] files = getFileByDates(date);
      for(File file : files) {
        file.delete();
      }
    } catch (Exception e) {
      return;
    }
  }
  
  public static void write(Domain domain, String metaId, int status) {
    long entryId = Long.parseLong(metaId);
    MetaIdEntry entry = new MetaIdEntry(entryId, status);
    if(status == Article.WAIT) {
      entry.setType(Entry.INSERT); 
    } else {
      entry.setType(Entry.UPDATE);
    }
    File [] files = EIDFolder2.getFiles(domain, metaId);
    PageIOs.getInstance().write(files, entry);
//    TrackIdService.getInstance().save(domain, metaId);
  }
  
  public static Track loadTrack(String date) throws Exception {
    Track track = (Track)CACHED.getCachedObject("track_" + date);
//    System.out.println(" thay co track "+ track);
    if(track != null) {
      return track;
    }
    Date dateInstance = CalendarUtils.getDateFormat().parse(date);
    String fileName = CalendarUtils.getFolderFormat().format(dateInstance);
    File folder = new File(UtilFile.getFolder("content/summary/eid"), fileName+"/");
    
    track = new Track(date, Track.DATE);
    if(!folder.exists() || folder.length() < 1) return track;
    
    File [] files = folder.listFiles();
    TextSpliter spliter = new TextSpliter();
    for(int i = 0; i < files.length; i++) {
      String name  = files[i].getName();
      String [] elements = spliter.toArray(name, '.');
      if(elements.length < 5) continue;
      elements[2] = NameConverter.decode(elements[2]);
      elements[3] = NameConverter.decode(elements[3]);
      Domain domain = new Domain(date, elements[1], elements[2], elements[3]);
      track.addData(domain);
    }
    
    CACHED.putCachedObject("track_" + date, track);
    
    return track;
  }
  
  public static String[] loadDates() {
    String[] dates = (String[])CACHED.getCachedObject("all_date");
//    System.out.println(" thay co "+ dates);
    if(dates != null) {
      return dates;
    }
    File folder = UtilFile.getFolder("content/summary/eid/");
    File [] files = UtilFile.listFiles(folder);
    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    SimpleDateFormat folderFormat = CalendarUtils.getFolderFormat();
    List<String> values = new ArrayList<String>();
    for(File file : files) {
      if(!file.isDirectory() || isEmptyFolder(file)) continue;
      try {
        values.add(dateFormat.format(folderFormat.parse(file.getName())));
      } catch (Exception e) {
      }
    }
    dates = values.toArray(new String[values.size()]);
    CACHED.putCachedObject("all_date", dates);
    return dates;
  }
  
  private static boolean isEmptyFolder(File folder) {
    File [] files = folder.listFiles();
    if(files == null || files.length < 1) return true;
    for(File f : files) {
      if(f.length() > 0) return false;
    }
    return true;
  }

}

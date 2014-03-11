/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.model.Track;
import org.vietspider.paging.MetaIdEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 20, 2007  
 */
public class GCDeleteDatabase {

  public Meta deleteDataByMeta(String id, boolean deleteDatabase)  throws Exception {
    if(id == null || id.trim().isEmpty()) return null;
    
    //delete indexed data for search
    /*if(DbIndexerService.getInstance() != null) {
      DbIndexerService.getInstance().deleleIndexedById(id);
    }*/
    
    Meta meta  = DatabaseService.getLoader().loadMeta(id);
    if(meta == null) return null;
    Domain domain = DatabaseService.getLoader().loadDomainById(meta.getDomain());

    //delete data
    EIDFolder2.write(domain, meta.getId(), Article.DELETE);
    
//    IDTracker.getInstance().update(meta.getId(), -1);
    
    if(deleteDatabase) DatabaseService.getDelete().deleteArticle(id);
    
    String category = domain.getCategory();
    int idx = category.indexOf('.'); 
    if(idx > -1) category = category.substring(idx+1);
    
//    StringBuilder builder  = new StringBuilder(domain.getGroup());
//    builder.append('.').append(domain.getCategory());
//    String sourceName = builder.append('.').append(domain.getName()).toString();
//    SourceLogHandler.getInstance().getSaver().updateTotalData(sourceName, domain.getDate(), -1);
    return meta;
  }

  public void deleteExpireDatabaseData() throws Exception {
    List<String> dates = null;
    
    try {
      dates = DatabaseService.getLoader().loadDateFromDomain();
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    if(dates == null || dates.size() < CrawlerConfig.EXPIRE_DATE) return ;

    SystemProperties systemProperties = SystemProperties.getInstance();
   /* SystemProperties systemProperties = SystemProperties.getInstance();
    String backupFolder = systemProperties.getValue(Application.BACKUP_FOLDER);
    boolean backup = backupFolder != null && backupFolder.trim().length() > 0 ;

    File file = null;
    if(backup) {
      file = new File(backupFolder);
      backup = file.exists() && file.isDirectory();
    }

    if(backup) {
//    export = ServicesContainer.get(ExportData.class);
//    export.setOutputFolder(file.getAbsolutePath());
    }*/

    for(int i = CrawlerConfig.EXPIRE_DATE; i < dates.size(); i++) {
      String date = dates.get(i);
      try {
//        System.out.println(" chuan bi xoa date "+ date);
//      if(backup) export.toArchive(dates.get(i));
        if("true".equals(systemProperties.getValue(Application.CLEAN_DATABASE))) {
          DatabaseService.getDelete().deleteDomain(date);
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      /*try {
        DbIndexerService indexerService = DbIndexerService.getInstance();
        Calendar calendar = new ParseDateUtils().parse(date); 
        String dateInstance = CalendarUtils.getDateFormat().format(calendar.getTime());
        if(indexerService != null) indexerService.deleleIndexedByDate(dateInstance);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }*/
    }
  }
  
  void deleteDataByDomain(String date, String group, String category)  throws Exception {
    Track track = EIDFolder2.loadTrack(date);
    List<Track> sourceTracks = track.getTrackIds(Track.SOURCE);
    for(int i = 0; i < sourceTracks.size(); i++) {
      deleteDataByDomain(date, group, category, sourceTracks.get(i));
    }
//    MenuInfo menuInfo = SourceLogHandler.getInstance().loadData(date);
//    if(menuInfo == null) return;
//    CategoryInfo categoryInfo =  menuInfo.getCategoryInfo(group+"."+category);
//    if(categoryInfo == null) return;
//    List<SourceInfo> sourceInfos = categoryInfo.getSources();
//    for(SourceInfo sourceInfo : sourceInfos) {
//      deleteDataByDomain(date, group, category, sourceInfo.getName());
//    }
  }
  
  void deleteDataByDomain(String date, String group, String category, String name)  throws Exception {
    Track track = EIDFolder2.loadTrack(date);
    List<Track> sourceTracks = track.getTrackIds(Track.SOURCE);
    for(int i = 0; i < sourceTracks.size(); i++) {
      if(sourceTracks.get(i).getName().equals(name)) {
        deleteDataByDomain(date, group, category, sourceTracks.get(i));
      }
    }
  }
  
  void deleteDataByDomain(String date, String group, String category, Track sourceTrack)  throws Exception {
    Domain domain = new Domain(date, group, category, sourceTrack.getName());
    IEntryDomain entryDomain = new SimpleEntryDomain(date, group, category, sourceTrack.getName());
//    MenuInfo menuInfo = SourceLogHandler.getInstance().loadData(date);
//    if(menuInfo == null) return;
//    CategoryInfo categoryInfo =  menuInfo.getCategoryInfo(group+"."+category);
//    if(categoryInfo == null) return;
//    SourceInfo sourceInfo  = categoryInfo.getSource(source);
//    TrackId trackId = TrackIdService.getInstance().load(date);
//    TrackId sourceTrack = trackId.getSource(date, group, category, source);
    if(sourceTrack == null) return;
    MetaList metas = new MetaList("vietspider");
    
//    int total = sourceTrack.getIds().size();

//    int total = sourceInfo.getData();
    
//    StringBuilder builder  = new StringBuilder(group);
//    builder.append('.').append(category);
//    String sourceName = builder.append('.').append(source).toString();
//    SourceLogHandler.getInstance().getSaver().updateTotalData(sourceName, date, 0-total);
    
    DatabaseReader getter = DatabaseService.getLoader();
    
    int totalPage = 10;
//    total / metas.getPageSize() ;
//    if (total % metas.getPageSize() > 0) totalPage++ ;
//    metas.setTotalPage(totalPage);
    
    int page = 1;
    EntryReader entryReader = new EntryReader();
    
    //working with database
    while(page <= totalPage) {
      metas.setCurrentPage(page);
      List<String> ids = new ArrayList<String>();
      
      List<MetaIdEntry> entries =  entryReader.readData(entryDomain, metas, -1);
      if(entries.size() > 0) {
        for(int i = 0; i < entries.size(); i++) {
          String id = String.valueOf(entries.get(i).getMetaId());
          deleteDataByMeta(id, false);
          ids.add(id);
        }
      } else {
        getter.loadMetaFromDomain(domain, metas);
        List<Article> articles = metas.getData();
        for(int i = 0; i < articles.size(); i++) {
          String id  = articles.get(i).getMeta().getId();
          deleteDataByMeta(id, false);
          ids.add(id);
        }
      }
      
      totalPage = metas.getTotalPage();
      
      DatabaseService.getDelete().deleteArticle(ids.toArray(new String[ids.size()]));
      
      page++;
    }
    
    page = 1;
    while(true) {
      metas.setCurrentPage(page);
      List<String> ids = new ArrayList<String>();
      
      getter.loadMetaFromDomain(domain, metas);
      List<Article> articles = metas.getData();
      for(int i = 0; i < articles.size(); i++) {
        String id  = articles.get(i).getMeta().getId();
        deleteDataByMeta(id, false);
        ids.add(id);
      }
      if(ids.size() < 1) break;
      DatabaseService.getDelete().deleteArticle(ids.toArray(new String[ids.size()]));
      page++;
    }
  }

  void deleteNoConstraintData() {
//    SourceLogUtils log = new SourceLogUtils();
    String [] dates = EIDFolder2.loadDates();//log.loadDate();
    if(dates.length < 1) return;
    try {
      Date date = CalendarUtils.getDateFormat().parse(dates[dates.length-1]);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 3);
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
      String value = dateFormat.format(calendar.getTime());
      DatabaseService.getDelete().deleteNoConstraintData(value);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}

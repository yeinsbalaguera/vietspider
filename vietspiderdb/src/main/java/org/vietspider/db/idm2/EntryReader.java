/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.idm2;

import java.io.File;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.paging.Entry;
import org.vietspider.paging.EntryFilter;
import org.vietspider.paging.MetaIdEntry;
import org.vietspider.paging.PageIO;
import org.vietspider.paging.PageIOs;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 8, 2008  
 */
public class EntryReader {
  
  public EntryReader() {
  }
  
  public void read(IEntryDomain domain, MetaList metas, int filter) throws Exception {
    read(domain, metas, filter, Article.NORMAL);
  }
  
  public void read(IEntryDomain domain, MetaList metas, int filter, short mode) throws Exception {
    List<MetaIdEntry> entries = readData(domain, metas, filter/*page_, metas.getTotalPage()*/);
    
//    for(int i = 0; i < entries.size(); i++) {
//      System.out.println(" Luc truoc doc tai day co " + entries.get(i).getMetaId());
//    }
    
//    System.out.println(" da co o day roi "+ domain.getFile().getName());
    
    int size = entries.size();
    int page_ = metas.getCurrentPage();
    while(size < 10 && page_ < metas.getTotalPage()) {
      metas.setCurrentPage(page_+1);
      entries.addAll(readData(domain, metas, filter));
      size = entries.size();
      page_ = metas.getCurrentPage();
    }
    
    if(entries.size() < 1) return;
    
    String [] metaIds = new String[entries.size()];
    for(int i = 0; i < entries.size(); i++) {
//      System.out.println(" doc tai day co " + entries.get(i).getMetaId());
      metaIds[i] = String.valueOf(entries.get(i).getMetaId());
    }
    
    DatabaseReader getter = DatabaseService.getLoader();
//    for(int i = 0; i < metaIds.length; i++) {
//      System.out.println(" doc tai day co " + metaIds[i]);
//    }
    metas.getData().clear();
    metas.setData(getter.loadArticles(metaIds, mode));
    
    //filter:1
    List<Article> articles = metas.getData();
    
    if(size < 1) return;
    if(size == entries.size()) {
      for(int i = 0; i < Math.min(size, articles.size()); i++) {
        Article article = articles.get(i);
        if(article == null) continue;
        article.setStatus(entries.get(i).getStatus());
      }
      return;
    } 
    
    for(int i = 0; i < size; i++) {
      Article article = articles.get(i);
      Entry entryID = searchEntry(article.getMeta().getId(), entries);
      if(entryID == null) continue;
      article.setStatus(entryID.getStatus());
    }
//    System.out.println(" truoc khi thu nghiem "+ metas.getData().size()+ "  trong khi "+ metaIds.length);
  }
  
  public String [] readMetaIds(IEntryDomain domain, MetaList metas, int filter) throws Exception {
    List<MetaIdEntry> entries = readData(domain, metas, filter);
    //    System.out.println(" da co o day roi "+ entries.size());

    int size = entries.size();
    int page_ = metas.getCurrentPage();
    while(size < 10 && page_ < metas.getTotalPage()) {
      metas.setCurrentPage(page_+1);
      entries.addAll(readData(domain, metas, filter));
      size = entries.size();
      page_ = metas.getCurrentPage();
    }

    if(entries.size() < 1) return new String[0];

    String [] metaIds = new String[entries.size()];
    for(int i = 0; i < entries.size(); i++) {
      metaIds[i] = String.valueOf(entries.get(i).getMetaId());
    }

    return metaIds;
    //    System.out.println(" truoc khi thu nghiem "+ metas.getData().size()+ "  trong khi "+ metaIds.length);
  }
  
  private Entry searchEntry(String metaId, List<MetaIdEntry> entries) {
    long value = Long.parseLong(metaId);
    for(int i = 0; i < entries.size(); i++) {
      if(value == entries.get(i).getMetaId()) return entries.get(i);
    }
    return null;
  }
  
  public int getTotalPage(IEntryDomain domain, int filter, int pageSize) {
    PageIO<MetaIdEntry> io = getPageIO(domain);
    return io.getTotalPage(pageSize);
  }
  
  public List<MetaIdEntry> readData(IEntryDomain domain, MetaList metas, final int filter) {
    PageIO<MetaIdEntry> io = getPageIO(domain);
    
    metas.setTotalPage(io.getTotalPage(metas.getPageSize()));
    int page = metas.getCurrentPage();
    
    if(page < 1) {
      page = 1;
      metas.setCurrentPage(page);
    } 
    
    if(page > metas.getTotalPage()) {
      page = metas.getTotalPage();
      metas.setTotalPage(page);
    }
    
    if(filter < 0) {
      return io.loadPageByDesc(page, metas.getPageSize(), null);
    } 
    
    return io.loadPageByDesc(metas.getCurrentPage(), metas.getPageSize(), new EntryFilter() {
      public boolean validate(Entry entry) {
        return entry.getStatus() == filter;
      }
    });
  }
  
  @SuppressWarnings("unchecked")
  public PageIO<MetaIdEntry>  getPageIO(IEntryDomain domain) {
    File file = domain.getFile();//EIDFolder2.getFileName(domain);
    PageIO<MetaIdEntry> io = (PageIO<MetaIdEntry>)PageIOs.getInstance().getPageIO(file);
    if(io == null) {
      io = new PageIO<MetaIdEntry>() {
        public MetaIdEntry createEntry() {
          return new MetaIdEntry();
        }
      };
      io.setData(file, 8);
      PageIOs.getInstance().putPageIO(file, io);
    }
    return io;
  }
}

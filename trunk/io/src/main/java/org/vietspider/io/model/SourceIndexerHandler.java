/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.model;

import java.io.File;
import java.net.URL;
import java.util.Calendar;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.vietspider.bean.website.Website;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.model.Source;
import org.vietspider.model.XML2Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 28, 2008  
 */
abstract class SourceIndexerHandler {

  protected Source loadSource(File file ){
    if(!file.exists()) return null;
    try {
      byte [] bytes = RWData.getInstance().load(file);
      if(bytes.length < 1) return null;
      
      XML2Source xml2Source = new XML2Source();
      return xml2Source.toSource(bytes);
      
//      String xml = new String(bytes, Application.CHARSET);
//      XMLDocument document = XMLParser.createDocument(xml, null);
//      return XML2Object.getInstance().toObject(Source.class, document);
    } catch (Exception exp) {
      LogService.getInstance().setThrowable(exp);
    }
    return null;
  }

  protected Document toDocument(String id, Source source) {
    Document doc = new Document();
    Field field = new Field(SourceIndexerService.ID, id, Field.Store.YES, Field.Index.NOT_ANALYZED);
    doc.add(field);
    field = new Field(SourceIndexerService.GROUP, source.getGroup(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    doc.add(field);
    field = new Field(SourceIndexerService.CATEGORY, source.getCategory(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    doc.add(field);
    field = new Field(SourceIndexerService.NAME, source.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    doc.add(field);
    field = new Field(SourceIndexerService.STATUS, "1", Field.Store.YES, Field.Index.NOT_ANALYZED);
    doc.add(field);
    String [] addresses = source.getHome();
    if(addresses == null) return null;
    for(int i = 0; i < addresses.length; i++) {
      field = new Field(SourceIndexerService.URL, addresses[i], Field.Store.YES, Field.Index.NOT_ANALYZED);
      doc.add(field);
    }

    try {
      URL url  = new URL(addresses[0]);
      String [] elements = url.getHost().split("\\.");
      for(int i = 0; i < elements.length; i++) {
        field = new Field(SourceIndexerService.HOST, elements[i], Field.Store.YES, Field.Index.NOT_ANALYZED);
        doc.add(field);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    Calendar calendar = Calendar.getInstance();
    String date = CalendarUtils.getDateTimeFormat().format(calendar.getTime());
    field = new Field(SourceIndexerService.DATE, date, Field.Store.YES, Field.Index.NOT_ANALYZED);
    doc.add(field);

    return doc;
  }

  protected IndexWriter createIndexModifier(boolean recursive) {
    File file = UtilFile.getFolder("sources/index/");

//  try {
//  Directory directory  = FSDirectory.getDirectory(file, true);
//  if(IndexReader.isLocked(directory)) IndexReader.unlock(directory) ;
//  } catch (Exception e) {
//  LogService.getInstance().setThrowable(e);
//  }  

    MaxFieldLength mfl = new IndexWriter.MaxFieldLength(IndexWriter.DEFAULT_MAX_FIELD_LENGTH);
    if(file.listFiles().length > 0) {
      try {
        Directory directory  = FSDirectory.open(file);
        return new IndexWriter(directory, new WhitespaceAnalyzer(), false, mfl);
      } catch (LockObtainFailedException e) {
        try {
          Directory directory  = FSDirectory.open(file);
          if(IndexWriter.isLocked(directory)) IndexWriter.unlock(directory) ;
          if(recursive) return createIndexModifier(false); 
        } catch (Exception e2) {
          LogService.getInstance().setThrowable(e);
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        return null;
      }   
    } 

    try {
      Directory directory  = FSDirectory.open(file);
      return new IndexWriter(directory, new WhitespaceAnalyzer(), true, mfl);
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  Website createWebsite(String address, String lang, int status) {
    Website website = new Website();
    website.setAddress(address);
    
    website.setLanguage(lang);
    website.setStatus(status);
    return website;
  }
}

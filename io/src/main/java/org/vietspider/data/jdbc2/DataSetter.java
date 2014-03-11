/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.data.jdbc.DBConnections;
import org.vietspider.data.jdbc.JdbcConnection;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.DatabaseWriter;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class DataSetter implements DatabaseWriter {

  public DataSetter()  {
  }
  
  public void save(Article article) throws Exception {
//    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
//      SyncDbService.getService().save(article);
//    }
    save1(article.getMeta(), article.getDomain(), article.getContent());
    
    List<Image> images = article.getImages();
    for(int i = 0; i < images.size(); i++) {
      try {
        save(images.get(i));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  public void saveAs(Article article) throws Exception {
    save1(article.getMeta(), article.getDomain(), article.getContent());
  }

  @Deprecated()
  public void save(Meta meta, Domain domain, Content content) throws Exception {
    save1(meta, domain, content);
  }

  private void save1(Meta meta, Domain domain, Content content) throws Exception {
    JdbcService.getInstance().save(domain);
    JdbcService.getInstance().save(meta);
    JdbcService.getInstance().save(content);
  }

  public void set(Content content) throws Exception {
    JdbcService.getInstance().updateStatus(content);
  }

  public void save(Image image) throws Exception {
    JdbcService.getInstance().save(image);
  }

  public void save(Relation relation) throws Exception {
    JdbcService.getInstance().save(relation);
  }

  public void save(List<Relation> relations) throws Exception {
    if(relations == null) return;
    for(Relation relation : relations) {
      JdbcService.getInstance().save(relation);
    }
  }
  
  public void save(String metaId, String title, String desc, String newContent)  throws Exception {
    String updateTitle = "UPDATE META SET TITLE = ";
    String updateDesc = "UPDATE META SET DES = ";
    
    if(JdbcConnection.isType(DBConnections.MYSQL) 
        || JdbcConnection.isType(DBConnections.SQLSERVER)) {
      updateTitle += "N'" + title + "' WHERE ID = " + metaId;
      updateDesc += "N'" + desc  + "' WHERE ID = " + metaId;
    } else {
      updateTitle += "'" + title + "' WHERE ID = " + metaId;
      updateDesc += "'" + desc  + "' WHERE ID = " + metaId;
    }
    DatabaseService.getUtil().execute(updateTitle, updateDesc);
    
    if(newContent == null) return;
    
    DatabaseService.getDelete().deleteContent(metaId);
    
    Calendar calendar = Calendar.getInstance();
    String date = CalendarUtils.getDateFormat().format(calendar.getTime());
    Content content = new Content(metaId, date, newContent);

    DatabaseService.getSaver().save(new Article(null, null, content));
  }
  
}

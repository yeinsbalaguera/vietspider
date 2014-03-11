/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.NLPRecord;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public interface IArticleDatabase {

  public void delete(long id) throws Throwable ;

  public boolean isDelete(long id) throws Throwable ;
  
  public boolean contains(long id) ;

  public void save(Article article) throws Throwable;

//  void sync() throws Throwable;

  public void save(Content content) throws Throwable  ;
  
  public void save(Relation relation) throws Throwable ;
  
  public void save(Image image) throws Throwable;
  
  public void save(List<Relation> list) throws Throwable  ;

  public Article loadArticle(String metaId) throws Throwable ;

  public Article loadArticle(long id, short type) throws Throwable;

  public Meta loadMeta(String metaId) throws Throwable;

  public Relations loadRelations(String metaId) throws Throwable ;

  public void loadMetaRelation(Article article, long id) throws Throwable ;
  
  public List<MetaRelation> loadShortMetaRelation(long id) throws Throwable;
  
  public Image loadImage(String id) throws Throwable ;

  public Domain loadDomain(long id) throws Throwable ;
  
  public long getLastAccess() ;

  public boolean isClose() ;

  public void close() ;

  @SuppressWarnings("serial")
  public static class CloseDatabaseException extends Exception {
  }
  
  public String getFolder() ;

  public IArticleIterator getIterator() ;


}

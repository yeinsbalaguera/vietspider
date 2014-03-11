/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public interface DatabaseWriter {
  
//  public void save(Domain domain)throws Exception ;
//  public void save(Meta meta) throws Exception ;
//  public void save(Content content) throws Exception ;
  
  @Deprecated()
  public void save(Meta meta, Domain domain, Content content) throws Exception;
  
  public void save(Article article) throws Exception;
  
  public void saveAs(Article article) throws Exception;
  
  public void set(Content content) throws Exception ;
 
  public void save(Relation relation) throws Exception ;
  
  public void save(List<Relation> relations) throws Exception ;
  
  public void save(Image image) throws Exception ;
  
  public void save(String metaId, String title, String desc, String content)  throws Exception;
  
}

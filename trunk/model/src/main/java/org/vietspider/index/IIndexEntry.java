/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import java.io.File;
import java.io.Serializable;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 28, 2008  
 */
public abstract class IIndexEntry implements Serializable {
  
  private final static long serialVersionUID = -7849013235l;

  final public static String FIELD_ID      = "id" ;
  final public static String FIELD_DATE    = "date" ;
  final public static String FIELD_TITLE   = "title" ;
  final public static String FIELD_TITLE_INDEX   = "title_index" ;

  final public static int DELETE = 0, ADD = 1;

  protected String id;
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  protected String date;
  public String getDate()  { return date; }
  public void setDate(String date) { this.date = date; }

  protected int status = ADD;
  public int getStatus() { return status; }
  public void setStatus(int status) { this.status = status; }

  public abstract void fromDocument(Document document);
  public abstract Document toDocument();
  
  protected File folder;
  public File getFolder() { return folder; }
  public void setFolder(File folder) { this.folder = folder; }

  protected int expireDate;
  public int getExpireDate() { return expireDate; }
  public void setExpireDate(int value) { this.expireDate = value; }
  
  public Analyzer getAnalyzer() { return new WhitespaceAnalyzer(); }

}

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2009  
 */
public abstract class ITextIndex extends IIndexEntry {
  
  private final static long serialVersionUID = -46263035l;
  
  protected String title = "";
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  protected Document createDocument() { return createDocument(2.0f); }
  protected Document createDocument(float titleBoot) {
    Document doc = new Document();

    doc.add(new Field(FIELD_ID, id, Field.Store.YES, Field.Index.NOT_ANALYZED));
    doc.add(new Field(FIELD_DATE, date, Field.Store.YES, Field.Index.NOT_ANALYZED));
    if(title == null) return doc;

    doc.add(new Field(FIELD_TITLE, title, Field.Store.YES, Field.Index.NOT_ANALYZED));

    Field titleField = new Field(FIELD_TITLE_INDEX, title.toLowerCase(), Field.Store.YES, Field.Index.ANALYZED) ;
    titleField.setBoost(titleBoot) ;//
    doc.add(titleField);

    return doc;
  }

  
}

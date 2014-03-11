/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Meta;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 11, 2007
 */
public class DefaultDataUtils  implements DatabaseUtils {

  public DefaultDataUtils()  {
  }

  @SuppressWarnings("unchecked")
  public void loadEvent(String date, MetaList data) throws Exception {
  }

  @SuppressWarnings("unchecked")
  public void searchMeta(String pattern, MetaList data) throws Exception {
  } 

  public List<Meta> loadTopEvent() throws Exception {
    return new ArrayList<Meta>();
  }

  @SuppressWarnings("unchecked")
  public void loadTopArticles(int top, MetaList list) throws Exception {
   
  }
  
  public void execute(String...sqls) throws Exception  {
  }
  

}

/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.remote;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.db.database.DatabaseUtils;
import org.vietspider.db.database.MetaList;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 11, 2007
 */
public class RemoteDataUtils implements DatabaseUtils {

  @SuppressWarnings("unused")
  public void execute(String... sqls) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void loadEvent(String _date, MetaList data) throws Exception {
  }

  @SuppressWarnings("unused")
  public void loadTopArticles(int top, MetaList list) throws Exception {
  }

  @Override
  public List<Meta> loadTopEvent() throws Exception {
    List<Meta> metas = new ArrayList<Meta>();
    return metas;
  }

  @SuppressWarnings("unused")
  public void searchMeta(String pattern, MetaList list) throws Exception {
  }

}

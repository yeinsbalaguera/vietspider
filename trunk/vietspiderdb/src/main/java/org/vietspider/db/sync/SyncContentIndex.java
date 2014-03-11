/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.sync;

import java.util.ArrayList;

import org.vietspider.index.ITextIndex;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 22, 2009  
 */
public class SyncContentIndex extends SyncHandler<ITextIndex> {
  
  public SyncContentIndex() {
    super("content_index", "sync.content.index.server",
        "add.content.index", URLPath.REMOTE_DATA_HANDLER);
  }

  @Override
  public void removeSuccessfullValue(ArrayList<ITextIndex> list, String id) {
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getId().equals(id)) {
        list.remove(i);
        return;
      }
    }
    return;
  }
  
  

}

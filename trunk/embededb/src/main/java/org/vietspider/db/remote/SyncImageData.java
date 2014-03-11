/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.remote;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.Header;
import org.vietspider.bean.Image;
import org.vietspider.db.sync.SyncHandler;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 10, 2009  
 */
public class SyncImageData extends SyncHandler<Image> {
  
  //sync.data.server=localhost:9245
  //action  = add.image
  //cached folder = article
  public SyncImageData() {
    super("image", "sync.data.server", "add.image", URLPath.REMOTE_DATA_HANDLER);
  }
  
  public Object load(String id, String action, Header...headers) {
    Iterator<Image> iterator = queue.iterator();
    while(iterator.hasNext()) {
      Image value = iterator.next();
      if(value.getId().equals(id)) return value;
    }
    return super.load(id, action, headers);
  }
  
  public void removeSuccessfullValue(ArrayList<Image> list, String id) {
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getId().equals(id)) {
        list.remove(i);
        return;
      }
    }
    return;
  }
  
  
}

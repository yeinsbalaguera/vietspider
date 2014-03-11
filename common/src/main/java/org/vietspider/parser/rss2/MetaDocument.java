/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2007  
 */
public class MetaDocument {

  private IMetaChannel channel;
  
  private List<IMetaItem> items = new ArrayList<IMetaItem>();

  public IMetaChannel getChannel() { return channel; }
  public void setChannel(IMetaChannel channel) { this.channel = channel; }

  public List<IMetaItem> getItems() { return items; }
  public void setItems(List<IMetaItem> items) { this.items = items; }
  public void addItem(IMetaItem item) { items.add(item); }
  public IMetaItem getItem(int index) {
    if(index < 0 || index > items.size()) return null;
    return items.get(index);
  }
  
}

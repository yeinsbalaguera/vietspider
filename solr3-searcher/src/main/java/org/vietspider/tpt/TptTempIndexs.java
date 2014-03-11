/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vietspider.solr2.common.TempQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 9, 2011  
 */
public class TptTempIndexs extends TempQueue<TptIndex> {
  
  public TptTempIndexs() {
    super("content/solr2/tpt_temp/");
  }
  
  TptIndex loadTemp(long id) {
    Iterator<TptIndex> iterator = queue.iterator();
    while(iterator.hasNext()) {
      TptIndex temp = iterator.next();
      if(temp.getId() == id) return temp;
    }
    return null;
  }
  
  void searchByPhone(Collection<String> set, List<String> phones) {
    Iterator<TptIndex> iterator = queue.iterator();
    while(iterator.hasNext()) {
      TptIndex temp = iterator.next();
      for(int i = 0; i < phones.size(); i++) {
        if(!temp.getPhones().contains(phones.get(i))) continue;
        String id = String.valueOf(temp.getId());
        if(!set.contains(id)) set.add(id);
        break;
      }
    }
  }
  
  void searchByPhone(Collection<String> set, String phone) {
    Iterator<TptIndex> iterator = queue.iterator();
    while(iterator.hasNext()) {
      TptIndex temp = iterator.next();
      if(temp.getPhones().contains(phone)) {
        set.add(String.valueOf(temp.getId()));
      }
    }
  }
  
  void searchByEmail(Collection<String> set, List<String> emails) {
    Iterator<TptIndex> iterator = queue.iterator();
    while(iterator.hasNext()) {
      TptIndex temp = iterator.next();
      for(int i = 0; i < emails.size(); i++) {
        if(!temp.getEmails().contains(emails.get(i))) continue;
        String id = String.valueOf(temp.getId());
        if(!set.contains(id)) set.add(id);
        break;
      }
    }
  }

}

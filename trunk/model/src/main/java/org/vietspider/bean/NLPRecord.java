/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 20, 2009  
 */
@NodeMap("nlp-record")
public class NLPRecord implements Serializable {
  
  private final static long serialVersionUID = -5623063235l;
  
  @NodeMap("meta-id") 
  private String metaId;
  
  @NodesMap(value = "records", item = "item")
  private List<NLPRecordItem> items = new ArrayList<NLPRecordItem>();
  
  @NodesMap(value = "datas", item = "item")
  private List<NLPData> datas = new ArrayList<NLPData>();
  
  public NLPRecord() {
  }
  
  public NLPRecord(String metaId) {
   this.metaId = metaId; 
  }
  
  public String getMetaId() { return metaId; }
  public void setMetaId(String metaId) { this.metaId = metaId; }

  public List<NLPRecordItem> getItems() { return items; }
  @SetterMap("records")
  public void setValues(List<NLPRecordItem> _items) { this.items = _items;  }
  
  public List<NLPData> getDatas() { return datas; }
  public void setDatas(List<NLPData> _datas) { this.datas = _datas;  }
  
  public List<String> getData(short _type) {
    for(int i = 0; i < datas.size(); i++) {
      if(datas.get(i) == null 
          || datas.get(i).getType() != _type) continue;
      if(datas.get(i).getValues() == null) {
        return new ArrayList<String>();
      }
      return datas.get(i).getValues();
    }
    return new ArrayList<String>();
  }
  
  public boolean hasData(short _type) {
    for(int i = 0; i < datas.size(); i++) {
      if(datas.get(i) == null 
          || datas.get(i).getType() != _type) continue;
      return datas.get(i).getValues() != null && 
              datas.get(i).getValues().size() > 0;
    }
    return false;
  }
  
  public String getOneData(short _type) {
    for(int i = 0; i < datas.size(); i++) {
      if(datas.get(i).getType() != _type) continue;
      List<String> list = datas.get(i).getValues();
      if(list.size() > 0)  return list.get(0);
      return null;
    }
    return null;
  }
  
  public void setData(short _type, String...values) {
    for(int i = 0; i < datas.size(); i++) {
      if(datas.get(i).getType() != _type) continue;
      List<String> list = datas.get(i).getValues();
      list.clear();
      Collections.addAll(list, values);
      return;
    }
    datas.add(new NLPData(_type, values));
  }
  
  public void setData(short _type, Collection<?>values) {
    for(int i = 0; i < datas.size(); i++) {
      if(datas.get(i).getType() != _type) continue;
      List<String> list = datas.get(i).getValues();
      list.clear();
      for(Object ele : values) {
        list.add(ele.toString());
      }
      return;
    }
    datas.add(new NLPData(_type, values));
  }
  
  public String getData(String _type, String item) {
    for(int i = 0; i < items.size(); i++) {
      String data = items.get(i).getData(_type, item);
      if(data != null) return data;
    }
    return null;
  }
  
  public void setData(String _type, String item, String value) {
    for(int i = 0; i < items.size(); i++) {
      if(items.get(i).setData(_type, item, value)) break;
    }
  }
  
}

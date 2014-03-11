/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.link.track;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 7, 2010  
 */
public class LinkItem {
  
  private static final long serialVersionUID = 1L;

  private int code;
  private int type;
  private long size;
  private int time = 1;
  private long metaId = -1;

  public LinkItem(int code, int type, long size) {
    this.code = code;
    this.type = type;
    this.size = size;
  }

  public int getType() { return type; }
  public void setType(int type) { this.type = type; }
  
  public long getSize() { return size; }
  public void setSize(long size) { this.size = size; }
  
  public long getMetaId() { return metaId; }
  public void setMetaId(long metaId) { this.metaId = metaId; }

  void categorize(HashMap<Integer, Integer> types) {
    int value = types.get(type);
    types.put(type, value+time);
  }

  public int getCode() { return code; }
  public void setCode(int code) { this.code = code; }
  
  public void write(DataOutputStream stream) throws Exception {
    stream.writeInt(code);
    stream.writeInt(type);
    stream.writeLong(size);
    stream.writeInt(time);
    stream.writeLong(metaId);
    stream.flush();
  }
  
  public long read(DataInputStream stream, long pos, long length) throws Exception {
    return pos;
  }
  
}

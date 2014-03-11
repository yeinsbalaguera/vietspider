/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.link.track;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 26, 2010  
 */
public class SourceTracker implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String fullName;
  private Long storageId;
  private int id;

  private List<Message> messages = new ArrayList<Message>();

  SourceTracker(LinkLog log) {
    fullName = log.getChannel();
    this.id = fullName.hashCode();
    messages.add(new Message(log));
  }
  
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  Long getStorageId() { return storageId; }

  void setStorageId(Long storageId) { this.storageId = storageId; }

  void add(LinkLog log) {
    String message = log.getMessage();
    for(int i = 0; i < messages.size(); i++) {
      if(messages.get(i).message.equals(message)) {
        messages.get(i).add(log);
        return;
      }
    }
    messages.add(new Message(log));
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();

    String [] elements = fullName.split("\\.");
    builder.append(elements[2]).append('.');
    builder.append(elements[1]).append('.');
    builder.append(elements[0]);

    Message max = null;

    double score = 0;
    long size = 0;

    HashMap<Integer, Integer> types = new HashMap<Integer, Integer>();
    types.put(LinkLog.TYPE_DATA, 0);
    types.put(LinkLog.TYPE_HOME, 0);
    types.put(LinkLog.TYPE_INVALID, 0);
    types.put(LinkLog.TYPE_LINK, 0);
    types.put(LinkLog.TYPE_SCRIPT, 0);

    int data = 0;

    for(int i = 0; i < messages.size(); i++) {
      if(max == null 
          || messages.get(i).items.size() > max.items.size()) {
        max = messages.get(i);
      }

      score += getScore(messages.get(i).message)*messages.get(i).time();
      size += messages.get(i).size();
      messages.get(i).categorize(types);

//      System.out.println("|" + messages.get(i).message+ "|");
      if("done".equals(messages.get(i).message)) data += messages.get(i).time();
    }

    builder.append('|').append(score);
    builder.append('|').append(max != null ? max.message : "");
    builder.append('|').append(data);

    builder.append('|').append(types.get(LinkLog.TYPE_HOME));
    builder.append('|').append(types.get(LinkLog.TYPE_LINK));
    builder.append('|').append(types.get(LinkLog.TYPE_DATA));
    builder.append('|').append(types.get(LinkLog.TYPE_INVALID));
    builder.append('|').append(types.get(LinkLog.TYPE_SCRIPT));

    builder.append('|').append(size);

    return builder.toString();
  }

  double getScore(String message) {
    if("{link.downloaded}".equals(message)) return 1;
    if("done".equals(message)) return 1;
    //      if("".equals(message)) return 1;
    //      if("".equals(message)) return 1;
    //      if("".equals(message)) return 1;
    //      if("".equals(message)) return 1;
    //      if("".equals(message)) return 1;

    if("{out.of.depth.downloaded}".equals(message))  return -0.5;
    if("{out.of.depth}".equals(message)) return -0.5;
    if("{data.length.incorrect}".equals(message))  return -0.5;
    //      if("".equals(message))  return -0.5;
    //      if("".equals(message))  return -0.5;
    //      if("".equals(message))  return -0.5;
    //      if("".equals(message))  return -0.5;
    //      if("".equals(message))  return -0.5;


    if("{document.is.empty}".equals(message)) return -1;
    if("{http.code.404}".equals(message)) return -1;
    if("{empty.link}".equals(message)) return -1;
    if("{row.extract.document.not.found}".equals(message)) return -1;
    if("{not.found.response}".equals(message)) return -1;
    if("{not.found.data}".equals(message)) return -1;
    //      if("".equals(message)) return -1;
    //    if("".equals(message)) return -1;
    //    if("".equals(message)) return -1;
    //    if("".equals(message)) return -1;

    return 0;
  }
  
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  private static class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    private List<Item> items = new ArrayList<Item>();
    
    Message(LinkLog log) {
      message = log.getMessage().trim();
      items.add(new Item(log.getUrlCode(), log.getType(), log.getBytes()));
    }

    void add(LinkLog log) {
      int code = log.getUrlCode();
      for(int i = 0; i < items.size(); i++) {
        if(items.get(i).code == code) {
          items.get(i).time++;
          items.get(i).size += log.getBytes();
          return;
        }
      }
      items.add(new Item(code, log.getType(), log.getBytes()));
    }

    long size() {
      long size = 0;
      for(int i = 0; i < items.size(); i++) {
        size += items.get(i).size;
      }
      return size;
    }

    void categorize(HashMap<Integer, Integer> types) {
      for(int i = 0; i < items.size(); i++) {
        items.get(i).categorize(types);
      }
    }

    int time() {
      int time = 0;
      for(int i = 0; i < items.size(); i++) {
        time += items.get(i).time;
      }
      return time;
    }

  }


  public static class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private int type;
    private long size;
    private int time = 1;
    private String metaId = null;

    Item(int code, int type, long size) {
      this.code = code;
      this.type = type;
      this.size = size;
    }

    public int getType() {
      return type;
    }
    public void setType(int type) {
      this.type = type;
    }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public String getMetaId() { return metaId; }
    public void setMetaId(String metaId) { this.metaId = metaId; }

    void categorize(HashMap<Integer, Integer> types) {
      int value = types.get(type);
      types.put(type, value+time);
    }
  }

}




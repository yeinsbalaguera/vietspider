/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.BooleanQuery;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2009  
 */
@SuppressWarnings("serial")
public abstract class CommonSearchQuery implements Serializable {

  @NodeMap("pattern")
  protected String pattern;
  @NodeMap("lpattern")
  protected String lpattern;
  @NodeMap("epattern")
  protected String epattern;
  
  @NodeMap("region")
  protected String region;
  
  @NodeMap("owner")
  protected boolean owner = false;

  @NodeMap("price")
  protected String price;
  
  @NodeMap("index-from")
  protected long indexFrom = -1;
  @NodeMap("index-to")
  protected long indexTo = -1;
  @NodeMap("date")
  protected int date = -1;

  @NodesMap(value = "history", item = "item")
  protected List<String> history;

  @NodeMap(value  = "highlight-start", cdata = true)
  protected String highlightStart = "<b>";
  @NodeMap(value  =  "highlight-end", cdata = true)
  protected String highlightEnd = "</b>";

  //  protected boolean refresh = false;

  @NodeMap("article-id")
  protected String articleId;

  @NodeMap("code")
  protected int code = -1;
  @NodeMap("time")
  protected long time = 0;

  @NodeMap("total")
  protected long total = 0;

  @NodesMap(value = "actions", item = "action")
  private String [] actions;
  
  private Map<String, Object> properties = new HashMap<String, Object>();

  public CommonSearchQuery() {
  }

  public CommonSearchQuery(String pattern) {
    this();
    setPattern(pattern);
  }

 /* private void loadActions() {
    File file  = new File(UtilFile.getFolder("system"), "search.action");
    if(!file.exists() || file.length() < 1) return;
    try {
      String value = new String(RWData.getInstance().load(file), "utf-8");
      if((value = value.trim()).length() < 1) return;
      actions = value.split("\n");
    } catch (Exception e) {
      actions = new String[0];
    }
  }*/

  public String getPattern() {return pattern; }
  public void setPattern(String pattern) { 
    this.pattern = pattern;
  }

  public String getPrice() { return price; }
  public void setPrice(String price) { this.price = price; }

  public String getRegion() { return region; }
  @SuppressWarnings("unchecked")
  public List<String> getRegions() {
    if(region == null) return null;
    List<String> elements = (List<String>)properties.get("regions");
    if(elements != null) return elements;
    TextSpliter spliter = new TextSpliter();
    elements = spliter.toList(region, ',');
    properties.put("regions", elements);
    return elements; 
  }
  public void setRegion(String region) {
    if(region != null && region.trim().isEmpty()) return;
    this.region = region; 
  }

  public int getDate() { return date; }
  public void setDate(int date) {
    if(date == 1) {
      Calendar calendar = Calendar.getInstance();
      indexTo  = calendar.getTimeInMillis();
//      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      indexFrom = calendar.getTimeInMillis();
    } else if(date > 1) {
      Calendar calendar = Calendar.getInstance();
      indexTo  = calendar.getTimeInMillis();
      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - date);
      indexFrom = calendar.getTimeInMillis();
    }
    
    this.date = date; 
  }
  
  public long getIndexTo() { return indexTo; }
  public void setIndexTo(long to) { this.indexTo = to; }

  public long getIndexFrom() { return indexFrom; }
  public void setIndexFrom(long from) { this.indexFrom = from; }

  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }

  public String[] getActions() { return actions; }
  public void setActions(String[] _actions) {
    if(_actions != null && _actions.length == 0) _actions = null;
    this.actions = _actions;
  }
  public boolean containsAction(String action) {
    if(actions == null || action == null) return false;
    for(int i = 0; i < actions.length; i++) {
      if(actions[i].equals(action)) return true;
    }
    return false;
  }

  public String getLPattern() {
    if(lpattern == null) lpattern = pattern.toLowerCase(); 
    return lpattern; 
  }

  public String getEncodePattern() {
    if(epattern != null) return epattern;
    try {
      epattern = URLEncoder.encode(pattern, Application.CHARSET);
    } catch (Exception e) {
      epattern = pattern;
    }
    return epattern; 
  }

  public abstract BooleanQuery createQuery() throws Exception ;

  public abstract void savePattern();

  public String getArticleId() { return articleId; }
  public void setArticleId(String articleId) { this.articleId = articleId; }

  public List<String> getHistory() { return history;  }
  public void setHistory(List<String> history) { this.history = history; }

  public long getTotal() { return total; }
  public void setTotal(long total) { this.total = total; }

  public String getHighlightStart() { return highlightStart; }
  public void setHighlightStart(String highlightStart) { this.highlightStart = highlightStart;  }

  public String getHighlightEnd() { return highlightEnd; }
  public void setHighlightEnd(String highlightEnd) { this.highlightEnd = highlightEnd; }
  
  public boolean isOwner() { return owner; }
  public void setOwner(boolean owner) { this.owner = owner; }
  
  public Map<String, Object> getProperties() { return properties; }

  public int getCode() {
    if(code != -1) return code;
    StringBuilder builder = new StringBuilder(getLPattern());
    if(actions != null) {
      for(int i = 0; i < actions.length; i++) {
        if(actions[i] != null) builder.append('/').append(actions[i]);
      }
    }
//    if(region != null) builder.append('/').append(region);
    if(date > 0) builder.append('/').append(date);
    if(price != null) builder.append('/').append(price);
    code = builder.toString().hashCode();
    return code;
  }


}

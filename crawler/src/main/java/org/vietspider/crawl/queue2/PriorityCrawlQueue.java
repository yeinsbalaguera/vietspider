/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue2;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.io.access.SourceTrackerService;
import org.vietspider.crawl.queue.SourceQueueEntryReader;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.io.model.GroupIO;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;
import org.vietspider.pool.CrawlQueue;
import org.vietspider.pool.QueueEntry;
import org.vietspider.pool.SourceQueue;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2011  
 */
public class PriorityCrawlQueue extends CrawlQueue<String> implements Runnable {
  
  private List<Integer> crawlHours = new ArrayList<Integer>();

  public final static Comparator<Source> COMPARATOR = new Comparator<Source>() {
    public int compare(Source source1, Source source2) {
      long current = System.currentTimeMillis();
      long last1 = current - source1.getLastCrawledTime();
      long last2 = current - source2.getLastCrawledTime();
      long out = 12*60*60*1000l;
      if(last1 >= out && last2 < out) return -1;
      if(last1 < out && last2 >= out) return 1;
      return source1.getPriority() - source2.getPriority();
    }
  };

  private String [] lines;

  private PriorityQueueEntry queueEntry;
  private PrioritySourceQueue sourceQueue;
  private SourceQueueEntryReader entryReader;

  private boolean sleep = false;

  private long expire = 5*60*1000l;
  private int max = 30;
  private int totalWait = 50;

  private Groups groups;

  public PriorityCrawlQueue() {
    validateLicense();
    
    SystemProperties system = SystemProperties.getInstance();
    String value = system.getValue("crawler.crawl.hours");
    if(value != null) {
      String [] elements = value.split(",");
      for(int i = 0; i < elements.length; i++) {
        if((elements[i] = elements[i].trim()).isEmpty()) continue;
        try {
          crawlHours.add(Integer.parseInt(elements[i]));
        } catch (Exception e) {
        }
      }
    }
    
    try {
      value = system.getValue("weight.priority.executor");
      if(value != null && !value.trim().isEmpty()) {
        expire = Integer.parseInt(value)*60*1000l;
      }
    } catch (Exception e) {
      expire = 5*60*1000l;
    }
    
    if(Application.LICENSE == Install.PERSONAL) {
      expire = 30*1000l;
    }
    

    totalWait = getTotalWait();
    //    System.out.println(" ==== > " + totalWait);

    queueEntry = new PriorityQueueEntry();
    sourceQueue = new PrioritySourceQueue();

    entryReader = new SourceQueueEntryReader();

    this.groups = GroupIO.getInstance().loadGroups();

    new Thread(this).start();
  }

  public void run() {
    while(true) {
//      System.out.println(" chay vao day " + crawlHours.size());
      if(crawlHours.size() > 0) {
        Calendar calendar = Calendar.getInstance(); 
//        CrawlPool pool = CrawlService.getInstance().getThreadPool();
//        if(pool != null && pool.getExecutors() != null) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        System.out.println(" ===  >"+ hour);
        if(crawlHours.contains(hour)) {
          if(Application.containsError(PriorityCrawlQueue.this)) {
            LogService.getInstance().setMessage(null, "Source queue continue by crawl time schedule");
            Application.removeError(PriorityCrawlQueue.this);
          }
        } else {
          if(!Application.containsError(PriorityCrawlQueue.this)) {
            LogService.getInstance().setMessage(null, "Source queue pause by crawl time schedule");
            Application.addError(PriorityCrawlQueue.this);
          }
        }
//        }
      }
      
//      System.out.println(queueEntry.size() + " : "+ totalWait);
      if(queueEntry.size() < totalWait) {
        loadFile();
        loadCrawlEntry();
      }
      
      // free version expire value will increase to 10 minutes.
      if(Application.LICENSE == Install.PERSONAL
          && expire < 15*60*1000l) {
        expire += 30*1000l;
//        System.out.println(" expire "+ expire);
      }
      
      try {
        Thread.sleep(30*1000l);
      } catch (Exception e) {
      }
    }
  }

  public void loadCrawlEntry() {
    List<Source> hights = new ArrayList<Source>();
    List<Source> commons = new ArrayList<Source>();
    long current = System.currentTimeMillis();
    for(int i = 0; i < Math.min(max, lines.length); i++) {
      Source source = loadSource(lines[i]);
      if(source == null) continue;
      SourceTrackerService tracker = SourceTrackerService.getInstance();
      long last = tracker.search(source);
//      System.out.println(source+  " : "+ lines[i]);
      long time = current - last;
      source.setLastCrawledTime(last);
      //      System.out.println(" ==== > "+ source.getFullName());

      String [] times = source.getCrawlTimes();
      //      System.out.println(times);
      if(times != null && times.length > 0) {
        //        System.out.println(source);
        //        for(String ele : times) {
        //          System.out.println(" crawl time: "+ ele);
        //        }

//        System.out.println(" time "+ time + " : "+ expire + " : "+ (time <= expire));

        if(time > expire) hights.add(source);
      } else {
        commons.add(source);
      }
      //      System.out.println(commons.size());
      //      System.out.println(line + " : "+ source);
    }
    
//    System.out.println(commons.size() + " : "+ hights.size());

    Collections.sort(hights, COMPARATOR);
    for(int i = hights.size() - 1; i > -1; i--) {
      CrawlSessionEntry[] entries = entryReader.createSessionEntry(hights.get(i));
//      System.out.println(hights.get(i).getFullName() +  " : "+ entries);
      if(entries == null) continue;
      if(queueEntry.size() > 1) {
        queueEntry.appendFirst(entries);
      }  else {
        for(CrawlSessionEntry entry : entries) {
          queueEntry.add(entry);
        }
      }
    }
    
//    System.out.println(queueEntry.size() + " : "+ totalWait);

    if(queueEntry.size() >= totalWait) return;

    Collections.sort(commons, COMPARATOR);
    for(int i = 0; i < commons.size(); i++) {
      CrawlSessionEntry[] entries = entryReader.createSessionEntry(commons.get(i));
      if(entries == null) continue;
      for(CrawlSessionEntry entry : entries) {
        queueEntry.add(entry);
      }
    }
//    System.out.println("buoc 2 " + queueEntry.size() + " : "+ totalWait);
  }

  private Source loadSource(String line) {
    int idx = line.indexOf('.');
    if(idx < 0) return null;
    //check group;
    String groupName = line.substring(0, idx);
    Calendar calendar = Calendar.getInstance();
    Group group = groups.getGroup(groupName);

    int startDownloadTime = group.getStartTime();
    int endDownloadTime = group.getEndTime();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);

    if(group.isDownloadInRangeTime()) {
      if(hour < startDownloadTime || hour > endDownloadTime)  {
        return null;
      }
    } else {
      if(hour >= startDownloadTime && hour <= endDownloadTime) {
        return null;
      }
    }

    Source source = SourceIO.getInstance().loadSource(line);
    if(source == null) {
      LinkLogStorages.getInstance().sourceNull(line);
      return null;
    }

    int priority = source.getPriority();

    String [] addresses = source.getHome();
    if(addresses == null || addresses.length < 1)  return null;

    if(priority < 0) {
      SourceIO.getInstance().putDisableSource(source);
      return null;
    }
    SourceIO.getInstance().removeDisableSource(source);

    if(isOutOfCrawlTime(source)) return null;

    return source;
  }

  private boolean isOutOfCrawlTime(Source source) {
    String [] crawlTimes = source.getCrawlTimes();
    if(crawlTimes == null || crawlTimes.length < 1) return false;
    //    System.out.println(" =----- > "+ source + " : "+ crawlTimes);
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    TextSpliter spliter = new TextSpliter();
    for(String time : crawlTimes) {
      String [] elements = spliter.toArray(time, '-');
      if(elements.length < 2) continue;
      try {
        int _min = Integer.parseInt(elements[0]);
        int _max = Integer.parseInt(elements[1]);
        if(_min == 0 && _max == 23) {
          source.setCrawlTimes(null);
          return false;
        }
        //        System.out.println(source.toString() + " : "  + _min + " : "+ hour + " : "+ _max);
        if(_min <= hour && hour <= _max) return false;
      } catch (Exception e) {
        LinkLogStorages.getInstance().save(source, e.toString());
      }
    }
    return true;
  }

  public QueueEntry<String> getQueueEntry() { return queueEntry; }

  public void removeElement(String key) {
    queueEntry.removeElement(key);
  }

  public CrawlSessionEntry nextEntry(int id) {
    CrawlSessionEntry entry = queueEntry.nextEntry(id);
    if(entry != null) return entry;
    return null;
  }

  public CrawlSessionEntry[] next(String line) { 
    return sourceQueue.next(line);
  }

  public SourceQueue getNormalQueue() { return sourceQueue;  }

  public boolean validate(Source source) {
    if(System.currentTimeMillis() - source.getLastCrawledTime() <= expire)  {
//      System.out.println(" expire source "+ expire);
      return false;
    }
    if(isOutOfCrawlTime(source)) return false;
    return true; 
  }

  @SuppressWarnings("unused")
  public void setCapacity(int capacity) {
  }

  public boolean isEmpty() { 
    return queueEntry.isEmpty(); 
  }

  public void appendFirst(CrawlSessionEntry [] entries) {
    queueEntry.appendFirst(entries);
  }

  public boolean isSleep() { return sleep ; }

  public void wake() {  }

  public boolean isInterrupted() { return Thread.currentThread().isInterrupted(); }

  public boolean isAlive() { return Thread.currentThread().isAlive(); }

  private void loadFile() {
    File file = UtilFile.getFile("system", "load");
    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      lines = CrawlQueue.split(text);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      lines = new String[0];
    }
    
    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      if(lines.length > 10) {
        expire = 15*60*1000l;
      } else if(lines.length >= 10 && lines.length < 30) {
        expire = 25*60*1000l;
      } else if(lines.length >= 30 && lines.length < 50) {
        expire = 30*60*1000l;
      } else if(lines.length >= 50 && lines.length < 70) {
        expire = 45*60*1000l;
      } else if(lines.length >= 70 && lines.length < 100) {
        expire = 60*60*1000l;
      } else if(lines.length >= 100 && lines.length < 200) {
        expire = 2*60*60*1000l;
      } else {
        expire = 3*60*60*1000l;
      }
    } 
  }

  private void validateLicense() {
    switch (Application.LICENSE) {
    case PERSONAL:
      max = 30;
      return;
    case PROFESSIONAL:
      max = 100;
      return;
    case ENTERPRISE:
      max = 1000;
      return;
    case SEARCH_SYSTEM:
      max = 5000;
      return;
    default:
      break;
    }
  }

  private int getTotalWait() {
    if(CrawlerConfig.TOTAL_EXECUTOR <= 3) {
      return 10;
    } else if(CrawlerConfig.TOTAL_EXECUTOR > 3
        && CrawlerConfig.TOTAL_EXECUTOR <= 5) {
      return 15;
    } else if(CrawlerConfig.TOTAL_EXECUTOR > 5
        && CrawlerConfig.TOTAL_EXECUTOR <= 10) {
      return 20;
    } else if(CrawlerConfig.TOTAL_EXECUTOR > 10
        && CrawlerConfig.TOTAL_EXECUTOR <= 15) {
      return 25;
    } else if(CrawlerConfig.TOTAL_EXECUTOR > 15
        && CrawlerConfig.TOTAL_EXECUTOR <= 25) {
      return 30;
    } else if(CrawlerConfig.TOTAL_EXECUTOR > 25
        && CrawlerConfig.TOTAL_EXECUTOR <= 30) {
      return 50;
    } else {
      return 100;
    }
  }

}

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.net.InetAddress;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2008  
 */

final public class CrawlerPoolPing implements Runnable {

  private volatile static CrawlerPoolPing poolPing;

  synchronized static public CrawlerPoolPing getInstance() { return poolPing; }

  static void createInstance() { poolPing = new CrawlerPoolPing(); }

  private volatile int time = 0;
  private volatile boolean pause = false;

  private CrawlerPoolPing() {
    new Thread(this).start();
  }

  public void run() {
    while(true) {
      //      System.out.println(" chay thay time "+ time);
      if(time > 10) {
        //        System.out.println(" chuan bi ping ");
        if(ping()) {
          //          System.out.println(" van okies ma ");
          time = 0;
          execute();
        } else {
          pause();
        }
      } 
      try {
        Thread.sleep(60*1000);
      } catch (Exception e) {
      }
    }
  }

  private void pause() {
//    org.vietspider.io.websites.WebsiteLanguageDetector.pauseService();

    /*CrawlService service = CrawlService.getInstance();
    if(service == null) return;*/
    Application.addError(CrawlerPoolPing.this);
    /*CrawlPool pool = service.getThreadPool();
    if(pool == null || pool.isPause()) return;                
    pool.setPause(true);*/
    pause = true;
  }

  private void execute() {
//    org.vietspider.io.websites.WebsiteLanguageDetector.continueService();

    if(!pause) return;
   /* CrawlService service = CrawlService.getInstance();
    if(service == null) return;
    CrawlPool pool = service.getThreadPool();
    if(pool == null || !pool.isPause()) return;
    pool.continueExecutors();*/
    Application.removeError(CrawlerPoolPing.this);
    pause = false;
  }

  /* boolean ping(CrawlSessionEntry entry) {
    String [] addresses = entry.getValue().getHome();
    if(addresses == null || addresses.length < 1) return false;
    try {
      String host  = new URL(addresses[0]).getHost();
      if(pinging(host)) {
        entry.setPingStatus(CrawlSessionEntry.LIVE);
        time = 0;
        return true;
      }
      InetAddress.getByName(host);
      entry.setPingStatus(CrawlSessionEntry.LIVE);
    } catch (MalformedURLException e) {
      LogService.getInstance().setMessage(entry.getValue(), e, e.toString());
      entry.setPingStatus(CrawlSessionEntry.DEAD);
      time++;
      return false;
    } catch (UnknownHostException e) {
      entry.setPingStatus(CrawlSessionEntry.DEAD);
      LogService.getInstance().setMessage(entry.getValue(), e, e.toString());
      time++;
      return false;
    } catch (Exception e) {
      entry.setPingStatus(CrawlSessionEntry.DEAD);
      LogService.getInstance().setMessage(entry.getValue(), e, e.toString());
      return false;
    } 
    time = 0;
    return true;
  }*/
 
  /* boolean ping(CrawlSessionEntry entry) {
      String [] addresses = entry.getValue().getHome();
      if(addresses == null || addresses.length < 1) return false;
      String host = null;
      try {
        host = new URL(addresses[0]).getHost();
      } catch (Exception e) {
        entry.setPingStatus(CrawlSessionEntry.DEAD);
        LogService.getInstance().setMessage(entry.getValue(), e, e.getMessage());
        return false;
      }
  
      if(host == null) {
        entry.setPingStatus(CrawlSessionEntry.DEAD);
        return false;
      }
  
      int status = pinging(host);
      if(status == 1) {
        entry.setPingStatus(CrawlSessionEntry.LIVE);
        time = 0;
      } else {
        entry.setPingStatus(CrawlSessionEntry.DEAD);
        time++;
      }
      return true;
        try {
        InetAddress.getByName(new URL(addresses[0]).getHost());
      } catch (MalformedURLException e) {
        LogService.getInstance().setMessage(entry.getValue(), e, e.getMessage());
        entry.setPingStatus(CrawlSessionEntry.DEAD);
        time++;
        return false;
      } catch (UnknownHostException e) {
        entry.setPingStatus(CrawlSessionEntry.DEAD);
        LogService.getInstance().setMessage(entry.getValue(), e, e.getMessage());
        time++;
        return false;
      } catch (Exception e) {
        entry.setPingStatus(CrawlSessionEntry.DEAD);
        LogService.getInstance().setMessage(entry.getValue(), e, e.getMessage());
        time++;
        return false;
      } 
    }*/

  boolean ping() {
    try {
      //      if(pinging("www.vnexpress.net")) return true;
      InetAddress.getByName("www.vnexpress.net");
      return true;
    } catch (Exception e) {
    }

    try {
      //      if(pinging("www.vietnamnet.vn")) return true;
      InetAddress.getByName("vietnamnet.vn");
      return true;
    } catch (Exception e) {
    }

    try {
      InetAddress.getByName("home.vnn.vn");
      return true;
      //      if(pinging("home.vnn.vn")) return true;
      //      return true;
    } catch (Exception e) {
    }

    try {
      InetAddress.getByName("www.google.com");
      return true;
    } catch (Exception e) {
    }

    try {
      //      if(pinging("www.yahoo.com")) return true;
      InetAddress.getByName("www.yahoo.com");
      return true;
    } catch (Exception e) {
    }

    return false;
  }

  //  int getTime() { return time; }

  synchronized public void increaTime() { time++; }

  //  void setTime(int ping) { this.time = ping; }

 /* boolean pinging(String ip)  {
    String cmd = "ping " + ip;
    Runtime runtime = Runtime.getRuntime();
    Process p = null;
    try {
      p = runtime.exec(cmd);
    } catch (Exception e) {
      LogService.getInstance().setMessage(ip, e, e.getMessage());
      time++;
      return false;
    } 

    InputStreamReader isr=new InputStreamReader(p.getInputStream());
    BufferedReader brd = new BufferedReader(isr);

    while(true) {
      String line;
      try {
        line= brd.readLine();
      } catch (Exception e) {
        return false;
      }

      if(line == null) break;
      line  = line.toLowerCase();
      //      System.out.println(line);
      if(line.indexOf("ttl=") > -1 
          && line.indexOf("time=") > -1) return true;
    }

    return false;
  }
*/
}

/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.proxy2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2011  
 */
class ProxiesSaver {
  
  private long lastModified = -1;
  
  void saveProxies() {
    try {
      File infile  = UtilFile.getFile("system/proxy/", "proxies.temp.txt");
      System.out.println(infile.length());
      if(!infile.exists() || infile.length() < 10) return;
      
      if(infile.lastModified() == lastModified) return;
      lastModified = infile.lastModified();

      String value  = new String(RWData.getInstance().load(infile), "utf-8");
      String [] elements = value.split("\n");
      List<Data> datas = new ArrayList<Data>();
      for(int i = 0; i < elements.length; i++) {
        if((elements[i] = elements[i].trim()).isEmpty()) continue;
        try {
          add(datas, new Data(elements[i]));
        } catch (Throwable e) {
        }
      }

      Collections.sort(datas, new Comparator<Data>() {
        public int compare(Data o1, Data o2) {
          return (int)(o1.getTime() - o2.getTime());
        }
      });

      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < datas.size(); i++) {
        if(datas.get(i).getTime() > 3000) continue;
        if(builder.length() > 0) builder.append('\n');
        builder.append(datas.get(i).getProxy());
      }

      File outfile  = UtilFile.getFile("system/proxy/", "proxies.txt");
      outfile.delete();
      //      System.out.println(" thay co "+ builder);
      RWData.getInstance().save(outfile, builder.toString().trim().getBytes("utf-8"));
      LogService.getInstance().setMessage(null, "Proxy Loader updated new proxies for crawling");
    } catch (IOException e) {
      LogService.getInstance().setMessage("PROXY", null, e.toString());
    } catch (Exception e) {
      LogService.getInstance().setThrowable("PROXY", e);
    }
  }

  private void add(List<Data> datas, Data data) {
    for(int i = 0; i < datas.size(); i++) {
      if(datas.get(i).getProxy().equals(data.getProxy())) {
        datas.get(i).setTime((datas.get(i).getTime() + data.getTime())/2);
        return;
      }
    }
    datas.add(data);
  }

  private class Data {

    private String proxy;
    private long time;

    public Data(String line) {
      String [] elements = line.split("/");
      proxy = elements[0];
      time  = Long.parseLong(elements[1].trim());
    }

    long getTime() { return time; }
    void setTime(long time) { this.time = time; }

    String getProxy() { return proxy; }
  }
}

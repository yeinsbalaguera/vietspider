/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.Application;
import org.vietspider.crawl.CrawlingPool;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.ExecutorStatusBuilder;
import org.vietspider.crawl.PoolStatusBuilder;
import org.vietspider.crawl.link.Link;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.io.loader.LoaderListGenerator;
import org.vietspider.io.loader.LoaderListHandler;
import org.vietspider.net.server.CrawlerStatus;
import org.vietspider.pool.Session;
import org.vietspider.pool.ThreadPool;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 15, 2007  
 */
public class CrawlerHandler extends CommonHandler {

  @SuppressWarnings("unused")
  public void execute(final HttpRequest request, final HttpResponse response,
                      final HttpContext context, OutputStream output) throws Exception  {
    Header header = request.getFirstHeader("action");
    String action = header.getValue();
    
    String value = new String(getRequestData(request), Application.CHARSET).trim();
    
    CrawlingPool threadPool = CrawlService.getInstance().getThreadPool();
    
    if("add.source".equals(action)) {
      if(!checkAdminRole(request)) return;
      //add sources to crawler download list 
      Header addTypeHeader = request.getFirstHeader("add.type");
      boolean addType = false;
      if(addTypeHeader != null && "*".equals(addTypeHeader.getValue())) {
        if("-1".equals(value)) {
          LoaderListGenerator.getInstance().setExecute(false);
        } else {
          LoaderListGenerator.getInstance().add(value);
        }
      } else {
//        boolean running = threadPool != null && !threadPool.isPause();
//        if(running) threadPool.pauseExecutors();
        LoaderListHandler.getInstance().add(value);
        //continue crawling
//        if(running) threadPool.continueExecutors();
      }
//      super.logAction(request, action, "add source "+value+" to crawler");
      return;
    }
    
    /*if("sort.download.list.source".equals(action)) {
      File file = UtilFile.getFile("system", "load");
      
      LineListSort lineListSort = LineListSort.create(file);
      new Thread(lineListSort).start();
    }*/
    
    
    if("remove.source".equals(action)) {
      if(!checkAdminRole(request)) return;
      boolean running = threadPool != null && !threadPool.isPause();
      if(running) {
        threadPool.removeElement(toSourceName(value));
      }
      //remove sources from crawler download list 
      LoaderListHandler.getInstance().remove(value);
      //continue crawling
      return;
    }
    
    if("monitor.crawler".equals(action)) {
      if(!checkAdminRole(request)) return;
      CrawlerStatus instance = XML2Object.getInstance().toObject(CrawlerStatus.class, value);

      try {
        SystemProperties properties = SystemProperties.getInstance();
        instance.setTotalThread(CrawlerConfig.EXCUTOR_SIZE);
      } catch (Exception e) {
        int totalThread = 1;
        instance.setTotalThread(totalThread);
      }
      
      if(instance.getStatus() == CrawlerStatus.START_OR_STOP){
        if(threadPool == null) {
          CrawlService.getInstance().initServices();
          setData(instance, output, CrawlerStatus.RUNNING);
          return ;
        }
        
        if(!threadPool.isPause()) {
          threadPool.setPause(true); 
          setData(instance, output, CrawlerStatus.STOPED);
          return ;
        }
        
        threadPool.continueExecutors();
        setData(instance, output, CrawlerStatus.RUNNING);
        return;
      }
      

      if(threadPool == null) {
        setData(instance, output, CrawlerStatus.NULL);
        return;
      }

     /* if(threadPool.isEndSession()) {
        setData(instance, output, CrawlerStatus.IS_END_SESSION);
        return;
      }*/

      if(instance.getStatus() == CrawlerStatus.RUNNING) {
        if(threadPool.isPause()) {
          setData(instance, output, CrawlerStatus.STOPED);
        } else {
          setData(instance, output, CrawlerStatus.RUNNING);
        }
        return;
      }
      
      if(instance.getStatus() == CrawlerStatus.STOP_ITEM && threadPool.getExecutors().size() > 0) {
        threadPool.abortExecutor(toSourceName(instance.getSources().get(0))); 
        setData(instance, output, CrawlerStatus.RUNNING);
      }

      if(instance.getStatus() == CrawlerStatus.GO_TO_ITEM && threadPool.getExecutors().size() > 0) {
        threadPool.nextElement(toSourceName(instance.getSources().get(0))); 
        setData(instance, output, CrawlerStatus.RUNNING);
      }
      
      if(instance.getStatus() == CrawlerStatus.GO_TO_ITEM_WITH_REDOWNLOAD && threadPool.getExecutors().size() > 0) {
        threadPool.nextElement(toSourceName(instance.getSources().get(0)), true); 
        setData(instance, output, CrawlerStatus.RUNNING) ;
      }
      return;
    }
    
    if("view.executor".equals(action)) {
      if(threadPool == null || threadPool.getExecutors() == null) {
        output.write("Executors is null".getBytes());
        return;
      }
      int idx = Integer.parseInt(value.trim());
      
      output.write(new ExecutorStatusBuilder().get(threadPool, idx).getBytes(Application.CHARSET));
      return;
    }
    
    if("abort.executor".equals(action)) {
      if(!checkAdminRole(request)) return;
      if(threadPool == null || threadPool.getExecutors() == null) {
        output.write("Executors is null".getBytes());
        return;
      }
      int idx = Integer.parseInt(value.trim());
      new ExecutorStatusBuilder().abort(threadPool, idx);
      return;
    }
    
    if("view.crawl.pool".equals(action)) {
      if(threadPool == null) {
        output.write("Crawler is null".getBytes());
        return;
      }
      output.write(new PoolStatusBuilder().get(threadPool).getBytes(Application.CHARSET));
      return;
    }
    
    if("pool.clear.queue".equals(action)) {
      if(!checkAdminRole(request)) return;
      if(threadPool != null) threadPool.getQueueEntry().clear();
      return;
    }
  }
  
  private void setData(CrawlerStatus instance, OutputStream output, int status) throws Exception {
    instance.setStatus(-1);
    Object2XML mapper = Object2XML.getInstance();
    ThreadPool<String, Link> threadPool = CrawlService.getInstance().getThreadPool();
    if(threadPool == null) {
      output.write(mapper.toXMLDocument(instance).getTextValue().getBytes(Application.CHARSET));
      return ;
    }
    instance.setStatus(status);

    instance.clearSource();

    for(Session<String, Link> executor : threadPool.getExecutors()) {
      String value = executor.getValue();
      if(value == null) continue;
      instance.addSource(toSourceKey(value));
    }
    
    String [] txtStatus = new String[instance.getTotalThread()];
    int counter  = 0;
    for(Session<String, Link> executor: threadPool.getExecutors()) {
      for(int i = 0; i < executor.size(); i++) {
        if(counter >= txtStatus.length) continue;
        txtStatus[counter] = executor.getStatus(i);
        if(txtStatus[counter] == null) {
          StringBuilder builder  = new StringBuilder("Executor ");
          builder.append(executor.getId());
          if(executor.getValue() != null) {
            builder.append(' ').append('[');
            builder.append(executor.getValue());
            builder.append(']').append(' ');
          }
          builder.append(threadPool.isPause() ? 
              " pause; " : executor.isEndSession() ? " end session " : " in session ");
          txtStatus[counter] = builder.toString() ;
        }
        counter++;
      }
    }
    instance.setThreadStatus(txtStatus);
    output.write(mapper.toXMLDocument(instance).getTextValue().getBytes(Application.CHARSET));
  }
  
  private String toSourceName(String key) {
    StringBuilder builder = new StringBuilder();
    String [] elements = key.split("\\.");
    if(elements.length < 3) return key;
    builder.append(elements[1]).append('.').append(elements[2]).append('.').append(elements[0]);
    return builder.toString();
  }
  
  private String toSourceKey(String key) {
    StringBuilder builder = new StringBuilder();
    String [] elements = key.split("\\.");
    if(elements.length < 3) return key;
    builder.append(elements[2]).append('.').append(elements[0]).append('.').append(elements[1]);
    return builder.toString();
  }

}

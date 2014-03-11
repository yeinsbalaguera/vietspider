/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.link.Link;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;
import org.vietspider.pool.Session;
import org.vietspider.pool.Task;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 6, 2008  
 */
public final class ExecutorStatusBuilder {
  
  public String get(CrawlingPool pool, int idx) {
    List<Session<String, Link>> executors = pool.getExecutors();
    if(executors == null) return "Not found executor.";
    int counter = 0;
    for(int i = 0; i < executors.size(); i++) {
      Session<String, Link> executor = executors.get(i);
      int size = executor.size();
      if(idx >= counter && idx < counter+size) {
        return getExecutorStatus((CrawlingSession)executor);  
      }
      counter += size;
    }
    return "Not found executor.";
  }
  
  public void abort(CrawlingPool pool, int idx) {
    List<Session<String, Link>> executors = pool.getExecutors();
    if(executors == null) return;
    int counter = 0;
    for(int i = 0; i < executors.size(); i++) {
      Session<String, Link> executor = executors.get(i);
      int size = executor.size();
      if(idx >= counter && idx < counter+size) {
        ((CrawlingSession)executor).endSession(true);
        return;
      }
      counter += size;
    }
  }
  
  
  private String getExecutorStatus(CrawlingSession executor) {
    if(executor == null) return "Executor is null";
    int id = executor.getId();
    Source source = CrawlingSources.getInstance().getSource(executor.getValue());
    int homepageCode = source.getCodeName();
    SessionStore store = SessionStores.getStore(homepageCode);

    StringBuilder builder = new StringBuilder();//"[Thread ").append(executor.getId());
   /* Thread thread = executor.getThreadExecutor();
    if(thread == null) {
      builder.append(" is null]\n");
    } else {
      builder.append(thread.isInterrupted() ? " interrupted, " : " not interrupted, ");
      builder.append(thread.isAlive() ? "alive" : "dead" ).append("]\n");
    }*/

    builder.append("[Executor ").append(id).append(" start at ");

    long sessionStart = source.getStartCrawling();
    if(sessionStart > 0) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(sessionStart);
      builder.append(calendar.getTime().toString());
    } else {
      builder.append(sessionStart);
    }
    
   /* if(executor.getValue() != null) {
      builder.append(' ').append('[');
      builder.append(executor.getValue().getFullName());
      builder.append(']').append(' ');
    }*/

    builder.append(executor.isEndSession() ? ", end session," : ", in session");
    
    builder.append(source.isTimeout() ? " is timeout" : " not timeout").append("]\n");
    
    int sizeQueue = store.size();
    builder.append("[Session data: Total ").append(sizeQueue).append(" links of queue]\n");
    
    WebClient webClient = executor.getResource(WebClient.class);
    HttpClient httpClient = webClient.getHttpClient();
    HttpHost httpProxy = (HttpHost)httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
    if(httpProxy != null) {
      int proxyPort = httpProxy.getPort();
      String proxyHost = httpProxy.getHostName();
      builder.append("[Web client: Proxy is ").append(proxyHost).append(':').append(proxyPort).append(" ]\n");
    } else {
      builder.append("[Web client: No Proxy ]\n");
    }
    
    builder.append("[Session value: ").append(source != null ? source.toString() : " null,");
    builder.append(store.isEmpty() ? " ,no link" : " ,has link] \n" );
    
    builder.append('\n');
    for(int i=0; i < executor.size(); i++){    
      /*builder.append("[Thread ").append(i).append(": ");
      if(threads[i] == null) {
        builder.append(" is null]\n");
      } else {
        builder.append(threads[i].isInterrupted() ? " interrupted, " : " not interrupted, ");
        builder.append(threads[i].isAlive() ? "alive" : "dead" ).append("]\n");
      }*/
      buildWorkerStatus(builder, executor.getWorker(i)).append('\n');
    }

    return builder.toString();
  }
  
  private StringBuilder buildWorkerStatus(StringBuilder builder, Worker<String, Link> worker) {
    int id = worker.getId();
    
    builder.append("[Worker ").append(id);
    /*builder.append("[Worker ").append(id).append(" start at ");
    long start = worker.getStart();
    if(start > 0) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(start);
      builder.append(calendar.getTime().toString());
    } else {
      builder.append(start);
    }
    if(worker.isFree()) {
      builder.append(", is free");
    } else {
      builder.append(worker.isTimeout() ? ", is timeout" : ", not timeout");
    }*/
    
//    builder/*.append(executing ? ", executing" : ", aborted")*/.append("]\n");
    builder.append(", working value: ");
    Link value  = worker.getValue();
    builder.append(value != null ? value.toString() : " null ").append("]\n");

    builder.append("[Execute task ");
    Task<Link> task = null; //worker.getTask(); 
    return builder.append(task != null  ? task.getClass().getSimpleName() : " null ").append("]\n");
  }

}

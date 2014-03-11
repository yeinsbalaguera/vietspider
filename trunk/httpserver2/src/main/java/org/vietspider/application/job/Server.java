package org.vietspider.application.job;


public class Server {
  /*static public class ShutdownJob extends Job {
    final static public String NAME = "shutdown" ;
    
    public void execute() throws Exception {
      CrawlService crawlService = CrawlService.getInstance();
      ThreadPool<?,?,?> threadPool = crawlService.getThreadPool() ;
      if(threadPool != null) threadPool.pauseExecutors(); 
      DynaObject status = new DynaObject() ;
      String shutdownAt = new Date().toString() ;
      status.addField("server:shutdown:in", "3000") ;
      status.addField("server:shutdown:at", shutdownAt) ;
      status.addField("server:shutdown:desc", "Server is shutdown at " + shutdownAt + " in 3000ms") ;
      Response response = getRequestContext().getResponse() ;
      response.addHeader(Header.CONTENT_TYPE, Header.CONTENT_TYPE_DYNA_OBJECT) ;
      response.getOutputStream().write(status.serialize()) ;
      getRequestContext().getApplication().shutdown(3000) ;
      return ;
    }
  }*/
}

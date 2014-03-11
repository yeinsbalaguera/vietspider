package org.vietspider.application.job;


public class Crawler {
 /* static public class StatusJob extends Job {
    final static public String NAME = "status" ;
    public void execute() throws Exception {
      RequestContext context = getRequestContext() ;
      CrawlPool threadPool = CrawlService.getInstance().getThreadPool() ;
      DynaObject status = new DynaObject() ;
      boolean pause = true ;
      if(threadPool != null) {
        pause = threadPool.isPause() ;
      }
      status.addField("crawler:status", pause) ;
      if(pause) status.addField("crawler:status:desc", "Crawler status is stopped") ;
      else      status.addField("crawler:status:desc", "Crawler status is running") ;
      
      Response response = context.getResponse() ;
      response.addHeader(Header.CONTENT_TYPE, Header.CONTENT_TYPE_DYNA_OBJECT) ;
      response.getOutputStream().write(status.serialize()) ;
    }
  }
  
  static public class StartJob extends Job {
    final static public String NAME = "start" ;
    public void execute() throws Exception {
      RequestContext context = getRequestContext() ;
      CrawlPool threadPool = CrawlService.getInstance().getThreadPool() ;
      DynaObject status = new DynaObject() ;
      if(threadPool == null) {
        CrawlService.getInstance().initServices();
        return ;
      }
      status.addField("crawler:status", 1) ;
      status.addField("crawler:status:desc", "Crawler status is running") ;
      
      Response response = context.getResponse() ;
      response.addHeader(Header.CONTENT_TYPE, Header.CONTENT_TYPE_DYNA_OBJECT) ;
      response.getOutputStream().write(status.serialize()) ;
    }
  }
  
  static public class StopJob extends Job {
    final static public String NAME = "start" ;
    public void execute() throws Exception {
      RequestContext context = getRequestContext() ;
      CrawlPool threadPool = CrawlService.getInstance().getThreadPool() ;
      DynaObject status = new DynaObject() ;
      boolean pause = true ;
      if(threadPool != null) {
        pause = threadPool.isPause() ;
      }
      status.addField("crawler:status", pause) ;
      if(pause) status.addField("crawler:status:desc", "Crawler status is stopped") ;
      else      status.addField("crawler:status:desc", "Crawler status is running") ;
      
      Response response = context.getResponse() ;
      response.addHeader(Header.CONTENT_TYPE, Header.CONTENT_TYPE_DYNA_OBJECT) ;
      response.getOutputStream().write(status.serialize()) ;
    }
  }*/
}
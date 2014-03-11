/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MalformedChunkCodingException;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.params.HttpParams;
import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.crawl.WebRedirectHandler.CrawlRedirectException;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.CrawlingSetup;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.LinkCreator;
import org.vietspider.crawl.plugin.ProcessPlugin;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.model.Source;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.Proxies;
import org.vietspider.net.client.ProxiesMonitor;
import org.vietspider.net.client.WebClient;
import org.vietspider.offices.DocumentConverter;
import org.vietspider.pool.Task;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 29, 2008  
 */
abstract class Step2DownloadLink extends Task<Link> {

  //  protected boolean isVisited = false;
  protected boolean log  = true;

  protected HttpMethodHandler httpHandler = null;

  public Step2DownloadLink() {
    SystemProperties properties = SystemProperties.getInstance();
    log = "true".equalsIgnoreCase(properties.getValue(Application.LOG_WEBSITE_ERROR));
  }

  char [] download(Link link) throws Exception {
    //    if(link.getAddress().endsWith(".doc")) {
    //      System.out.println(" ====  link ++++> "+ link.getAddress());
    //    }

    //    boolean save =  "true".equals(System.getProperty("save.link.download"));
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
//    if(source.getDebug() < 0) {
//      java.io.File file;
//      org.vietspider.serialize.Object2XML bean2XML = org.vietspider.serialize.Object2XML.getInstance();
//      org.vietspider.parser.xml.XMLDocument document = bean2XML.toXMLDocument(link);
//      java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/");
//      java.io.File file = new java.io.File(folder, String.valueOf(link.hashCode())+ ".txt");
//      RWData.getInstance().save(file, document.getTextValue().getBytes("utf-8"));
//    }

    CrawlingSession executor = (CrawlingSession) worker.getExecutor();

    WebClient webClient = executor.getResource(WebClient.class);
    if(webClient == null) {
      LinkLogIO.saveLinkLog(link, "web.client.not.found", LinkLog.PHASE_DOWNLOAD);
      return null;
    }

//    if(source.getDebug() < 0) webClient.setLog(true);

    char [] chars = webClient.getCacheData().getCachedObject(link.getUrl());
    if(chars != null) return chars;

    if(httpHandler != null) httpHandler.abort();

    try {
      httpHandler = new HttpMethodHandler(webClient);

      webClient.getHttpClient().getConnectionManager().closeExpiredConnections();
      //worker.getResource(HttpMethodHandler.class);
      //    if(httpGetHandler == null) return null;

      CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
      sourceLog.increaseTotalDownload();

      Proxies proxies = ProxiesMonitor.getInstance().getProxies(webClient.getHost());
      if(proxies != null && proxies.isType(Proxies.DEFAULT)) webClient.registryProxy(proxies.next());

      //    long start = System.currentTimeMillis();
      HttpResponse response = null;
      try {
        List<NameValuePair> params = link.getRequestParams();
        if(params == null) {
          response = httpHandler.execute(link.getUrl(), link.getReferer());
        } else {
          response = httpHandler.execute(link.getUrl(), link.getReferer(), params, source.getEncoding());  
        }
      } catch (ClientProtocolException e) {
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        }
        LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
        return null;
      } catch (InterruptedException e) {
        LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        }
        return null;
      } catch (NoHttpResponseException e) {
        LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        }
        return null;
      } catch (SocketException e) {
        LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        }
        return null;
      } catch (UnknownHostException e) {
        LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        } else if(log) {
//        LogWebsite.getInstance().setMessage(source, e, link.getUrl());
        //        }
        CrawlerPoolPing.getInstance().increaTime();
      } catch (CrawlRedirectException e) {
        LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        }
        try {
          if(link.isData()) PageDownloadedTracker.saveUrl(link);
        } catch (Throwable e1) {
          LinkLogStorages.getInstance().save(source, e1);
//          LogWebsite.getInstance().setThrowable(source, e1);
          executor.endSession(true);
        }
        return null;
      } catch (ProtocolException e) {
        LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        }
        String message = e.getMessage();
        if(message != null && message.startsWith(WebRedirectHandler.START_NEW_LINK)) {
          String redirect = message.substring(WebRedirectHandler.START_NEW_LINK.length());
          LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
          List<Link> redirects = linkCreator.createRedirect(webClient.getHost(), link, redirect);
          executor.addElement(redirects, link.getSourceFullName());
        }

        if(link.isData()) {
          try {
            PageDownloadedTracker.saveUrl(link);
          } catch (Throwable e1) {
            LinkLogIO.saveLinkLog(link, e1, LinkLog.PHASE_DOWNLOAD);
            //            if(source.isDebug()) {
            //              LogSource.getInstance().setThrowable(source, e, link.getAddress());
            //            } else {
//            LogWebsite.getInstance().setThrowable(source, e1);
            //            }
            executor.endSession(true);
            LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
            return null;
          }
        }
        return null;
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress());
        //        } else {
//        LogWebsite.getInstance().setThrowable(source, e);
        //        }
        return null;
      } 

      if(response == null) {
        if(!DeadWebsiteChecker.getInstance().check(webClient, link)) {
          LinkLogIO.saveLinkLog(link, "{not.found.response}", LinkLog.PHASE_DOWNLOAD);
        }
        return null;
      }

      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();

      if(proxies != null && proxies.isType(Proxies.ERROR)) {
        if(proxies.getErrorCode() == statusCode) {
          webClient.registryProxy(proxies.next());
        }
        //      else if(proxies.isProxyTimeout()) {
        //        webClient.registryProxy(proxies.next());
        //      }
      }

      switch (statusCode) {
      case HttpStatus.SC_NO_CONTENT:
      case HttpStatus.SC_BAD_REQUEST:
      case HttpStatus.SC_REQUEST_TIMEOUT:
      case HttpStatus.SC_NOT_ACCEPTABLE:
      case HttpStatus.SC_SERVICE_UNAVAILABLE:
      case 999:
        StringBuilder msgBuilder = new StringBuilder(link.getAddress());
        msgBuilder.append(' ').append(statusLine.getReasonPhrase());
        HttpParams params = webClient.getHttpClient().getParams();
        Object param = params.getParameter(ConnRoutePNames.DEFAULT_PROXY);
        if(param != null) {
          HttpHost proxy = (HttpHost)param;
          msgBuilder.append(" connect by proxy: ");
          msgBuilder.append(proxy.getHostName()).append(':').append(proxy.getPort());
        }
        LinkLogIO.saveLinkLog(link, msgBuilder.toString(), LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setMessage(source, null, msgBuilder.toString());
//        } else if(log) {
//          LogWebsite.getInstance().setMessage(link.getSource(), null, msgBuilder.toString());
//        }
        response.getEntity().getContent().close();
        LinkLogIO.saveLinkLog(link, "{http.code."
            + String.valueOf(statusCode) + "}", LinkLog.PHASE_DOWNLOAD);
        return null;
      case HttpStatus.SC_NOT_FOUND:
        if(!DeadWebsiteChecker.getInstance().check(webClient, link)) {
          response.getEntity().getContent().close();
          LinkLogIO.saveLinkLog(link, "{http.code."
              + String.valueOf(statusCode) + "}", LinkLog.PHASE_DOWNLOAD);
        }
        return null;
      default:
        break;
      }

      String contentType = "text/html";

      Header header = response.getFirstHeader("Content-Type");
      if(header != null) contentType = header.getValue();
      if(contentType == null 
          || contentType.trim().isEmpty()) contentType = "text/html";

      String extension = null;
      
//      System.out.println(" =============== > "+ contentType);

      if(contentType.indexOf("html") < 0
          && contentType.indexOf("text") < 0 
          && contentType.indexOf("xml") < 0 ) {
        if(DocumentConverter.getInstance() == null) {
          LinkLogIO.saveLinkLog(link, "{document.converter.null}", LinkLog.PHASE_DOWNLOAD);
          return null;
        }
        
        extension = DocumentConverter.getInstance().getExtension(link.getAddress(), contentType);
//        System.out.println(" thay co "+ extension);
        if(extension == null) {
//          if(source.isDebug()) {
//            LogSource.getInstance().setMessage(source, null, link.getAddress()+": {invalid.extension}");
//          }
          LinkLogIO.saveLinkLog(link, "{invalid.extension}", LinkLog.PHASE_DOWNLOAD);
          return null;
        }
      }

      byte [] data = null;

      if(isInvalidLength(source.getMinSizeOfPage(), response)) {
        try {
          PageDownloadedTracker.saveUrl(link);
        } catch (Throwable e1) {
          LinkLogIO.saveLinkLog(link, e1, LinkLog.PHASE_DOWNLOAD);
//          if(source.isDebug()) {
//            LogSource.getInstance().setThrowable(source, e1);
//          } else {
//            LogWebsite.getInstance().setThrowable(source, e1);
//          }
          executor.endSession(true);
        }
        LinkLogIO.saveLinkLog(link, "{invalid.length}", LinkLog.PHASE_DOWNLOAD);
        return null;
      }

      String pluginName = worker.getResource(CrawlingSetup.PLUGIN_NAME);
      ProcessPlugin plugin = worker.<ProcessPlugin>getResource(pluginName);  
      if(plugin == null || !plugin.isValidSize(link, response)) {
        LinkLogIO.saveLinkLog(link, "{plugin.invalid.size}", LinkLog.PHASE_DOWNLOAD);
        return null;
      }

      try {
        data = httpHandler.readBody();
      } catch (InterruptedException e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setThrowable(source, e);
//        }
        return null;
      } catch (MalformedChunkCodingException e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setThrowable(source, e);
//        } else if(log) {
//          LogWebsite.getInstance().setMessage(link.getSource(), e, link.getAddress());
//        }
        return null;
      } catch (IllegalStateException e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setThrowable(source, e);
//        } else  if(log) {
//          LogWebsite.getInstance().setMessage(link.getSource(), e, link.getAddress());
//        }
        return null;
      } catch (IOException e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setThrowable(source, e);
//        } else if(log) {
//          LogWebsite.getInstance().setMessage(link.getSource(), e, link.getAddress());
//        }
        return null;
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setThrowable(source, e);
//        } else {
//          LogWebsite.getInstance().setThrowable(source, e, link.getAddress());
//        }
        return null;
      }
      
      link.setTotalOfBytes(data.length);
      //    long end = System.currentTimeMillis();
      //    System.out.println("step. 3 "+ link.getUrl()+ " data " 
      //        + (data == null ? "null"  : data.length) + " mat " );
      //    isVisited = true;
      if(data == null || data.length < 1 || data.length > HttpResponseReader.BIG_SIZE) {
        LinkLogIO.saveLinkLog(link, "{data.length.incorrect}", LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setMessage(source, null, link.getAddress()+ ": {data.length.incorrect}.");
//        }
        return null;
      }
      
//      org.vietspider.serialize.Object2XML bean2XML = org.vietspider.serialize.Object2XML.getInstance();
//      org.vietspider.parser.xml.XMLDocument document = bean2XML.toXMLDocument(link);
//      java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/");
//      java.io.File file = new java.io.File(folder, String.valueOf(link.hashCode())+ ".html");
//      RWData.getInstance().save(file, data);

      if(extension != null) {
        //      System.out.println(" chuan bi convert "+ link.getAddress()+ " : "+ extension);
//        StringBuilder converterId = new StringBuilder();  
//        converterId.append(executor.getId()).append('_').append(worker.getId());
        data = DocumentConverter.getInstance().convert(data, extension, source.getEncoding());
        //      System.out.println(" convert xong "+ data.length);
        if(data == null || data.length < 1) {
          LinkLogIO.saveLinkLog(link, "{data.empty.converted}", LinkLog.PHASE_DOWNLOAD);
//          if(source.isDebug()) {
//            LogSource.getInstance().setMessage(source, null, link.getAddress()+ ": {data.empty.converted}.");
//          }
          return null;
        }
      }

      if(!plugin.isValidSize(link, data)) {
        LinkLogIO.saveLinkLog(link, "{plugin.invalid.size}", LinkLog.PHASE_DOWNLOAD);
//        if(source.isDebug()) {
//          LogSource.getInstance().setMessage(source, null, link.getAddress()+ ": {plugin.data.length}.");
//        }
        return null;
      }

      if(proxies != null && proxies.isType(Proxies.SIZE)) {
        if(data.length <= proxies.getErrorSize()) {
          //        System.out.println(link.getAddress());
          //        System.out.println(" thay tai day bi roi "+ data.length + " / "+ proxies.getErrorSize());
          //        System.out.println(new String(data));
          webClient.registryProxy(proxies.next());
        } 
        //      else if(proxies.isProxyTimeout()) {
        //        webClient.registryProxy(proxies.next());
        //      }
      }

      sourceLog.increaseTotalDownloadSuccessfull();
      
//      MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//      logSaver.updateTotalLink(link.getSource(), Calendar.getInstance(), 1);
      
      //    System.out.println("step. 3 "+ link.getUrl()+ " data " + data.length);

      //    if(link.getParams() != null && link.getParams().size() > 0) {
      //    if(link.getParams() != null && link.getParams().size() > 0 && link.getLevel() > 0) {
//      if(source.getDebug() < 0) {
//        java.io.File file;
//        org.vietspider.common.io.DataWriter writer = org.vietspider.common.io.RWData.getInstance();
//        java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/");
//        file = new java.io.File(folder, String.valueOf(link.hashCode())+ ".html");
//        writer.save(file, data);
//      }
      //    }

      //    if(responseReader.getReadData() != data.length) {
      //      System.out.println(" thay co thuc "+ responseReader.getReadData() + " : " + data.length);
      //    }

      String charset = extension != null ? null : source.getEncoding();
      if(charset == null || charset.trim().isEmpty()) {
        charset = new HTMLParserDetector().detectCharset(data);
      } 
      chars = CharsDecoder.decode(charset, data, 0, data.length);

      if(source.isDecode()) chars = new RefsDecoder().decode(chars);
      if(proxies != null && proxies.isType(Proxies.MESSAGE)) {
        String html = new String(new RefsDecoder().decode(chars));
        //      System.out.println(link.getAddress());
        //      System.out.println(html);
        if(html.indexOf(proxies.getErrorMessage()) > -1) {
          //        System.out.println(" thay tai day bi roi "+ data.length + " / "+ proxies.getErrorMessage());
          webClient.registryProxy(proxies.next());
        } 
        //      else if(proxies.isProxyTimeout()) {
        //        webClient.registryProxy(proxies.next());
        //      }
      }

      if(link.getRef() != null) webClient.cacheResponse(link.getUrl(), chars);
      return chars;
    } finally {
      httpHandler.abort();
    }
  }

  private boolean isInvalidLength(long minSize, HttpResponse response) {
    if(minSize < 0) return false;
    Header header = response.getFirstHeader(HttpResponseReader.CONTENT_LENGTH);
    if(header == null) return false;
    String value = header.getValue();
    if(value == null) return false;
    long length = -1l;
    try {
      length = Long.parseLong(value.trim());
    } catch (Exception e) {
      length = -1;
    }
    if(length > minSize) return false;
    return true;
  }

  public void abort() {
    if(httpHandler != null) httpHandler.abort();
  }
}

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.image;

import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.handler.ImageIO;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.model.Group;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 16, 2008  
 */
public class DocumentImageDownloader extends ImageHandler {

  //  protected HttpMethodHandler httpHandler;

  public DocumentImageDownloader(/*HttpMethodHandler httpHandler*/WebClient webClient) {
    super(webClient);
  }

  @SuppressWarnings("unused")
  public boolean download(HTMLNode root, List<HTMLNode> nodes, PluginData pluginData) {
    Group group  = pluginData.getGroup();
    return download(nodes, pluginData, group.isForceDownloadImage());
  }

  public boolean download(List<HTMLNode> nodes, PluginData pluginData, boolean isForce) {
//    images.clear();
    
    SystemProperties systemProperties = SystemProperties.getInstance();
    String application = systemProperties.getValue(Application.APPLICATION_NAME);
    StringBuilder builder = new StringBuilder(50).append('/').append(application).append("/IMAGE/");
    int rootLength = builder.length();
    int counter = 0;

    Meta meta = pluginData.getMeta();
    Link link = pluginData.getLink();
    Group group = pluginData.getGroup();

    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
      Attributes attributes = node.getAttributes(); 
      
      Attribute attr = null;
      if(node.isNode(Name.IMG)) {
        attr = attributes.get("src");
      } else if(node.isNode(Name.A)) {
        attr = attributes.get("href");
      }
      if(attr == null) continue;
      
      String srcValue  = attr.getValue();
      if(srcValue == null || srcValue.length() < 1) continue;
      
      if(srcValue.length() > 100000) continue;
      
//      if(srcValue.indexOf("base64") > 0) {
//        System.out.println(srcValue.length());
//      }

      ImageData imageData = new ImageData(meta.getId(), ++counter);
      String fullLink = createLink(link, srcValue);
      imageData.setLink(fullLink);
      attributes.add(new Attribute("longdesc", fullLink));
      try {
        read(imageData, link.getAddress());
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        LogWebsite.getInstance().setMessage(link.getSource(), e, null);
        continue;
      }

//      Source source = link.getSource();
      Calendar time = meta.getCalendar(); 
//      MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//      logSaver.updateTotalLink(source, time, 1);

      srcValue = imageData.getLink();
      if(imageData.getImage().getImage() == null) {
        attr.setValue(srcValue == null ? "#" : srcValue);
        attributes.set(attr);
        continue;
      }

      long imgDataLength  = imageData.getImage().getImage().length;
      if(imgDataLength == 0) {
        if(isForce) return false;
        continue;
      }

      builder.setLength(rootLength);
      srcValue = builder.append(imageData.getImage().getId()).toString();
      attr.setValue(srcValue);
      attributes.set(attr);
      imageData.setLink(srcValue);

      if(group.isSetImageToMeta()) {
        saveImage(pluginData.getArticle(), time, imageData, meta);
      } else {
        saveImage(pluginData.getArticle(), time, imageData, null);
      }
    }
    return true;
  }  

  protected void saveImage(Article article, Calendar calendar, ImageData imageData, Meta meta) {
    //save image to file
    if(meta != null) setImage2Meta(imageData, meta);
    if(CrawlerConfig.SAVE_IMAGE_TO_FILE) {
      new ImageIO("content/images/").saveImage(calendar, imageData.getImage());
      imageData.getImage().setImage(new byte[0]);
    }

    article.addImage(imageData.getImage());
  }

  public void read(ImageData imageData, String referer) throws Exception {
    if(httpHandler != null) httpHandler.abort();
    httpHandler = new HttpMethodHandler(webClient);
    
   
    try {
      HttpResponse response = httpHandler.execute(imageData.getLink(), referer);
//      if(imageData.getLink().indexOf("base64") < 0) {
//        System.out.println(imageData.getCounter());
//        System.out.println(response);
//      }
      if(response == null) return ;

      int statusCode = response.getStatusLine().getStatusCode();
      byte [] data = null;
      if(statusCode == HttpStatus.SC_NOT_FOUND) {
        data = new byte[0];
      } else  if(statusCode == HttpStatus.SC_OK 
          || statusCode == HttpStatus.SC_ACCEPTED) {
        data = httpHandler.readBody();
      } else {
        data = new byte[0];
      }
      if(data == null) {
        data  = new byte[0];
      } else { //read meta data for image
        readImageInfo(response, imageData);
      }

      //set data
      imageData.getImage().setImage(data);
    } catch (Exception e) {
      httpHandler.abort();
      throw e;
    } finally {
      httpHandler.release();
    }
  }

}

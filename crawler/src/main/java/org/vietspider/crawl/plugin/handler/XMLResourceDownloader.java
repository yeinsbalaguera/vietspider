/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.vietspider.bean.Article;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.image.ImageData;
import org.vietspider.crawl.image.ImageHandler;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.handler.XMLResource;
import org.vietspider.html.HTMLNode;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 28, 2008  
 */
public class XMLResourceDownloader extends ImageHandler {

  public XMLResourceDownloader(WebClient webClient) {
    super(webClient);
  }

  @SuppressWarnings("unused")
  public boolean download(HTMLNode root, List<HTMLNode> nodes, PluginData pluginData) {
    return false;
  }

  public boolean download(List<XMLResource> resources, PluginData pluginData) {
    Meta meta = pluginData.getMeta();
    Link link = pluginData.getLink();

    URLUtils urlUtils = new URLUtils();
    URL url  = null;
    try {
      url  = new URL(link.getAddress());
    } catch (Exception e) {
      return true;
    }

    int counter = 0;
    for(int i = 0; i < resources.size(); i++) {
      XMLResource resource = resources.get(i);

      ImageData imageData = new ImageData(meta.getId(), ++counter);
      imageData.getImage().setId(resource.getId());
      imageData.getImage().setName(resource.getName());

      imageData.setLink(urlUtils.createURL(url, resource.getLink()));

//            System.out.println(" chuan bi download "+ imageData.getLink());
      try {
        read(imageData, link.getAddress());
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
        continue;
      }

      if(imageData == null
          || imageData.getImage() == null 
          || imageData.getImage().getImage() == null
          || imageData.getImage().getImage().length == 0) continue;

//            System.out.println(" chuan bi save "+ imageData.getLink()+ " : "+imageData.getImage().getImage().length);

      saveImage(pluginData.getArticle(), meta.getCalendar(), imageData);
    }
    return true;
  }  

  protected void saveImage(Article article, Calendar calendar, ImageData imageData) {
//    System.out.println(" co muon save vao file ko "+ CrawlerConfig.SAVE_IMAGE_TO_FILE);
    if(CrawlerConfig.SAVE_IMAGE_TO_FILE) {
      saveResource(calendar, imageData.getImage());
      imageData.getImage().setImage(new byte[0]);
    }

    //    System.out.println(" chuan bi save vao diel "+ imageData.getImage().getImage().length);
    article.addImage(imageData.getImage());
  }

  public void read(ImageData imageData, String referer) throws Exception {
    if(httpHandler != null) httpHandler.abort();
    httpHandler = new HttpMethodHandler(webClient);

    HttpResponse response = httpHandler.execute(imageData.getLink(), referer);
    if(response == null) return ;

    int statusCode = response.getStatusLine().getStatusCode();
    byte [] data = null;
    if(statusCode == HttpStatus.SC_NOT_FOUND) {
      data = new byte[0];
    } else  if(statusCode == HttpStatus.SC_OK 
        || statusCode == HttpStatus.SC_ACCEPTED) {
      data = httpHandler.readBody(response);
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
  }

  protected void readImageInfo(HttpResponse response, ImageData imageData) {
    // read response header
    String name = imageData.getImage().getName();

    Header header =  response.getFirstHeader("Content-Type");
    String contentType = header != null ? header.getValue() : "application/msword";
    if(contentType == null || contentType.trim().length() < 1) contentType = "application/msword";

    if(name == null || name.length() < 4 || name.charAt(name.length() - 4) != '.') {
      header =  response.getFirstHeader("Content-Disposition");
      if(header != null && header.getValue() != null) {
        Attribute[] attributes = AttributeParser.getInstance().get(header.getValue());
        for(Attribute attribute : attributes) {
          if(attribute.getName().equalsIgnoreCase("filename")) {
            name = attribute.getValue();
            break;
          }
        }
      }

      if(name == null || name.trim().isEmpty()) {
        String address = imageData.getLink();
        if(address.indexOf('/') > -1) {
          name = address.substring(address.lastIndexOf('/')+1);
        } else {
          name = address;
        }
      }

      if(name.indexOf('?') > -1) name = name.substring(0, name.indexOf('?'));

      try{
        name = rename(name);
      }catch (Exception e) {
        name = String.valueOf(imageData.getCounter());
      }
      if(name != null && name.trim().length() > 1) imageData.getImage().setName(name);
    }

    imageData.getImage().setType(contentType);

    //    System.out.println(" download duoc tep "
    //         + imageData.getImage().getName()+ "  : "+ imageData.getImage().getType());
  }

  private void saveResource(Calendar calendar, final Image image) {
    String today = CalendarUtils.getFolderFormat().format(calendar.getTime());
    File  imgFolder  = UtilFile.getFolder("content/images/"+today);
    StringBuilder builder  = new StringBuilder().append(image.getId()).append('.');
    String name = image.getName();
    //    System.out.println(" thay co "+ image.getId());
    builder.append(name == null ? image.getId() : name);
    final File file = new File(imgFolder, builder.toString());
    final byte [] data = image.getImage();

    try {
      RWData.getInstance().save(file, data);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}

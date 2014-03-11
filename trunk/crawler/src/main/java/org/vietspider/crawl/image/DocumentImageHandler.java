/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.vietspider.bean.Meta;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.util.NodeHandler;
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
public class DocumentImageHandler extends ImageHandler {

  public final static String DOWNLOAD_IMAGE = "downloadImage";
  public final static String FORCE_DOWNLOAD_IMAGE = "forceDownloadImage";

  private NodePath[] nodePaths = null;

  private HTMLExtractor extractor;

  public DocumentImageHandler(WebClient webClient, HTMLExtractor extractor) {
    super(webClient);
    this.extractor = extractor;
  }

  public boolean download(HTMLNode root, List<HTMLNode> nodes, PluginData pluginData) {
    if(nodePaths == null) {
      complete(nodes, pluginData);
      return true;
    }

    NodeHandler nodeHandler = new NodeHandler();
    for(NodePath path : nodePaths) {
      HTMLNode node = extractor.lookNode(root, path);
      if(node == null) continue;
      HTMLNode imgNode = nodeHandler.searchImageNode(node.iterator());
      if(imgNode == null) continue;

      Attributes attributes = imgNode.getAttributes();
      
      Attribute attr = null;
      if(imgNode.isNode(Name.IMG)) {
        attr = attributes.get("src");
      } else if(imgNode.isNode(Name.A)) {
        attr = attributes.get("href");
      }
      if(attr == null) continue;
      
      String srcValue  = attr.getValue();
      if(srcValue == null || srcValue.length() < 1) continue;
      Link link = pluginData.getLink();
      srcValue = createLink(link, srcValue);
      pluginData.getMeta().setImage(srcValue);
      attr.setValue(srcValue);
      attributes.set(attr);
      break;
    }
    complete(nodes, pluginData);
    return true;
  }

  private void complete(List<HTMLNode> nodes, PluginData pluginData) {
    Meta meta = pluginData.getMeta();
    Link link = pluginData.getLink();
    Group group = pluginData.getGroup();

    List<ImageData> listImages = new ArrayList<ImageData>(nodes.size());

    boolean notSetImage = !group.isSetImageToMeta();
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

      srcValue = createLink(link, srcValue);
      attr.setValue(srcValue == null ? "#" : srcValue);
      attributes.set(attr);

      if(srcValue == null 
          || nodePaths != null 
          || notSetImage
          || meta == null || meta.getImage() != null) continue;

      /*int width  = getWidth(attributes);
      if(width > 70) meta.setImage(srcValue);
      if(width > 0) continue;*/

      ImageData imageData = new ImageData();
      imageData.setLink(srcValue);
      listImages.add(imageData);
    }

    //    System.out.println("thay co " + (nodePaths != null 
    //        || notSetImage || !group.isAutoDetectImage()
    //        || meta == null || meta.getImage() != null));
    //      System.out.println(" doc "+ imageData.getLink());
    if(nodePaths != null 
        || notSetImage /*|| !group.isAutoDetectImage()*/
        || meta == null || meta.getImage() != null) return; 

    for(ImageData imageData : listImages) {
//      ImageInputStream imageInputStream = null;
      try {
        /*imageInputStream = */read(imageData, link.getAddress());
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        LogWebsite.getInstance().setMessage(link.getSource(), e, null);
        continue;
      }
      
//      Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
//      Calendar time = meta.getCalendar();
//      MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//      logSaver.updateTotalLink(source, time, 1);
      //      System.out.println(imageData.getImage().getImage());
      if(imageData.getInputStream() == null) continue;
      setImage2Meta(imageData, meta);

//      long imgLength = imageInputStream.getTotalRead();
//      logSaver.updateTotalDownload(source, time, imgLength);

      try {
        imageData.getInputStream().close();
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
//        LogWebsite.getInstance().setMessage(link.getSource(), e, null);
      }

      if(meta.getImage() != null) return ;
    }
    return ;
  }

 /* private int getWidth(Attributes attributes) {
    Attribute widthAttr = attributes.get("width");
    if(widthAttr != null) {
      try {
        return Integer.parseInt(widthAttr.getValue().trim());
      } catch (Exception e) {
      }
    }
    widthAttr = attributes.get("style");
    if(widthAttr != null) {
      String value  = widthAttr.getValue();
      if(value != null) {
        String [] elements = value.split(";");
        value = null;
        for(int i = 0 ; i < elements.length; i++) {
          elements[i] = elements[i].toLowerCase();
          if(!elements[i].startsWith("width")) continue;
          value  = elements[i];
          break;
        }
        if(value != null) {
          elements = value.split(":");
          if(elements.length > 1) {
            elements[1] = elements[1].trim();
            if(elements[1].endsWith("px")) {
              elements[1] = elements[1].substring(0, elements[1].length()-2);
            }
            try {
              return Integer.parseInt(elements[1]);
            } catch (Exception e) {
            }
          }
        }
      }
    }
    return -1;
  }*/

  public void setNodePaths(NodePath[] nodePath) { this.nodePaths = nodePath; }

  public ImageInputStream read(ImageData imageData, String referer) throws Exception {
    if(httpHandler != null) httpHandler.abort();
    httpHandler = new HttpMethodHandler(webClient);

      HttpResponse response = httpHandler.execute(imageData.getLink(), referer);
      if(response == null) return null;

      int statusCode = response.getStatusLine().getStatusCode();
      InputStream inputStream = null;
      if(statusCode == HttpStatus.SC_NOT_FOUND) {
        inputStream = null;
      } else  if(statusCode == HttpStatus.SC_OK 
          || statusCode == HttpStatus.SC_ACCEPTED) {
        inputStream = getInputStream(response.getEntity());
      } else {
        inputStream = null;
      }
      readImageInfo(response, imageData);
      //set data
      ImageInputStream imageInputStream = new ImageInputStream(inputStream); 
      imageData.setInputStream(inputStream);
      return imageInputStream;
    //    imageData.getImage().setImage(data);
  }

  private InputStream getInputStream(HttpEntity entity) throws Exception {
    InputStream inputStream = entity.getContent();
    int encType = getContentEncodingType(entity.getContentEncoding());
    if(encType < 0) return inputStream;
    if(encType == 0) new InflaterInputStream(inputStream, new Inflater(true));
    return new GZIPInputStream(inputStream);
  }

  private int getContentEncodingType(Header header) {
    if(header == null) return -1;
    String encoding = header.getValue();
    if(encoding == null || (encoding = encoding.trim()).isEmpty()) return -1;

    if (encoding.equals ("gzip")) return 1;
    // DEFLATE
    if (encoding.equals ("deflate")) return 0;
    return -1;
  }

}

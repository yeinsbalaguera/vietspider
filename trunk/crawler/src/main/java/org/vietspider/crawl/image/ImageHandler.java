/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.NodePath;
import org.vietspider.io.ImageInfo;
import org.vietspider.model.Source;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 11, 2007  
 */
public abstract class ImageHandler {
  
  protected WebClient webClient;

  protected HttpMethodHandler httpHandler = null;
//  protected LinkedList<Image> images = new LinkedList<Image>();
  
  public ImageHandler(WebClient webClient) {
    this.webClient = webClient;
  }
  
  public abstract boolean download(HTMLNode root, List<HTMLNode> nodes, PluginData pluginData);
  
  protected void readImageInfo(HttpResponse response, ImageData imageData) {
    // read response header
    Header header =  response.getFirstHeader("Content-Type");
    String contentType = header != null ? header.getValue() : "image/jpeg";
    String name = null;
    String address = imageData.getLink();
    if(address.indexOf('/') > -1) {
      name = address.substring(address.lastIndexOf('/')+1);
    } else {
      name = address;
    }
    
    try{
      name = rename(name);
    }catch (Exception e) {
      name = String.valueOf(imageData.getCounter());
    }
    if(name != null && name.trim().length() > 1) imageData.getImage().setName(name);

    if(contentType == null || contentType.trim().length() < 1) {
      if(name != null && name.indexOf('.') > -1) {
        contentType = "image/"+name.substring(name.lastIndexOf('.')+1);
      } else {
        contentType = "image/jpg";
      }
    }
    imageData.getImage().setType(contentType);
  }
  
  protected String rename(String name) throws Exception {
    char [] chars = URLDecoder.decode(name, Application.CHARSET).toCharArray();
    char [] specs = {'-', '\\', '?', '|', '"', '=', '<', '>'};
    int i = 0;
    while(i < chars.length){
      for(char c : specs){
        if(chars[i] != c) continue;
        chars[i] = '.';
        break;
      }
      i++;
    }
    return new String(chars);
  }
  
  public void setImage2Meta(ImageData imageData, Meta meta) {
    if(meta.getImage() != null /*|| data == null || data.length < 1*/) return;    
    
    InputStream inputStream = imageData.getInputStream();
    ImageInfo imageInfo = new ImageInfo();
    try {
      if(inputStream != null) {
        imageInfo.setInput(inputStream);
      } else {
        byte [] data = imageData.getImage().getImage();
        imageInfo.setInput(new ByteArrayInputStream(data));
      }
      if (!imageInfo.check()) return;
    } catch (Exception e) {
      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e1) {
        LogService.getInstance().setThrowable(e);
      }
    }
//    System.out.println(imageInfo.getFormatName() + ", " + imageInfo.getMimeType() + 
//      ", " + imageInfo.getWidth() + " x " + imageInfo.getHeight() + " pixels, " + 
//      imageInfo.getBitsPerPixel() + " bits per pixel, " + imageInfo.getNumberOfImages() +
//      " image(s), " + imageInfo.getNumberOfComments() + " comment(s).");
//    System.out.println(imageInfo.getWidth()+ " : "+ imageInfo.getHeight());
    if(imageInfo.getWidth() < 50 || imageInfo.getHeight() < 50) return;
    meta.setImage(imageData.getLink());      
  }

  protected void searchImageNodes(HTMLNode root, List<HTMLNode> list){
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.IMG)) list.add(n);
    }
//    if(node.isNode(Name.IMG)) list.add(node);
//    List<HTMLNode> children = node.getChildren();
//    if(children == null) return;
//    for(HTMLNode ele : children) searchImageNodes(ele, list);
  }
  
  public String createLink(Link refer, String value) {
    URLUtils urlUtils = new URLUtils();
    if(refer.getBaseHref() != null ) {
      try {
        return urlUtils.createURL(new URI(refer.getBaseHref()).normalize().toURL(), value);
      }catch (Exception e) {
        return urlUtils.createURL(refer.getBaseHref(), value);
      }
    } 
    
    try {
      String link = refer.getAddress();
      if(link.startsWith("http://") || link.startsWith("shttp://") || link.startsWith("https://")) {
        return urlUtils.createURL(new URL(link), value);
      } 
      link = urlUtils.createURL(link, value);
      Source source = CrawlingSources.getInstance().getSource(refer.getSourceFullName());
      String addressHome = source.getHome()[0];
      return urlUtils.createURL(new URL(addressHome), link);//href.getSource().getHome()[0]
    }catch (Exception e) {
      return urlUtils.createURL(refer.getAddress(), value);
    }
  }
  
  @SuppressWarnings("unused")
  public void setNodePaths(NodePath[] nodePath) { }
  
//  public void saveImageToDatabase() {
//    while(!images.isEmpty()) {
//      Image image = images.poll();
//      try {
//        DatabaseService.getSaver().save(image);
//      }catch (Exception e) {
//        LogService.getInstance().setThrowable(e);
//      }
//    }
//   
//  }
  
  public static class ImageInputStream extends InputStream {
    
    private InputStream input;
    private long totalRead = 0;
    
    public ImageInputStream(InputStream input_) {
      this.input = input_;
    }
    
    @Override
    public int available() throws IOException {
      return input.available();
    }
    
    @Override
    public void close() throws IOException {
      input.close();
    }
    
    @Override
    public synchronized void mark(int readlimit) {
      input.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
      return input.markSupported();
    }
    
    @Override
    public int read() throws IOException {
      int read = input.read();
      if(read > -1) totalRead++;
      return read;
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int read = input.read(b, off, len);
      if(read > -1) totalRead += read;
      return read;
    }
    
    @Override
    public int read(byte[] b) throws IOException {
      int read = input.read(b);
      if(read > -1) totalRead += read;
      return read;
    }
    
    @Override
    public synchronized void reset() throws IOException {
      input.reset();
    }
    
    @Override
    public long skip(long n) throws IOException {
      return input.skip(n);
    }

    public long getTotalRead() {
      return totalRead;
    }
  }
  
  public void abort() { 
    if(httpHandler != null) httpHandler.abort();
  }
  
}

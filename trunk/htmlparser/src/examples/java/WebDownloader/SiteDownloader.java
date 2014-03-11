/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.SpecChar;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.DataWriter;
import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.net.client.HttpHandlers;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * May 30, 2007
 */
public class SiteDownloader {
  
  private File folder;
  private URL url;
  
  private WebClient webClient = new WebClient();
  private URLUtils urlEncoder = new URLUtils();
  privateorg.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
  private URLUtils urlUtils = new URLUtils();

  public SiteDownloader() throws Exception {
  }

  public void crawl(String address, String path, int depth) throws Exception {
    folder = new File(path);
    url = new URL(address);
    webClient.setURL(address, url);
    crawl(url, address, toName(address), 0, depth);
  }
  
  private byte[] download(String referer, String address) throws Exception {
    HttpGet httpGet = null;
    try {
      address = urlEncoder.getCanonical(address);
      httpGet = webClient.createGetMethod(address, referer);      

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

      HttpResponseReader httpResponseReader = HttpHandlers.getInstance().createReader();
      return httpResponseReader.readBody(httpResponse);
    } catch(Exception exp) {
      throw exp;
    }
  }
  
  private String loadResource(String referer, String address, File file) throws Exception {
    byte [] bytes = download(referer, address);
    if(bytes == null) return  address;
    writer.save(file, bytes);
    return address;
  }

  public void crawl(URL parent, String address, String name, int level ,int depth) throws Exception {
    File file = new File(folder, name+".html");
    if(file.exists()) return;
    if(level == depth) return ;
    System.out.println("\nstart download "+ address +" level "+level +" depth "+depth +" ...");
    byte [] bytes = download(parent.toString(), address);
    if(bytes == null || bytes.length < 0) return;
    HTMLParserDetector parser = new HTMLParserDetector();
    String charset = parser.detectCharset(bytes);
    char [] chars = CharsDecoder.decode(charset, bytes, 0, bytes.length);

    List<NodeImpl> tokens = parser.createTokens(chars);

    List<Resource> resources = new ArrayList<Resource>();
//    resources.add(new Resource("img", "src"));
//    resources.add(new Resource("link", "href"));
//    resources.add(new Resource("script", "src"));
    downloadResources(address, tokens, resources);

    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl token = tokens.get(i);
      if(token.getType() != TypeToken.TAG || !token.isNode(Name.A)) continue;
      Attributes attributes = token.getAttributes();
      Attribute attribute = attributes.get("href");
      if(attribute == null) continue;
      String link  = attribute.getValue();
      if(link == null || link.trim().length() < 1) continue;
      link  = urlUtils.createURL(parent, link);
      String subName = "";
      try {
        URL subUrl = new URL(link);
        String temp = subUrl.getPath();
        if(temp != null) subName += temp;
        temp = subUrl.getQuery();
        if(temp != null) subName += "_"+temp;
        subName = toName(subName);
      }catch (Exception e) {
      }
      if(subName == null || subName.trim().length() < 1) subName = toName(link);
      try{
        crawl(new URL(address), link, subName, level+1, depth);
      }catch (Exception e) {
        System.err.println(e);
      }
      attribute.setValue(subName+".html");
      attributes.set(attribute);
    }

    
    StringBuilder contentBuilder = new StringBuilder();
    for(int i = 0; i < tokens.size(); i++) {
      buildValue(contentBuilder, tokens.get(i));
    }
    
    FileOutputStream output = new FileOutputStream(file);
    output.write(contentBuilder.toString().getBytes("utf-8"));
    output.close();
  }
  
  private void buildValue(StringBuilder builder, NodeImpl node){
    builder.append(SpecChar.n);
    
    Name name = node.getName();
    char [] value = node.getValue();
    
    if(name == Name.CONTENT || name == Name.COMMENT) {
      builder.append(value);
      return;
    }
    
    builder.append('<');
    if(node.getType() == TypeToken.CLOSE) builder.append('/');
    builder.append(value);
    builder.append('>');
  }

  public void downloadResources(String referer, List<NodeImpl> tokens, List<Resource> resources) {
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl token = tokens.get(i);
      if(token.getType() != TypeToken.TAG) continue;
      for(Resource resource : resources) {
        if(!token.isNode(resource.tag))  continue;
        try {
          Attributes attributes = token.getAttributes();
          for(Attribute attribute : attributes) {
            if(!attribute.getName().equalsIgnoreCase(resource.attr))  continue;
            String rscName = toName(attribute.getValue());
            File file = new File(folder, rscName);
            String link = urlUtils.createURL(url, attribute.getValue());
            System.out.println("downloading "+link+" ...");
            if(!file.exists()) loadResource(referer, link, file);
            attribute.setValue(rscName);
            attributes.set(attribute);
          }
        } catch (Exception e) {
          System.err.println(e);
        }
      }
    }
  }

  private String toName(String name) {
    name  = name.replace('?', '_');
    name  = name.replace('/', '_');
    name  = name.replace('\\', '_');
    name  = name.replace(':', '_');
    name  = name.replace('"', '_');
    name  = name.replace('<', '_');
    name  = name.replace('>', '_');
    name  = name.replace('|', '_');
    return name;
  }

  private static class Resource {
    
    String tag;
    String attr;

    private Resource(String tag, String attr) {
      this.tag = tag;
      this.attr = attr;
    }
  }

  public static void main(String[] args) throws Exception  {
    SiteDownloader downloader = new SiteDownloader();
    URL url = SiteDownloader.class.getResource("/");
    File file  = new File(url.toURI());
    file  = new File(file, "web/");
    file.mkdirs();
    System.out.println(" save to "+ file.getAbsolutePath());
    downloader.crawl("http://botd.wordpress.com/top-posts/?lang=vi", file.getAbsolutePath(), 2);
  }

}

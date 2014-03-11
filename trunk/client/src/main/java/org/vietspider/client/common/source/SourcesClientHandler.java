/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common.source;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.common.Application;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.text.VietComparator;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.model.SourceFileFilter;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.server.URLPath;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2007  
 */
@SuppressWarnings("serial")
public class SourcesClientHandler extends BasicSourceClientHandler {
  
  public static final int SOURCE_NORMAL = 0;
  public static final int SOURCE_TEMPLATE = 1;

  public SourcesClientHandler(String group) throws Exception {
    super(group);
  }
  
  public String [] loadCategories() throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", "sources/sources/"+group+"/")
    };
    
    return toElements(headers, new CommonFileFilter.Folder());
  }

  public String [] getCategories(String name) throws Exception {
    String [] categories = loadCategories();
    List<String> values = new ArrayList<String>();
    for(String category : categories) {
      Source source = loadSource(category, name);
      if(source == null) continue;
      values.add(category);
    }
    return values.toArray(new String[values.size()]);
  }

  public void createCategory(String category) throws Exception {
    category = NameConverter.encode(category);
    
    Header [] headers = new Header[] {
        new BasicHeader("action", "create.folder"), 
        new BasicHeader("file", "sources/sources/"+group+"/"+category+"/")
    };
    connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
  }

  public void deleteCategories(String [] categories) throws Exception {
    if(categories == null || categories.length < 1)return;
    
    Header deleteIndexHeader = new BasicHeader("action", "source.index.delete.category");
    
    for(String category : categories) {
      if(group.equals(Group.DUSTBIN)) {
        Header [] headers = new Header[] {
            new BasicHeader("action", "delete"), 
            new BasicHeader("file", "sources/sources/"+group+"/"+NameConverter.encode(category))
        };
        connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
        continue;
      } 
      
      Header [] headers = new Header[] {
          new BasicHeader("action", "sources.move.dustbin"), 
          new BasicHeader("file", "sources/sources/"+group+"/"+NameConverter.encode(category)+"/")
      };
      connector.post(URLPath.FILE_HANDLER, new byte[0], headers);

      byte [] bytes = (group+"\n"+category).getBytes(Application.CHARSET);
      connector.post(URLPath.DATA_HANDLER, bytes, deleteIndexHeader);
    }
  }
  
  public String [] loadSources() throws Exception {
    Header [] headers = new Header[] {
      new BasicHeader("action", "source.search.name"),
    };
    byte [] bytes = group.getBytes(Application.CHARSET);
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return new String(bytes, Application.CHARSET).split("\n");
  }
  
  public String [] loadSources(String category) throws Exception {
    return loadSources(category, SOURCE_NORMAL);
  }

  public String [] loadSources(String category, int type) throws Exception {
    category = NameConverter.encode(category);
    
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", "sources/sources/"+group+"/"+category)
    };
    
    return toElements(headers, new SourceFileFilter(type == SOURCE_TEMPLATE));
  }
  
  public String [] loadDisableSources(String category) throws Exception {
    Header [] headers = new Header[] {
      new BasicHeader("action", "load.disable.source"),
    };
    byte [] bytes = (group+"."+category+".").getBytes(Application.CHARSET);
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return new String(bytes, Application.CHARSET).split("\n");
  }
  
  public String [] loadSourceVersions(String category, String name) throws Exception {
    if(group == null || category == null || name == null) return new String[0];
    category = NameConverter.encode(category);
    name = category +"."+NameConverter.encode(name);
      
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.version"),
        new BasicHeader("file", "sources/sources/"+group+"/"+category+"/"+name)
    };
    
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
    return new String(bytes, Application.CHARSET).trim().split("\n");
  }
  
  public Source loadSource(String category, String name) throws Exception {
    return loadSource(category, name, 0);
  }
  
  public Source loadSource(String category, String name, int version) throws Exception {
    if(group == null || category == null || name == null) return null;
    category = NameConverter.encode(category);
    name = category + "." + NameConverter.encode(name);
      
    if(version != 0) name = name+".v."+ String.valueOf(version);
    
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "sources/sources/"+group+"/"+category+"/"+name)
    };
    
    HttpData httpData = connector.loadResponse(URLPath.FILE_HANDLER, new byte[0], headers);
    byte [] bytes = null;
//    long lastModified = -1;
    try {
      if(httpData == null) return null;
      bytes =  new GZipIO().load(httpData.getStream());
      // connector.readBytes(httpData.getResponse());
//      Header header = httpData.getResponse().getFirstHeader("last-modified");
//      try {
//        if(header != null)  {
//          lastModified = Long.parseLong(header.getValue());
//        } else {
//          lastModified = System.currentTimeMillis();
//        }
//      } catch (Exception e) {
//      }
    } finally {
      connector.release(httpData);
    }
    
    if(bytes == null) return null;
    
//    byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
    
    String xml = new String(bytes, Application.CHARSET);
    if(xml.trim().length() < 1 || xml.trim().equals("-1")) return null;
    XMLDocument document = XMLParser.createDocument(xml, null);
    Source source = XML2Object.getInstance().toObject(Source.class, document);
    
    try {
      headers = new Header[] {
          new BasicHeader("action", "source.crawl.time")
      };
      StringBuilder builder = new StringBuilder(source.getName()).append('.');
      builder.append(source.getGroup()).append('.').append(source.getCategory());
      
      bytes = builder.toString().getBytes(Application.CHARSET);
      bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
      source.setLastCrawledTime(Long.parseLong(new String(bytes)));
    } catch (Exception e) {
    }
    
    return source;
  }
  
  public String [] searchSourceByHost(String url) throws Exception {
    Header header = new BasicHeader("action", "source.search.host");
    byte [] bytes = (group+"\n"+url).getBytes();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, header);
    return new String(bytes, Application.CHARSET).split("\n");
  }
  
  public String [] searchSourceByURL(String url) throws Exception {
    Header header = new BasicHeader("action", "source.search.url");
    byte [] bytes = url.getBytes();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, header);
    return new String(bytes, Application.CHARSET).split("\n");
  }

  public void deleteSources(String category, String [] sources) throws Exception {
    if(category == null || category.trim().length() < 1) return ;
    if(sources == null || sources.length < 1) return ;
    
    Header deleteIndexHeader = new BasicHeader("action", "source.index.delete");
    
    String encCategory = NameConverter.encode(category);
    
    for(String source : sources) {
      String encName = NameConverter.encode(source);
      String name = encCategory + "." + encName;
      if(group.equals(Group.DUSTBIN)) {
        Header [] headers = new Header[] {
            new BasicHeader("action", "delete"), 
            new BasicHeader("file", "sources/sources/DUSTBIN/"+encCategory+"/"+name)
        };
        connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
        continue;
      } 
      
      Header [] headers = new Header[] {
          new BasicHeader("action", "sources.move.dustbin"), 
          new BasicHeader("file", "sources/sources/"+group+"/"+encCategory+"/"+name)
      };
      connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
      
   /*   //delete homepage codes
      headers = new Header[] {
          new BasicHeader("action", "delete"), 
          new BasicHeader("file",  "sources/homepages/codes/"+group+"/"+encCategory+"/"+name)
      };
      connector.post(URLPath.FILE_HANDLER, new byte[0], headers);*/
      
      //delete homepage tracker
      headers = new Header[] {
          new BasicHeader("action", "delete"), 
          new BasicHeader("file",  "sources/homepage/"+group+"."+name)
      };
      connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
      
     /* //delete homepage
      HomepagesClientHandler homepageHandler = new HomepagesClientHandler();
      int total = homepageHandler.loadTotalHomepages(group, category, source);
      String folder = "sources/homepages/url/"+group+"/"+encCategory+"/";
      for(int i = 1; i <= total; i++) {
        headers = new Header[] {
            new BasicHeader("action", "delete"), 
            new BasicHeader("file", folder+name+".homepages."+String.valueOf(i))
        };
        connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
      }*/

      byte [] bytes = (group+"."+category+"."+source).getBytes(Application.CHARSET);
      connector.post(URLPath.DATA_HANDLER, bytes, deleteIndexHeader);
    }
  }
  
  public void saveConfigAsName(String category, String name) throws Exception {
    Source source = loadSource(category, name);
    String [] categories = loadCategories();
    for(String cate : categories) {
      Source newSource = loadSource(cate, name);
      if(newSource == null) continue;
      copyConfig(source, newSource);
      saveSource(newSource);
    }
  }
  
  public void saveConfigAsCategory(String category, String name) throws Exception {
    Source source = loadSource(category, name);
    String [] names = loadSources(category, SOURCE_NORMAL);
    for(String element : names) {
      int idx = element.indexOf('.');
      if(idx > 0) element = element.substring(idx+1);
      Source newSource = loadSource(category, element);
      if(newSource == null) continue;
      copyConfig(source, newSource);
      saveSource(newSource);
    }
  }
  
  private void copyConfig(Source source, Source newSource) throws Exception {
    newSource.setUpdateRegion(source.getUpdateRegion());
    newSource.setExtractRegion(source.getExtractRegion());
    newSource.setProcessRegion(source.getProcessRegion());
    
    newSource.setDepth(source.getDepth());
    newSource.setPriority(source.getPriority());
    newSource.setEncoding(source.getEncoding());
    newSource.setExtractType(source.getExtractType());
    
    newSource.setProperties(source.getProperties());
  }
  
  /*private void copyTemplate(Source template, Source newSource) throws Exception {
    if(isEmptyPath(newSource.getUpdateRegion())) {
      newSource.setUpdateRegion(template.getUpdateRegion());
    }
    
    if(isEmptyPath(newSource.getExtractRegion())) {
      newSource.setExtractRegion(template.getExtractRegion());
    }
    
    if(isEmptyPath(newSource.getProcessRegion())) {
      newSource.setProcessRegion(template.getProcessRegion());
    }
    
    if(newSource.getDepth() <= template.getDepth()) {
      newSource.setDepth(template.getDepth());
    }
    
    if(newSource.getPriority() <= template.getPriority()) {
      newSource.setPriority(template.getPriority());
    }
    
    newSource.setEncoding(template.getEncoding());
    newSource.setExtractType(template.getExtractType());
    
    newSource.setProperties(template.getProperties());
  }*/
  
 /* private boolean isEmptyPath(Region...paths) {
    if(paths == null || paths.length < 1) return true;
    for(Region path : paths) {
      if(path == null) continue;
      String [] data = path.getPaths();
      if(data != null 
          && data.length > 0 
          && data[0] != null
          && !data[0].trim().isEmpty()) return false;
    }
    return true;
  }*/
  

  public void saveSource(Source source) throws Exception {
    saveSource(connector, source);
  }
  
  /*public void saveSource(Source source, String template) throws Exception {
    template = template+SourceFileFilter.TEMPLATE_SUFFIX;
    Source templateSource = loadSource(source.getGroup(), source.getCategory(), template);
    copyTemplate(templateSource, source);
    if(templateSource.getPattern() != null &&
        !templateSource.getPattern().trim().isEmpty() &&
        (source.getPattern() == null || source.getPattern().trim().isEmpty())) {
        source.setPattern(templateSource.getPattern());
    }
    
    RemoteSourceClientHandler remote = new RemoteSourceClientHandler();
    remote.saveSource(connector, source);
  }*/
  
  private String [] toElements(Header[] headers, CommonFileFilter fileFilter) throws Exception {
    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
    objectOutputStream.writeObject(fileFilter);
    objectOutputStream.close();
  
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, bytesOutput.toByteArray(), headers);
//    System.out.println(new String(bytes));
    
    String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
    for(int i = 0; i < elements.length; i++) {
      elements[i]  = NameConverter.decode(elements[i]);
    }
    
    Arrays.sort(elements, new  VietComparator());
    return elements;
  }
  
}

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.db.website.WebsiteDatabases;
import org.vietspider.io.model.SourceIndexerService;
import org.vietspider.io.model.SourceSearcher;
import org.vietspider.model.Source;
import org.vietspider.model.SourceUtils;
import org.vietspider.serialize.Object2XML;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 1, 2009  
 */
public class GenerateWebsiteExecutor   {

  private SourceSearcher sourceSearcher;
  private WebsiteDatabases databases;

  public GenerateWebsiteExecutor(WebsiteDatabases databases) {
    this.databases = databases;
    this.sourceSearcher = new SourceSearcher();
  }

  public void generate(String lang) throws Exception {
    try {
      int page  = 1;
      while(true) {
        Websites websites = new Websites();
        websites.setStatus(Website.CONFIGURATION);
        websites.setLanguage(lang);
        websites.setPage(page);
        websites.setPageSize(100);
        databases.load(websites);
        
        for(int i = 0; i < websites.getList().size(); i++) {
          String line  = generate(websites.getList().get(i));
          if(line == null) continue;
        }

        if(websites.getPage() >= websites.getTotalPage()) break;

        page++;
      }

    } finally {
    }
  }

  public String generate(Website website) {
    String group = getGroup(website);
    if(group == null) return null;
    String full = checkExists(website);
    if(full != null) return full;
    return saveSource(website, group, true);
  }
  
  private String checkExists(Website website) {
    String sourceName = website.getSource();
    if(sourceName ==  null) return null;
    
    String [] elements = sourceName.split("\\.");
    if(elements.length < 3) return null;

    String group = elements[0];
    String category = elements[1];
    NameConverter convert = new NameConverter();
    String name = category +"."+convert.encode(elements[2]);
    File folder = UtilFile.getFolder("sources/sources/");
    File file = new File(folder, group+"/"+category+"/"+name);
    if(!file.exists() || file.length() < 10) return null;

    StringBuilder builder = new StringBuilder(elements[2]).append('.');
    builder.append(elements[0]).append('.').append(elements[1]);
    return builder.toString();
  }

  private String[] generateHomepages(Website website) {
    Set<String> homepages = new HashSet<String>();
    homepages.add(website.getAddress());
    homepages.add("http://"+website.getHost());
    Collections.addAll(homepages, website.getHomepages()) ;
    return homepages.toArray(new String[homepages.size()]);
  }

  private String getGroup(Website website) {
    String sourceName = website.getSource();
    if(website.getStatus() != Website.CONFIGURATION) return null;
    if(sourceName == null || sourceName.trim().isEmpty()) return "SITE";
    if(sourceName.startsWith("BLOG_SITE") 
        || sourceName.startsWith("BLOG.BLOG_")) return "BLOG";
    if(sourceName.startsWith("SITE") 
        || "WEB".equals(sourceName) 
        || "SITE".equals(sourceName) ) return "SITE";
    return null;
  }

  private String saveSource(Website website, String group, boolean index) {
    try {
      if(existSource(website.getHost())) return null;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(website.getHost(), e);
    }
    Source source = new Source();

    String category = searchCategory(group);

    source.setGroup(group);
    source.setCategory(category);

    source.setName(SourceUtils.getFileName(website.getAddress()));

    String fileName = category +"."+source.getName();
    File file = new File(UtilFile.getFolder("sources/sources/"+group+"/"+category+"/"), fileName);
    if(file.exists()) return null;

    source.setHome(generateHomepages(website));

    source.getProperties().put("Referer", website.getAddress());
    source.setDepth(10);
    source.setPriority(720);
    String charset = website.getCharset();

    if(charset != null  
        && !(charset = charset.trim()).isEmpty()) {
      source.setEncoding(charset);
    } else {
      source.setEncoding("");  
    }

    try {
      String xml = Object2XML.getInstance().toXMLDocument(source).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      RWData.getInstance().save(file, bytes);

      String pathIndex  = group+"/"+category+"/"+fileName;
      if(index) {
        SourceIndexerService.getInstance().put(pathIndex, SourceIndexerService.SAVE);
      }

      website.setSource(source.getFullName());
      databases.save(website);

      StringBuilder builder = new StringBuilder(source.getName()).append('.');
      builder.append(source.getGroup()).append('.').append(source.getCategory());
      return builder.toString();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(source, e);
    }
    return null;
  }

  private String searchCategory(String group) {
    int index = 0;
    while(true) {
      String category = group+"_"+String.valueOf(index);
      File folder = UtilFile.getFolder("sources/sources/"+group+"/"+category+"/");
      File [] files = folder.listFiles();
      if(files == null || files.length < 100) return category;
      index++;
    }
  }

  private boolean existSource(String host) throws Exception {
    List<String> list = sourceSearcher.searchByURL(host);
    if(list.size() < 1)  return false;
    String fullName = list.get(0);
    String [] elements = fullName.split("\\.");
    if(elements.length < 3) return false;

    String group = elements[0];
    String category = elements[1];
    NameConverter convert = new NameConverter();
    String name = category +"."+convert.encode(elements[2]);

    File file = UtilFile.getFile("sources/sources/"+group+"/"+category+"/", name);
    return file.exists() && file.length() > 0;
  }

}

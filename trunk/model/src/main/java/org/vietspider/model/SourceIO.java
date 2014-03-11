/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.File;
import java.nio.channels.ClosedByInterruptException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 11, 2009  
 */
public class SourceIO extends Thread {
  
  private volatile static SourceIO sourceIO;
  
  public final static synchronized  SourceIO getInstance() {
    if(sourceIO == null) sourceIO = new SourceIO();
    return sourceIO;
  }

  private ConcurrentHashMap<String, Source> temp;
  private HashSet<String> disables = new HashSet<String>();
  private boolean updateDisableSource = false;
  
  public SourceIO() {
    temp = new ConcurrentHashMap<String, Source>();
    Application.addShutdown(new Application.IShutdown() {
      
      public String getMessage() {
        return "";
//        if(homepage == null) return "";
//        return homepage.getSource()+ " : " + getClass().getName(); 
      }
      
      public void execute() {
//        if(homepage == null) return;
        saveSource();//sessionStore.getQueue().size());
        saveDisableSource();
      }
    });
    loadDisableSource();
    this.start();
  }
  
//  private void save(Source source) {
//    if(source == null) return;
//    String category = NameNameConverter.encode(source.getCategory());
//    String group = source.getGroup().toString();
//    String name = category + "." + NameNameConverter.encode(source.getName());
//    String path = group+"/"+category+"/"+name;
//    temp.put(path, source);
//  }
  
  public Source saveProperty(Source source, String propertyName, String propertyValue) {
    if(source == null || source.getName() == null) return source;
    String category = NameConverter.encode(source.getCategory());
    String group = source.getGroup().toString();
    String name = category + "."+ NameConverter.encode(source.getName());
    String path = group+"/"+category+"/"+name;
    Source tempSource = temp.get(path);
    if(tempSource == null) tempSource = source;
    tempSource.getProperties().put(propertyName, propertyValue);
    temp.put(path, tempSource);
    return tempSource;
  }
  
  public void savePriority(Source source, int priority) {
    String category = NameConverter.encode(source.getCategory());
    String group = source.getGroup().toString();
    String name = category +"."+ NameConverter.encode(source.getName());
    String path = group+"/"+category+"/"+name;
    Source tempSource = temp.get(path);
    if(tempSource == null) tempSource = source;
    tempSource.setPriority(priority);
    temp.put(path, tempSource);
  }
  
  public Source loadSource(String line) {
    if(line == null) return null;
    String [] elements = line.split("\\.");
    if(elements.length < 3) return null;

    String group = NameConverter.encode(CharsUtil.cutAndTrim(elements[0]));
    String category = NameConverter.encode(CharsUtil.cutAndTrim(elements[1]));
    String name  = category+"."+NameConverter.encode(CharsUtil.cutAndTrim(elements[2]));
    
    return loadSource(group, category, name);
  }
  
  public Source reload(Source source) {
    String group = source.getGroup();
    String category = source.getCategory();
    String name = source.getName();
   
    group = NameConverter.encode(CharsUtil.cutAndTrim(group));
    category = NameConverter.encode(CharsUtil.cutAndTrim(category));
    name  = category + "." + NameConverter.encode(CharsUtil.cutAndTrim(name));
    
    File folder = UtilFile.getFolderNotCreate("sources/sources/"+group+"/"+category+"/");
    if(folder == null || !folder.exists()) return null;
    File file = new File(folder, name);
    if(!file.exists()) return null;
    
    if(file.lastModified() <= source.getCreatedTime() )  return source;
    return load(file);
  }
  
  
  public Source loadSource(String group, String category, String name) {
    String path = group+"/"+category+"/"+name;
    Source source = temp.get(path);
    if(source != null) return source;
    
    File folder = UtilFile.getFolderNotCreate("sources/sources/"+group+"/"+category+"/");
    if(folder == null || !folder.exists()) {
      return null;
    }
    File fileSource = new File(folder, name);
    if(!fileSource.exists()) return null;
    return load(fileSource);
  }
  
  public Source load(File fileSource) {
    try {
      if(!fileSource.exists() || fileSource.length() < 1) return null;
      byte [] bytes = RWData.getInstance().load(fileSource);
      if(bytes == null || bytes.length < 1) return null;
      Source source = XML2Object.getInstance().toObject(Source.class, bytes);
//      XML2Source xml2Source = new XML2Source();
//      Source source = xml2Source.toSource(bytes);
      if(source == null) return null;
      if(source.getLastModified() < 0) {
        source.setLastModified(fileSource.lastModified());
      }
      return source;
      
     /* String xml = new String(RWData.getInstance().load(fileSource), Application.CHARSET);
      if(xml == null || xml.trim().length() < 1) return null;
      XMLDocument document = XMLParser.createDocument(xml, null);
      return XML2Object.getInstance().toObject(Source.class, document);*/
    } catch (ClosedByInterruptException e) {
      return null;
    } catch (Exception e) {
      
      LogService.getInstance().setThrowable(fileSource.getName(), e);
    }
    return null;
  }
 
  public void run() {
    while(true) {
      saveSource();
      saveDisableSource();
      try {
        Thread.sleep(15*1000);
      }catch (Exception e) {
      } 
    }
  }
  
  private void saveSource() {
    Iterator<String> iterator = temp.keySet().iterator();
    while(iterator.hasNext()) {
      String path = iterator.next();
      Source source = temp.get(path);
      save(path, source);
      iterator.remove();
    }
  }
  
  private void save(String path, Source source) {
    if(source == null) return;
    File file = UtilFile.getFile("sources/sources/", path);
    if(file.lastModified() - source.getCreatedTime() > 10)  return;
    
    try {
//      System.out.println(" save vao source "+ file.getAbsolutePath());
      String xml = Object2XML.getInstance().toXMLDocument(source).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      RWData.getInstance().save(file, bytes);
      source.setCreatedTime(file.lastModified());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(source, e);
    }
  }
  
  private void saveDisableSource() {
    if(!updateDisableSource) return ;
    File file = UtilFile.getFile("sources/type/", "disable");
    StringBuilder builder = new StringBuilder();
    Iterator<String> iterator = disables.iterator();
    TextSpliter spliter = new TextSpliter();
    while(iterator.hasNext()) {
      String name = iterator.next();
      if(builder.length() > 0) builder.append('\n');
      
      String [] elements = spliter.toArray(name, '.');
      if(elements.length != 3) continue;
      for(int i = 0; i < elements.length; i++) {
        elements[i] = NameConverter.encode(elements[i]);
      }
      
      File folder = UtilFile.getFolderNotCreate(
          "sources/sources/" + elements[0] + "/" + elements[1] + "/");
      if(folder == null || !folder.exists()) continue;
      
      elements[2]  = elements[1] + "." +  CharsUtil.cutAndTrim(elements[2]);
      File f = new File(folder, elements[2]);
//      System.out.println(f.getAbsolutePath() + " : "+ f.exists());
      if(!f.exists()) continue;
      
      builder.append(name);
    }
    try {
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  private void loadDisableSource() {
    File file = UtilFile.getFile("sources/type/", "disable");
    String value = null;
    try {
      value = new String(RWData.getInstance().load(file), Application.CHARSET);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if(value == null) return;
    TextSpliter spliter = new TextSpliter();
    String [] elements = spliter.toArray(value, '\n');
    for(String element : elements) {
      if(element.length() < 3) continue;
      disables.add(element);
    }
  }
  
  public synchronized void putDisableSource(Source source) {
    if(source == null) return;
    disables.add(source.getFullName());
    updateDisableSource = true;
  }
  
  public synchronized void removeDisableSource(Source source) {
    if(!disables.contains(source.getFullName())) return;
    disables.remove(source.getFullName());
    updateDisableSource = true;
  }
  
  public String filterDisable(String category) {
    StringBuilder builder = new StringBuilder();
    Iterator<String> iterator = disables.iterator();
    while(iterator.hasNext()) {
      String name = iterator.next();
//      System.out.println(name + "  : "+ category + " : " + name.startsWith(category));
      if(!name.startsWith(category)) continue;
      if(builder.length() > 0) builder.append('\n');
      int idx = name.lastIndexOf('.');
      if(idx > 0) name = name.substring(idx+1);
      builder.append(name);
    }
//    System.out.println(" co " +  builder);
    return builder.toString();
  }
  
}

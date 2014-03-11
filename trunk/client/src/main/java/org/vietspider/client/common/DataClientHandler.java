/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.DBInfo;
import org.vietspider.bean.Domain;
import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.cache.InmemoryCache;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Track;
import org.vietspider.net.channel.DefaultSourceConfig;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
@SuppressWarnings("serial")
public class DataClientHandler implements Serializable {
  
  private static DataClientHandler INSTANCE;
  
  public final static synchronized DataClientHandler getInstance() {
    if(INSTANCE == null) INSTANCE = new DataClientHandler();
    return INSTANCE;
  }
  
  private InmemoryCache<String, Object> cached;
  
  private DataClientHandler() {
    cached = new InmemoryCache<String, Object>("data.client", 100); 
    cached.setLiveTime(3*60);
  }
  
  public String[] getDate() throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", "content/summary/eid/")//"track/logs/"
    };
    
    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
    objectOutputStream.writeObject(new CommonFileFilter.FolderContainsData());//new CommonFileFilter.OnlyFile()
    objectOutputStream.close();
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, bytesOutput.toByteArray(), headers);
    String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    SimpleDateFormat folderFormat = CalendarUtils.getFolderFormat();
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim(); 
      try {
        values.add(dateFormat.format(folderFormat.parse(elements[i])));
      } catch (Exception e) {
      }
    }
    
    return values.toArray(new String[values.size()]);
    
    
//    Header [] headers = new Header[] {
//        new BasicHeader("action", "load.monitor.date")
//    };
//    
//    ClientConnector2 connector = ClientConnector2.currentInstance();
//    byte [] bytes = connector.post(URLPath.DATA_HANDLER, new byte[0], headers);
//    String value = new String(bytes, Application.CHARSET);
//    return value.split("\n");
  }
  
  public String[] getDateLogs(String folder, CommonFileFilter filder) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", folder)//"track/logs/"
    };
    
    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
    objectOutputStream.writeObject(filder);//new CommonFileFilter.OnlyFile()
    objectOutputStream.close();
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, bytesOutput.toByteArray(), headers);
//    System.out.println(" list log folder "+ new String(bytes));
//    bytes = new GZipIO().unzip(bytes);
    String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
    List<String> list = new ArrayList<String>();
    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    SimpleDateFormat folderFormat = CalendarUtils.getFolderFormat();
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim(); 
      if(CommonFileFilter.Folder.class.isInstance(filder)) {
        elements[i] = dateFormat.format(folderFormat.parse(elements[i]));
        list.add(elements[i]);
        continue;
      }
      
      if(elements[i].isEmpty() || !elements[i].endsWith(".log")) continue;
      int idx = elements[i].lastIndexOf(".log");
      if(idx < 1) continue;
      elements[i] = elements[i].substring(0, idx);
      try {
        Date date = CalendarUtils.getFolderFormat().parse(elements[i]);
        list.add(CalendarUtils.getDateFormat().format(date));
      } catch (Exception e) {
        ClientLog.getInstance().setMessage(e);
      }
    }
    return list.toArray(new String[list.size()]);
  }
  
  public void exportSourceLogFromDate(String date) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "export.source.log.from.database")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    connector.post(URLPath.DATA_HANDLER, date.getBytes(), headers);
  }
  
//  public String[] loadLogSourceDates(int type) throws Exception {
//    CommonFileFilter filter = new CommonFileFilter.OnlyFile();
//    String path = "track/logs/";
//    if(type == 1) {
//      filter = new CommonFileFilter.Folder();
//      path = "track/logs/sources/";
//    } else if(type == 2) {
//        path = "track/link_log/";
//        filter = new CommonFileFilter.Folder();
//    }
//    Header [] headers = new Header[] {
//        new BasicHeader("action", "list.folder"),
//        new BasicHeader("file", path)
//    };
//    
//    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
//    ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
//    objectOutputStream.writeObject(filter);
//    objectOutputStream.close();
//    
//    ClientConnector2 connector = ClientConnector2.currentInstance();
//    byte [] bytes = connector.post(URLPath.FILE_HANDLER, bytesOutput.toByteArray(), headers);
//    String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
//    List<String> list = new ArrayList<String>();
//    for(int i = 0; i < elements.length; i++) {
//      elements[i] = elements[i].trim();
//      try {
//        Date date = CalendarUtils.getFolderFormat().parse(elements[i]);
//        list.add(CalendarUtils.getDateFormat().format(date));
//      } catch (Exception e) {
//        ClientLog.getInstance().setMessage(e);
//      }
//    }
//    return list.toArray(new String[list.size()]);
//  }
  
  public String[] loadAllSources(String group) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "user.load.sources")
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.DATA_HANDLER, group.getBytes(), headers);
    return new String(bytes, Application.CHARSET).trim().split("\n");
  }
  
  public String[] loadSourceByDate(String folder, CommonFileFilter filder) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", folder)//"track/logs/sources/"+ date)
    };
    
    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
    objectOutputStream.writeObject(filder);
    objectOutputStream.close();
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, bytesOutput.toByteArray(), headers);
    String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
      if(!elements[i].endsWith(".log")) continue;
      elements[i] = elements[i].substring(0, elements[i].length()-4);
    }
    return elements;
  }
  
  public String[] loadSummary(String path) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", path)
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    
    byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
    return new String(bytes, Application.CHARSET).trim().split("\n");
  }
  
  public Track loadTrackId(String date, int page) throws Exception {
   /* Date dateInstance = CalendarUtils.getDateFormat().parse(date);
    String fileName = CalendarUtils.getFolderFormat().format(dateInstance);
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "content/track/" + fileName)
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
    return XML2Object.getInstance().toObject(TrackId.class, bytes);*/
    
//    System.out.println(" chay vao day roi "+date);
    
    String elements [] = (String[])cached.getCachedObject("track.id." + date);
//    System.out.println(" =====  >"+ elements);
    if(elements == null) {
      Date dateInstance = CalendarUtils.getDateFormat().parse(date);
      String fileName = CalendarUtils.getFolderFormat().format(dateInstance);
      //    File folder = new File(UtilFile.getFolder("content/summary/eid"), fileName+"/");

      Header [] headers = new Header[] {
          new BasicHeader("action", "list.folder"),
          new BasicHeader("min.size", "1"),
          new BasicHeader("file", "content/summary/eid/" + fileName + "/")//"track/logs/sources/"+ date)
      };

//      ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
//      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
//      objectOutputStream.writeObject(new CommonFileFilter() {
//        @Override
//        public boolean accept(File f) {
//          return f.getName().endsWith(".eid");
//        }
//
//      });
//      objectOutputStream.close();

      ClientConnector2 connector = ClientConnector2.currentInstance();
      byte [] bytes = connector.post(URLPath.FILE_HANDLER, /*bytesOutput.toByteArray()*/new byte[0], headers);
      elements = new String(bytes, Application.CHARSET).trim().split("\n");
      
      cached.putCachedObject("track.id", elements);
    }
    
    Track track = new Track(date, Track.DATE);
    
    int pageSize = 15;
    if(page < 1) page = 1;
    int totalPage = elements.length%pageSize == 0 ? 
        elements.length/pageSize : elements.length/pageSize + 1;
    if(page > totalPage) page = 1;
    
    int start = (page-1)*pageSize;
    int end = page*pageSize;
    
//    System.out.println(start + " : "+ end);
//    if(!folder.exists() || folder.length() < 1) return trackId;
    
//    File [] files = folder.listFiles();
    TextSpliter spliter = new TextSpliter();
    for(int i = start; i < Math.min(elements.length, end); i++) {
      if(elements[i].endsWith("\\.eid")) continue;
//      System.out.println("====  >" + elements[i]);
//      String name  = files[i].getName();
      String [] names = spliter.toArray(elements[i], '.');
      if(names.length < 5) continue;
      names[2] = NameConverter.decode(names[2]);
      names[3] = NameConverter.decode(names[3]);
      Domain domain = new Domain(date, names[1], names[2], names[3]);
      track.addData(domain);
//      RandomAccessFile random = new RandomAccessFile(files[i], "rws");
//      while(random.getFilePointer() < files[i].length()) {
//        long id = random.readLong();
//        int status = random.readInt();
//        trackId.addId(domain, String.valueOf(id));
////        System.out.println(random.readLong() + " : "+ random.readInt());
//      }
//      System.out.println(files[i].getName());
    }
    
    track.getProperties().setProperty("total.page", String.valueOf(totalPage));
    
    return track;
  }
  
  public String checkDatabaseConnection(DBInfo dbinfo) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "check.database.connection")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    String xml = Object2XML.getInstance().toXMLDocument(dbinfo).getTextValue();
    byte [] bytes = xml.getBytes(Application.CHARSET);
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return new String(bytes, Application.CHARSET);
  }
  
  public String loadDatabaseSetting() throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.database.setting")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.DATA_HANDLER, new byte[0], headers);
    return new String(bytes, Application.CHARSET);
  }
  
  public void saveDBInfo(DBInfo config, String action) throws Exception {
    if(config == null) return;
    Header [] headers = new Header[] {
        new BasicHeader("action", action)
    };

    ClientConnector2 connector = ClientConnector2.currentInstance();
    String xml = Object2XML.getInstance().toXMLDocument(config).getTextValue();
    byte [] bytes = xml.getBytes(Application.CHARSET);
    connector.post(URLPath.DATA_HANDLER, bytes, headers);
  }
  
  public Properties getSystemProperties() throws Exception {
    Header header = new BasicHeader("action", "config.system.load");
    ClientConnector2 connector = ClientConnector2.currentInstance();
    
    HttpData httpData = connector.loadResponse(URLPath.DATA_HANDLER, new byte[0], header);
    InputStream stream = httpData.getStream();
    Properties properties = new Properties();
    try {
      properties.load(stream);
      stream.close();
    } finally {
      connector.release(httpData);
      stream.close();
    }
    
    return properties;
  }

  public void saveSystemProperties(Properties properties) throws Exception {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
    properties.store(arrayStream, null);
    Header header = new BasicHeader("action", "config.system.store");
    connector.post(URLPath.DATA_HANDLER, arrayStream.toByteArray(), header);
  }
  
  public void exitApplication() throws Exception {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    Header header = new BasicHeader("action", "server.exit");
    connector.post(URLPath.APPLICATION_HANDLER, new byte[0], header);
  }
  
  public void restartServer(String type, String port) throws Exception {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    Header header = new BasicHeader("action", type);
    connector.post(URLPath.APPLICATION_HANDLER, port.getBytes(), header);
  }
  
  public void saveWebsites(Websites websites) throws Exception {
    Header header = new BasicHeader("action", "save.website.list");
    ClientConnector2 connector = ClientConnector2.currentInstance();
    String xml = Object2XML.getInstance().toXMLDocument(websites).getTextValue();
    byte [] bytes = xml.getBytes(Application.CHARSET);
    connector.post(URLPath.DATA_HANDLER, bytes, header);
  }
  
  public Website load(String host) throws Exception  {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.website.by.host")
    };

    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = host.getBytes(Application.CHARSET);
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return XML2Object.getInstance().toObject(Website.class, bytes);
  }
  
  public Properties loadMailConfig() throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "system/mail.config")
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    Properties properties = new Properties();
    
    byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
//    HttpData httpData = connector.loadResponse(URLPath.FILE_HANDLER, new byte[0], headers);
//    InputStream inputStream = httpData.getStream();
    
    try {
      properties.load(inputStream);
      inputStream.close();
    } finally {
//      connector.release(httpData);
      inputStream.close();
    }
    return properties;
  }

  public void saveMailConfig(Properties properties) throws Exception {
    Header [] headers =  new Header[] {
        new BasicHeader("action", "save"),
        new BasicHeader("file", "system/mail.config")
    };

    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream();
    properties.store(bytesOutput, null);

    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = bytesOutput.toByteArray();
    connector.post(URLPath.FILE_HANDLER, bytes, headers);
  }
  
  public void saveAction(String value) throws Exception {
    Header [] headers =  new Header[] {
        new BasicHeader("action", "save.log")
    };

    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = value.getBytes(Application.CHARSET);
    connector.post(URLPath.DATA_HANDLER, bytes, headers);
  }
  
  /*public String loadSourceLogMessage(String date, String sourceName) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.source.log.desc")
    };
    String [] elements = sourceName.split("\\.");
    sourceName = elements[1] + "." +  elements[2] + "." + elements[0];
    byte [] bytes = (date + "#" + sourceName).getBytes(Application.CHARSET);
    ClientConnector2 connector = ClientConnector2.currentInstance();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return new String(bytes, Application.CHARSET);
  }*/
  
  public String analyze(String text) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "analyze.text")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = text.getBytes(Application.CHARSET);
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return new String(bytes, Application.CHARSET);
  }
  
  public void saveWordToDict(String word) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "analyze.save.word")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = word.getBytes(Application.CHARSET);
    connector.post(URLPath.DATA_HANDLER, bytes, headers);
  }
  
  public void removeWordToDict(String word) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "analyze.remove.word")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = word.getBytes(Application.CHARSET);
    connector.post(URLPath.DATA_HANDLER, bytes, headers);
  }
  
  
  public String loadIdByURL(String date, String url) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "search.id.by.url")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = new byte[0];
    if(date == null) {
      bytes = url.getBytes(Application.CHARSET); 
    } else {
     bytes = (date + "\n" + url).getBytes(Application.CHARSET);
    }
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return new String(bytes, Application.CHARSET);
  }
  
  public DefaultSourceConfig loadDoc(DefaultSourceConfig config) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "config.load.document")
    };
    
    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
    objectOutputStream.writeObject(config);
    objectOutputStream.close();
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = bytesOutput.toByteArray();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
   
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
    DefaultSourceConfig config2 = (DefaultSourceConfig)objectInputStream.readObject();
    byteInputStream.close();
    
    return config2;
  }
  
  public void clearCached(String id) {
    cached.removeCachedObject(id);
  }
  
}

/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.DBInfo;
import org.vietspider.bean.DatabaseConfig;
import org.vietspider.bean.Meta;
import org.vietspider.bean.sync.SyncDatabaseConfig;
import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.browser.FastWebClient;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.crawl.io.access.SourceTrackerService;
import org.vietspider.data.jdbc.install.DatabaseChecker;
import org.vietspider.data.jdbc.install.DatabaseSetting;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.dict.VietnameseDictionary;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.db.url.HomepageDatabase;
import org.vietspider.db.website.IWebsiteDatabases;
import org.vietspider.db.website.WebsiteDatabases;
import org.vietspider.index.analytics.ViIndexAnalyzer2;
import org.vietspider.io.model.SourceIndexerService;
import org.vietspider.io.model.SourceSearcher;
import org.vietspider.io.websites2.GenerateWebsiteScheduler;
import org.vietspider.io.websites2.WebsiteStorage;
import org.vietspider.model.SourceIO;
import org.vietspider.net.channel.DefaultSourceConfig;
import org.vietspider.net.channel.DocumentLoader;
import org.vietspider.net.server.Metas;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.user.Group;
import org.vietspider.user.User;
import org.vietspider.users.Organization;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 15, 2007  
 */
public class DataHandler extends CommonHandler {
  
  private FastWebClient configWebClient;

  @SuppressWarnings("unused")
  public void execute(final HttpRequest request, final HttpResponse response,
      final HttpContext context, OutputStream output) throws Exception  {
    Header header = request.getFirstHeader("action");
    String action = header.getValue();

    byte [] bytes = getRequestData(request);
    if("config.load.document".equals(action)) {
      if(configWebClient == null) {
        configWebClient = new FastWebClient();
      }
      DefaultSourceConfig config = readAsObject(bytes);
      config.setWebClient(configWebClient);
      new DocumentLoader(config).load();
//      System.out.println("chay vao day roi " + config.getDoc());
      writeObject(output, config);
      return;
    }
    
    String value = new String(bytes, Application.CHARSET).trim();

    if("load.meta".equals(action)) {
      List<Meta> list =  null;    
      try {
        list = DatabaseService.getLoader().loadMetaFromDomain(value);
      }catch(Exception exp){
        LogService.getInstance().setThrowable("SERVER", exp);
        list = new ArrayList<Meta>();
      }
      Metas metas  = new Metas();
      metas.setList(list);
      value = Object2XML.getInstance().toXMLDocument(metas).getTextValue();
      output.write(value.getBytes(Application.CHARSET));
      return;
    }
    
    if("search.id.by.url".equals(action)) {
//      if(!DataGetter.class.isInstance(DatabaseService.getLoader())) {
      if(DatabaseService.isMode(DatabaseService.RDBMS)) {
        output.write("Not found! Database don't support.".getBytes());
        return;
      }
      
      TextSpliter spliter = new TextSpliter();
      String [] elements = spliter.toArray(value.trim(), '\n');
      
      value  = "";
      
      if(elements.length < 1) {
        value = "Not found! The input is invalid.";
      } else if(elements.length == 1) {
        value = DatabaseService.getLoader().loadIdByURL(elements[0]);
      } else {
        value  = DatabaseService.getLoader().searchIdByURL(elements[0], elements[1]);
      } 
      
      if(value == null) value = "Not found!";        
      
      output.write(value.getBytes());
      return;
    }


    if("source.index.save".equals(action)) {
      SourceIndexerService.getInstance().put(value, SourceIndexerService.SAVE);
      return;
    }

    if("source.index.delete".equals(action)) {
      SourceIndexerService.getInstance().put(value, SourceIndexerService.DELETE);
      return;
    }

    if("source.index.delete.category".equals(action)) {
      String [] elements = value.split("\n");
      List<String> sources = new SourceSearcher().searchByCategory(elements[0], elements[1]);
      for(String source : sources) {
        SourceIndexerService.getInstance().put(source, SourceIndexerService.DELETE);
      }
      return;
    }
    
    if("source.crawl.time".equals(action)) {
      SourceTrackerService tracker = SourceTrackerService.getInstance();
      long accessCode = tracker.search(value.trim());
      output.write(String.valueOf(accessCode).getBytes(Application.CHARSET));
      return;
    }

    if("source.search.name".equals(action)) {
      List<String> list = new SourceSearcher().searchNameByGroup(value);
      StringBuilder builder  = new StringBuilder();
      for(String ele : list) {
        builder.append(ele).append('\n');
      }
      output.write(builder.toString().getBytes(Application.CHARSET));
      return;
    }

    if("source.search.host".equals(action)) {
      String [] elements = value.split("\n");
      List<String> list = new SourceSearcher().searchByHost(elements[0], elements[1]);
      StringBuilder builder  = new StringBuilder();
      for(String ele : list) {
        builder.append(ele).append('\n');
      }
      output.write(builder.toString().getBytes(Application.CHARSET));
      return;
    }

    if("source.search.url".equals(action)) {
      try {
        List<String> list = new SourceSearcher().searchByURL(value);
        for(String ele : list) {
          output.write(ele.getBytes(Application.CHARSET));
          output.write("\n".getBytes());
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        output.write(e.toString().getBytes(Application.CHARSET));
        output.write("\n".getBytes());
      }
      return;
    }

    if("user.load.categories".equals(action)) {
      Header userHeader = request.getFirstHeader("username");
      if(userHeader == null) throw new InvalidActionHandler();
      String username = userHeader.getValue();
      Organization org = Organization.getInstance();
      User user = org.loadInstance("user", User.class, username);
      List<String> groupNames = user.getGroups();
      Set<String> categories = new HashSet<String>();

      for(String groupName : groupNames) {
        Group group = org.loadInstance("group", Group.class, groupName); 
        List<String> groupCategories = group.getWorkingCategories();
        for(String category : groupCategories) {
          if(category == null || category.trim().isEmpty()) continue;
          categories.add(category);
        }
      }

      for(String category : categories) {
        output.write(category.getBytes(Application.CHARSET));
        output.write("\n".getBytes());
      }

      return;
    }
    
    if("user.load.sources".equals(action)) {
      Header userHeader = request.getFirstHeader("username");
      if(userHeader == null) throw new InvalidActionHandler();
      if(value == null || value.trim().isEmpty()) return;
      File folder = new File(UtilFile.getFolder("sources/sources/"), value);
      if(!folder.exists()) return;
      
      StringBuilder  builder = new StringBuilder();
      buildSource(folder, builder);
      output.write(builder.toString().getBytes(Application.CHARSET));
      return;
    }

    if("config.system.load".equals(action)) {
      Properties properties = SystemProperties.getInstance().getProperties();
      properties.store(output, "VietSpider config");
      return;
    }

    if("config.system.store".equals(action)) {
      Properties properties = new Properties();
      properties.load(new StringReader(value));
      Iterator<?> iterator = properties.keySet().iterator();
      SystemProperties system = SystemProperties.getInstance();
      while(iterator.hasNext()) {
        String key = (String)iterator.next();
        String newValue = properties.getProperty(key);
        String oldValue = system.getValue(key);
        if(newValue.equals(oldValue)) continue;
        system.putValue(key, newValue, true);
      }
      logAction(request, action, "save server config");
      return;
    }
    
    if("load.website.list".equals(action)) {
      try {
        Websites websites = XML2Object.getInstance().toObject(Websites.class, value);
        WebsiteStorage.getInstance().load(websites);
        String xml = Object2XML.getInstance().toXMLDocument(websites).getTextValue();
        //    response.addHeader(new BasicHeader("Content-Length", String.valueOf(xml.length())));
        output.write(xml.getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        output.write("".getBytes());
      }
      return;
    }
    
    if("save.website.list".equals(action)) {
      Websites websites = XML2Object.getInstance().toObject(Websites.class, value);
      WebsiteStorage.getInstance().saveWebsites(websites.getList());
      return;
    }
    
    if("save.website.urls".equals(action)) {
      String [] elements = value.split("\n");
      WebsiteStorage.getInstance().save(elements);
      return;
    }
    
    if("generate.download.list".equals(action)) {
      IWebsiteDatabases databases = WebsiteStorage.getInstance();
      if(databases instanceof WebsiteDatabases) {
        GenerateWebsiteScheduler scheduler =
          GenerateWebsiteScheduler.createService((WebsiteDatabases)databases);
        scheduler.setGenerateNow(true);
      }
      return;
    }
    
    
    if("load.website.by.host".equals(action)) {
      Websites websites = XML2Object.getInstance().toObject(Websites.class, value);
      Website website = WebsiteStorage.getInstance().search(value);
      if(website != null) {
        String xml = Object2XML.getInstance().toXMLDocument(website).getTextValue();
        output.write(xml.getBytes(Application.CHARSET));
      } else {
        output.write("".getBytes(Application.CHARSET));
      }
      return;
    }
    

    //////////////////////////// HOME PAGE SERVICE ////////////////////////////////////////////

    if("save.homepage.list".equals(action)) {
      String  [] elements  = value.split("\n");
      if(elements == null || elements.length < 2) return;
      HomepageDatabase db = new HomepageDatabase(elements[0], true);
      for(int i = 1; i < elements.length; i++) {
        db.saveUrl(elements[i]);
      }
      output.write("".getBytes(Application.CHARSET));
      return;
    }

    if("load.homepage.list".equals(action)) {
      Header pageHeader = request.getFirstHeader("page");
      int page = 0;
      try {
        page = Integer.parseInt(pageHeader.getValue());
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.getMessage());
      }
      HomepageDatabase db = new HomepageDatabase(value, true);
      List<String> list = db.loadPage(page);
      StringBuilder builder = new StringBuilder();
      if(list != null) {
        for(int i = 0; i < list.size(); i++){
          if(builder.length() > 0) builder.append('\n');
          builder.append(list.get(i));
        }
      }
      output.write(builder.toString().getBytes(Application.CHARSET));
      return;
    }

    if("total.page.homepage".equals(action)) {
      HomepageDatabase db = new HomepageDatabase(value, true);
      output.write(String.valueOf(db.getTotalPage()).getBytes(Application.CHARSET));
      return;
    }
    
    ////////////////////////////  END HOME PAGE SERVICE ///////////////////////////

    if("check.database.connection".equals(action)) {
      DBInfo config = null;
      if (value.indexOf("<databaseConfig") > -1) {
        config = XML2Object.getInstance().toObject(DatabaseConfig.class, value);
      } else {
        config = XML2Object.getInstance().toObject(SyncDatabaseConfig.class, value);
      }
      DatabaseChecker checker = new DatabaseChecker();
      try {
        output.write(checker.connect(config).getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        output.write(e.toString().getBytes(Application.CHARSET));
      }
      return;
    }

    if("database.connection.setting".equals(action)) {
      DatabaseConfig config = XML2Object.getInstance().toObject(DatabaseConfig.class, value);
      DatabaseSetting setting = new DatabaseSetting();
      try {
        setting.set(config);
        output.write("".getBytes(Application.CHARSET));
      } catch (Exception e) {
        output.write(e.toString().getBytes(Application.CHARSET));
      }
      return;
    }

    if("load.database.setting".equals(action)) {
      DatabaseSetting setting = new DatabaseSetting();
      try {
        DatabaseConfig config = setting.loadDefault();
        String xml = Object2XML.getInstance().toXMLDocument(config).getTextValue();
        output.write(xml.getBytes(Application.CHARSET));
      } catch (Exception e) {
        output.write(e.toString().getBytes(Application.CHARSET));
      }
      return;
    }

    if("save.log".equals(action)) {
      LogService.getInstance().setMessage("USER", null, value);  
      //      System.out.println("LogServiceInstant: " + LogService.getInstance().getClass().getName());
      String username = value.substring(0, value.indexOf(" read.article"));
      String id = value.substring(value.lastIndexOf('=')+1);
      //      UserActionTracker.getInstance().append(new UserActionLog(username, UserActionLog.READ, id));
      return;
    }
    
    /*if("load.source.log".equals(action)) {
      value = value.trim();
      String [] elements = value.split("\n");
      if(elements.length < 1) return;
      File file  = null;
      if(elements.length < 2) {
        file = SourceLogHandler.getInstance().exportToFile(value);
      } else {
        file = new MultiSourceLogExporter().export(elements);
      }
      
      if(file != null) {
        response.setEntity(new FileEntity(file, "text/plain"));
      }
      return;
    }*/
    
    //remove 
    if("export.source.log.from.database".equals(action)) {
      LinkLogStorages.getInstance().export(value);
      return;
    }
    
//    if("load.monitor.date".equals(action)) {
//      SourceLogUtils log = new SourceLogUtils();
//      String [] elements = log.loadDate();
//      StringBuilder builder = new StringBuilder();
//      for(int i = 0; i < elements.length; i++) {
//        if(builder.length() > 0) builder.append('\n');
//        builder.append(elements[i]);
//      }
//      output.write(builder.toString().getBytes(Application.CHARSET));
//      return;
//    }
    
   /* if("load.source.log.desc".equals(action)) {
      String [] elements = value.trim().split("[#]");
      SourceLogHandler handler = SourceLogHandler.getInstance();
      try {
        SourceLog sourceLog = handler.loadSourceLog(elements[0].trim(), elements[1].trim());
        if(sourceLog != null) {
          output.write(sourceLog.getDesc().getBytes(Application.CHARSET));
        } else {
          output.write(" ".getBytes());
        }
      } catch (Exception e) {
        output.write(" ".getBytes());
      }
      return;
    }*/
    
    if("analyze.text".equals(action)) {
      ViIndexAnalyzer2 analyzer2 = new ViIndexAnalyzer2();
      value = analyzer2.extract(value);
      output.write(value.getBytes(Application.CHARSET));
      return;
    }
    
    if("analyze.save.word".equals(action)) {
      VietnameseDictionary.getInstance().save(value, "");
      output.write(" ".getBytes());
      return;
    }
    
    if("analyze.remove.word".equals(action)) {
      VietnameseDictionary.getInstance().remove(value);
      output.write(" ".getBytes());
      return;
    }
    
    if("load.disable.source".equals(action)) {
      String sourceList = SourceIO.getInstance().filterDisable(value);
//      System.out.println(" chay vao day "+ sourceList);
      output.write(sourceList.getBytes());
      return;
    }
    
  }
  
  private void buildSource(File folder, StringBuilder builder) {
    File [] files = folder.listFiles();
    if(files == null) return;
    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) {
        buildSource(files[i], builder);
        continue;
      }
      String name = files[i].getName();
      if(name.indexOf(".v.") > -1) continue;
      TextSpliter spliter = new TextSpliter();
      String [] elements = spliter.toArray(name, '.');
      if(elements.length != 2) continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(NameConverter.decode(elements[1]));
    }
  }

}

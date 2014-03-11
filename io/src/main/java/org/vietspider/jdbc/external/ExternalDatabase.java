/***************************************************************************
 * Copyright 2003-2012 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.jdbc.external;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.data.jdbc.SerialClob;
import org.vietspider.db.StorableTempQueue;

import com.ibm.icu.util.Calendar;


/**
 *  Author : Nhu Dinh Thuan
 *  Email:nhudinhthuan@yahoo.com
 *  Website: vietspider.org       
 * Jan 16, 2012
 */
public class ExternalDatabase extends StorableTempQueue<Article> implements Runnable {
  
  private static volatile ExternalDatabase instance;
  
  public synchronized static final ExternalDatabase getInstance() {
    if(instance != null) return instance;
    instance = new ExternalDatabase();
    return instance;
  }

  public final static short ORACLE = 0;
  public final static short SQLSERVER = 1;
  public final static short MYSQL = 2;
  public final static short POSTGRES = 3;
  public final static short HSQL = 4;
  public final static short H2 = 5;
  public final static short APACHEDB = 6;

  private short type = 0;

  private Connection connection;
  private List<SQL> sqls = new ArrayList<SQL>();
  private InmemoryCache<String, String> uniqueIdentifierCache;
  private String imageFolderPath = null;
  
  private boolean sampleVariable = false;

  public ExternalDatabase() {
    super(UtilFile.getFolder("content/temp/external/"), 1000);
    File file = new File(UtilFile.getFolder("system"), "external.db.sql");
    if(!file.exists() || file.length() < 1) return;

    String driver = null;
    String url = null;
    String user = null;
    String password = null;

    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      String [] elements = text.split("\n");
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < elements.length; i++) {
        if(elements[i].trim().length() > 0
            && elements[i].trim().charAt(0) == '#') continue;
        if(elements[i].trim().startsWith("driver")) {
          int idx = elements[i].indexOf('=');
          driver = elements[i].substring(idx+1).trim();
        } else if(elements[i].trim().startsWith("connection")) {
          int idx = elements[i].indexOf('=');
          url = elements[i].substring(idx+1).trim();
        } else if(elements[i].trim().startsWith("user")) {
          int idx = elements[i].indexOf('=');
          user = elements[i].substring(idx+1).trim();
        } else if(elements[i].trim().startsWith("password")) {
          int idx = elements[i].indexOf('=');
          password = elements[i].substring(idx+1).trim();
        } else if(elements[i].trim().startsWith("image.folder")) {
          int idx = elements[i].indexOf('=');
          imageFolderPath = elements[i].substring(idx+1).trim();
        } else if(elements[i].trim().length() < 1) {
          if(builder.length() > 0) {
            sqls.add(new SQL(builder.toString()));
            builder.setLength(0);
          }
        } else {
          if(builder.length() > 0) builder.append('\n');
          builder.append(elements[i]);
        }
      }

      if(builder.length() > 0) {
        sqls.add(new SQL(builder.toString()));
        builder.setLength(0);
      }

      if(driver == null || driver.length() < 1) {
        LogService.getInstance().setMessage(getClass().getSimpleName().toUpperCase(), null, "No driver!");
        return;
      }

      if(url == null || url.length() < 1) {
        LogService.getInstance().setMessage(getClass().getSimpleName().toUpperCase(), null, "No connection!");
        return;
      }

      if(user == null || user.length() < 1) {
        LogService.getInstance().setMessage(getClass().getSimpleName().toUpperCase(), null, "No user!");
        return;
      }

      if(password == null || password.length() < 1) {
        LogService.getInstance().setMessage(getClass().getSimpleName().toUpperCase(), null, "No password!");
        return;
      }

      if(driver.indexOf("oracledriver") > -1) {
        type = ORACLE;
      } else if (driver.indexOf("sqlserver") > -1) {
        type = SQLSERVER;
      } else if (driver.indexOf("mysql") > -1) {
        type = MYSQL;
      }  else if (driver.indexOf("postgresql") > -1) {
        type = POSTGRES;
      } else if (driver.indexOf("derby") > -1) {
        type = APACHEDB;
      } else if (driver.indexOf("hsqldb") > -1) {
        type = HSQL;
      }  else {
        type = H2;
      }

//      System.out.println(driver);
//      System.out.println(url);
//      System.out.println(user + " : "+ password);

      Class.forName(driver);

      connection = DriverManager.getConnection(url, user, password);
      
      uniqueIdentifierCache = new InmemoryCache<String, String>("unique.identifier.query", 5000);
      new Thread(this).start();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      Application.addError(this);
    }

    Application.addShutdown(new Application.IShutdown() {
      public void execute() {
        try {
          storeTemp();
          if(connection != null) connection.close();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    });
  }

  public void run() {
    File file = new File(UtilFile.getFolder("system"), "external.db.sample.variable.txt");
    if(file.exists() && file.length() > 1) sampleVariable = true;
    
    boolean execute = true;
    while(execute) {
      commit();
      try {
        execute = !connection.isClosed();
      } catch (Exception e) {
        execute = false;
        LogService.getInstance().setThrowable(e);
      }

      try {
        Thread.sleep(15*1000);
      } catch (Exception e) {
      }
    }
  }

  private void commit() {
    Queue<Article> working = new LinkedList<Article>();
    try {
      load(working);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }

    while(!working.isEmpty()) {
      Article data = working.poll();

      try {
        saveArticle(data);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(getClass().getSimpleName().toUpperCase(), e);
        continue;
      }
    }
  }

  private void load(java.util.Collection<Article> working) throws Throwable {
    File [] files = listTempFiles();

    if(files == null || files.length < 1) {
      //      if(queue.size() >= 1000) {
      int idx = 0;
      Iterator<Article> iterator = queue.iterator();
      while(iterator.hasNext()) {
        Article data  = iterator.next();
        working.add(data);
        queue.remove(data);
        idx++;
        if(idx >= sizeOfWorking) break;
      }
      //      }
      return;
    }

    load(files, working);
  }

  public void saveArticle(Article article) {
    TreeMap<String, Object> map = new TreeMap<String, Object>();
    Meta meta = article.getMeta();
    if(meta == null) return;
    Content content = article.getContent();
    if(content == null) return;
    map.put("@meta.id", meta.getId());
    map.put("@article.id", meta.getId());

    String title = meta.getTitle();
    title = title.replaceAll("'", "''");
    map.put("@meta.title", title);
    
    String desc = meta.getDesc();
    desc = desc.replaceAll("'", "''");
    map.put("@meta.des", desc);
    map.put("@meta.desc", desc);
    map.put("@meta.description", desc);

    map.put("@meta.image", meta.getImage());

    map.put("@meta.time", meta.getTime());
    map.put("@meta.source_time", meta.getSourceTime());
    map.put("@meta.source.time", meta.getSourceTime());

    String source = meta.getSource();
    source = source.replaceAll("'", "''");
    map.put("@meta.url", source);
    map.put("@meta.source", source);
    map.put("@meta.link", source);
    
    int idx = source.indexOf('/', 10);
    if(idx < 0) idx = source.indexOf('?', 10);
    if(idx > 0) {
      map.put("@meta.source.homepage", source.substring(0, idx));
    }
    
    try {
      URL url = new URL(source);
      map.put("@meta.source.host", url.getHost());
    } catch (Exception e) {
    }

    String alias = meta.getAlias();
    alias = alias.replaceAll("'", "''");
    map.put("@meta.title.alias", alias);
    map.put("@meta.alias", alias);
    
    List<Image> images = article.getImages();
    if(images != null) {
      for(int i = 0; i < images.size(); i++) {
        Image image = images.get(i);
        if(image.getId().equals(meta.getImage())) {
          map.put("@meta.image.id", image.getId());
          map.put("@meta.image.name", image.getName());
          map.put("@meta.image.type", image.getType());
          if(image.getImage() == null) image.setImage(new byte[0]);
          map.put("@meta.image.raw", image.getImage());
          break;
        }
      }
    }

    String contentValue = content.getContent();
    contentValue = contentValue.replaceAll("'", "''");
    map.put("@article.content", contentValue);
    map.put("@content.content", contentValue);
    map.put("@content.date", content.getDate());
    map.put("@content.id", content.getMeta());

    Domain domain = article.getDomain();
    if(domain != null) {
      map.put("@domain.id", domain.getId());
      map.put("@domain.date", domain.getDate());
      map.put("@domain.group", domain.getGroup());
      map.put("@domain.category", domain.getCategory());
      map.put("@domain.group.category", domain.getGroup() + "." + domain.getCategory());
      map.put("@domain.name", domain.getName());
      map.put("@domain.source", domain.getName());
    }
    
    for(int i = 0; i < sqls.size(); i++) {
      SQL sql = sqls.get(i);
      if(sql.getQuery().size() < 1) continue;
      if(sql.getVariable() == null 
          || !sql.getVariable().startsWith("@datetime.")) continue;
      try {
//        System.out.println("======= datat time======>"+ sql.getQuery().get(0));
        SimpleDateFormat dateFormat = new SimpleDateFormat(sql.getQuery().get(0));
        Calendar calendar = Calendar.getInstance();
        map.put(sql.getVariable(), dateFormat.format(calendar.getTime()));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        Application.addError(this);
      }
    }

    for(int i = 0; i < sqls.size(); i++) {
      SQL sql = sqls.get(i);
      if(sql.getQuery().size() < 1) continue;
      if(sql.getVariable() != null 
          && sql.getVariable().startsWith("@datetime.")) continue;
      if(sql.isType("@image.") || sql.isType("@relation")) continue;
      execute(sql, map);
    }

    //update image 
    File imageFolder = null;
    if(imageFolderPath != null && imageFolderPath.length() > 0) {
      imageFolder = new File(imageFolderPath);
      if(!imageFolder.exists() || !imageFolder.isDirectory()) {
        LogService.getInstance().setMessage(
            getClass().getName(), null, "Image Folder not exists or not directory");
        imageFolder = null;
      }
    }
    
    for(int k = 0; k < images.size(); k++) {
      Image image = images.get(k);
      map.remove("@image.id");
      map.remove("@image.name");
      map.remove("@image.type");
      map.remove("@image.raw");
      map.remove("@image.data");

      map.put("@image.id", image.getId());
      String name = image.getName(); 
      name = name.replaceAll("'", "''");
      map.put("@image.name", name);
      map.put("@image.type", image.getType());
      if(image.getImage() == null) image.setImage(new byte[0]);
//      System.out.println(" ----  >"+ image.getImage());
      map.put("@image.raw", image.getImage());
      map.put("@image.data", image.getImage());
      if(imageFolder != null) {
        try {
          RWData.getInstance().save(new File(imageFolder, image.getName()), image.getImage());
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
      for(int i = 0; i < sqls.size(); i++) {
        SQL sql = sqls.get(i);
        if(sql.getQuery().size() < 1) continue;
        if(sql.getVariable() != null 
            && sql.getVariable().startsWith("@datetime.")) continue;
        if(sql.isType("@image.")) execute(sql, map);
      }
    }

    List<Relation> relations =  article.getRelations();
    if(relations != null) {
      for(int k = 0; k < relations.size(); k++) {
        Relation relation = relations.get(k);
        map.remove("@relation.meta");
        map.remove("@relation.relation");
        map.remove("@relation.percent");

        map.put("@relation.meta", relation.getMeta());
        map.put("@relation.id", relation.getMeta());
        map.put("@relation.relation", relation.getRelation());
        map.put("@relation.percent", String.valueOf(relation.getPercent()));

        for(int i = 0; i < sqls.size(); i++) {
          SQL sql = sqls.get(i);
          if(sql.getQuery().size() < 1) continue;
          if(sql.getVariable() != null 
              && sql.getVariable().startsWith("@datetime.")) continue;
          if(sql.isType("@relation.")) execute(sql, map);
        }
      }
    }
    
    if(!sampleVariable) {
      File file = new File(UtilFile.getFolder("system"), "external.db.sample.variable.txt");
      StringBuilder builder = new StringBuilder();
      Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
      while(iterator.hasNext()) {
        if(builder.length() > 0) builder.append("\n\n");
        Map.Entry<String, Object> entry = iterator.next();
        Object value = entry.getValue();
         if( value instanceof Integer 
             || value instanceof Character
             || value instanceof Double
             || value instanceof Float
             || value instanceof Long
             || value instanceof Short
             || value instanceof Date
             || value instanceof Calendar
             || value instanceof Boolean
             ) {
           builder.append(entry.getKey()).append('=').append(value);
         } else if(value instanceof String) {
          String text = (String) value;
          if(text.length() > 200) text = text.substring(0, 200) + "...";
          builder.append(entry.getKey()).append('=').append(text);
        } else {
          if(value == null) value = "";
          builder.append(entry.getKey()).append('=').append(value.getClass().getSimpleName());
        }
      }
      try {
        RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      sampleVariable = true;
    }
  }

  private void execute(SQL sql, Map<String, Object> map) {
    Statement statement = null;
    PreparedStatement preparedStatement = null;
    try {
      if(sql.getUniqueIdentifierQuery().size() > 0) {
        Object params[] = SQLUtils.build(sql.getUniqueIdentifierQuery(), map);
        if(params.length > 0) {
          if(uniqueIdentifierCache.getCachedObject((String)params[0]) != null) {
//            System.out.println("load data from cached " + (String)params[0]  + " : " 
//                + uniqueIdentifierCache.getCachedObject((String)params[0]));
            return;
          }
          statement = connection.createStatement();
          ResultSet resultSet = statement.executeQuery((String)params[0]);
          if(resultSet != null && resultSet.next()) {
            String value = resultSet.getString(1);
            uniqueIdentifierCache.putCachedObject((String)params[0], value);
            return;
          }
        }
      }
      
      
      Object params[] = SQLUtils.build(sql.getQuery(), map);
      if(params.length == 1) {
        if(statement == null) statement = connection.createStatement();
        if(sql.getVariable() != null) {
          ResultSet resultSet = statement.executeQuery((String)params[0]);
          if(resultSet != null && resultSet.next()) {
            map.put(sql.getVariable(), resultSet.getObject(1));
          } else {
            map.put(sql.getVariable(), sql.getDefaultValue());
          }
        } else {
          statement.executeUpdate((String)params[0]);
        }
        return;
        //execute cau lenh
      } 

      if(params.length > 1) {
        preparedStatement = connection.prepareStatement((String)params[0]);
        int idx = 1;
        for(int k = 1; k < params.length; k++) {
          if(params[k] instanceof String) {
            if(type == ORACLE){
              oracle.sql.CLOB clob= oracle.sql.CLOB.createTemporary(connection, true, oracle.sql.CLOB.DURATION_SESSION);
              clob.setString(1, (String)params[k]);
              preparedStatement.setClob(idx, clob);       
            } else {
              SerialClob clob = new SerialClob(((String)params[k]).toCharArray());
              preparedStatement.setClob(idx, clob);
            }  
          } else if(params[k] instanceof byte[]) {
            byte [] bytes = (byte[]) params[k];
            preparedStatement.setBinaryStream(idx, new ByteArrayInputStream(bytes), bytes.length);
          }
          idx++;
        }
        if(sql.getVariable() != null) {
          ResultSet resultSet = preparedStatement.executeQuery();
          if(resultSet != null && resultSet.next()) {
            map.put(sql.getVariable(), resultSet.getObject(1));
          } else {
            map.put(sql.getVariable(), sql.getDefaultValue());
          }
        } else {
          preparedStatement.executeUpdate();
        }
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(statement != null) statement.close(); 
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      try {
        if(preparedStatement != null) preparedStatement.close(); 
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public void add(Article article) {
    try {
      if(connection == null || connection.isClosed()) return;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }
    if(!license()) return;
    queue.add(article);
  }
  
  private boolean license() {
    File licenseFile = new File(UtilFile.FOLDER_DATA+"/../lib/sync.vs.license");
    if(licenseFile.exists() && licenseFile.length() > 0) return true;
    
    java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(getClass());
    int value = 0;
    try {
      value = Integer.parseInt(prefs.get("external.database.demo", ""));
    } catch (Exception e) {
      value = 1;
    }
    if(value > 5000) {
      LogService.getInstance().setMessage("ERROR", null, "External Database Demo expired!");
      Application.addError(this);
      return false;
    }
    prefs.put("external.database.demo", String.valueOf(value+1));
    return true;
  }

}

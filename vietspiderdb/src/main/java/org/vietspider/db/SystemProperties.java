package org.vietspider.db;

import java.util.Properties;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.UtilFile;

public class SystemProperties {
  
  private volatile Properties properties = null;
  
  private volatile boolean modified = false;
  
  private static final SystemProperties instance  = new SystemProperties();
  
  public static SystemProperties getInstance() { return instance; }

  private SystemProperties(){
    load();    
    
    Application.addShutdown(new Application.IShutdown() {
      public void execute() {
        store();
      }
    });
  }
  
  public String getValue(String key) {
    if(properties == null) return null;
    String value  = properties.getProperty(key);
    if(value == null) return null;
    return value.trim();
  }
  
  public void remove(String key) {
    if(properties == null) return;
    properties.remove(key);
    modified = true;
  }
  
  public int getInt(String key, int _default) {
    if(properties == null) return _default;
    String value  = properties.getProperty(key);
    if(value == null) return _default;
    if((value = value.trim()).isEmpty()) return _default;
    try {
      return Integer.parseInt(value);
    } catch (Exception e) {
      return _default;
    }
  }
  
  public long getLong(String key, long _default) {
    if(properties == null) return _default;
    String value  = properties.getProperty(key);
    if(value == null) return _default;
    if((value = value.trim()).isEmpty()) return _default;
    try {
      return Long.parseLong(value);
    } catch (Exception e) {
      return _default;
    }
  }
  
  public void putValue(String key, String value, boolean isModified) {
    if(properties == null) return;
    properties.put(key, value);
    modified = isModified;
  }
  
  public synchronized void store() {
    if(properties.isEmpty()) return;
    PropertiesFile file = new PropertiesFile(false);
    try {
      file.save(UtilFile.getFile("system", "system.properties"), properties);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    modified = false;
  }
  
  private synchronized void load() {
    PropertiesFile file = new PropertiesFile(false);
    try {
      properties = file.load(UtilFile.getFile("system", "system.properties"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    putDefaultValue ();
  }

  public Properties getProperties() { return properties; }
  
  private void putDefaultValue (){
    if(properties.get(Application.PROXY_ENABLE) == null) {
      properties.put(Application.PROXY_ENABLE, "false");
    }
    
    if(properties.get(Application.PROXY_HOST) == null) {
      properties.put(Application.PROXY_HOST, "");
    }
    
    if(properties.get(Application.PROXY_PORT) == null) {
      properties.put(Application.PROXY_PORT, "");
    }
    
    if(properties.get(Application.PROXY_USER) == null) {
      properties.put(Application.PROXY_USER, "");
    }
    
    if(properties.get(Application.PROXY_PASSWORD) == null) {
      properties.put(Application.PROXY_PASSWORD, "");
    }
    
    if(properties.get(Application.AUTO_START_CRAWLER) == null) {
      properties.put(Application.AUTO_START_CRAWLER, "false");
    }
    
    if(properties.get(Application.TOTAL_EXECUTOR) == null) {
      properties.put(Application.TOTAL_EXECUTOR, "2");
    }
    
    if(properties.get(Application.TOTAL_WORKER_OF_EXECUTOR) == null) {
      properties.put(Application.TOTAL_WORKER_OF_EXECUTOR, "2");
    }
    
    if(properties.get(Application.WORKER_TIMEOUT) == null) {
      properties.put(Application.WORKER_TIMEOUT, "120");
    }
    
    if(properties.get(Application.AUTO_SET_PRIORITY) == null) {
      properties.put(Application.AUTO_SET_PRIORITY, "false");
    }
    
//    if(properties.get(Application.TEST_CONFIG_SOURCE) == null) {
//      properties.put(Application.TEST_CONFIG_SOURCE, "true");
//    }
    
    if(properties.get(Application.HTTP_CLIENT_TIMEOUT) == null) {
      properties.put(Application.HTTP_CLIENT_TIMEOUT, "60000");
    }
    
    if(properties.get(Application.LOG_WEBSITE_ERROR) == null) {
      properties.put(Application.LOG_WEBSITE_ERROR, "false");
    }
    
    if(properties.get(Application.LAST_DOWNLOAD_SOURCE) == null) {
      properties.put(Application.LAST_DOWNLOAD_SOURCE, "0");
    }
    
    
    if(properties.get(Application.DOWNLOAD_LIMIT_DATE) == null) {
      properties.put(Application.DOWNLOAD_LIMIT_DATE, "0");
    }
    
    if(properties.get(Application.SAVE_IMAGE_TO_FILE) == null) {
      properties.put(Application.SAVE_IMAGE_TO_FILE, "false");
    }
    
    if(properties.get(Application.START_MINING_INDEX_SERVICE) == null) {
      properties.put(Application.START_MINING_INDEX_SERVICE, "true");
    }
    
    if(properties.get(Application.SCHEDULE_CLEAN_DATA) == null) {
      properties.put(Application.SCHEDULE_CLEAN_DATA, "0");
    }
    
    if(properties.get(Application.CLEAN_DATABASE) == null) {
      properties.put(Application.CLEAN_DATABASE, "true");
    }
    
    if(properties.get(Application.EXPIRE_DATE) == null) {
      properties.put(Application.EXPIRE_DATE, "100");
    }
    
    if(properties.get(Application.HOST) == null) {
      properties.put(Application.HOST, "");
    }
    
    if(properties.get(Application.PORT) == null) {
      properties.put(Application.PORT, "9245");
    }
    
    if(properties.get(Application.LANGUAGE) == null) {
      properties.put(Application.LANGUAGE, "en,vn");
    }
    
    if(properties.get(Application.LOCALE) == null) {
      properties.put(Application.LOCALE, "vn");
    }
    
    if(properties.get(Application.CMS_LAYOUT) == null) {
      properties.put(Application.CMS_LAYOUT, "vietspider");
    }
    
    if(properties.get(Application.BACKUP_FOLDER) == null) {
      properties.put(Application.BACKUP_FOLDER, "");
    }
    
    if(properties.get(Application.APPLICATION_NAME) == null) {
      properties.put(Application.APPLICATION_NAME, "vietspider");
    }
    
    if(properties.get(Application.WEIGHT_PRIORITY_EXECUTOR) == null) {
      properties.put(Application.WEIGHT_PRIORITY_EXECUTOR, "30");
    }
    
    if(properties.get(Application.DETECT_LANGUAGE) == null) {
      properties.put(Application.DETECT_LANGUAGE, "false");
    }
    
    if(properties.get(Application.DETECT_WEBSITE) == null) {
      properties.put(Application.DETECT_WEBSITE, "false");
    }
    
    if(properties.get(Application.START_INDEX_DATA_SERVICE) == null) {
      properties.put(Application.START_INDEX_DATA_SERVICE, "false");
    }
    
    if(properties.get(Application.LOG_WEBSITE_ERROR) == null) {
      properties.put(Application.LOG_WEBSITE_ERROR, "true");
    }
  }

  public boolean isModified() { return modified; }
  
}

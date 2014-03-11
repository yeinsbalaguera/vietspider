package org.vietspider.ui.htmlexplorer;

import java.util.Properties;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.UtilFile;

class ExplorerProperties {

  private Properties properties = null;
  
  ExplorerProperties(){
    load();
  }
  
  String getValue(String key) {
    return properties != null ? properties.getProperty(key) : null;
  }
  
  public void putValue(String key, String value) {
    if(properties == null) return;
    properties.put(key, value);
  }
  
  public synchronized void store() {
    PropertiesFile file = new PropertiesFile(true);
    try {
      file.save(UtilFile.getFile("client", "config.properties"), properties);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public synchronized void load() {
    PropertiesFile file = new PropertiesFile(true);
    try {
      properties = file.load(UtilFile.getFile("client", "config.properties"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public Properties getProperties() { return properties; }
}
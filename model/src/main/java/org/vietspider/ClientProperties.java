package org.vietspider;

import java.util.Properties;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.UtilFile;

public class ClientProperties {
  
  private volatile static  ClientProperties cp;
  
  public synchronized static ClientProperties getInstance() {
    return cp != null ? cp : (cp = new ClientProperties());
  }
  
  private Properties properties = null;
  
  private ClientProperties(){
    load();
  }
  
  public String getValue(String key) {
    return properties != null ? properties.getProperty(key) : null;
  }
  
  public String getValue(String key, String _default) {
    String value = properties != null ? properties.getProperty(key) : _default;
    if(value != null) return value;
    return _default;
  }
  
  public void putValue(String key, String value) {
    if(properties == null) return;
    properties.put(key, value);
  }
  
  public synchronized void store() {
    PropertiesFile file = new PropertiesFile(false);
    try {
      file.save(UtilFile.getFile("client", "config.properties"), properties);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public synchronized void load() {
    PropertiesFile file = new PropertiesFile(false);
    try {
      properties = file.load(UtilFile.getFile("client", "config.properties"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public Properties getProperties() { return properties; }
}
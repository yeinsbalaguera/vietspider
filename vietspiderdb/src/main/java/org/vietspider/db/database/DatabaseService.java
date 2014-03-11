/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.util.concurrent.atomic.AtomicInteger;

import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class DatabaseService {
  
  public static final int DEFAULT = 0;
  public static final int RDBMS = 1;
  public static final int ENTIRE = 2;
  public static final int SEARCH = 3;
  public static final int REMOTE = 4;
  
//  dataCleaner=org.vietspider.db.content.DataCleaner
//  dataGetter=org.vietspider.db.content.DataGetter
//  dataSaver=org.vietspider.db.content.DataSetter
//  dataUtils=org.vietspider.db.content.DataUtils


  //  public static int PAGE_SIZE = 10;

  /* private static ThreadSoftRef<SaveData> SAVE_DATA;
  private static ThreadSoftRef<DeleteData> DELETE_DATA;
  private static ThreadSoftRef<LoadData> LOAD_DATA;
  private static ThreadSoftRef<UtilData> UTIL_DATA;

  static {
    try{
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      PropertiesFile ioProperties = new PropertiesFile(); 
      File file = UtilFile.getFile("/System", "data.properties");
      Properties dataPros = ioProperties.load(file);

      String path = dataPros.getProperty("SaveData");
      Class<?> clazz = null;
      if(path != null && path.trim().length() > 0){
        clazz = classLoader.loadClass(path);
      }else{
        clazz = DataSetter.class;
      }      
      SAVE_DATA = new ThreadSoftRef<SaveData>(clazz);


      path = dataPros.getProperty("DeleteData");
      if(path != null && path.trim().length() > 0){
        clazz = classLoader.loadClass(path);
      }else{
        clazz = DataCleaner.class;
      } 
      DELETE_DATA = new ThreadSoftRef<DeleteData>(clazz);

      path = dataPros.getProperty("LoadData");
      if(path != null && path.trim().length() > 0){
        clazz = classLoader.loadClass(path);
      }else{
        clazz = DataGetter.class;
      } 
      LOAD_DATA = new ThreadSoftRef<LoadData>(clazz);

      path = dataPros.getProperty("UtilData");
      if(path != null && path.trim().length() > 0){
        clazz = classLoader.loadClass(path);
      }else{
        clazz = UtilityData.class;
      }     
      UTIL_DATA = new ThreadSoftRef<UtilData>(clazz);
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(-1);
    }
  }*/

  //  private static Class<?> saveClazz;
  //  private static Class<?> loadClazz;
  //  private static Class<?> cleanClazz;
  //  private static Class<?> utilClazz;

  private static DatabaseWriter writer;
  private static DatabaseReader loader;
  private static DatabaseCleaner cleaner;
  private static DatabaseUtils utils;
  private static AtomicInteger mode = new AtomicInteger(RDBMS);

  static {
    try {
      writer = (DatabaseWriter) loadInstance("dataSaver", "org.vietspider.db.content.DataSetter");
      loader = (DatabaseReader) loadInstance("dataGetter", "org.vietspider.db.content.DataGetter");
      cleaner = (DatabaseCleaner) loadInstance("dataCleaner", "org.vietspider.db.content.DataCleaner");
      utils = (DatabaseUtils) loadInstance("dataUtils", "org.vietspider.db.content.DataUtils");
    } catch (Exception e) {
      LogService.getInstance().setThrowable("APPLICATION", e);
      System.exit(0);
    }
  }

  //"dataSaver"
  private static Object loadInstance(String label, String defaultClazz) throws Exception {
    SystemProperties properties = SystemProperties.getInstance();
    String clazzName = properties.getValue(label);
    if(clazzName == null || clazzName.trim().isEmpty()) {
      clazzName  = defaultClazz;
      properties.putValue(label, clazzName, true);
    }
    try {
      return Class.forName(clazzName).newInstance();
    } catch (ClassNotFoundException e) {
      try {
        return Class.forName(defaultClazz).newInstance();
      } catch (ClassNotFoundException e1) {
        LogService.getInstance().setThrowable(e);
        LogService.getInstance().setThrowable(e1);
      } 
    } 
    return null;
  }

  public static DatabaseWriter getSaver() { return writer; }

  //  public static JdbcDataWriter getSaver() { return ServicesContainer.get(WebdbDataSetter.class); }

  public static DatabaseCleaner getDelete() { return cleaner;  }

  public static DatabaseReader getLoader(){ return loader;  }

  //  public static JdbcDataReader getLoader(){ return ServicesContainer.get(WebdbDataGetter.class); }

  public static DatabaseUtils getUtil(){  return utils; }
  
  public synchronized  static void setMode(int m) {
    if(mode == null) mode = new AtomicInteger(RDBMS);
    mode.set(m);
//    System.out.println(" da thay chay vao day "+ mode.get());
  }
  
  public static boolean isMode(int type) {
//    System.out.println(" vao mode la "+ mode.get());
    return type  == mode.get(); 
  }
  
}

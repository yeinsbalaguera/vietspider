/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.io.bean;

import org.vietspider.container.copy.ServicesContainer;
import org.vietspider.data.jdbc.DataCleaner;
import org.vietspider.data.jdbc.DataGetter;
import org.vietspider.data.jdbc.DataSetter;
import org.vietspider.data.jdbc.DataUtils;
import org.vietspider.db.database.DatabaseCleaner;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.DatabaseUtils;
import org.vietspider.db.database.DatabaseWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
class DataService {
  
  public static int PAGE_SIZE = 10;
  
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
  
  static DatabaseWriter getSaver() { return ServicesContainer.get(DataSetter.class); }
  
  static DatabaseCleaner getDelete(){ return ServicesContainer.get(DataCleaner.class); }
  
  static DatabaseReader getLoader(){ return ServicesContainer.get(DataGetter.class); }
  
  static DatabaseUtils getUtil(){ return ServicesContainer.get(DataUtils.class); }
}

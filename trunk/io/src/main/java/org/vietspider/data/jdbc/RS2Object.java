/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.List;

import org.vietspider.bean.Bean;
import org.vietspider.bean.Image;
import org.vietspider.bean.RSField;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class RS2Object {

 public <T extends Bean<?>> void toObjects(ResultSet resultSet, 
      Class<T> clazz, List<T> list) throws Exception {
    while(resultSet.next()){     
      T newBean  = clazz.newInstance();
      toObject(resultSet, newBean);
      list.add(newBean);
    }
  }

 public <T extends Bean<?>> void toObjects(ResultSet resultSet, Class<T> clazz, List<T> list, 
                                  Class<? extends Bean<?>>...classes) throws Exception {
    while(resultSet.next()){
      Bean<?> [] beans = new Bean[classes.length];
      for(int i=0; i<beans.length; i++){
        beans[i] = classes[i].newInstance();
      }
      toObjects(resultSet, beans);
      T t = clazz.newInstance();
      for(Bean<?> bean : beans){
        t.setField(bean.getField(), bean);
      }
      list.add(t);
    }
  }

  public void toObject(ResultSet resultSet, Bean<?> bean) throws Exception {
    ResultSetMetaData rsmd = resultSet.getMetaData();
    int numberOfColumns = rsmd.getColumnCount();
    for(int i=1; i<=numberOfColumns; i++){
      String name  = rsmd.getColumnName(i);
      RSField field = RSField.valueOf(name.toUpperCase());
      try {
        if(bean.getClass() == Image.class) {
          if(name.equals("META_ID")) name = "META";
          else if(name.equals("CONTENT_TYPE")) name = "TYPE";
        }
        bean.setField(field, getValue(rsmd.getColumnType(i), resultSet, name));
      } catch (Exception e) {    
//        if(name.equals("META_ID") && bean.getClass() == Image.class) {
//          bean.setField(field, getValue(rsmd.getColumnType(i), resultSet, "META"));
//          continue;
//        }
        LogService.getInstance().setMessage(e, bean + " : " + name);  
        continue;
      }  
    }
  }

  public void toObjects(ResultSet resultSet, Bean<?>[] beans) throws Exception {
    ResultSetMetaData rsmd = resultSet.getMetaData();
    int numberOfColumns = rsmd.getColumnCount();
    for(int i=1; i<=numberOfColumns; i++){
      String name  = rsmd.getColumnName(i);
      RSField field = null;
      try{
        field = RSField.valueOf(name.toUpperCase());
      }catch (Exception e) {
        LogService.getInstance().setMessage(e, name);  
        continue;
      }
      Object value = getValue(rsmd.getColumnType(i), resultSet, name);
      for(Bean<?> bean : beans){
        try{ 
          bean.setField(field, value);
        }catch (Exception e) {
          LogService.getInstance().setMessage(e, bean +" : "+name);      
          continue;
        }  
      }
    }
  }

  public synchronized Object getValue(int type, ResultSet resultSet, String name) throws Exception {
    switch(type){
    case Types.CLOB :
      return loadClob(resultSet, name);
    case Types.BLOB :
      return loadBlob(resultSet, name);
    case Types.BINARY :
      return loadBinary(resultSet, name);
    default :
      return resultSet.getObject(name.toUpperCase());
    }
  }

  public synchronized byte [] loadBinary(ResultSet resultSet, String name) throws Exception {
    InputStream input = resultSet.getBinaryStream(name);
    if(input == null) return null;
    ByteArrayOutputStream output = RWData.getInstance().loadInputStream(input);
    return output.toByteArray();
  }

  public synchronized byte [] loadBlob(ResultSet resultSet, String name) throws Exception {
    Blob clob = resultSet.getBlob(name);  
    if(clob == null) return null;
    ByteArrayOutputStream output = RWData.getInstance().loadInputStream(clob.getBinaryStream());
    return output.toByteArray();
  }

  public synchronized String loadClob(ResultSet resultSet, String name) throws Exception {
    Clob clob = resultSet.getClob(name);  
    if(clob == null) return null;
    Reader input = clob.getCharacterStream();    
    if(input == null) return  null;
    LineNumberReader lineReader = new LineNumberReader(input); 
    StringBuilder builder = new StringBuilder(); 
    String line;
    while((line = lineReader.readLine()) != null){
      builder.append(line);
    }    
    return builder.toString();
  }
}

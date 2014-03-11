/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.lang.reflect.Field;

import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Reflection;
import org.vietspider.bean.Relation;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class SqlUtil {
  
  private final static String ID = "$id",
                              TITLE = "$title",
                              DES = "$des",
                              IMAGE = "$image",
                              TIME = "$time",
                              DOMAIN = "$domain",
                              SOURCE_TIME = "$source_time",
                              URL = "$url";
  private final static String META = "$meta",
                              CONTENT = "$content",
                              DATE = "$date",
                              GROUP = "$group",
                              STATUS = "$status";
  private final static String CATEGORY = "$category",
                              NAME = "$name";
//  private final static String FILTER = "$filter";
  private final static String TYPE = "$type";
  private final static String RELATION = "$relation",
                              PERCENT = "$percent";
  
  public String toSql(Object object, String sql) throws Exception {
    Field [] fields = object.getClass().getDeclaredFields();
    StringBuilder sqlBuilder = new StringBuilder(sql);
    for(Field field : fields){
      String name = "$"+field.getName();
      if(sqlBuilder.indexOf(name)< 0) continue;
      Object value = Reflection.getValue(object, field);
      replace(name, sqlBuilder, value != null ? value.toString() : new String(), true);
    }    
    return sqlBuilder.toString();
  }
  
  String toSql(Meta bean, String sql) {
    StringBuilder sqlBuilder = new StringBuilder(sql);
    replace(ID, sqlBuilder, bean.getId(), false);
    replace(TITLE, sqlBuilder, bean.getTitle(), true);
    replace(DES, sqlBuilder, bean.getDesc(), true);
    replace(IMAGE, sqlBuilder, bean.getImage(), true);
    replace(URL, sqlBuilder, bean.getSource(), true);
    replace(DOMAIN, sqlBuilder, bean.getDomain(), false);
    replace(TIME, sqlBuilder, bean.getTime(), true);
    replace(SOURCE_TIME, sqlBuilder, bean.getSourceTime(), true);
    return sqlBuilder.toString();
  }
  
  String toSql(Content bean, String sql) {
    StringBuilder sqlBuilder = new StringBuilder(sql);
    replace(META, sqlBuilder, bean.getMeta(), false);
    replace(CONTENT, sqlBuilder, bean.getContent(), true);
    replace(STATUS, sqlBuilder, String.valueOf(bean.getStatus()), false);
    replace(DATE, sqlBuilder, bean.getDate(), false);
    return sqlBuilder.toString();
  }
  
  String toSql(Domain bean, String sql) {
    StringBuilder sqlBuilder = new StringBuilder(sql);
    replace(ID, sqlBuilder, bean.getId(), false);
    replace(DATE, sqlBuilder, bean.getDate(), false);
    replace(GROUP, sqlBuilder, bean.getGroup(), false);
    replace(CATEGORY, sqlBuilder, bean.getCategory(), true);
    replace(NAME, sqlBuilder, bean.getName(), true);
    return sqlBuilder.toString();
  }
  
//  String toSql(Filter bean, String sql) {
//    StringBuilder sqlBuilder = new StringBuilder(sql);
//    replace(NAME, sqlBuilder, bean.getName(), true);
//    replace(FILTER, sqlBuilder, bean.getFilter(), true);
//    replace(META, sqlBuilder, bean.getMeta(), true);
//    return sqlBuilder.toString();
//  }
  
  String toSql(Image bean, String sql) {
    StringBuilder sqlBuilder = new StringBuilder(sql);
    replace(ID, sqlBuilder, bean.getId(), false);
    replace(META, sqlBuilder, bean.getMeta(), false);
    replace(NAME, sqlBuilder, bean.getName(), true);
    replace(TYPE, sqlBuilder, bean.getType(), true);
    return sqlBuilder.toString();
  }
  
  String toSql(Relation bean, String sql) {
    StringBuilder sqlBuilder = new StringBuilder(sql);
    replace(META, sqlBuilder, bean.getMeta(), false);
    replace(RELATION, sqlBuilder, bean.getRelation(), false);
    replace(PERCENT, sqlBuilder, String.valueOf(bean.getPercent()), false);
    return sqlBuilder.toString();
  }
  
  public void replace(String name, StringBuilder builder, String value, boolean safe){
    if(value == null) value = new String();
    int index = builder.indexOf(name);
    if(index < 0) return ;
    if(safe) value = value.replace('\'', '"');
    int end;
    while(index > -1){
      end  = index+name.length();
      builder.replace(index, end, value);
      index = builder.indexOf(name, index+value.length()+1);
      if(index >= builder.length()) break;
    }
  }
  
//  public static void main(String[] args) {
//    Meta meta1 = new Meta();
//    meta1.setId("3232423");
//    meta1.setTitle("title for this meta");
//    meta1.setDes("description 1");
//    meta1.setImage("image/1/2ewrer");
//    meta1.setDomain("domain123");
//    meta1.setSource("http://wwww.vnn.vn");
//    meta1.setTime("5:6:3 9-11-2004");
//    String sql = "INSERT INTO META " +
//        "(ID, DOMAIN_ID, TITLE, DES, IMAGE, TIME, URL)"+ 
//      "VALUES "+
//        "($id, $domain, N'$title', N'$des', '$image', '$time', '$url')";
//    System.out.println(SqlUtil.getInstance().toSql(meta1, sql));
//  }
  
}

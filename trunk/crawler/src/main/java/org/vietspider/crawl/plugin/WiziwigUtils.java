/***************************************************************************
 * Copyright 2001-2012 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.html.HTMLNode;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * May 3, 2012  
 */
public class WiziwigUtils {

  private static SimpleDateFormat scheduledFormat =
      new SimpleDateFormat("E, MMMM dd 'from' HH:mm yyyy");
  private static SimpleDateFormat yourTimeFormat = 
      new SimpleDateFormat("HH:mm:ss");

  private static SimpleDateFormat scheduledTimeFormat = 
      new SimpleDateFormat("HH:mm");

  private static SimpleDateFormat newScheduledFormat =
      new SimpleDateFormat("E, MMMM dd 'from' HH:mm");

  public static String preProcess(Meta meta, 
      Properties properties, String xml, long diff) throws Exception {
    XMLDocument xmlDoc = XMLParser.createDocument(xml, null);
    XMLNode root = xmlDoc.getRoot();
    if(root.getChildren() == null
        || root.getChildren().size() < 1) return xml;
    return preProcess(meta, properties, xmlDoc, diff);
  }

  public static String preProcess(Meta meta, Properties properties,
      XMLDocument xmlDoc, long diff) throws Exception {
    XMLNode root = xmlDoc.getRoot();
    List<XMLNode> nodes = root.getChild(0).getChildren();
    for(int i = 0; i < nodes.size(); i++) {
      XMLNode node = nodes.get(i);
      if(node.getChildren() == null
          || node.getChildren().size() < 1) continue;
      String name = node.getName();
      if(name == null) continue;
      XMLNode child = node.getChild(0);
      if(child == null) continue;

      if(name.equals("match")
          || name.equals("scheduled")) {
        String value = child.getTextValue();
        int idx = value.indexOf(':');
        if(idx > 0) value = value.substring(idx+1).trim();
        child.setValue(value.toCharArray());
        //        System.out.println(value);
      } 

      if(name.equals("match")) {
        String value = child.getTextValue();
        value = translate(properties, value, "vs");
        child.setValue(value.toCharArray());
        meta.setTitle(value);
      }

      if(name.equals("competition")) {
        String value = child.getTextValue();
        value = translate(properties, value, "-");
        child.setValue(value.toCharArray());
        meta.setDesc(value);
      }

      if(diff != -1 && name.equals("scheduled")) {
        String value = child.getTextValue();
        Calendar from = createFromTime(value, diff);
        if(from != null) {
          String to = createToTime(value, diff);
          if( to != null) {
            value = newScheduledFormat.format(from.getTime());
            value += " to " + to;
            child.setValue(value.toCharArray());
          }
        }

      }

      // only for test
      //      if(name.equals("scheduled")) {
      //        String value = child.getTextValue();
      //        SimpleDateFormat scheduledFormat = new SimpleDateFormat("E, MMMM dd 'from' HH:mm yyyy");
      //        System.out.println(parse(scheduledFormat, value));
      //      }
    }
    StringBuilder builder = new StringBuilder(500);
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
    builder.append(xmlDoc.getRoot().getChildren().get(0).getTextValue());
    return builder.toString();
  }

  public static boolean hasURL(XMLDocument xmlDoc) throws Exception {
    XMLNode root = xmlDoc.getRoot();

    List<XMLNode> nodes = root.getChild(0).getChildren();
    for(int i = 0; i < nodes.size(); i++) {
      XMLNode node = nodes.get(i);
      String name = node.getName();
      if(name == null
          || !"url".equals(name)) continue;

      return (node.getChildren() != null
          && node.getChildren().size() > 0);
    }
    return false;
  }

  public static Calendar getScheduled(XMLDocument xmlDoc) throws Exception {
    XMLNode root = xmlDoc.getRoot();

    List<XMLNode> nodes = root.getChild(0).getChildren();
    for(int i = 0; i < nodes.size(); i++) {
      XMLNode node = nodes.get(i);
      String name = node.getName();
      if(name == null) continue;

      if(node.getChildren() == null
          || node.getChildren().size() < 1) continue;

      XMLNode child = node.getChild(0);
      if(child == null) continue;

      if(name.equals("scheduled")) {
        String value = child.getTextValue();
        return parse(value);
      }
    }
    return null;
  }

  public static Calendar parse(String value) throws Exception {
    int idx = value.indexOf("to");
    if(idx < 1) return null;
    String dateValue = value.substring(0, idx).trim();
    Calendar calendar = Calendar.getInstance();
    dateValue += " " + String.valueOf(calendar.get(Calendar.YEAR));
    try {
      Date date = scheduledFormat.parse(dateValue);
      calendar.setTime(date);
      //    System.out.println(calendar.getTime());
      return calendar;
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, dateValue + " -  " + e.getMessage());
      throw e;
    }
  }

  public static Calendar createFromTime(String value, long diff) {
    int idx = value.indexOf("to");
    
    try {
      String timeValue = value.substring(0, idx);
      Calendar calendar = Calendar.getInstance();
      timeValue += String.valueOf(calendar.get(Calendar.YEAR));
      
      idx = 0;
      while(idx < timeValue.length()) {
        char c = timeValue.charAt(idx);
        if(Character.isDigit(c)) break;
        idx++;
      }
      
      while(idx < timeValue.length()) {
        char c = timeValue.charAt(idx);
        if(!Character.isDigit(c)) break;
        idx++;
      }
      
      StringBuilder builder = new StringBuilder(timeValue);
      while(idx < builder.length()) {
        char c = builder.charAt(idx);
        if(Character.isLetter(c)) {
          builder.deleteCharAt(idx);
          continue;
        }
        break;
      }
      
      timeValue = builder.toString();
      
      Date date = scheduledFormat.parse(timeValue);
      calendar.setTime(date);
      
//      System.out.println(diff/(60*60*1000l));

      calendar.setTimeInMillis(calendar.getTimeInMillis() + diff);

      return calendar;
      //        String newHour = scheduledTimeFormat.format(calendar.getTime());
      //        
      //        value = value.replaceFirst(hourValue, newHour);
      //        System.out.println("thuan tahy co " + newHour);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, value+ " - " + e.getMessage());
    }
    return null;
  }


  public static String createToTime(String value, long diff) {
    int idx = value.indexOf("to");
    if(idx  < 0 || idx >= value.length() - 4) return null;
    while(idx < value.length()) {
      char c = value.charAt(idx);
      if(Character.isDigit(c)) break;
      idx++;
    }
    try {
      int end = value.indexOf(' ', idx); 
      if(end < 0) end = value.length();
      String hourValue = value.substring(idx, end);
      
      Date date = scheduledTimeFormat.parse(hourValue);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(Calendar.YEAR, 
          Calendar.getInstance().get(Calendar.YEAR));
      calendar.set(Calendar.MONTH, 
          Calendar.getInstance().get(Calendar.MONTH));
      calendar.set(Calendar.DAY_OF_MONTH, 
          Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

      calendar.setTimeInMillis(calendar.getTimeInMillis() + diff);

      return scheduledTimeFormat.format(calendar.getTime());

      //        value = value.replaceFirst(hourValue, newHour);
      //        System.out.println("thuan tahy co " + newHour);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String translate(Properties properties,
      String value, String separator) {
    String transValue = properties.getProperty(value);
    if(transValue != null) return transValue;

    int idx = value.indexOf(separator);
    if(idx < 1) return value;


    String key1 = value.substring(0, idx).trim();
    String transValue1 = properties.getProperty(key1);

    String key2 = value.substring(idx + separator.length()).trim();
    String transValue2 = properties.getProperty(key2);

    if(transValue1 != null) {
      value = value.replaceAll(key1, transValue1);
    }

    if(transValue2 != null) {
      value = value.replaceAll(key2, transValue2);
    }

    return value;
  }

  public static Properties loadDictionary(File file) {
    Properties properties = new Properties();
    if(!file.exists() || file.length() < 1) return properties;
    try {
      byte[] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      String [] elements = text.split("\n");
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim();
        if(elements[i].length() < 1) continue;
        int idx = elements[i].indexOf('=');
        if(idx < 0) continue;
        String key = elements[i].substring(0, idx).trim();
        String value = elements[i].substring(idx+1).trim();
        properties.put(key, value);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return properties;
  }

  public static long getDiffTime(List<HTMLNode> nodes) {
    int i = 0; 
    for(; i < nodes.size(); i++) {
      String value = nodes.get(i).getTextValue();
      if(value.indexOf("Your time") > -1) break;
    }
    if(i < nodes.size() -1) i++;
    if(i >= nodes.size()) return -1;

    return getDiffTime(nodes.get(i).getTextValue().trim());
  }

  public static long getDiffTime(String value) {
    try {
      Date date = yourTimeFormat.parse(value);
      Calendar current = Calendar.getInstance();
      Calendar systemTime = Calendar.getInstance();
      systemTime.setTime(date);
      systemTime.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
      systemTime.set(Calendar.MONTH, current.get(Calendar.MONTH));
      systemTime.set(Calendar.YEAR, current.get(Calendar.YEAR));

      return current.getTimeInMillis() - systemTime.getTimeInMillis();
      //      int hour = (int)(diff/(60*60*1000l));
      ////      System.out.println(" thay co "+ hour);
      //      return hour;
    } catch (Exception e) {
    }
    return -1;
  }
}

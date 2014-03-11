/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp.common;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.bean.Meta;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.locale.DetachDate;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2007  
 */
public final class EnDateTimeExtractor {

  private Object [][] timePatterns ;

  private Pattern [] amPmMarkersPatterns;

  private Object [][] datePatterns ;
  
  private String [][] replacePatterns;
  
  private String [] dateTimeWords;
  
  private String [] trustWords;
  
  private String longAgo = "ago";
  
  private NodeHandler nodeHandler;
  
  public EnDateTimeExtractor(NodeHandler nodeHandler) { 
    this.nodeHandler = nodeHandler;
    timePatterns = new Object[][]{
        {Pattern.compile("\\b\\d{1,2}\\s*[:]\\s*\\d{1,2}\\s*[:]\\s*\\d{1,2}\\b"), 2}, 
        {Pattern.compile("\\b\\d{1,2}\\s*[:]\\s*\\d{1,2}\\b"), 1},
        {Pattern.compile("\\b\\d{1,2}\\s+giờ\\s+\\d{1,2}\\b"), 2},
        {Pattern.compile("\\b\\d{1,2}\\s*h\\s*\\d{1,2}\\b"), 1},
        {Pattern.compile("\\b\\d{1,2}h\\b"), 1}
    };

    amPmMarkersPatterns = new Pattern[]{
        Pattern.compile("\\bam\\b"),
        Pattern.compile("\\bpm\\b"),
        Pattern.compile("\\bsa\\b"),
        Pattern.compile("\\bch\\b"),
        Pattern.compile("\\ba.m\\b"),
        Pattern.compile("\\bp.m\\b")
    };

    datePatterns = new Object[][]{
        {Pattern.compile("\\b\\d{1,2}\\s*[/]\\s*\\d{1,2}\\s*[/]\\s*\\d{4}\\b"), 2}, 
        {Pattern.compile("\\b\\d{1,2}\\s*[-]\\s*\\d{1,2}\\s*[-]\\s*\\d{4}\\b"), 2}, 
        {Pattern.compile("\\b\\d{1,2}[.]\\d{1,2}[.]\\d{4}\\b"), 2},

        {Pattern.compile("\\b\\d{1,2}\\s*[/]\\s*\\d{1,2}\\s*[/]\\s*\\d{2}\\b"), 2},
        {Pattern.compile("\\b\\d{1,2}\\s*[-]\\s*\\d{1,2}\\s*[-]\\s*\\d{2}\\b"), 2},
        {Pattern.compile("\\b\\d{1,2}[.]\\d{1,2}[.]\\d{2}\\b"), 2},

        {Pattern.compile("\\b\\d{1,2}\\s*[/]\\s*\\d{1,2}\\b"), 0},
        {Pattern.compile("\\b\\d{1,2}\\s*[-]\\s*\\d{1,2}\\b"), 0},
        {Pattern.compile("\\b\\d{1,2}[.]\\d{1,2}\\b"),  0},
        
        {Pattern.compile("\\b\\d{4}\\s*[/]\\s*\\d{1,2}\\s*[/]\\s*\\d{1,2}\\b"), 2},
        {Pattern.compile("\\b\\d{4}\\s*[-]\\s*\\d{1,2}\\s*[-]\\s*\\d{1,2}\\b"), 2},
        {Pattern.compile("\\b\\d{4}[.]\\d{1,2}[.]\\d{1,2}\\b"), 2},
        
        {Pattern.compile("\\b\\stháng\\s+\\d{1,2}\\s*[,]\\s*\\d{1,2}\\s*[,]\\s*\\d{4}\\b"), 3},
        {Pattern.compile("\\b\\s\\d{1,2}\\s+tháng\\s+\\d{1,2}\\b"), 2}
        
    };
    
    replacePatterns = new String[][]{
        {"january", "jan", "tháng 01,"},  {"february", "feb", "tháng 02,"},
        {"march", "mar", "tháng 03,"}, {"april", "apr", "tháng 04,"},
        {"may", "may", "tháng 05,"}, {"june", "jun", "tháng 06,"}, 
        {"july", "jul", "tháng 07,"}, {"august", "aug", "tháng 08,"},
         {"september", "sep", "tháng 09,"}, {"october", "oct", "tháng 10,"},
        {"november", "nov", "tháng 11,"}, {"december", "dec", "tháng 12,"}
    };
    
    dateTimeWords = new String []{"monday", "tuesday", "wednesday", "thursday", 
                               "friday", "saturday", "sunday"};   
    
    trustWords = new String[]{"last updated", "last update", "last modified", "article launched", 
                              "first posted", "updated", "posted", "published", "added", "at", "post"};
  }
  
  public void removeDateTimeNode(List<HTMLNode> list, Meta meta) {
    HTMLNode lastNode = null;
    DetachDate lastDetachDate = null;
    int lastEnd = -1;
    StringBuilder lastBuilder = null;
    Date date = null;
    HTMLText htmlText = new HTMLText();
    for(int i = 0; i < list.size(); i++) {
      if(i >= 10 && i < list.size() - 3) {
        i = list.size() - 4;
        continue;
      }
      
      HTMLNode node = upToParent(list.get(i));
      StringBuilder builder = new StringBuilder();
  
      htmlText.buildText(builder, node);
//      nodeHandler.buildContent(builder, node);
            
      String value = null;
      int end = -1;
      if(builder.charAt(0) == '(') {
        end = builder.indexOf(")", 1);
        if(end > -1) value  = builder.substring(1, end);
      } 
      if(value == null) value = builder.toString();
      if(value.length() >= 100) continue;
      value = value.toLowerCase();
      
      int idx = value.indexOf(longAgo);
      if(idx > -1 && hasTimeWord(value)) {
        date = computeDate(value.substring(idx));
        lastEnd = end;
        lastBuilder = builder;
        lastNode = node;
        break;
      }
      
      DetachDate detachDate = extractDateTime(value);
      if(detachDate == null || 
          (lastDetachDate != null && lastDetachDate.getScrore() >= detachDate.getScrore())) continue;
      lastDetachDate = detachDate;
      lastNode = node;
      lastEnd = end;
      lastBuilder = builder;     
    }
    if(lastNode == null) return;
    
    if(date == null && lastDetachDate != null) date = lastDetachDate.toDate();
    meta.setSourceTime(CalendarUtils.getDateTimeFormat().format(date));
    
    if(lastEnd > -1) {
      String value = lastBuilder.substring(lastEnd+1);
      lastNode.setValue(value.toCharArray());
    } else {
      nodeHandler.removeNode(lastNode);
    }
//    System.out.println(lastNode.getTextValue());
    nodeHandler.removeContent(lastNode.iterator(), list);
  }
  
  private HTMLNode upToParent(HTMLNode node) {
    HTMLNode parent = node.getParent();
    List<HTMLNode> children = parent.getChildren();
    int i = 0;
    for(; i < children.size(); i++ ){
      if(children.get(i) == node) break;
    }
    if(i < children.size()-1 && children.get(i+1).getName() == Name.SUP) return parent;
    return node;
  }
  
  public DetachDate extractDateTime(String value) {
    DetachDate detachDate = new DetachDate();
    for(int i = 0; i < replacePatterns.length; i++) {
      if(value.indexOf(replacePatterns[i][0]) < 0) continue;
      value = value.replaceFirst(replacePatterns[i][0], replacePatterns[i][2]);      
    }
    
    for(int i = 0; i < replacePatterns.length; i++) {
      if(value.indexOf(replacePatterns[i][1]) < 0) continue;
      value = value.replaceFirst(replacePatterns[i][1], replacePatterns[i][2]);      
    }
//    System.out.println(value);
    
    int am = -1;
    for(int j = 0; j < amPmMarkersPatterns.length; j++){ 
      Matcher matcher = amPmMarkersPatterns[j].matcher(value);
      if(!matcher.find()) continue;
      am = j;
      detachDate.increScrore(2);
      break;
    }
    
    for(int i = 0; i < timePatterns.length; i++){ 
      Matcher matcher = ((Pattern)timePatterns[i][0]).matcher(value);
      if(!matcher.find()) continue;

      String time = matcher.group().trim();     
      int [] numbers = getIntValue(time, 0);
      int hour = numbers[0];
      if(hour < 0 || hour > 24) return null;
      
      numbers = getIntValue(time, numbers[1]);
      if(numbers[0] > -1) {
        if(numbers[0] > 60) return null;
        if(hour < 12 && am > -1 && am%2 != 0) hour += 12; 
        detachDate.setHour(hour);
        detachDate.setMinute(numbers[0]);
        detachDate.increScrore(2);
        detachDate.increScrore((Integer)timePatterns[i][1]);
      }
      break;
    }
    
//    System.out.println("extracted time "+detachDate.getScrore());
    
    //extract date, month, year;
    for(int i = 0; i < datePatterns.length; i++) { 
      Matcher matcher = ((Pattern)datePatterns[i][0]).matcher(value);
      if(!matcher.find()) continue;
      String dateValue = matcher.group();
      int start = matcher.start();
      int end = matcher.end();
      if(start > 0 && value.charAt(start-1) == '(' 
         && end < value.length() && value.charAt(end) == ')') detachDate.increScrore(1);      
      //extract date
      int [] numbers = getIntValue(dateValue, 0);
      if(numbers[0] > 0 && numbers[0] < 32) {
        detachDate.setDate(numbers[0]);
        detachDate.increScrore(2);
      } else if(numbers[0] > 1990 && numbers[0] < 2100) {
        detachDate.setYear(numbers[0]);
        detachDate.increScrore(2);
      } else {
        return null;
      }
      //extract month
      numbers = getIntValue(dateValue, numbers[1]);
      if(numbers[0] > 0 && numbers[0] < 13) {
        detachDate.setMonth(numbers[0]);
        detachDate.increScrore(2);
      } else if(numbers[0] > 12 && numbers[0] < 32) {
        int date = detachDate.getDate();
        if(date > 12) return null;
        detachDate.setDate(numbers[0]);
        detachDate.setMonth(date);
        detachDate.increScrore(2);
      } else {
        return null;
      }
      
      if(detachDate.getDate() < 0 || detachDate.getMonth() < 0) break;
      //extract year;
      numbers = getIntValue(dateValue, numbers[1]);
      if(numbers[0] > 1900 && numbers[0] < 2200) {
        detachDate.setYear(numbers[0]);
        detachDate.increScrore(2);
      } else if(numbers[0] > 0 && numbers[0] < 90) {
        detachDate.setYear(2000+numbers[0]);
        detachDate.increScrore(2);
      } 
      detachDate.increScrore((Integer)datePatterns[i][1]);
      break;
    }
    
    for(String ele : trustWords) {
      if(value.indexOf(ele) < 0)  continue;
      detachDate.increScrore(4);
    }
    
    for(String ele : dateTimeWords) {
      if(value.indexOf(ele) < 0)  continue;
      detachDate.increScrore(3);
    }
    
//    System.out.println("extracted date " + detachDate.getScrore());
//    System.out.println(value  +  " ====> count "+count(value));
    
    detachDate.decreScrore(nodeHandler.count(value));
    
//    System.out.println("end score "+detachDate.getScrore());
    
    if(detachDate.getScrore() < 1) return null;
    return detachDate;
  }
  
  private int[] getIntValue(String value, int start) {
    int i = start;
    int from = -1;
    int to = -1;
    while(i < value.length()) {
      if(from < 0 && Character.isDigit(value.charAt(i))) from = i; 
      if(from > -1 && !Character.isDigit(value.charAt(i))) {
        to = i;
        break;
      }
      i++;
    }
    if(i <= value.length() && Character.isDigit(value.charAt(i-1))) to = i;
    if(from < 0 || to < 0) return new int[] {-1, start};
    return new int[]{Integer.parseInt(value.substring(from, to)), to};
  }
  
  public Date computeDate(String value) {
    int date = match(value, "\\d{1}\\s+ngày\\s");
    int hour = match(value, "\\d{1,2}\\s+giờ");
    if(hour < 0) hour = match(value, "\\d{1,2}\\s+h");
    int minute = match(value, "\\d{1,2}\\s+phút");
    if(minute < 0) minute = match(value, "\\d{1,2}\\s+[']");
    
//    System.out.println(value+" : "+date + " : "+hour+ " : "+ minute);
    
    if(date < 0 && hour < 0 && minute < 0) return null;
    Calendar calendar = Calendar.getInstance();
    if(date > -1) calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - date);
    if(hour > -1) calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
    if(minute > -1) calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minute);
    
    return calendar.getTime();
  }
  
  public boolean hasTimeWord(String value) {
    for(String ele : trustWords) {
      if(value.indexOf(ele) > -1)  return true;
    }
    return false;
  }
  
  private int match(String value, String template) {
    Pattern pattern  = Pattern.compile(template);
    Matcher matcher = pattern.matcher(value);
    if(matcher.find()) {
      String time = matcher.group();
      return getIntValue(time, 0)[0];
    }
    return -1;
  }
  
  
}

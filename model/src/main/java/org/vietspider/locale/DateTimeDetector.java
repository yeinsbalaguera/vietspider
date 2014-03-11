/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 2, 2009  
 */
public class DateTimeDetector {
  
  private Object [][] timePatterns ;

  private Pattern [] amPmMarkersPatterns;

  private Object [][] datePatterns ;
  
  private String [][] replacePatterns;
  
  private String [] dateTimeWords;
  
  private String [] trustWords;
  
  protected String longAgo = "cách đây";
  
  public DateTimeDetector() {
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
        Pattern.compile("\\bsáng\\b"),
        Pattern.compile("\\bchiều\\b")
    };

    datePatterns = new Object[][]{
        {Pattern.compile("\\b\\d{1,2}\\s*[/]\\s*\\d{1,2}\\s*[/]\\s*\\d{4}\\b"), 2}, 
        {Pattern.compile("\\b\\d{1,2}\\s*[-]\\s*\\d{1,2}\\s*[-]\\s*\\d{4}\\b"), 2}, 
        {Pattern.compile("\\b\\d{1,2}[.]\\d{1,2}[.]\\d{4}\\b"), 2},

        {Pattern.compile("\\b\\d{1,2}\\s*[/]\\s*\\d{1,2}\\s*[/]\\s*\\d{2}\\b"), 2},
        {Pattern.compile("\\b\\d{1,2}\\s*[-]\\s*\\d{1,2}\\s*[-]\\s*\\d{2}\\b"), 2},
        {Pattern.compile("\\b\\d{1,2}[.]\\d{1,2}[.]\\d{2}\\b"), 2},
        
        {Pattern.compile("\\b\\d{4}\\s*[/]\\s*\\d{1,2}\\s*[/]\\s*\\d{1,2}\\b"), 2},
        {Pattern.compile("\\b\\d{4}\\s*[-]\\s*\\d{1,2}\\s*[-]\\s*\\d{1,2}\\b"), 2},
        {Pattern.compile("\\b\\d{4}[.]\\d{1,2}[.]\\d{1,2}\\b"), 2},

        {Pattern.compile("\\b\\d{1,2}\\s*[/]\\s*\\d{1,2}\\b"), 0},
        {Pattern.compile("\\b\\d{1,2}\\s*[-]\\s*\\d{1,2}\\b"), 0},
        {Pattern.compile("\\b\\d{1,2}[.]\\d{1,2}\\b"),  0}, 
        

        {Pattern.compile("\\bngày\\s+\\d{1,2}\\s+tháng\\s+\\d{1,2}\\s+năm\\s+\\d{4}\\b"), 5},
        {Pattern.compile("\\bngày\\s+\\d{1,2}\\s+tháng\\s+\\d{1,2}\\s+năm\\s+\\d{2}\\b"), 5},
        {Pattern.compile("\\b\\d{1,2}\\s+tháng\\s+\\d{1,2}\\s+\\d{4}\\b"), 5},
        {Pattern.compile("\\b\\d{1,2}\\s+tháng\\s+\\d{1,2}\\s*[,]\\s+\\d{4}\\b"), 5},
               
        {Pattern.compile("\\bngày\\s+\\d{1,2}\\s+tháng\\s+\\d{1,2}\\b"), 3},
        {Pattern.compile("\\b\\s\\d{1,2}\\s+tháng\\s+\\d{1,2}\\b"), 2}
    };
    
    replacePatterns = new String[][]{
        {"tháng giêng", "tháng 01"},  {"tháng một", "tháng 01"},
        {"tháng hai", "tháng 02"}, {"tháng ba", "tháng 03"}, {"tháng tư", "tháng 04"},
        {"tháng năm", "tháng 05"}, {"tháng sáu", "tháng 06"}, {"tháng bảy", "tháng 07"},
        {"tháng tám", "tháng 08"}, {"tháng chín", "tháng 09"}, {"tháng mười", "tháng 10"},
        {"tháng mười một", "tháng 11"}, {"tháng chạp", "tháng 12"}, {"tháng mười hai", "tháng 12"}
    };
    
   dateTimeWords = new String []{"thứ hai", "thứ ba", "thứ tư", "thứ năm", 
                               "thứ sáu", "thứ bảy", "thứ bẩy", "chủ nhật", "gmt+7"};   
    
    trustWords = new String[]{"cập nhật", "update", "ngày đăng", "đăng lúc", 
                              "ngày gửi", "tin ngày", "tin đăng", "tin đưa", "đưa lúc", "post"};
  }
  
  
  public DetachDate detect(String value) {
    DetachDate detachDate = new DetachDate();
    for(int i = 0; i < replacePatterns.length; i++) {
      if(value.indexOf(replacePatterns[i][0]) < 0) continue;
      value = value.replaceFirst(replacePatterns[i][0], replacePatterns[i][1]);      
    }
    
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
//      System.out.println(datePatterns[i][0]+ " : " + dateValue);
      int start = matcher.start();
      int end = matcher.end();
      if(start > 0 && value.charAt(start-1) == '(' 
         && end < value.length() && value.charAt(end) == ')') detachDate.increScrore(1);      
      //extract date
      int [] numbers = getIntValue(dateValue, 0);
//      System.out.println("buoc truoc "+ dateValue+ " : "+0+ " ra "+ numbers[0]);
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
//      System.out.println("datateValue "+ dateValue+ " : "+numbers[1]);
      if(numbers[0] > 0 && numbers[0] < 13) {
//        System.out.println("tui tui "+numbers[0]);
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
      
//      if(detachDate.getDate() < 0 || detachDate.getMonth() < 0) break;
      //extract year;
      numbers = getIntValue(dateValue, numbers[1]);
//      System.out.println(" year hihi "+ detachDate.getDate());
      if(detachDate.getYear() > 2000 
          && detachDate.getDate() < 1 && numbers[0] < 32) {
        detachDate.setDate(numbers[0]);
        detachDate.increScrore(2);
      } else if(numbers[0] > 1900 && numbers[0] < 2200) {
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
    
    NodeHandler nodeHandler = new NodeHandler();
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
    int date = match(value, "\\d{1}\\s+ngày\\s*");
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

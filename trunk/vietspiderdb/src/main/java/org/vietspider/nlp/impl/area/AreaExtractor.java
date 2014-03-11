/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.area;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.chars.TextSpliter;
import org.vietspider.nlp.INlpExtractor;
import org.vietspider.nlp.impl.Unit;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2010  
 */
public class AreaExtractor implements INlpExtractor<Unit> {
  
  @SuppressWarnings("all")
  public void extract(String Id, Collection<?> values, TextElement element) {
    List<Point> points = element.getPoint(type());
    String text = element.getValue();
//    System.out.println(text);
    List<Unit> list = (List<Unit>) values;
    for(int i = 0; i < points.size(); i++) {
      Point point = points.get(i);
      StringBuilder builder = new StringBuilder();
      while(point != null) {
        if(point.getScore() > 0) {
          String value  = extract(text, point.getScore(), point.getStart(), point.getEnd());
//          System.out.println(" extracted value "+ value);
          if(value == null) continue;
          if(builder.length() > 0) builder.append('-');
          builder.append(value);
        }
        point = point.getNext();
      }
      
//      System.out.println(builder);
      
      Unit unit = null;
      String [] elements = split(builder.toString());
      for(int k = 0; k < elements.length; k++) {
        try {
          float value = convert(elements[k]);
//          System.out.println(" ===  >"+ value);
          if(value < 5) continue;
          if(unit == null) {
            unit = new Unit(value, "m2");
          } else {
//            System.out.println(" value "+ value);
            unit.setNext(value);
          }
        } catch (Exception e) {
//          System.err.println(elements[k]);;
        }
      }
      if(unit != null) {
        unit.sort();
        list.add(unit);
      }
    }
//    System.out.println(list.size());
    removeDuplicate(list);
  }

  private String extract(String text, int score, int start, int end) {
    //    return text.substring(start, end);
    
    if(score == 11) {
      StringBuilder builder = new StringBuilder();
      int i = start;
      while(i < end) {
        char c = text.charAt(i);
        if(c == 'm'
          && i > 0 && Character.isDigit(text.charAt(i-1))
          && i < text.length() - 1 && Character.isDigit(text.charAt(i+1))) c = '.';
        if(Character.isDigit(c)) {
          builder.append(c);
          i++;
          continue;
        }
        if((c == '.' || c == ',') 
            && builder.length() > 0
            && i < end - 1
            && Character.isDigit(text.charAt(i+1))) {
          builder.append('.');
          i++;
          continue;
        }
        if(builder.length() > 0) break;
        i++;
      }
      
      builder.append('x');
      
      if(end >= text.length()) end--;
      
      while(end > 0) {
        char c = text.charAt(end);
        if(!Character.isDigit(c)) {
          end--;
          continue;
        }
        end++;
        break;
      }
      
      StringBuilder builder2 = new StringBuilder();
      while(i < end) {
        char c = text.charAt(i);
        if(Character.isLetter(c) 
            && c != 'm'
              && c != 'x') {
          builder2.setLength(0);
          i++;
          continue;
        }
        if(Character.isDigit(c)) {
          builder2.append(c);
          i++;
          continue;
        }
        if((c == '.' || c == ',') 
            && builder2.length() > 0
            && i < end - 1
            && Character.isDigit(text.charAt(i+1))) {
          builder2.append('.');
          i++;
          continue;
        }
        i++;
      }
      
      builder.append(builder2);
//      System.out.println("=== > "+ builder2);
      return builder.toString();
    }

//    System.out.println(score);
//        System.out.println(" text : "+ text.substring(start, end));
    StringBuilder builder = new StringBuilder();
    int i = start;

    while(i < end) {
      char c = text.charAt(i);
      if(c == '&' 
        && i < text.length() - 4
        && text.charAt(i+1) == 's'
          && text.charAt(i+2) == 'u'
            && text.charAt(i+3) == 'p') {
        i += 4;
        continue;
      }
      if(c == '(' || c == ')') c = ' ';
      else if(c == '²') c = '2';
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        i++;
        continue;
      }
      if(c == 'M') c = 'm';
      if(c == '*' || c == 'X') c = 'x'; 
      builder.append(c);
      i++;
    }
//        System.out.println(" value "+ builder);
    return builder.toString();
  }
  
  private String[] split(String text) {
//    System.out.println(text);
    int counter = count(text, 0, ',');
    if(counter > 3) {
      TextSpliter spliter = new TextSpliter();
      return spliter.toArray(text, ',');
    }
    
    counter = count(text, 0, ';');
    if(counter > 0) {
      TextSpliter spliter = new TextSpliter();
      return spliter.toArray(text, ';');
    }
    
    int start = 0;
    int index = 0;
    List<String> list = new ArrayList<String>();
    //62m2-141m2
    while(index < text.length()) {
      char c = text.charAt(index);
      if(c == '-' || c == '–') {
        list.add(text.substring(start, index));
        while(index < text.length()) {
          c = text.charAt(index);
          if(Character.isDigit(c)) {
            start = index;
            break;
          }
          index++;
        }
      }
      index++;
    }
    
    if(start < text.length()) {
      list.add(text.substring(start, text.length()));
    }
    
    return list.toArray(new String[0]);
  }
  
  private int count(String text, int index, char separator) {
    int counter = 0;
    while(index < text.length()) {
      char c = text.charAt(index) ;
      if(separator == c) counter++;
      index++;
    }
    return counter;
  }

  private float convert(String text) throws Exception  {
    int idx = text.indexOf('x') ;
    if(idx > -1) {
      String width = text.substring(0, idx);
      String height = text.substring(idx+1);
      float a1 = parse(width);
      float a2 = parse(height);
      return ((float)((int)(a1*a2*100)))/100;
    }
    return parse(text);
  }

  private String normalize(String value) throws Exception {
    StringBuilder builder = new StringBuilder();
    char c1 = '-';
    char c2 = '-';
    for(int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if(c == ',') {
        if(c1 == '-') {
          c1 = c;
        } else {
          continue;
        }
      } else if(c == '.') {
        if(c2 == '-') {
          c2 = c;
        } else {
          continue;
        }
      } else if(c == 'm') {
//        System.out.println(" ==  >"+ c +  " : "+ i + " : " + value.length()+  " : " + value.charAt(i+1));
        if(i == value.length() -1) break;
        else if(i == value.length() - 2
            && value.charAt(i+1) == '2') break;
        else if(i == value.length() - 3
            && value.charAt(i+1) == '2'
              && value.charAt(i+2) == '-') break;
        
        else if(i == value.length() - 3
            && (value.charAt(i+1) == 'é' || value.charAt(i+1) == 'e')
            && value.charAt(i+2) == 't') break;
        c = '.';
      }
      
      if(c != '.' && c != ',' && !Character.isDigit(c)) continue;
      builder.append(c);
    }
//    System.out.println(" thay co "+ value);
//    System.out.println(" builder "+ builder);
    return builder.toString();
  }

  private float parse(String value) throws Exception {
    boolean ha = value.trim().endsWith("ha");
//    System.out.println(" truoc "+ value);
    value = normalize(value);
//    System.out.println("=====  >"+ value);

    int idx1 = value.indexOf('.');
    int idx2 = value.indexOf(',');
    if(idx1 < 0 && idx2 < 0) {
      if(ha) return Float.parseFloat(value)*1000;
      return Float.parseFloat(value); 
    }

    if(idx1 > -1 && idx2 > -1) {
      StringBuilder builder = new StringBuilder(value);
      if(idx2 > idx1) {
        builder.setCharAt(idx2, '.');
        builder.deleteCharAt(idx1);
      } else {
        builder.deleteCharAt(idx2);
      }
      value  = builder.toString();
      if(ha) return Float.parseFloat(value)*1000;
      return Float.parseFloat(value);
    }

    int idx = idx1 > 0 ? idx1 : idx2;
    int counter = value.length() - idx;
    StringBuilder builder = new StringBuilder();
    if(counter > 3) {
      for(int i = 0; i < value.length(); i++) {
        char c = value.charAt(i);
        if(!Character.isDigit(c)) continue;
        builder.append(c);
      }
    } else {
      builder.append(value);
      builder.setCharAt(idx, '.');
    }
    value  = builder.toString();
    if(ha) return Float.parseFloat(value)*1000;
    return Float.parseFloat(value);
  }
  
  private void removeDuplicate(List<Unit> list) {
    if(list.size() < 1) return;
    Unit unit = list.get(0);
    for(int i = 1; i < list.size(); i++) {
      if(!unit.equals(list.get(i))) return ;
    }
    list.clear();
    list.add(unit);
    return;
  }

  public boolean isExtract(TextElement element) {
    return element.getPoint(type()) != null;
  }

  public short type() { return NLPData.AREA; }
  
  public Collection<Unit> createCollection() {
    return new ArrayList<Unit>();
  }
  
  @SuppressWarnings("unused")
  public void closeCollection(Collection<?> collection) {
  }

  public static void main(String[] args) throws Exception {
    AreaExtractor extractor = new AreaExtractor();
//    System.out.println(extractor.parse("3.6"));
//    System.out.println(extractor.parse("3,6"));
//    System.out.println(extractor.parse("20,22,23"));
//    System.out.println(extractor.parse("148,6"));
//    System.out.println(extractor.parse("32.199"));
//    System.out.println(extractor.parse("32.199,42"));
//    System.out.println(extractor.parse("32,199.42"));
//    System.out.println(extractor.parse("32,199.42.45"));
//    System.out.println(extractor.parse("1m4"));
    System.out.println(extractor.parse("10ha"));
//    
//    System.out.println(extractor.convert("5x20"));
//    System.out.println(extractor.convert("20.6m2"));
//    System.out.println(extractor.convert("1m5x1m4"));
//    System.out.println(extractor.convert("4.5x40"));
    
//    String [] elements = extractor.split("62m2-141m2");
//    elements = extractor.split("52---93m2");
//    elements = extractor.split("68m2-85");
//    elements = extractor.split("6x12-8x24-12x20");
//    elements = extractor.split("200m2-160m2-70m2-72m2");
//    elements = extractor.split("51,68m2-85,76m2");
//    elements = extractor.split("84m2-87,7m2-89,3m2-104m2");
//    elements = extractor.split("78-81-85-100-102mét");
//    elements = extractor.split("33,35,38,51,74,98,137,157,233m2");
//    elements = extractor.split("56m2-65m2-69m2-81m2-97m2");
//    elements = extractor.split("80m2-86.3m2-94m2-107m2-111.1m2-120m2");
//    
//    for(String ele : elements) {
//      System.out.println(ele);
//    }
  }

}

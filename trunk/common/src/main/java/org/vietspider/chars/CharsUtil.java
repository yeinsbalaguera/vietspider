/***************************************************************************
 * Copyright 2001-2006 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.chars;


/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 13, 2006  
 */
public final class CharsUtil {
  
  public static int indexOf(char[] value, char [] c, int start){
    boolean is = false;
    for(int i = start; i < value.length; i++){
      is = true;
      for(int j = 0; j< c.length; j++){
        if(i+j < value.length && c[j] ==  value[i+j]) continue;
        is = false;
        break;
      }      
      if(is) return i;
    }
    return -1;
  }
  
  public static String cutAndTrim(String text) {
    char [] chars = text.toCharArray();
    return new String(cutAndTrim(chars, 0, chars.length)); 
  }
  
  public static char [] cutAndTrim(char [] data, int start, int end) {
    int s = start;
    int e = end-1;
    while(s < end){
      if(!Character.isWhitespace(data[s]) && !Character.isSpaceChar(data[s])) break;
      s++;
    }
    while(e > start){
      if(!Character.isWhitespace(data[e]) && !Character.isSpaceChar(data[e])) break;
      e--;
    }
    e++;
//    if(s == start && e == end) return data;
//    System.out.println(s + " : "+ s);
    if(e <= s) return new char[0];
    char [] newChar = new char[e - s];
    System.arraycopy(data, s, newChar, 0, newChar.length);
    return newChar;
  }
  
  public static char [] cutBySpace(char [] data, int start){
    int e = start;
    while(e < data.length){
      if(Character.isWhitespace(data[e])) break;
      e++;
    }   
    if(e <= start) return new char[0];
    char [] newChar = new char[e-start];   
    System.arraycopy(data, start, newChar, 0, newChar.length);
    return newChar;
  }
  
  public static int indexOfIgnoreCase(char[] value, char [] c, int start){
    return indexOfIgnoreCase(value, c, start, value.length);
  }
  
  public static int indexOfIgnoreCase(char[] value, char [] c, int start, int max){
    boolean is = false;
    for(int i = start; i < max; i++){
      is = true;
      for(int j = 0; j< c.length; j++){        
        if(i+j < value.length 
           &&  Character.toLowerCase(c[j]) == Character.toLowerCase(value[i+j])) continue;
        is = false;
        break;
      }      
      if(is) return i;
    }
    return -1;
  }
  
  public static int indexOf(char[] value, char c, int start){
    for(int i = start; i < value.length; i++){
      if(c == value[i]) return i;
    }
    return -1;
  }
  
  public static int startWith(char[] value, char c){
    for(int i = 0; i < value.length; i++){
      if(Character.isSpaceChar(value[i]) || Character.isWhitespace(value[i])) continue;
      else if(c == value[i]) return i;
      else return -1;
    }
    return -1;
  }
  
  public static char[] copyOfRange(char[] original, int from, int to) {
    int newLength = to - from;
    if (newLength < 0) throw new IllegalArgumentException(from + " > " + to);
    char[] copy = new char[newLength];
    System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
    return copy;
  } 
  
  /*public static String[] split2Array(String value, char separator) {
    List<String> list = split2List(value, separator);
    return list.toArray(new String[list.size()]);
  }
  
  public static List<String> split2List(String value, char separator) {
    int start  = 0;
    int index  = 0;
    List<String> list = new ArrayList<String>();
    while(index < value.length()) {
      char c = value.charAt(index);
//      if(!Character.isLetterOrDigit(c) 
//          && c != ' ' && c != ',' && c != '-' && c != '/') {
//        System.out.println(" =====>" + (int)c + " : "+ (int)separator + " : " + (c ==  separator));
//        System.out.println(" =====>" + c + " : "+ separator + " : " + (c ==  separator));
//        System.out.println("=======>" + (c == '\r'));
//      }
      if(c == separator) {
        list.add(value.substring(start, index));
        start = index+1;
      }
      index++;
    }
    if(start < value.length()) {
      list.add(value.substring(start, value.length()));
    }
    return list;
  }*/
  
  public static boolean startsWith(String text, String prefix, int toffset) {
    int po = 0;
    int pc = prefix.length();
    if(toffset < 0 || pc > text.length()) return false;
    
    char ta[] = text.toCharArray();
    int to = toffset;
    char pa[] = prefix.toCharArray();
    
    while (--pc >= 0) {
      char c1 = ta[to++];
      char c2 = pa[po++];
      if (c1 == c2 
          || Character.toLowerCase(c1) == Character.toLowerCase(c2)) continue;
      return false;
    }
    return true;
  }

//  public static void main(String args[]){ 
//    CharsUtil test = new CharsUtil();
//    String text = "TrAn thu do";
//    String prefix = "trAn ";
//    System.out.println(test.startsWith(text, prefix, 0));
//    String [] elements = test.split(value, ';');
    //  System.out.println(" === >"+ elements.length);
    //  for(String ele : elements) {
    //    System.out.println(ele);
    //  }
//  }

 /* public static void main(String args[]){
  * 
  *  String value = "Rất vui ; khi nghe; tin sẽ ;có Joomla ;plugin ;";
    String [] elements = test.split(value, ';');
    System.out.println(" === >"+ elements.length);
    for(String ele : elements) {
      System.out.println(ele);
    }
    int max = 1000000;
    
    long start  = System.currentTimeMillis();
    for(int i = 0; i < max; i++) {
      test2(value);
    }
    long end  = System.currentTimeMillis();
    long total1 = end - start;
    
    start  = System.currentTimeMillis();
    for(int i = 0; i < max; i++) {
      test1(value);
    }
    end  = System.currentTimeMillis();
    long total2 = end - start;
    
    String yahoo =" nhu dinh thuan nhu      dinh  ";
    String pattern = "dinh";
    System.out.println(indexOf(yahoo.toCharArray(), pattern.toCharArray(), 7));
    System.out.println(indexOf(yahoo.toCharArray(), 'd', 0));
    pattern = "DiNh";
    System.out.println(indexOf(yahoo.toCharArray(), pattern.toCharArray(), 7));
    System.out.println(indexOfIgnoreCase(yahoo.toCharArray(), pattern.toCharArray(), 7));
    
    char [] data = cutAndTrim(yahoo.toCharArray(), 19, 31);
    System.out.println("|"+new String(data)+"|");
    data = cutBySpace(yahoo.toCharArray(), 1);
    System.out.println("|"+new String(data)+"|");
  }*/
  
  // trim and to lowercase
  public static String normalize(String text) {
    StringBuilder builder = new StringBuilder(text) ;
    
    while(builder.length() > 0) {
      char c = builder.charAt(0);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        builder.deleteCharAt(0);
        continue;
      }
      break;
    }
    
    while(builder.length() > 0) {
      char c = builder.charAt(builder.length() - 1);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        builder.deleteCharAt(builder.length() - 1);
        continue;
      }
      break;
    }
    
    int length  = builder.length();
    if(length < 1) return builder.toString();
    
    if(Character.isLowerCase(builder.charAt(0))) return builder.toString();
    
    int index = 1;
    while(index < length) {
      char c = builder.charAt(index);
      if(Character.isUpperCase(c) || Character.isDigit(c)) return builder.toString();
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) break;
      index++;
    }
    
    while(index < length) {
      char c = builder.charAt(index);
      if(Character.isDigit(c)) {
        c = builder.charAt(index-1);
        if(Character.isWhitespace(c) 
            || Character.isSpaceChar(c)) {
          builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        }
        return builder.toString();
      }
      if(Character.isLetter(c)) {
        if(Character.isUpperCase(c)) return builder.toString();
//        System.out.println(" == > "+ builder);
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
//        System.out.println(" == > "+ builder);
        return builder.toString();
      }
      index++;
    }
    return builder.toString();
  }
  
  public static boolean equals(String pattern1, String pattern2) {
    int index1 = 0;
    int index2 = 0;
    int length1 = pattern1.length();
    int length2 = pattern2.length();
    
    char  c1 = pattern1.charAt(index1);
    while(!Character.isLetterOrDigit(c1)) {
      index1++;
      if(index1 >= length1) break;
      c1 = pattern1.charAt(index1);
    }
    
    char  c2 = pattern2.charAt(index2);
    while(!Character.isLetterOrDigit(c2)) {
      index2++;
      if(index2 >= length2) break;
      c2 = pattern2.charAt(index2);
    }
    
    while(true) {
      if(Character.isLetterOrDigit(c1) && Character.isLetterOrDigit(c2)) {
        if(Character.toLowerCase(c1) != Character.toLowerCase(c2)) return false;
      } 
      index1++;
      if(index1 >= length1) break;
      c1 = pattern1.charAt(index1);
      
      index2++;
      if(index2 >= length2) break;
      c2 = pattern2.charAt(index2);
      
//      System.out.println(c1 + " : "+ c2);
      if(Character.isLetterOrDigit(c1) && !Character.isLetterOrDigit(c2)) return false;
      if(!Character.isLetterOrDigit(c1) && Character.isLetterOrDigit(c2)) return false;
      
      index1++;
      if(index1 >= length1) break;
       
      c1 = pattern1.charAt(index1);
      while(!Character.isLetterOrDigit(c1)) {
        index1++;
        if(index1 >= length1) break;
        c1 = pattern1.charAt(index1);
      }
      
      index2++;
      if(index2 >= length2) break;
      c2 = pattern2.charAt(index2);
      while(!Character.isLetterOrDigit(c2)) {
        index2++;
        if(index2 >= length2) break;
        c2 = pattern2.charAt(index2);
      }
    }
    
    index1++;
    while(index1 < pattern1.length()) {
      c1 = pattern1.charAt(index1);
      if(Character.isLetterOrDigit(c1)) return false;
      index1++;
    }
    
    index2++;
    while(index2 < pattern2.length()) {
      c2 = pattern2.charAt(index2);
      if(Character.isLetterOrDigit(c2)) return false;
      index2++;
    }
    
    return true;
  }
  
  public static String cutLabel(String label, int size) {
    if(label.length() < size) return label;
    StringBuilder builder = new StringBuilder();
    int index =  0;
    while(index < label.length()) {
      char c = label.charAt(index);
      if(c == ' ' && builder.length() >= size) {
        builder.append("...");
        break;
      }
      builder.append(c);
      index++;
    }
    if(builder.length() > size) {
      builder.delete(size - 3, builder.length() - 3);
    }
    return builder.toString();
  }
/*  
  public static  List<String> splitLines(String text) {
    List<String> list = new ArrayList<String>();
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length) {
      if(text.charAt(i) == '\n') {
        String line  = text.substring(start, i).trim();
        start = i+1;
        i++;
        if(line.length() < 1) continue;
        list.add(line);
        continue;
      }
      i++;
    }
    if(start < length) list.add(text.substring(start, length));
    return list;
//    return list.toArray(new String[list.size()]);
  }*/
}

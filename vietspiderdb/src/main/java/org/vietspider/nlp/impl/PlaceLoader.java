/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 25, 2011  
 */
public class PlaceLoader {

  private static Country instance;
  
  public static Country load() throws Exception {
    if(instance != null)  return instance;
    instance = new PlaceLoader().load(UtilFile.getFolder("system/nlp/place/"));
    return instance;
  }
  
  private PlaceVariantCreator variantCreator;
  
  public PlaceLoader() {
    variantCreator = new PlaceVariantCreator();
  }
  
  public Country load(File folder) throws Exception {
    Country country = new Country();
    load(country, folder);
    File parent  = folder;
    File file  = new File(parent, "cities.dat");
    while(!file.exists()) {
      parent = parent.getParentFile();
      if(parent == null) break;
      file  = new File(parent, "cities.dat");
    }
    if(!file.exists()) return country;
    String text = new String(RWData.getInstance().load(file), Application.CHARSET);
    String [] elements = text.split("\n");
    for(int i = 0 ; i < elements.length; i++) {
       int index = elements[i].indexOf(':');
       elements[i] = elements[i].substring(index+1).trim().toLowerCase();
       Place place =  country.addDefaultCity(elements[i]);
       if(place == null) continue;
       String no_mark= VietnameseConverter.toTextNotMarked(elements[i]);
       place.addVariant(no_mark);
    }
    
    return country;
  }
  
  
  private void load(Country country, File file) throws Exception {
    List<File> list = new ArrayList<File>();
    loadFiles(list, file);
    for(int i = 0; i < list.size(); i++) {
      load2(country, list.get(i));
    }
    searchDuplicate(country.getHCM());
    searchDuplicate(country.getHN());
    List<Place> cities = country.getCities();
    for(int i = 0; i < cities.size(); i++) {
      searchDuplicate(cities.get(i));
    }
  }
  
  private void load2(Country country, File file) throws Exception {
    if(!file.getName().endsWith(".dat")) return;
    String text = new String(RWData.getInstance().load(file), Application.CHARSET);
    String [] elements = text.split("\n");
    
    Place city = null;
    Place district = null;
    TextSpliter spliter = new TextSpliter();
    for(int i = 0; i < elements.length; i++) {
      if((elements[i] = elements[i].trim()).isEmpty()) continue;
      int idx = elements[i].indexOf('#');
      if(idx > 0) elements[i] = elements[i].substring(idx);
//      System.out.println(elements[i].charAt(0));
//      System.out.println(elements[i].startsWith("#|name"));
      
      int index = elements[i].indexOf(':');
      String ignore = null; 
      int end = elements[i].indexOf('/');
      if(end < 0) {
        end = elements[i].length();
      } else {
        ignore = elements[i].substring(end+1);
      }
      String value = elements[i].substring(index+1, end);
      String [] names = spliter.toArray(value, ',');
      
      if(elements[i].startsWith("#|name")) {
        if(names.length < 1) throw new Exception(elements[i]+ ": Invalid city");
        
        city = country.addCity(names[0]);
        if(ignore != null) {
          city.addIgnore(spliter.toArray(ignore, ','), 0);
        }
//        System.out.println(city.hashCode() + " : "+ city.getName());
        city.addVariant(names, 1);
      } else if(elements[i].startsWith("@|name")){
        if(city == null) throw new Exception(elements[i]+ ": Not found city!");
        
        if(names.length < 1) throw new Exception(elements[i] + ": Invalid district");
        
        district = city.addChild(names[0], Place.DISTRICT);
        if(ignore != null) {
          district.addIgnore(spliter.toArray(ignore, ','), 0);
        }
        district.addVariant(names, 1);
      } else if(elements[i].startsWith("$|name")){
        if(district == null) throw new Exception(elements[i]+ ": Unknown district!");
        addStreet(city, district, names, ignore);
      } else {
        addStreet(city, district, names, ignore);
      }
    }
  }
  
  private void addStreet(Place city,
      Place district, String [] names, String ignore) throws Exception {
    if(district == null) throw new Exception("Unknown district!");
//    Place parent = district != null ? district : city;
//    if(parent == null) throw new Exception(value + ": Unknown district!");
    
    if(names.length < 1) throw new Exception("Invalid street");
    Place street = district.addChild(names[0], Place.STREET);
    variantCreator.createVariant(street, names[0]);
    if(!city.getName().equals("hà nội")
        && !city.getName().equals("thành phố hồ chí minh")) {
      variantCreator.createVariant2(street, names[0]);
      variantCreator.createVariant3(street, names[0]);
    }
    if(ignore != null) {
      TextSpliter spliter = new TextSpliter();
      street.addIgnore(spliter.toArray(ignore, ','), 0);
    }
    street.addVariant(names, 1);
  }
  
 
  
  private void loadFiles(List<File> list, File file) {
    if(file.isDirectory()) {
      File [] files = file.listFiles();
      if(files == null) return ;
      for(int i = 0; i < files.length; i++) {
        if(file.isFile()) {
          list.add(files[i]);
        } else {
          loadFiles(list, files[i]);
        }
      }
      return ;
    }
    list.add(file);
  }
  
  private void searchDuplicate(Place city) {
    if(city == null) return;
    List<Place> list = new ArrayList<Place>();
    city.toList(list);
    for(int i = 0; i < list.size(); i++) {
      String name = list.get(i).getName();
      for(int j = i + 1; j < list.size(); j++) {
        String name2 = list.get(j).getName();
        if(name2.length() <= name.length()) continue;
        int idx = name2.indexOf(name);
//        if(name2.indexOf("thanh oai") > -1 
//            && name.indexOf("thanh oai") > -1) {
//          System.out.println(idx);
//          System.out.println(name);
//          System.out.println(name2);
//          
//        }
        if(idx < 0) continue;
        if(idx > 0 
            && Character.isLetterOrDigit(name2.charAt(idx-1))) continue;
        if(idx+name.length() < name2.length() 
            && Character.isLetterOrDigit(name2.charAt(idx+name.length()))) continue;
        
          list.get(i).addIgnore(list.get(j));
      }
    }
  }
  
   
}

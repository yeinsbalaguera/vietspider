/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap("tree_menu")
public class TreeMenu2 {

  @NodeMap("date")
  private String date ;
  @NodesMap(value="categories", item="item")
  private List<Category> categories = new ArrayList<Category>();
  
  public TreeMenu2() {
  }
  
  public TreeMenu2(String date) {
    this.date = date;
  }
  
  @NodeMap("total")
  private int total = 0;
  
  public void increaseTotal() {  total = total+1; }
  
  public void decreaseTotal() {  total = total-1; }
  
  public int getTotal()  { return total; }
  
  public void setTotal(int value )  { total = value; }
  
  public List<Category> getCategories() { return categories; }
  
  public Category add(String cate, String name){
    for(Category ele : categories){
      if(ele.category.equals(cate)){
        ele.add(name);
        return ele;
      }
    }
    Category category = new Category(cate);
    category.getSourcesName().add(new SourceName(name));
    categories.add(category);
    return category;
  }
  
  public Category getCategory(String name) {
    for(Category ele : categories){
      if(ele.category.equals(name)) return ele;
    }
    return null;
  }
  
  public String getDate() {return date; }
  public void setDate(String date) {
    this.date = date;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }


  @NodeMap("category")
  public static class Category {
    
    @NodeMap("category")
    private String category ;
    
    @NodesMap(value="sources_name", item="item")
    private List<SourceName> sources = new ArrayList<SourceName>();
    
    public Category() {
    }
    
    public Category(String category){
      this.category = category;
    }
    
    public SourceName add(String name){
      for(SourceName ele : sources){
        if(ele.getName().equals(name)) return ele;
      } 
      SourceName sourceName = new SourceName(name);
      sources.add(sourceName);
      return sourceName;
    }
    
    public SourceName getSourceName(String name) {
      for(SourceName ele : sources){
        if(ele.getName().equals(name)) return ele;
      } 
      return null;
    }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }    

    @GetterMap("sources_name")
    public List<SourceName> getSourcesName() { return sources; }
    @SetterMap("sources_name")
    public void setSources(List<SourceName> sources) {
      this.sources = sources;
    }


    @NodeMap("total")
    private int total = 0;
    
    public void increaseTotal() {  total = total+1; }
    
    public void decreaseTotal() {  total--; }
    
    public int getTotal()  { return total; }
    
    public void setTotal(int value )  { total = value; }

  }
  
  @NodeMap("source_name")
  public static class SourceName {
    
    @NodeMap("name")
    private String name;
    
    public SourceName() {
    }
    
    public SourceName(String name) {
      this.name = name;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    
    @NodeMap("total")
    private int total ;
    
    public void increaseTotal() {  total = total+1; }
    
    public void decreaseTotal() {  total--; }
    
    public int getTotal()  { return total; }
    
    public void setTotal(int value )  { total = value; }
  }
  
}

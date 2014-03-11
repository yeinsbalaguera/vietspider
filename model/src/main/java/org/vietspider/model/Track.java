/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.bean.Domain;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2011  
 */
@NodeMap("track")
public class Track {

  public final static short DATE = 1;
  public final static short GROUP = 2;
  public final static short CATEGORY = 3;
  public final static short SOURCE = 4;

  @NodeMap("name")
  private String name;
//  @NodesMap(value="ids", item="id")
//  private List<Long> ids = new ArrayList<Long>();
  @NodesMap(value="children", item="item")
  private List<Track> children = new ArrayList<Track>();
  @NodeMap("level")
  private short level = 0;

  private Track parent;
  
  private Properties properties = new Properties();

  public Track() {

  }

  public Track(String name, short lvl) {
    this.name = name;
    this.level = lvl;
  }

  public String getName() { return name; }
  public void setName(String name)  { this.name = name; }
  
  public String getFullName() {
    StringBuilder builder = new StringBuilder();
    builder.append(name);
    
    Track ascesstor = parent;
    while(ascesstor != null) {
      builder.insert(0, '.');
      builder.insert(0, ascesstor.getName());
      ascesstor = ascesstor.getParent();
    }
    
    return builder.toString();
  }

//  public List<Long> getIds() { return ids; }
//  public void setIds(List<Long> ids) { this.ids = ids; }

  public List<Track> getChildren() { return children; }
  public void setChildren(List<Track> children) { this.children = children; }

  public void setLevel(short level) { this.level = level; }
  public short getLevel() { return level; }

  public Track getParent() { return parent; }
  public void setParent(Track parent) { this.parent = parent; }

//  public void addAdd(List<Long> list) {
//    ids.addAll(list);
//  }

  public boolean addData(Domain domain/*, String id*/) {
    return addData(domain, /*id, */DATE);
  }

  public boolean addData(Domain domain, /*String id, */int lvl) {
//    if(lvl == SOURCE) {
//      if(name.equals(domain.getName())) {
////        ids.add(Long.parseLong(id));
//        return true;
//      };
//      return false;
//    }
//    System.out.println(name + " : "+ domain.getDate() + " : "+ lvl);
    //    if(lvl != this.level) return false;
    if(lvl == DATE) {
      if(name.equals(domain.getDate())) {
//        ids.add(Long.parseLong(id));
        for(int i = 0; i < children.size(); i++) {
          if(children.get(i).getName().equals(domain.getGroup())) {
            children.get(i).addData(domain, /*id,*/ lvl+1);
            return true;
          }
//          boolean _return = children.get(i).addData(domain, /*id,*/ lvl+1);
//          if(_return) return true;
        }

        Track track = new Track(domain.getGroup(), GROUP);
        track.setParent(this);
        children.add(track);
        track.addData(domain, /*id,*/ lvl+1);
        return true;
      }
      return false;
    }

    if(lvl == GROUP) {
      if(name.equals(domain.getGroup())) {
//        ids.add(Long.parseLong(id));
        for(int i = 0; i < children.size(); i++) {
          if(children.get(i).getName().equals(domain.getCategory())) {
            children.get(i).addData(domain, /*id,*/ lvl+1);
            return true;
          }
//          boolean _return = children.get(i).addData(domain, lvl+1);
//          if(_return) return true;
        }

        Track track = new Track(domain.getCategory(), CATEGORY);
        children.add(track);
        track.setParent(this);
        track.addData(domain, lvl+1);
        return true;
      }
      return false;
    } 

    if(lvl == CATEGORY){
      if(name.equals(domain.getCategory())) {
//        ids.add(Long.parseLong(id));
        for(int i = 0; i < children.size(); i++) {
          if(children.get(i).getName().equals(domain.getName())) {
            children.get(i).addData(domain, /*id,*/ lvl+1);
            return true;
          }
//          boolean _return = children.get(i).addData(domain, lvl+1);
//          if(_return) return true;
        }

        Track track = new Track(domain.getName(), SOURCE);
        children.add(track);
        track.setParent(this);
//        track.addData(domain, lvl+1);
        return true;
      }
      return false;
    } 

    return false;
  }

  //  public TrackId getSource(String date, String group, String cate, String source) {
  //    return getSource(date, group, cate, name, DATE);
  //  }

  public List<Track> getTrackIds(int lvl) {
    List<Track> tracks = new ArrayList<Track>();
    addTrackIds(tracks, lvl);
    return tracks;
  }

  private void addTrackIds(List<Track> list, int lvl) {
    if(level >  lvl) return;
//    System.out.println(lvl + " : "+ level + " : " + name );
    if(lvl == level) list.add(this);
    for(int i = 0; i < children.size(); i++) {
      children.get(i).addTrackIds(list, lvl);
    }
  }

  public Track getTrackId(String _name, int lvl) {
    if(lvl == level && name.equals(_name)) return this;

    for(int i = 0; i < children.size(); i++) {
      Track track = children.get(i).getTrackId(_name, lvl);
      if(track != null) return track;
    }

    return null;
  }
  
  public Properties getProperties() { return properties; }


}

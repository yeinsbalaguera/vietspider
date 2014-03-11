/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common.source;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;
import org.vietspider.model.Source;
import org.vietspider.model.SourceProperties;
import org.vietspider.net.server.CopySource;
import org.vietspider.net.server.URLPath;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 6, 2009  
 */
public class SimpleSourceClientHandler {
  
  private volatile static Groups CACHED_GROUP_COLLECTION;
  
  public static void clear() {
    CACHED_GROUP_COLLECTION = null;
  }

  public void saveSource(ClientConnector2 connector, Source source) throws Exception {
    String category = NameConverter.encode(source.getCategory());
    String group = source.getGroup().toString();
    String name = category + "." + NameConverter.encode(source.getName());
    source.getProperties().remove(SourceProperties.SOURCE_CONFIG_ERROR);

    Header [] headers = new Header[] {
        new BasicHeader("action", "save"), 
        new BasicHeader("versioning", "true"),
        new BasicHeader("file", "sources/sources/"+group+"/"+category+"/"+name)
    };
    
    String xml = Object2XML.getInstance().toXMLDocument(source).getTextValue();
    byte [] bytes = xml.getBytes(Application.CHARSET);
    bytes  = connector.post(URLPath.FILE_HANDLER, bytes, headers);
    if(bytes != null && bytes.length > 0){
      if("no save".equals(new String(bytes))) return;
    }

//    if(name.endsWith(SourceFileFilter.TEMPLATE_SUFFIX)) return;
    bytes = (group+"/"+category+"/"+name).getBytes();
    connector.post(URLPath.DATA_HANDLER, bytes, new BasicHeader("action", "source.index.save"));
  }
  
  public Groups loadGroups() throws Exception {
    if(CACHED_GROUP_COLLECTION != null) return CACHED_GROUP_COLLECTION;
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "sources/type/groups.xml")
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
    String xml = new String(bytes, Application.CHARSET);
    if(xml.trim().length() < 1 || xml.trim().equals("-1"))  return null;
    XMLDocument document = XMLParser.createDocument(xml, null);
    CACHED_GROUP_COLLECTION = XML2Object.getInstance().toObject(Groups.class, document);
    if(Application.LICENSE != Install.SEARCH_SYSTEM) computeGroups(CACHED_GROUP_COLLECTION);
    return CACHED_GROUP_COLLECTION;
  }
  
  public void saveGroups(Groups groups) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "save"),
        new BasicHeader("file", "sources/type/groups.xml")
    };
    String xml = Object2XML.getInstance().toXMLDocument(groups).getTextValue();
    byte [] bytes = xml.getBytes(Application.CHARSET);
    ClientConnector2 connector = ClientConnector2.currentInstance();
    connector.post(URLPath.FILE_HANDLER, bytes, headers);
  }

  private void computeGroups(Groups groupCollection) {
    Group [] groups = groupCollection.getGroups();
    ArrayList<Group> list = new ArrayList<Group>();
    String [] validGroups = Application.GROUPS;
    if(validGroups.length < 1) {
      validGroups = new String[]{"ARTICLE"};
    } 
    
    for(int i = 0; i < groups.length; i++) {
      for(String label : validGroups) {
        if(groups[i].getType().equalsIgnoreCase(label)) {
          list.add(groups[i]);
          break;
        }
      }
    }
    groupCollection.setGroups(list.toArray(new Group[list.size()]));
  }
  
  public byte[] loadPropertiesType() throws Exception {
    Header [] headers = new Header[] {        
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "sources/type/property_name")
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    return connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
  }
  
  public void copySources(CopySource copy) throws Exception {
    String srcGroup = copy.getSrcGroup();
    String srcCategory = copy.getSrcCategory();
    String [] srcNames = copy.getSrcNames();
    String [] desCategories = copy.getDesCategories();
    SourcesClientHandler handler = new SourcesClientHandler(srcGroup);
    for(String srcName : srcNames) {
      Source source = handler.loadSource(srcCategory, srcName);
      if(source == null) continue;
      for(String desCategory : desCategories) {
        Source source2 = source.clone();
        source2.setGroup(copy.getDesGroup());
        source2.setCategory(desCategory);
        new SourcesClientHandler(source2.getGroup()).saveSource(source2);
      }
    }
    if(!copy.isDelete()) return;
    handler.deleteSources(srcCategory, srcNames);
  }

}

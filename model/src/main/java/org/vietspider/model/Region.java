/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.PropertiesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 13, 2009  
 */
@NodeMap("region")
public class Region implements Serializable {

  private static final long serialVersionUID = 1L;
  
  public final static int DEFAULT = 0;
  public final static int TEXT = 1;
  public final static int CDATA = 2;
  public final static int FILE = 3;

  public final static String UPDATE = "region:update";

  public final static String EXTRACT = "region:extract";
  public final static String CLEAN = "region:clean";
  public final static String CLEAN_FROM = "article:region:clean:from";

  public final static String ARTICLE_TITLE = "article:title";
  public final static String ARTICLE_DESCRIPTION = "article:description";

  public final static String DATA_TITLE = ":title";
  public final static String DATA_DESCRIPTION = ":description";

  public final static String DATA_IMAGE = ":image";

  public final static String PRODUCT_NAME = "product:name";
  public final static String PRODUCT_PRICE = "product:price";
  public final static String PRODUCT_DESCRIPTION = "product:description";

  @NodeMap("name")
  private String name;

  @NodeMap("type")
  private int type = DEFAULT ;

  @NodesMap(value = "paths", item = "item", cdata=true)
  private String [] paths ;
  
  @PropertiesMap(value = "properties", item = "property")
  public Properties properties;

//  private transient boolean local = false;
  
  private transient NodePath [] nodePaths;

  public Region() {
  }

  public Region(String name) {
    this.name = name;
  }
  
  public Region(String name, String [] paths) {
    this.name = name;
    this.paths = paths;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public int getType() { return type; }
  public void setType(int t) { this.type = t;    }

  public String[] getPaths() { return paths; }
  public void setPaths(String[] paths) { this.paths = paths; }
  
  public boolean hasData() { return name != null && paths != null; }

//  public boolean isLocal() { return local; }
//  public void setLocal(boolean temp) { this.local = temp; }
  
  @GetterMap("properties")
  public Properties getProperties() {
    if(properties == null) properties = new Properties();
    return properties; 
  }
  @SetterMap("properties")
  public void setProperties(Properties sourceProperties) {
    this.properties = sourceProperties;
  }
  
  public Region clone() {
    Region newRegion = new Region(name);
    newRegion.setType(type);
    
    if(properties != null) {
      Properties newProperties = new Properties();
      Iterator<Object> iterator = properties.keySet().iterator();
      while(iterator.hasNext()) {
        String key = (String) iterator.next();
        String value  = properties.getProperty(key);
        newProperties.put(key, value);
      }
      newRegion.setProperties(newProperties);
    }
    
    if(paths != null) {
      String [] newPaths = Arrays.copyOf(paths, paths.length);
      newRegion.setPaths(newPaths);
    }
    
    return newRegion;
  }

  public NodePath[] getNodePaths()  throws Exception {
    if(paths == null) return null;
    if(nodePaths == null) {
      nodePaths = new NodePath [paths.length];
      NodePathParser pathParser = new NodePathParser();
      for(int i = 0; i < nodePaths.length; i++) {
        if(paths[i].indexOf('[') < 0 
            || paths[i].indexOf(']') < 0) continue;
        try {
          nodePaths[i] = pathParser.toPath(paths[i]); 
        } catch (Exception e) {
          throw e;
        }
      }
    }
    return nodePaths;
  }

}

/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.ads;

import java.util.Properties;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.PropertiesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 4, 2010  
 */
@NodeMap("advertise")
public class Advertise {
  
  public final static int TOP = 0;
  public final static int LEFT = 1;
  public final static int RIGHT = 2;

  @NodeMap("name")
  private String name;
  @NodeMap("link")
  private String link;
  @NodeMap("text")
  private String text;
  @NodeMap("image")
  private String image;
  
  @NodeMap("start")
  private long start;
  @NodeMap("end")
  private long end;
  
  @PropertiesMap(value = "properties", item = "property")
  public Properties properties;
  
  @NodeMap("type")
  private int type= TOP;
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public String getLink() { return link; }
  public void setLink(String link) { this.link = link; }
  
  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
  
  public String getImage() { return image; }
  public void setImage(String image) { this.image = image;  }
  
  public long getStart() { return start; }
  public void setStart(long start) { this.start = start; }
  
  public long getEnd() { return end; }
  public void setEnd(long end) { this.end = end; }
  
  public int getType() { return type;  }
  public void setType(int type) { this.type = type; }
  
  @GetterMap("properties")
  public Properties getProperties() {
    if(properties == null) properties = new Properties();
    return properties; 
  }
  
  @SetterMap("properties")
  public void setProperties(Properties sourceProperties) { 
    this.properties = sourceProperties; 
  }
  
  public boolean isValid() {
    return end - System.currentTimeMillis() > 0;
  }
  
  public String generateHTML() {
    StringBuilder builder = new StringBuilder();
    builder.append("<a style=\"text-decoration: none;\" href=").append(link).append(">");
    if(image != null) {
      if(image.endsWith(".swf")) {
        builder.append("<embed src=\"/site/file_adv/");
        builder.append(image).append("\"");
        builder.append(" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" wmode=\"transparent\"");
        builder.append(" type=\"application/x-shockwave-flash\"");
        if(type == TOP) {
          builder.append(" height=\"70\" width=\"100%\" ");
        } else {
          builder.append(" valign=\"top\" height=\"600\" width=\"160\" ");
        }
        builder.append(">");
      } else {
        builder.append(" <img src=\"/site/file_adv/").append(image).append("\"");
        builder.append("title=\"").append(text).append("\" ");
        if(type == TOP) {
          builder.append(" height=\"70\"  border=\"0\" ");
        } else {
          builder.append(" width=\"120\" border=\"0\" ");
        }
        builder.append(">");
      }
    } else {
      builder.append("<div  style=\"background: rgb(221, 221, 221);width: 120;\">")
              .append(text).append("</div>");
    }
    builder.append("</a>");
    return builder.toString();
  }
  
}

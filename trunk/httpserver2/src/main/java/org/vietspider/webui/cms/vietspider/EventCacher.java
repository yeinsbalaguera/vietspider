/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vietspider;

import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.db.database.DatabaseService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 6, 2007  
 */
public class EventCacher {
  
  private StringBuilder builder;
  
  static EventCacher INSTANCE = new EventCacher();
  
  public EventCacher() {
  }
  
  void render(String viewer) throws Exception {
    builder = new StringBuilder();
    List<Meta> metas = DatabaseService.getUtil().loadTopEvent();
    boolean hasImage = true;
    
    for(Meta meta : metas){
      hasImage = (meta.getImage() != null && meta.getImage().trim().length() > 0);
      builder.append("  <tr>\n");
      builder.append("    <td valign=\"top\" bgcolor=\"#ffffff\">\n");
      if(hasImage ) {
        builder.append("<table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        builder.append("  <tbody>");
        builder.append("    <tr>");
        builder.append("      <td> ");
        builder.append("        <img src=\"");builder.append(meta.getImage());builder.append("\" border=\"0\" width=\"50\">\n");
        builder.append("      </td>");
        builder.append("    </tr>");
        builder.append("  </tbody>");
        builder.append("</table>");
//        append("    </td>\n");  
      }
//      if(hasImage){
//        append("    <td valign=\"top\" bgcolor=\"#ffffff\">\n");
//      }else{
//        append("    <td colspan=\"2\" valign=\"top\" bgcolor=\"#ffffff\">\n");
//      }
      builder.append("<a href=\"");builder.append("/");builder.append(viewer);builder.append("/DETAIL/");builder.append(meta.getId());
      builder.append("\" align=\"justify\" class=\"tieudiem_link\">");builder.append(meta.getTitle());builder.append("</a>\n");
      builder.append("</td>\n");
      builder.append("</tr>\n");
    }
  }

  public StringBuilder getData() { return builder; }
  
}

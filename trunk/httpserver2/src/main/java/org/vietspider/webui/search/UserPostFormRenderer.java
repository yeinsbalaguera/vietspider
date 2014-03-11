package org.vietspider.webui.search;

import java.io.OutputStream;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.index.SearchQuery;
import org.vietspider.webui.search.page.PageChunk;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2006
 */
public class UserPostFormRenderer {
  
  private PageChunk form = new PageChunk("user_post_form.xml");

  private MenuRenderer menuRenderer;
  
  public UserPostFormRenderer() {
    menuRenderer = new MenuRenderer();
  }

  public void write(OutputStream out, String text, SearchQuery query) {  
    try {
      append(out, "<div class=\"info-bar\">");
      append(out, "</div>");

      append(out, "<div class=\"pagewrap\"><div class=\"page-content clearfix\"><div class=\"left-sidebar\"> ");
      menuRenderer.write(out, query, 0);
      append(out, "</div><div id=\"home_sidebar\" class=\"right-column\">");
      
      if(text == null) text = "";
      
      form.write(out, text);
      
      append(out, "</div></div>");
      append(out, "<div id=\"ft\"><div class=\"ft-top\"><div class=\"info-bar2\">");
      append(out, "</div>");
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }  

  protected void append(OutputStream output, String text) throws Exception { 
    try {
      output.write(text.getBytes(Application.CHARSET));
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }

}

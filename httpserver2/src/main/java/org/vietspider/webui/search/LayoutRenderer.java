package org.vietspider.webui.search;

import java.io.OutputStream;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.SearchQuery;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2006
 */
public class LayoutRenderer {

  private IteratorRenderer iteratorRenderer;
  private boolean bot = false;
  private MenuRenderer menuRenderer;

  public LayoutRenderer(boolean bot) {
    this.bot = bot;
    iteratorRenderer = new IteratorRenderer(bot);
    menuRenderer = new MenuRenderer();
  }

  public void write(OutputStream out, MetaList list, SearchQuery query) {  
    try {
      String pageData = iteratorRenderer.render1(list, query);

      append(out, "<div class=\"info-bar\">");
      append(out, pageData);
      append(out, "</div>");
//      append(out, "<tr><td height=\"15\"></td></tr>");

      append(out, "<div class=\"pagewrap\"><div class=\"page-content clearfix\"><div class=\"left-sidebar\"> ");
      if(!bot) {
        menuRenderer.write(out, query, list.getData().size());
      }
      append(out, "</div><div id=\"home_sidebar\" class=\"right-column\">");
      
      SuggestionRenderer suggestion = null;
      if(list.getCurrentPage() == 1) {
        suggestion = new SuggestionRenderer();
        StringBuilder builder = new StringBuilder();
        
//        if(query != null) {
//          suggestion.renderRegion(builder, query.getPattern(), query.getRegion());
//        }
        if(query != null) suggestion.renderRegion(builder, query);
        if(builder.length() > 0) {
          builder.insert(0, "<div class=\"suggestion suggestion_text\">");
          builder.append("</div>");
          append(out, builder.toString());
        }
      }
      
      if(list.getData().size() < 1) {
        append(out, "<div class=\"ErrorMessage\">");
        append(out, "<h3>&nbsp;&nbsp;Xin lỗi! Chúng tôi không tìm thất bất cứ kết quả nào.</h3>");
        append(out, "<ul>");
        append(out, "<li>&nbsp;&nbsp;Bạn có chắc chắn từ khóa nhập đúng chính tả?");
        append(out, "<li>&nbsp;&nbsp;Hãy sử dụng một từ khóa khác tương tự");
        append(out, "<li>&nbsp;&nbsp;Đừng sử dụng các liên từ hoặc ký tự số.");
        append(out, "<li>&nbsp;&nbsp;Hãy nhập tiếng Việt có dấu và thử tìm lại.");
        append(out, "</ul>");
        append(out, "</div>");
//        append(out, "</td></tr>");
      } else {
        MetaRenderer metaRenderer = new MetaRenderer(bot);
        for(Article ele : list.getData()) {
          if(ele == null) continue;
          out.write(metaRenderer.render(ele, query));
        }
      }
      
      if(list.getCurrentPage() == 1) {
        StringBuilder builder = new StringBuilder();
        suggestion.renderAction(builder, query);
        
        if(builder.length() > 0) {
//          builder.insert(0, "<div class=\"suggestion suggestion_text\">");
//          builder.append("</div>");
          append(out, builder.toString());
        }
      }
      
      if(query != null && list.getData().size() < list.getPageSize()) {
        List<String> histories = query.getHistory();
        if(histories != null) {
          append(out, menuRenderer.getHistory().renderForPage(histories));
        }
      }
      
      append(out, "</div></div>");

      append(out, "<div id=\"ft\"><div class=\"ft-top\"><div class=\"info-bar2\">");

      if(bot) {
        pageData = iteratorRenderer.render(list, query);
        append(out, pageData);
        append(out, "</div>");
      } else {
        pageData = iteratorRenderer.render(list, query);
        append(out, pageData);
        append(out, "</div>");
      }
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

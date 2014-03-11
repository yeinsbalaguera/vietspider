/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.model.Track;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2011  
 */
final class RMenuRenderer {
  
  private String[] configCategories;
  private String pcMenu;
  private String mobileMenu;
  
  RMenuRenderer() {
    File file = UtilFile.getFile("system/cms/vtemplate", "Menu.html");
    try {
      pcMenu = new String(RWData.getInstance().load(file), Application.CHARSET);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      pcMenu = e.toString();
    }
    
    file = UtilFile.getFile("system/cms/vtemplate", "MobileMenu.html");
    try {
      mobileMenu = new String(RWData.getInstance().load(file), Application.CHARSET);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      mobileMenu = e.toString();
    }
    
    file = UtilFile.getFile("system/cms/vtemplate/", "menu.txt");
    if(file.exists()) {
      try {
        String text = new String(RWData.getInstance().load(file), Application.CHARSET);
        List<String> values = new ArrayList<String>();
        String [] elements = text.trim().split("\n");
        for(int i = 0; i < elements.length; i++) {
          elements[i] = elements[i].trim();
          if(elements[i].indexOf('.') < 0) continue;
          values.add(elements[i]);
        }
        configCategories = values.toArray(new String[0]);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  String[] getConfigCategories() { return configCategories; }
  
  String render(HttpRequestData hrd, String [] items)  {
    String menu = hrd.isMobile() ? mobileMenu : pcMenu;
    //    String breadcumbs = CommonResources.INSTANCE.getBreadcumbdTemplate();
    //    String pattern = "$breadcumbs";
    //    int idx = html.indexOf(pattern);
    //    if(idx > -1) {
    //      builder.append(html.substring(start, idx));
    //      builder.append(renderBreadcumbs(hrd.getUriFolder(), items));
    //      start = idx + pattern.length();
    //    } 
    StringBuilder builder = new StringBuilder();
    int start = 0;
    String pattern = "$date_menu";
    int idx = menu.indexOf(pattern);
    if(idx > -1) {
      builder.append(menu.substring(start, idx));
      builder.append(renderDateMenu(hrd));
      start = idx + pattern.length();
    }

    Track track = null;
    pattern = "$category_menu";
    idx = menu.indexOf(pattern);
    if(idx > -1) {
      builder.append(menu.substring(start, idx));
      if(configCategories != null && configCategories.length > 0) {
        builder.append(renderCategories(hrd, items.length > 0 ? items[0] : null, configCategories));
      } else if(items != null && items.length > 0) {
        String date = items[0].replace('.', '/');
        try {
          track = EIDFolder2.loadTrack(date);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          builder.append(menu.substring(idx + pattern.length()));
          return builder.toString();
        }
        //SourceLogHandler.getInstance().loadData(date);
        
        List<Track> cateTrackIds = track.getTrackIds(Track.CATEGORY);

        String[] categories = new String[cateTrackIds.size()];
        for(int i = 0; i < categories.length; i++) {
          categories[i] = cateTrackIds.get(i).getParent().getName() 
            + "."+ cateTrackIds.get(i).getName();
        }
        builder.append(renderCategories(hrd, items[0], categories));
        
      } 
      start = idx + pattern.length();
    }

    /*pattern = "$sub_category_menu";
    idx = html.indexOf(pattern);
    if(idx > -1) {
      builder.append(html.substring(start, idx));
      if(items != null && items.length > 1 && domain != null) {
        List<CategoryInfo> cates = domain.getCategories();
        CategoryInfo cate = null;
        for(int i = 0; i < cates.size(); i++) {
          if(cates.get(i).getCategory().equals(items[1])) {
            cate = cates.get(i);
            break;
          }
        }
        if(cate != null) {
          String date = items[0].replace('.', '/');
          domain = SourceLogHandler.getInstance().loadData(date);
          builder.append(renderSubCategoryMenu(hrd, 
              items[0], cate.getCategory(), cate.getSources()));
        }
      }
      start = idx + pattern.length();
    }*/
    
    builder.append(menu.substring(start));

    return builder.toString();
  }

 /* private String renderBreadcumbs(String uriFolder, String [] items) {
    StringBuilder builder = new StringBuilder();
    builder.append("<span>");

    StringBuilder href = new StringBuilder(uriFolder);
    href.append('/').append("domain").append("/1/");

    if(items.length > 0) {
      builder.append(" <dfn>&nbsp;›&nbsp;</dfn> ");
      builder.append("<a href=\"#\" onclick=\"toggle_visibility('category_menu');\">");
      builder.append(items[0]); builder.append("</a>");
    } 

    if(items.length > 1) {
      builder.append(" <dfn>&nbsp;›&nbsp;</dfn> ");

      builder.append("<a href=\"#\" onclick=\"toggle_visibility('sub_category_menu');\">");

      int dot = items[1].indexOf('.');
      if(dot > 0) {
        builder.append(items[1].substring(dot+1));
      } else {
        builder.append(items[1]);
      }
      builder.append("</a>");
    }

    if(items.length > 2) {
      builder.append(" <dfn>&nbsp;›&nbsp;</dfn> ");
      builder.append("<a href=\"");
      builder.append(href.toString());builder.append(items[0]); builder.append("/");
      try {
        builder.append(URLEncoder.encode(items[1], "utf-8")); builder.append("/");
        builder.append(URLEncoder.encode(items[2], "utf-8")); builder.append("/");
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      builder.append("\">"); builder.append(items[2]); builder.append("</a>");
    }

    builder.append("</span>");

    return builder.toString();
  }*/

  private String renderDateMenu(HttpRequestData hrd) {
    StringBuilder builder = new StringBuilder();
//    SourceLogUtils log = new SourceLogUtils();
    String [] dates = EIDFolder2.loadDates();

    String date = null;
    //    for(int i = 0; i < hrd.getParams().length; i++) {
    //      System.out.println(hrd.getParams()[i]);
    //    }
    if(hrd.getParams().length > 1) {
      date = hrd.getParams()[1].replace('.', '/');
    } 

//    builder.append("");
    builder.append("<select style=\"visibility: visible;\" ");
    builder.append("onchange=\"window.location=this.options[this.selectedIndex].value;\");");
    builder.append(" id=\"boxmenu\" class=\"boxmenu\" name=\"select\">");
    
//    if(date == null) {
//      builder.append("<option selected value=\"");
//    } else {
//      builder.append("<option value=\"");
//    }
//    builder.append(hrd.getUriFolder());
//    builder.append("\">Tin nổi bật</option>");

    for(int i = 0; i < dates.length; i++) {
      String key = dates[i].replace('/', '.');
      if(dates[i].equals(date)) {
        builder.append("<option selected value=\"");
      } else {
        builder.append("<option value=\"");
      }
      builder.append(hrd.getUriFolder()).append('/').append("domain").append("/1/");
      builder.append(key).append("/").append("\">");
      builder.append(dates[i]);
      builder.append("</option>");
    }    
    builder.append("</select>");
    return builder.toString();
  }

  private String renderCategories(HttpRequestData hrd, String date, String [] categories) {
    if(date == null) {
      Calendar calendar = Calendar.getInstance();
      date = CalendarUtils.getParamFormat().format(calendar.getTime());
    }
//    System.out.println(" check mo bie "+ hrd.isMobile());
    if(hrd.isMobile()) {
      return renderCategoryMenuForMobile(hrd, date, categories);
    }
    StringBuilder builder = new StringBuilder();
//    List<CategoryInfo> cates = domain.getCategories();

    StringBuilder href = new StringBuilder(hrd.getUriFolder());
    href.append('/').append("domain").append("/1/");
    href.append(date).append('/');

    String category = "";
    if(hrd.getParams().length > 2) category = hrd.getParams()[2];

    for(int i = 0; i < categories.length; i++) {
      builder.append("&nbsp;&nbsp;<dfn>&nbsp;›</dfn>");
      boolean selected = categories[i].equals(category);
      if(selected) {
        builder.append("<span class=\"selected_head_menu\">");
      } else {
        builder.append("<a class=\"head_menu\" href=\"");
        builder.append(href.toString());
        try {
          builder.append(URLEncoder.encode(categories[i], "utf-8"));
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
        builder.append("/").append("\">");
      }
      String label = categories[i];
      int idx = categories[i].indexOf('.');
      if(idx > 0) label = label.substring(idx+1);
      builder.append(label);
      if(selected) {
        builder.append("</span>");
      } else {
        builder.append("</a>");
      }
    }
    return builder.toString();
  }
  
  private String renderCategoryMenuForMobile(HttpRequestData hrd, String date, String [] categories) {
    StringBuilder builder = new StringBuilder();

    StringBuilder href = new StringBuilder(hrd.getUriFolder());
    href.append('/').append("domain").append("/1/");
    href.append(date).append('/');

    String category = "";
    if(hrd.getParams().length > 2) category = hrd.getParams()[2];
    
    builder.append("&nbsp;&nbsp;&nbsp;<span class=\"boxmenu_title\">Mục:&nbsp;</span>");
    builder.append("<select style=\"visibility: visible;\" ");
    builder.append("onchange=\"window.location=this.options[this.selectedIndex].value;\");");
    builder.append(" id=\"boxmenu\" class=\"box_head_menu\" name=\"select\">");
    
    if(category == null) {
      builder.append("<option selected value=\"");
    }else{
      builder.append("<option value=\"");
    }
    builder.append(href.toString());
    builder.append("\">Tất Cả</option>");

    for(int i = 0; i < categories.length; i++) {
      boolean selected = categories[i].equals(category);
      if(selected) {
        builder.append("<option selected value=\"");
      }else{
        builder.append("<option value=\"");
      }
      builder.append(href.toString());
      try {
        builder.append(URLEncoder.encode(categories[i], "utf-8"));
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      builder.append("/").append("\">");
      String label = categories[i];
      int idx = label.indexOf('.');
      if(idx > 0) label = label.substring(idx+1);
      builder.append(label);
      builder.append("</option>");
    }    
    
    builder.append("</select>");
    return builder.toString();
  }

 /* private String renderSubCategoryMenu(HttpRequestData hrd, 
      String date, String category, List<SourceInfo> sources)  {
    StringBuilder builder = new StringBuilder();
    StringBuilder href = new StringBuilder(hrd.getUriFolder());
    href.append('/').append("domain").append("/1/");
    href.append(date).append('/');
    try {
      href.append(URLEncoder.encode(category, "utf-8")).append("/");
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }

    int rate = 6;
    if(hrd.isMobile()) rate = 3;

    for(int i = 0; i < sources.size(); i++) {
      if(i > 0 && i%rate == 0) builder.append("</tr><tr>");
      builder.append("<td><li>");
      builder.append("<dfn>&nbsp;›&nbsp;</dfn><a id=\"Menu1_dlMenu__ctl0_ltZone\" href=\"");
      builder.append(href.toString());
      try {
        builder.append(URLEncoder.encode(sources.get(i).getName(), "utf-8")); 
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      builder.append("/").append("\">");
      builder.append(sources.get(i).getName());
      builder.append("</a>");
      builder.append("</li></td>");
    }    

    return builder.toString();
  }*/
}

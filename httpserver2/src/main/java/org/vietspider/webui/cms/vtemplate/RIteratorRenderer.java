/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import org.vietspider.db.database.MetaList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2011  
 */
final class RIteratorRenderer {
  
  String render(HttpRequestData hrd, MetaList list)  {
    if(hrd.isMobile()) {
      return buildPageIteratorByIndexForMobile(hrd, list);
    }

    StringBuilder builder = new StringBuilder();
    builder.append("<form action=\"javascript:go_page();\" method=\"get\" id=\"page_form\">");
    builder.append("<div align=\"right\" id=\"iterator\">");

    int page  = list.getCurrentPage();
    int total = list.getTotalPage();
    int start = Math.max(page-2, 1);
    int end = Math.min(start+4, total);

    String uriFolder = hrd.getUriFolder();

    if(start > 1){
      builder.append("\n&nbsp;&nbsp;&nbsp;").append("<a class=\"other_page\" href=\"");
      builder.append(uriFolder).append("/").append(list.getAction()).append("/1/");
      builder.append(list.getUrl()).append("\">");      
      builder.append("Trang 1</a>&nbsp;&nbsp;...&nbsp;&nbsp;&nbsp;");
    }

    for(int idx = start ; idx <= end; idx++){
      if(idx == page) {
        builder.append("<span class=\"other_page\">(").append(total).append(")&nbsp;");
        builder.append("<input name=\"page\" size=\"3\" type=\"text\" style=\"text-align:center\" ");
        builder.append(" value=\"").append(page).append("\">");
        //        builder.append("<a href=\"javascript: go_page()\">&nbsp;duyệt&nbsp;</a>");
        builder.append("<script type=\"text/javascript\">");
        builder.append("function go_page() {");
        builder.append(" window.location='");
        builder.append(uriFolder).append("/").append(list.getAction()).append("/' + ");
        builder.append("document.forms[\"page_form\"].page.value");
        builder.append(" + '/");
        builder.append(list.getUrl()).append("';");
        builder.append("}</script>");
      } else {
        builder.append("<a class=\"other_page\" href=\"");
        builder.append(uriFolder).append("/").append(list.getAction()).append("/");
        builder.append(String.valueOf(idx)).append("/");
        builder.append(list.getUrl()).append("\"> Trang ");
        builder.append(String.valueOf(idx));
        builder.append("</a>");      
      }
      if(idx == end) break;
      builder.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
    }    

    if(end < total){
      builder.append("\n&nbsp;&nbsp;&nbsp;...&nbsp;&nbsp;").append("<a class=\"other_page\" href=\"");
      builder.append(uriFolder).append("/").append(list.getAction()).append("/");
      builder.append(String.valueOf(total)).append("/");
      builder.append(list.getUrl()).append("\">Trang ");      
      builder.append(String.valueOf(total)).append("</a>&nbsp;&nbsp;&nbsp;");
    }

    builder.append("</div>");
    builder.append("</form>");
    return builder.toString();
  }

  String buildPageIteratorByIndexForMobile(HttpRequestData hrd, MetaList list)  {
    int page  = list.getCurrentPage();
    int total = list.getTotalPage();
    if(total < 1) return "";

    StringBuilder builder = new StringBuilder();

    builder.append("<form action=\"javascript:go_page();\" method=\"get\" id=\"page_form\">");
    builder.append("<div align=\"right\" id=\"iterator\">");

    if(page > 1) {
      builder.append("<a class=\"other_page\" href=\"");
      builder.append(hrd.getUriFolder()).append("/").append(list.getAction()).append("/");
      builder.append(String.valueOf(page-1)).append("/");
      builder.append(list.getUrl()).append("\"> Trước</a>");
      builder.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
    }

    builder.append("<span class=\"other_page\">(").append(total).append(")&nbsp;");
    builder.append("<input name=\"page\" size=\"5\" type=\"text\" style=\"text-align:center\" ");
    builder.append(" value=\"").append(page).append("\">");
    builder.append("<a href=\"javascript: go_page()\">&nbsp;duyệt&nbsp;</a>");
    //    builder.append("<input name=\"go_page\" value=\"Go\" type=\"submit\"></span>");

    builder.append("<script type=\"text/javascript\">");
    builder.append("function go_page() {");
    builder.append(" window.location='");
    builder.append(hrd.getUriFolder()).append("/").append(list.getAction()).append("/' + ");
    builder.append("document.forms[\"page_form\"].page.value");
    builder.append(" + '/");
    builder.append(list.getUrl()).append("';");
    builder.append("}</script>");

    if(page < list.getTotalPage()) {
      builder.append("&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;");

      builder.append("<a class=\"other_page\" href=\"");
      builder.append(hrd.getUriFolder()).append("/").append(list.getAction()).append("/");
      builder.append(String.valueOf(page+1)).append("/");
      builder.append(list.getUrl()).append("\">Kế tiếp</a>");
    }

    builder.append("&nbsp;&nbsp;&nbsp;</div>");
    builder.append("</form> ");
    return builder.toString();

  }
}

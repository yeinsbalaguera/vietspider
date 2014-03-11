/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.MergeEntryDomains;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.server.handler.cms.DataNotFound;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class DomainHandler extends MetaListHandler {
  
  public DomainHandler() {
    super("site");
    name = "DOMAIN";
  }

  public DomainHandler(String type) {
    super(type); 
    name = "DOMAIN"; 
  }

  public void handle(final HttpRequest request, final HttpResponse response,
                     final HttpContext context) throws HttpException, IOException {
    
    if(disableBot != null) {
      String userAgent = getUserAgent(request);
      if(userAgent.toLowerCase().indexOf(disableBot) > -1) return;
    }
    
//    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    
    
    String path = request.getRequestLine().getUri();
    if(path.trim().length() < 2) {
      try {
        List<String> dates = DatabaseService.getLoader().loadDateFromDomain();
        if(dates.size() < 1)  throw new DataNotFound();
        handle(request, response, context, "1", dates.get(0).replace('/', '.'));
      } catch (Exception e) {
        throw new HttpException(e.getMessage());
      }
      return;
    }

    super.handle(request, response, context);
  }

  public String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    String page = "1";
    
    if(params.length > 0) page = params[0];
    
    MetaList metas = new MetaList(name);
    
    try {
      metas.setCurrentPage(Integer.parseInt(page));
    }catch (NumberFormatException e) {
      metas.setCurrentPage(1);
    }

//    Domain domain = null;
    
    int filter  = -1;
    int length  = params.length;
    if(length > 0 && params[length-1].startsWith("filter:")) length--;  
    
    String date = null;
    String category = null;
    String source = null;
    
    if(params.length > 1 && isDateFormat(params[1])) {
      date = params[1].replace('.', '/'); 
      if(length > 3){
        category = params[2];
        source  = params[3];
        filter = getType(metas, params, 4);
      } else if(length > 2){
        category = params[2];
        filter = getType(metas, params, 3);
      } else {
        filter = getType(metas, params, 2);
      }
    } else {
      if(length > 2) {
        category = params[1];
        source  = params[2];
        filter = getType(metas, params, 4);
      } else if(params.length > 1){
        category = params[1];
        filter = getType(metas, params, 3);
      } else {
        filter = getType(metas, params, 2);
      }
    }
    
    StringBuilder title = new StringBuilder();
    if(date != null) title.append(params[1]);
    
    if(category != null) {
      if(title.length() > 0) title.append('/');
      title.append(category);
    }
    
    if(source != null) {
      if(title.length() > 0) title.append('/');
      title.append(source);
    }
    
//    System.out.println(date + " : "+ category + " : "+ source);
    
    //working with entry
    EntryReader entryReader = new EntryReader();
    IEntryDomain entryDomain = null;
    if(date != null) {
      entryDomain = new SimpleEntryDomain(date, category, source);
    } else {
      entryDomain = MergeEntryDomains.getInstance().getIterator(category, source);
    }
//    System.out.println("thay co "+title);
//    System.out.println(" hihi "+ entryDomain.getFile());
    int time = 0;
    while(time < 1000000) {
      entryReader.read(entryDomain, metas, filter);
//      System.out.println(metas.getCurrentPage() + " : "+ metas.getData().size() + " : "+ time);
      if(hasData(metas)) break;
      metas.setCurrentPage(metas.getCurrentPage()+1);
      if(metas.getCurrentPage() > metas.getTotalPage()) break;
      time++;
    }
    
   
    
    /*//working with database
    if(metas.getData().size() < 1 && filter < 0) {
      int total  = 0;
      if(length > 3){
        total = menuInfo.getCategoryInfo(params[2]).getSource(params[3]).getData();
      } else if(length > 2){
        if(title.length() < 1) title.append(params[1]).append('/').append(params[2]);
        domain = new Domain();
        domain.setDate(date);
        domain.setCategory(params[2]);
        total = menuInfo.getCategoryInfo(params[2]).getTotalData();
      } else {
        if(length > 1) {
          if(title.length() < 1) title.append(params[1]); 
        } else {
          if(title.length() < 1) title.append(date.replace('/', '.'));
        }
        domain = new Domain();
        domain.setDate(date);
        total = menuInfo.getTotalData();
      }

      int totalPage = total / metas.getPageSize() ;
      if (total % metas.getPageSize() > 0) totalPage++ ;
      metas.setTotalPage(totalPage);

      if(metas.getCurrentPage()  < 1 
          || metas.getCurrentPage() > totalPage) metas.setCurrentPage(1);

      DatabaseService.getLoader().loadMetaFromDomain(domain, metas);
    }*/
    
    metas.setTitle(title.toString());
    metas.setUrl(title.toString());

    return write(request, response, context, metas, params);
  }

  private int getType(MetaList list, String [] params, int index) {
    if(params.length < (index+1)) return -1;
    params[index] = params[index].trim();
    if(!params[index].startsWith("filter:")) return  -1;
    String value = params[index].substring("filter:".length());
    try {
      int idxValue = Integer.parseInt(value.trim());
      list.setExtension(params[index]);
      return idxValue;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }
  
  private boolean hasData(MetaList metas) {
    List<Article> articles = metas.getData();
    if(articles == null || articles.size() < 1) return false;
    for(Article article : articles) {
      if(article != null) return true;
    }
    return false;
  }
  
  static boolean isDateFormat(String value) {
    if(value.length() != 10) return false;
    for(int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if(Character.isDigit(c) || c == '.') continue;
      return false;
    }
    return true;
  }

}

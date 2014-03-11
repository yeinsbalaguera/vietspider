/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.GZipIO;
import org.vietspider.content.db.article.AlertQueue;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.serialize.Object2XML;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 2, 2009  
 */
public class DataContentHandler extends CommonHandler {

  @SuppressWarnings("unused")
  public void execute(final HttpRequest request, final HttpResponse response,
      final HttpContext context, OutputStream output) throws Exception  {
    Header header = request.getFirstHeader("action");
    String action = header.getValue();

    byte [] bytes = getRequestData(request);

    String value = new String(bytes, Application.CHARSET).trim();

    if("load.new.list.metas".equals(action)) {
      String alert = SystemProperties.getInstance().getValue("alert.new.articles");
      if("false".equals(alert) 
          || (Application.LICENSE == Install.SEARCH_SYSTEM && (
              alert == null || alert.trim().isEmpty())))  {
        ArticleCollection collection = new ArticleCollection();
        String text = Object2XML.getInstance().toXMLDocument(collection).getTextValue();
        output.write(text.getBytes(Application.CHARSET));
        return ;
      }
      
     /* MetaList metas = new MetaList("vietspider");
      metas.setPageSize(10);
      metas.setCurrentPage(1);
      
      Calendar calendar = Calendar.getInstance();
      String date = CalendarUtils.getDateFormat().format(calendar.getTime());

      //working with entry
      EntryReader entryReader = new EntryReader();
      IEntryDomain entryDomain = null;
      entryDomain = new SimpleEntryDomain(date, null, null);
      entryReader.read(entryDomain, metas, -1);*/
      
      ArticleCollection collection = new ArticleCollection();
      AlertQueue.createInstance().get(collection);
      
//      List<Article> articles = metas.getData();
//      collection.get().addAll(articles);
      
      String text = Object2XML.getInstance().toXMLDocument(collection).getTextValue();
      output.write(text.getBytes(Application.CHARSET));
      
      return;
    }

    if("load.article".equals(action)) {
      Article article = null;
      try {
        article = DatabaseService.getLoader().loadArticle(value);
        StringBuilder builder = new StringBuilder(article.getMeta().getTitle());
        builder.append('\n').append(article.getMeta().getDesc());
        output.write(builder.toString().getBytes(Application.CHARSET));  
      } catch (SQLException e) {
        output.write(("error\n" + e.toString()).getBytes());
      } catch (Exception e) {
        output.write(("error\n" + e.toString()).getBytes());
      }
      return;
    }

    if("load.article.by.url".equals(action)) {
//      if(!DataGetter.class.isInstance(DatabaseService.getLoader())) {
      if(DatabaseService.isMode(DatabaseService.RDBMS)) {
        output.write(new byte[0]);
        return;
      }

      TextSpliter spliter = new TextSpliter();
      String [] elements = spliter.toArray(value, '\n');
      if(elements.length != 2) {
        output.write(new byte[0]);
        return;
      }

      String articleId = DatabaseService.getLoader().loadIdByURL(elements[1]);
      if(articleId == null) {
        output.write(new byte[0]);
        return;
      }

      Article article = null;
      try {
        article = DatabaseService.getLoader().loadArticle(articleId);
        if(article == null)  {
          output.write(new byte[0]);
          return;
        }

        if(!article.getDomain().getGroup().equals(elements[0])) {
          output.write(new byte[0]);
          return;
        }

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOutputStream);
        out.writeObject(article);
        out.flush();
        out.close();
        
        bytes = byteOutputStream.toByteArray();
        bytes = new GZipIO().zip(bytes);

        output.write(bytes);
      } catch (SQLException e) {
        output.write(new byte[0]);
      } catch (Exception e) {
        output.write(new byte[0]);
      }
      return;
    }
  }

}

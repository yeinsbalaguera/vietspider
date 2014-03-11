/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.webui.search.seo.LastAccessData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 10, 2009  
 */
public class RemoteDataHandler extends CommonHandler {

  @SuppressWarnings("all")
  public void execute(final HttpRequest request, final HttpResponse response,
      final HttpContext context, OutputStream output) throws Exception  {
    Header header = request.getFirstHeader("action");
    String action = header.getValue();
    
//    System.out.println(" ================  >"+ action);
//    if(!checkAdminRole(request)) return;

    byte [] bytes = getRequestData(request);

    /*if("add.content.index".equals(action)) {
      ArrayList<ContentIndex> contentIndexs = readAsObject(bytes);
      StringBuilder successCodes = new StringBuilder();
      for(int i = 0; i < contentIndexs.size(); i++) {
        ContentIndexers.getInstance().index(contentIndexs.get(i).clone());
        if(successCodes.length() > 0) successCodes.append("\n");
        successCodes.append(contentIndexs.get(i).getId());
      }
      output.write(successCodes.toString().getBytes());
      return;
    }*/

    if("add.article".equals(action)) {
      header = request.getFirstHeader("local-address");
      if(header != null && header.getValue() != null) {
        SystemProperties.getInstance().putValue("remote.crawler.ip", header.getValue(), true);
      }

      header = request.getFirstHeader("username");
      if(header != null && header.getValue() != null) {
        SystemProperties.getInstance().putValue("remote.crawler.username", header.getValue(), true);
      }

      StringBuilder successCodes = new StringBuilder();
      try {
//        ArticleCollection dataCollection = readAsObject(bytes);
        ArticleCollection dataCollection = 
          XML2Object.getInstance().toObject(ArticleCollection.class, bytes);
        List<Article> articles = dataCollection.get();
        for(int i = 0; i < articles.size(); i++) {
          Article article = articles.get(i);
//                    System.out.println(" =============== > "+ article.getId());
          switch (article.getSaveType()) {
          case Article.SAVE_NEW:
            try {
              DatabaseService.getSaver().saveAs(article);
              if(successCodes.length() > 0) successCodes.append("\n");
              successCodes.append(article.getId());
              Long numberId = Long.parseLong(article.getId());
              LastAccessData.getInstance().addArticle(numberId);
            } catch (Exception e) {
              LogService.getInstance().setThrowable(e);
            }
            break;
          case Article.SAVE_RELATION:
            try {
              DatabaseService.getSaver().save(article.getRelations());
              if(successCodes.length() > 0) successCodes.append("\n");
              successCodes.append(article.getId());
            } catch (Exception e) {
              LogService.getInstance().setThrowable(e);
            }
            break;
          case Article.SAVE_CONTENT:
            try {
              DatabaseService.getSaver().set(article.getContent());
              if(successCodes.length() > 0) successCodes.append("\n");
              successCodes.append(article.getId());
            } catch (Exception e) {
              LogService.getInstance().setThrowable(e);
            }
            break;
          default:
            break;
          }
          //          System.out.println(" ====== > "+ articles.get(i).getId()+ " : "+articles.get(i).getContent().getContent().length());

        }
        LogService.getInstance().setMessage("DATABASE", null, 
            "Crawler Remote Database: Received " + articles.size() + " articles from remote VietSpider!");
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      output.write(successCodes.toString().getBytes());
      return;
    }

    /*if("add.tp.data".equals(action)) {
      ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
      ObjectInputStream objectInputStream = null;
      StringBuilder successCodes = new StringBuilder();
      try {
        objectInputStream = new ObjectInputStream(byteInputStream);
        ArrayList<TpWorkingData> tps = (ArrayList<TpWorkingData>)objectInputStream.readObject();
//        TopicTrackingServices topicTrackingServices = TopicTrackingServices.getInstance();
//        for(int i = 0; i < tps.size(); i++) {
//          if(topicTrackingServices != null) topicTrackingServices.save(tps.get(i));
//          if(successCodes.length() > 0) successCodes.append("\n");
//          successCodes.append(tps.get(i).getTpDocument().getId());
//        }
        LogService.getInstance().setMessage("DATABASE", null, 
            "Crawler Remote Database: Received " + tps.size() + " topic documents from remote VietSpider!");
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        try {
          if(byteInputStream != null) byteInputStream.close();
        } catch (Exception e) {
        }
        try {
          if(objectInputStream != null)  objectInputStream.close();
        } catch (Exception e) {
        }
      } 
      output.write(successCodes.toString().getBytes());
      return;
    }*/

    if("load.article".equals(action)) {
      short mode = Article.NORMAL;
      try {
        Header mheader = request.getFirstHeader("mode");
        if(mheader  != null && mheader.getValue() != null) {
          mode = Short.parseShort(mheader.getValue());
        }
      } catch (Exception e) {

      }
//      Article article = CachedService.getInstance().loadArticle(mode, new String(bytes));
      Article article = DatabaseService.getLoader().loadArticle(new String(bytes), mode);
      if(article != null) writeAsXML(output, article);
//      writeObject(output, article);
      return;
    }
    
    if("load.article.as.raw.text".equals(action)) {
      String text = DatabaseService.getLoader().loadRawText(new String(bytes));
      output.write(text.getBytes(Application.CHARSET));
      return;
    }

    if("load.articles".equals(action)) {
      short mode = Article.NORMAL;
      boolean writeAsXML = false;
      try {
        Header mheader = request.getFirstHeader("mode");
        if(mheader  != null 
            && mheader.getValue() != null) {
          mode = Short.parseShort(mheader.getValue());
        }
      } catch (Exception e) {
      }
      try {
        Header xmlHeader = request.getFirstHeader("write.as.xml");
        if(xmlHeader  != null 
            && xmlHeader.getValue() != null) {
         writeAsXML = true;
        }
      } catch (Exception e) {
      }
      String [] elements = new String(bytes).split("\n");
      ArticleCollection collection  = new ArticleCollection();
      for(String ele : elements) {
//        System.out.println(" chay thu cai ni "+ ele);
        try {
          Article article = DatabaseService.getLoader().loadArticle(ele, mode);
//          System.out.println(" ====  >  "+ article);
          if(article != null) collection.get().add(article);
            //collection.get().add(CachedService.getInstance().loadArticle(mode, ele));
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
      }
      
      if(writeAsXML) {
        writeAsXML(output, collection);
        return;
      }

//      long start = System.currentTimeMillis();
      writeAsXML(output, collection);
//      writeObject(output, collection);
      //      write(output, collection);
//      long end = System.currentTimeMillis();
//      System.out.println(" write di "+ (end - start));
      return;
    }
    
    if("load.articles.as.raw.text".equals(action)) {
      String [] elements = new String(bytes).split("\n");
      StringBuilder builder = new StringBuilder();
      for(String ele : elements) {
        if(builder.length() > 0) {
          builder.append(Article.RAW_TEXT_ARTICLE_SEPARATOR);
        }
        try {
          builder.append(DatabaseService.getLoader().loadRawText(ele));
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
      }
      output.write(builder.toString().getBytes(Application.CHARSET));
      return;
    }

    if("load.relations".equals(action)) {
      String metaId = new String(bytes);
      List<Relation> list = DatabaseService.getLoader().loadRelation(metaId);
      if(list != null) {
        Relations relations = new Relations(metaId);
        relations.setRelations(list);
        writeAsXML(output, relations);
      }
//      Relations relations = CachedService.getInstance().loadRelations(metaId);
//      writeObject(output, relations);
      return;
    }

    if("load.meta".equals(action)) {
      String metaId = new String(bytes);
      Meta meta = DatabaseService.getLoader().loadMeta(metaId);
      if(meta != null) writeAsXML(output, meta); 
        
//      writeAsXML(output, CachedService.getInstance().loadMeta(metaId));
//      writeObject(output, CachedService.getInstance().loadMeta(metaId));
      return;
    }
    
    if("load.meta.as.raw.text".equals(action)) {
      String metaId = new String(bytes);
      String text = DatabaseService.getLoader().loadMetaAsRawText(metaId);
      output.write(text.getBytes(Application.CHARSET));
        
//      writeAsXML(output, CachedService.getInstance().loadMeta(metaId));
//      writeObject(output, CachedService.getInstance().loadMeta(metaId));
      return;
    }

    if("load.content".equals(action)) {
//      writeObject(output, CachedService.getInstance().loadContent(new String(bytes)));
      Content content = DatabaseService.getLoader().loadContent(new String(bytes));
      if(content != null) writeAsXML(output, content);
      return;
    }

    if("load.image".equals(action)) {
      writeAsXML(output, DatabaseService.getLoader().loadImage(new String(bytes)));
      return;
    }

    if("load.domain".equals(action)) {
      Domain domain = DatabaseService.getLoader().loadDomainById(new String(bytes));
      if(domain != null) writeAsXML(output, domain);
      return;
    }


    if("test.connection".equals(action)) {
      output.write("hi".getBytes());
      return;
    }

    if("load.metas".equals(action)) {
      String page = new String(bytes);

      SimpleDateFormat dateFormat =  CalendarUtils.getDateFormat();
      String date = dateFormat.format(Calendar.getInstance().getTime());
      MetaList metas = new MetaList("Searcher");      
      try {
        metas.setCurrentPage(Integer.parseInt(page));
      }catch (NumberFormatException e) {
        metas.setCurrentPage(1);
      }

      EntryReader entryReader = new EntryReader();
      IEntryDomain entryDomain = new SimpleEntryDomain(date, null, null);
      entryReader.read(entryDomain, metas, -1, Article.META);
      //      System.out.println(" thay co "+metas.getData().size());
      writeAsXML(output, metas);
    }      
    
    if("load.metas.as.raw.text".equals(action)) {
      String page = new String(bytes);

      SimpleDateFormat dateFormat =  CalendarUtils.getDateFormat();
     
      MetaList metas = new MetaList("Browse");      
      try {
        metas.setCurrentPage(Integer.parseInt(page));
      }catch (NumberFormatException e) {
        metas.setCurrentPage(1);
      }
      
      try {
        header = request.getFirstHeader("page.size");
        if(header != null && header.getValue() != null) {
          metas.setPageSize(Integer.parseInt(header.getValue()));
        }
      } catch (Exception e) {
      }

      EntryReader entryReader = new EntryReader();
      
      int counter = 0;
      Calendar calendar = Calendar.getInstance();
      StringBuilder builder = new StringBuilder();
      builder.append(100).append('\n');
      
      int time = 0;
      while(counter < metas.getPageSize() && time < 2) {
        String date = dateFormat.format(calendar.getTime());
        IEntryDomain entryDomain = new SimpleEntryDomain(date, null, null);
        String [] metaIds = entryReader.readMetaIds(entryDomain, metas, -1);
        
        for(String metaId : metaIds) {
          //        System.out.println(metaId +  " : " + DatabaseService.getLoader().loadMetaAsRawText(metaId));
          if(builder.length() > 0) {
            builder.append(Article.RAW_TEXT_ARTICLE_SEPARATOR);
          }
          try {
            builder.append(DatabaseService.getLoader().loadMetaAsRawText(metaId));
            counter++;
          } catch (Exception e) {
            LogService.getInstance().setMessage(e, e.toString());
          }
        }
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        time++;
      }
//      System.out.println(" hehehe co cai nay "+ builder);
      output.write(builder.toString().getBytes(Application.CHARSET));
      return;
    }

  }
  
  /*private void write(OutputStream output, Object value, boolean zip) throws Exception  {
    if(value == null) {
      output.write("".getBytes());
      return;
    }
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteOutputStream);
    out.writeObject(value);
    out.flush();
    out.close();    
    byte [] bytes = byteOutputStream.toByteArray();
    if(zip) bytes = new GZipIO().zip(bytes);
    output.write(bytes);
  }*/

  private void writeAsXML(OutputStream output, Object value) throws Exception  {
    if(value == null) {
      output.write("".getBytes());
      return;
    }
    String xml = Object2XML.getInstance().toXMLDocument(value).getTextValue();
//    System.out.println(xml);
    output.write(xml.getBytes(Application.CHARSET));
  }
 

}

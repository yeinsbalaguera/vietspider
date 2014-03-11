/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.content.ArticleDatabase;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.content.BackupDatabase;
import org.vietspider.db.content.CommonDatabase;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.nlp.NlpHandler;
import org.vietspider.serialize.XML2Object;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 11, 2011  
 */
public class AutoPriceIndexing extends Thread {

  private boolean execute = true;
  private List<String> names = new ArrayList<String>();
  private boolean test = false;
  
  private HTMLParser2 parser2 = new HTMLParser2();
  
  private int min = 180; //

  public AutoPriceIndexing() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Auto price indexing close";}

      public void execute() {
        execute = false;
        saveNames();
      }
    });

    this.start();
  }

  public void run() {
    loadNames();
    while(execute) {
      Calendar calendar = Calendar.getInstance();
      int hour = calendar.get(Calendar.HOUR_OF_DAY);
      
//      System.out.println(hour);

      if(hour < 1 || hour > 6) {
        try {
          Thread.sleep(10*60*1000l);
        } catch (Exception e) {
        }
        continue;
      }

      try {
        index();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        Thread.sleep(test ? 6*1000l : 30*1000l);
      } catch (Exception e) {
      }
    }
  }

  private void index() {
    File folder = UtilFile.getFolder("content/bak/database/");
    File [] files = folder.listFiles();
    if(files == null || files.length < 1) return;

    for(int i = 0; i < files.length; i++) {
//      if(files[i].getName().equals(current)) {
//        //          System.out.println(" hihi current "+ current);
//        continue;
//      }
      
      try {
        Calendar cal = Calendar.getInstance();
        cal.setTime(CalendarUtils.getFolderFormat().parse(files[i].getName()));
        
        
        Calendar calendar = Calendar.getInstance();
        
        if(calendar.getTimeInMillis() - cal.getTimeInMillis() > min*24*60*60*1000l) {
//          System.out.println(" ====  ignore >"+ files[i].getName());
          continue;
        }
        
        if(calendar.getTimeInMillis() - cal.getTimeInMillis() < 24*60*60*1000l) {
          LogService.getInstance().setMessage(null, "Price Index: Ignore current date "+ files[i].getName());
//          System.out.println(" ====  ignore 2 >"+ files[i].getName());
          continue;
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
        continue;
      }
      
      if(containsName(files[i].getName())) continue;
      
      LogService.getInstance().setMessage(null, "Price Index: Start price index "+ files[i].getName());
      try {
        index(files[i]);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      names.add(files[i].getName());
      LogService.getInstance().setMessage(null, "Price Index: Finish price index "+ files[i].getName());

      saveNames();

      return;
    }
    //    } catch (Throwable e) {
    //      LogService.getInstance().setThrowable(e);
    //    }
  }

  private void index(File folder) throws Throwable {
    ArticleDatabases databases = BackupDatabase.getInstance().getDatabase();
    if(databases == null) return;
    ArticleDatabase database = (ArticleDatabase)databases.getDatabase(null, folder, true);
    //    System.out.println(folder.getAbsolutePath() + " : " + database);
    if(database == null) return;
    //    IArticleIterator iterator = database.getIterator();

    int counter = 0;

    CommonDatabase nlpDb = database.createNlpDb();
    Iterator<Long> iterator2 = nlpDb.getMap().keySet().iterator();
    while(iterator2.hasNext()) {
      long id = iterator2.next();
      String xml = new String(nlpDb.load(id), Application.CHARSET);
      //      System.out.println(id + " : "+ xml);
      NLPRecord nlp = null;
      try {
        nlp = XML2Object.getInstance().toObject(NLPRecord.class, xml);
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      if(nlp == null) continue;
//      System.err.println(" ===  > "+ id +  " : "+ nlp);
      List<String> cities = nlp.getData(NLPData.CITY);
      if(cities == null || cities.size() != 1) {
        nlp = createNlp(database, id);
      }
      if(nlp == null) continue;
      
      PriceIndexDatabases.getInstance().save(nlp);

      counter++;
      if(counter%500 != 0) continue;
//      if(counter%5000 != 0) continue;
      LogService.getInstance().setMessage(null, "Price indexs " + counter + " articles.");

      try {
        Thread.sleep(test? 3*1000l : 30*1000l);
      } catch (Exception e) {
      }
    }

    LogService.getInstance().setMessage(null, "Price index " + counter + " articles.");
    //    databases.close(database);
  }
  
  private NLPRecord createNlp(ArticleDatabase database, long id) {
    try {
      Article article = database.loadArticle(String.valueOf(id));
      HTMLNode root = parser2.createDocument(article.getContent().getContent()).getRoot();
      return NlpHandler.process(article.getMeta(), root);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }

  private void saveNames() {
    File file = UtilFile.getFile("content/solr2/price_index/", "indexed.txt");
    file.delete();
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < names.size(); i++) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(names.get(i));
    }

    try {
      RWData.getInstance().save(file, builder.toString().getBytes());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private void loadNames() {
    File file = UtilFile.getFile("content/solr2/price_index/", "indexed.txt");
    try {
      String text = new String(RWData.getInstance().load(file));
      TextSpliter spliter = new TextSpliter();
      List<String> elements = spliter.toList(text, '\n');
      for(String element : elements) {
        if(element.length() > 5) names.add(element);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private boolean containsName(String name) {
    for(int i = 0; i < names.size(); i++) {
      if(name.equals(names.get(i))) return true;
    }
    return false;
  }

}

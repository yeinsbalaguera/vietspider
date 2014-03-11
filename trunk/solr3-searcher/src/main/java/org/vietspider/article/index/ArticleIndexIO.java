/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleIndex;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.TextRenderer2;
import org.vietspider.locale.vn.ArticleWordSplitter;
import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.locale.vn.Word;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
class ArticleIndexIO  {
  
  private CoreContainer coreContainer;
  private EmbeddedSolrServer server;

  private ArticleIndexWriter writer;
  private ArticleIndexReader reader;

  private HTMLParser2 parser;
  private ArticleWordSplitter spliter;
  //  private SolrIndexStorage storage;
  private Detector detector = null;

  public ArticleIndexIO(ArticleIndexStorage storage) throws Exception  {
    File confgFile  = UtilFile.getFolder("system/solr2");  
    System.setProperty("solr.solr.home", confgFile.getAbsolutePath());

    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    coreContainer = initializer.initialize();
    server = new EmbeddedSolrServer(coreContainer, "artlindex");

    parser = new HTMLParser2();
    
    this.writer = new ArticleIndexWriter(server);
    this.reader = new ArticleIndexReader(server, storage, writer);
    
//    SolrConfig solrConfig = coreContainer.getCore("artlindex").getSolrConfig();
//    solrConfig.fieldValueCacheConfig.setClazz(FastLRUCache.class);
//    solrConfig.filterCacheConfig.setClazz(FastLRUCache.class);
    spliter = new ArticleWordSplitter();
    
    try {
      detector = DetectorFactory.getInstance().create();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public ArticleIndex createSolrIndex(Article article) throws Exception {
    ArticleIndex bean = new ArticleIndex(article.getId());

    Meta meta = article.getMeta();
    Content content = article.getContent();

    if(meta == null) return null;

    bean.setTitle(meta.getTitle());
    
    StringBuilder builder = new StringBuilder();
    builder.append(meta.getDesc());
    HTMLDocument doc = parser.createDocument(content.getContent());
    TextRenderer2 textRenderer = new TextRenderer2(doc.getRoot(), TextRenderer2.HANDLER);
    builder.append('\n').append('\n').append(textRenderer.getTextValue());
    String text = builder.toString();
    bean.setText(text);
    
   
    String lang = null;
    try {
      lang = detector.detect(text);
    } catch (Exception e) {
//      System.out.println(" chay thu cai ni "+ text);
      LogService.getInstance().setMessage(e, e.toString());
    }
    
    if("vi".equals(lang)) {
      bean.setTitleNoMark(VietnameseConverter.toTextNotMarked(meta.getTitle()));
      bean.setTextNoMark(VietnameseConverter.toTextNotMarked(text));
      
      List<Word> words = spliter.split(meta.getTitle());
      for(int i = 0; i < words.size(); i++) {
        if(words.get(i).getNoun() < 1 ) continue;
        bean.getTags().add(words.get(i).getValue());
      }
      
      words = spliter.split(text);
      for(int i = 0; i < words.size(); i++) {
        if(words.get(i).getNoun() < 1 ) continue;
        bean.getTags().add(words.get(i).getValue());
      }
    }

    String time  = meta.getTime();
    SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
    bean.setTime(dateFormat.parse(time).getTime());

    time  = meta.getSourceTime();
    if(time != null && time.length() > 0) {
      bean.setSourceTime(dateFormat.parse(time).getTime());
    }

    bean.setUrl(meta.getSource());

//    bean.getTags().add(article.getDomain().getCategory());
    bean.setSource(article.getDomain().getName());
    bean.setCategory(article.getDomain().getCategory());

    return bean;
  }

  public ArticleIndexWriter getWriter() { return writer; }

  public ArticleIndexReader getReader() { return reader; }

  void close() {
    writer.commit(false, false);
    try {
      server.commit(true, false);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    coreContainer.shutdown();
  }

}


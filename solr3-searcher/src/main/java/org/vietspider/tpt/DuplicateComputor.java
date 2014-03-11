/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.vietspider.bean.Article;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.io.LogService;
import org.vietspider.db.IDVerifiedStorages;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.tpt.nlp.NlpTptModel;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2011  
 */
class DuplicateComputor {

  //  private SolrIndexingReader reader;
  //  private TempIndexs indexes;

  private TptService storage;

  public DuplicateComputor(TptService storage) {
    this.storage = storage;
  }

  public String search(Article article) {
    if(article == null) return null;
    
    NLPRecord record = article.getNlpRecord();
    if(record == null) return null;
    
    article.getMeta().removeProperty("owner");
    
    NlpTptModel tpt = /*NlpTptService.getInstance().*/createModel(article);
    if(tpt == null || tpt.isEmpty()) return null;

    if((record.hasData(NLPData.MOBILE)
        || record.hasData(NLPData.TELEPHONE)) 
        && record.hasData(NLPData.PRICE)
        && record.hasData(NLPData.AREA)
        && record.hasData(NLPData.ADDRESS)) {
      
      List<String> phones = record.getData(NLPData.MOBILE);
      if(phones.size() < 1) {
        phones = record.getData(NLPData.TELEPHONE);
      }
      
      if(phones.size() > 10) {
        article.setStatus(Article.DELETE);
        IDVerifiedStorages.save("invalid", article);
        return null;
      }
      
      List<String> metaIds = new ArrayList<String>();
      storage.solr.writer.tempIndexs.searchByPhone(metaIds, phones);
      search(metaIds, "field:phone:", phones);
      
      if(metaIds.size() < 1) {
//        System.out.println("step 0.1 " + article.getId() + " : " + metaIds.isEmpty());
        if(computeOwner(tpt)) {
          article.getMeta().putProperty("owner", "true");
        }
//        NlpTptService.getInstance().save(tpt);
        return null;
      }
      
      for(String metaId : metaIds) {
        if(metaId.equals(tpt.getId())) continue;
        NlpTptModel tpt2 = null;
        try {
          Article article2 = DatabaseService.getLoader().loadArticle(metaId);
          if(article2 != null) tpt2 = createModel(article2);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          continue;
        }
        //NlpTptService.getInstance().getStorage().getById(metaId);
        if(tpt2 == null || !equals1(tpt, tpt2)) continue;
        IDVerifiedStorages.save("duplicate", metaId + "/" + tpt.getId());
        return metaId;
      }
      
//      NlpTptService.getInstance().save(tpt);
      return null;
    }
    
    if(!record.hasData(NLPData.PRICE)
        && !record.hasData(NLPData.AREA)) return null;
    
    List<String> phones = record.getData(NLPData.MOBILE);
    if(phones.size() < 1) {
      phones = record.getData(NLPData.TELEPHONE);
    }
    
    if(phones.size() > 10) {
      article.setStatus(Article.DELETE);
      IDVerifiedStorages.save("invalid", article);
      return null;
    } 
    
    List<String> metaIds = new ArrayList<String>();
    if(phones.size() > 1) {
      storage.solr.writer.tempIndexs.searchByPhone(metaIds, phones);
      search(metaIds, "field:phone:", phones);  
    } else {
      List<String> emails = record.getData(NLPData.EMAIL);
      if(emails.size() > 0) {
        storage.solr.writer.tempIndexs.searchByEmail(metaIds, emails);
        search(metaIds, "field:email:", emails);
      }
    }
    
    if(metaIds.size() < 1) {
      if(computeOwner(tpt)) {
//        System.out.println("step 0.2 " + article.getId() + " : " + metaIds.isEmpty());
        article.getMeta().putProperty("owner", "true");
      }
//      NlpTptService.getInstance().save(tpt);
      return null;
    }
    
    String title = cutTitle(tpt.getTitle());
    
    for(String metaId : metaIds) {
      if(metaId.equals(tpt.getId())) continue;
      
      NlpTptModel tpt2 = null;
      try {
        Article article2 = DatabaseService.getLoader().loadArticle(metaId);
        if(article2 == null || article2.getMeta() == null) continue;
        tpt2 = createModel(article2);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        continue;
      }
        //NlpTptService.getInstance().getStorage().getById(metaId);
      if(tpt2 == null) continue;
      
      String title2 = cutTitle(tpt2.getTitle());
      if(!title.equals(title2)) continue;
      
      if(!compareAddress(tpt.getAddress(), tpt2.getAddress())) continue;
      
      if(tpt.getArea() != null && tpt.getArea().equals(tpt2.getArea())) {
        IDVerifiedStorages.save("duplicate", metaId + "/" + tpt.getId());
        return metaId;
      }
      
      if(tpt.getPrice() != null && tpt.getPrice().equals(tpt2.getPrice())) {
        IDVerifiedStorages.save("duplicate", metaId + "/" + tpt.getId());
        return metaId;
      }
    }
    
//    NlpTptService.getInstance().save(tpt);
    return null;
  }
  
  private String cutTitle(String title) {
    title = title.trim();
    if(title.charAt(title.length() - 1) != ')') return title;
    int index = title.length() - 2;
    while(index > -1) {
      char c = title.charAt(index);
      if(c == '(') break;
      index--;  
    }
    if(index < 1) return title;
    return title.substring(0, index);
  }
  
  private boolean compareAddress(String address1, String address2) {
    if(address1 == null) {
      if(address2 == null) return true;
      return false;
    }
    return address1.equals(address2);
  }
  
  private void search(Collection<String> ids, String field, List<String> list) {
    if(list == null) return;
    StringBuilder builder = new StringBuilder();
    for(String ele : list) {
      if(builder.length() > 0) builder.append(" ");
      builder.append(ele);
    }
    String pattern = builder.toString().trim();
    search(ids, field, pattern);
  }

  private void search(Collection<String> ids, String field, String pattern) {
    if(pattern.length() < 1) return;
    SolrQuery query = new SolrQuery();
    query = query.setQuery(field + pattern);
    query = query.setFields("id");
    query = query.setFacetMinCount(1);
    query = query.setHighlight(false);
    query = query.setQueryType("nlp_handler");
    query.setRows(500);

    query = query.setFacetLimit(2).setStart(0);
    QueryResponse response = storage.solr.reader.search(query);
    if(response == null) return;
    SolrDocumentList results = response.getResults();

    Iterator<SolrDocument> iter = results.iterator();
    while (iter.hasNext()) {
      SolrDocument resultDoc = iter.next();
      String metaId = (String)resultDoc.getFieldValue("id");
      if(metaId == null || ids.contains(metaId)) continue;
      ids.add(metaId);
    }
  }
  
  private boolean equals1(NlpTptModel model1, NlpTptModel model2) {
    
    if(!model1.getArea().equals(model2.getArea())) return false;

    if(!model1.getPrice().equals(model2.getPrice())) return false;

    if(!model1.getAction_object().equals(model2.getAction_object())) return false;

    if(!model1.getAddress().equals(model2.getAddress())) return false;
      
    return true;
  }
  
  private boolean computeOwner(NlpTptModel model) {
    if(model.getMobile() == null 
        && model.getTelephone() == null) return false;
    
    if(model.getMobile() != null 
        && model.getMobile().trim().isEmpty()
        && model.getTelephone() != null
        && model.getTelephone().trim().isEmpty()) return false;
    
//    if(model.getMobile() != null) {
//      List<String> metaIds = 
//        NlpTptService.getInstance().searchByMobile(model.getId(), model.getMobile());
////      System.out.println("step 1 " + model.getId() + " : " + metaIds.isEmpty());
//      if(metaIds.size() > 0) return false;
//    }
//    
//    if(model.getTelephone() != null) {
//      List<String> metaIds = 
//        NlpTptService.getInstance().searchByTelephone(model.getId(), model.getTelephone());
////      System.out.println("step 2 " + model.getId() + " : " + metaIds.isEmpty());
//      if(metaIds.size() > 0) return false;
//    }
    
    return true;
  }
  
  public NlpTptModel createModel(Article article) {
    if(article == null
        || article.getContent() == null) return null;
    NlpTptModel model = new NlpTptModel(article.getId());
    model.setTitle(article.getMeta().getTitle());

    NLPRecord record = article.getNlpRecord();
    model.setTelephone(getData(record, NLPData.TELEPHONE));
    model.setMobile(getData(record, NLPData.MOBILE));
    model.setEmail(getData(record, NLPData.EMAIL));
    model.setAddress(getData(record, NLPData.ADDRESS));
    model.setAction_object(getData(record, NLPData.ACTION_OBJECT));
    model.setPrice(getData2(record, NLPData.PRICE));
    model.setArea(getData2(record, NLPData.AREA));

    model.setDate(article.getContent().getDate());

    return model;
  }
  
  private String getData(NLPRecord record, short type) {
    if(record == null) return null;
    List<String> list = record.getData(type);
    StringBuilder builder = new StringBuilder();
    for(String ele : list) {
      if(builder.length() > 0) builder.append(';');
      builder.append(ele);
    }
    //    System.out.println(" type "+ type + " : "+ builder);
    if(builder.length() > 0) return builder.toString();
    return null;
  }
  
  private String getData2(NLPRecord record, short type) {
    if(record == null) return null;
    List<String> list = record.getData(type);
    StringBuilder builder = new StringBuilder();
    for(String ele : list) {
      if(builder.length() > 0) builder.append(';');
      int index = 0;
      while(index < ele.length()) {
        char c = ele.charAt(index);
        if(Character.isWhitespace(c) 
            || Character.isSpaceChar(c)) {
          index++;
          continue;
        } 
        if(c == '-') c = ';';
        builder.append(c);
        index++;
      }
    }
    //    System.out.println(" type "+ type + " : "+ builder);
    if(builder.length() > 0) return builder.toString();
    return null;
  }


}

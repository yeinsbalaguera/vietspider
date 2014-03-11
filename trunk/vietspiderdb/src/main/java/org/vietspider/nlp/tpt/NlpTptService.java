/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.tpt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 19, 2011  
 */
class NlpTptService extends Thread {

  private static NlpTptService service;

  public synchronized final static NlpTptService getInstance() {
    if(service == null) service = new NlpTptService();
    return service;
  }

  private NlpTptStorage storage;

  private volatile boolean execute = true;
  private volatile boolean commit = false;
  protected volatile java.util.Queue<NlpTptModel> queue = new ConcurrentLinkedQueue<NlpTptModel>();

  private NlpTptService() {
    File file = new File(UtilFile.getFolder("content/solr2/tpt/"), "data");
    try {
      storage = new NlpTptStorage(file);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if(storage != null) start();

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Article Database";}

      public void execute() {
        execute = false;
        commit();
        try {
          if(storage != null) storage.close();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    });
  }

  public void run() {
    while(execute) {
      commit();
      try {
        Thread.sleep(15*1000l);
      } catch (Exception e) {
      }
    }
  }

  private void commit() {
    commit = true;
    while(!queue.isEmpty()) {
      NlpTptModel model = queue.poll();
      //      System.out.println("save "+ model.getId());
      try {
        storage.write(model);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    try {
      if(storage.isCommit()) storage.commit();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }

    commit = false;
  }

  public /*List<Article>*/ NlpTptModel save(Article article) {
    try {
      if(storage == null) return null;
      //      System.out.println(article.getId()+ " : " + article.getNlpRecord());
      NlpTptModel model = createModel(article);
      if(model.isEmpty()) return null;
      //      System.out.println(model.isEmpty());
      NlpTptModel temp = search(model);
      if(temp != null) return temp;
      //      System.out.println(" phone model "+ model.getMobile());
      queue.add(model);
      //      return duplicates;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }

  private NlpTptModel createModel(Article article) {
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

  private /*List<Article>*/ NlpTptModel search(NlpTptModel model) {
    //    List<Article> duplicates = new ArrayList<Article>();
    //    List<NlpTptModel> models  = new ArrayList<NlpTptModel>();

    Iterator<NlpTptModel> iterator  = queue.iterator();
    while(iterator.hasNext()) {
      NlpTptModel temp = iterator.next();
      if(temp.getMobile() != null 
          && temp.getMobile().equals(model.getMobile())) {
        if(temp.isDuplicate(model)) return temp;
        //        models.add(temp);
      } else if(temp.getTelephone() != null 
          && temp.getTelephone().equals(model.getTelephone())) {
        if(temp.isDuplicate(model)) return temp;
        //        models.add(temp);
      } else if(temp.getEmail() != null 
          && temp.getEmail().equals(model.getEmail())) {
        if(temp.isDuplicate(model)) return temp;
        //        models.add(temp);
      }
    }

    while(commit) {
      try {
        Thread.sleep(500l);
      } catch (Exception e) {
      }
    }

    String phone = model.getTelephone();
    if(phone != null) {
      List<NlpTptModel> list = null;
      try {
        list = storage.getByTelephone(phone);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      if(list != null) {
        for(NlpTptModel temp : list) {
          if(temp.isDuplicate(model)) return temp;
        }
      }
      //      if(list != null) models.addAll(list);
    }

    while(commit) {
      try {
        Thread.sleep(500l);
      } catch (Exception e) {
      }
    }

    phone = model.getMobile();
    if(phone != null) {
      List<NlpTptModel> list = null;
      try {
        list = storage.getByMobile(phone);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      if(list != null) {
        for(NlpTptModel temp : list) {
          if(temp.isDuplicate(model)) return temp;
        }
      }
      //      if(list != null) models.addAll(list);
    }

    while(commit) {
      try {
        Thread.sleep(500l);
      } catch (Exception e) {
      }
    }

    String email = model.getEmail();
    if(email != null) {
      List<NlpTptModel> list = null;
      try {
        list = storage.getByEmail(email);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      if(list != null) {
        for(NlpTptModel temp : list) {
          if(temp.isDuplicate(model)) return temp;
        }
      }
      //      if(list != null) models.addAll(list);
    }

    //    if(models.size() < 1) return duplicates;
    //    System.out.println(model.getId() + " : " +models.size());
    //    return duplicates;
    return null;
  }

  public boolean equals(NlpTptModel model1, NlpTptModel model2) {
    if(model1.getArea() != null) {
      if(!model1.getArea().equals(model2.getArea())) return false;
    } else if(model2.getArea() != null) return false;

    if(model1.getPrice() != null) { 
      if(!model1.getPrice().equals(model2.getPrice())) return false;
    } else if(model2.getPrice() != null) return false;

    if(model1.getAction_object() != null) { 
      if(!model1.getAction_object().equals(model2.getAction_object())) return false;
    } else if(model2.getAction_object() != null) return false;

    if(model1.getAddress() != null) { 
      if(!model1.getAddress().equals(model2.getAddress())) return false;
    } else if(model2.getAddress() != null) return false;

    return true;
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

  public NlpTptStorage getStorage() { return storage; }
  
  
  public void searchByTelephone(Collection<String> ids, List<String> phones) {
    if(phones == null) return;
    for(String phone : phones) {
      searchByTelephone(ids, phone);
    }
  }

  public void searchByTelephone(Collection<String> ids, String phone) {
    List<NlpTptModel> list = null;
    try {
      list = storage.getByTelephone(phone);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }

    if(list == null) return;
    for(NlpTptModel temp : list) {
//      System.out.println("==========> " + temp.getId());
      ids.add(temp.getId());
    }
  }

  public void searchByMobile(Collection<String> ids, List<String> phones) {
    if(phones == null) return;
    for(String phone : phones) {
      searchByMobile(ids, phone);
    }
  }
  
  public void searchByMobile(Collection<String> ids, String phone) {
    List<NlpTptModel> list = null;
    try {
      list = storage.getByMobile(phone);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if(list == null) return;
    for(NlpTptModel temp : list) {
//      System.out.println("==========> " + temp.getId());
      ids.add(temp.getId());
    }
  }
  
  public void deleteExpire(int expireDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - expireDate);
    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    for(int i = 0; i < 3; i++) {
      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
      storage.deleteExpire(dateFormat.format(calendar.getTime()));
    }
    
    try {
      storage.commit();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    
    try {
      storage.defrag(null);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}

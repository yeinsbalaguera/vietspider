/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.data.jdbc2.JdbcInfo.Conn;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 30, 2011  
 */
class JdbcService extends Thread  {

  private static JdbcService INSTANCE = null;

  synchronized final static JdbcService getInstance() {
    if(INSTANCE == null) INSTANCE = new JdbcService(); 
    return INSTANCE;
  }

  protected boolean execute = true;
  volatile static long SLEEP = 30*1000l;
  private long startSleep = -1;

  protected volatile Queue<Domain> tempDomains = new ConcurrentLinkedQueue<Domain>();
  protected volatile Queue<Meta> tempMetas = new ConcurrentLinkedQueue<Meta>();
  protected volatile Queue<Content> tempContents = new ConcurrentLinkedQueue<Content>();
  protected volatile Queue<Image> tempImages = new ConcurrentLinkedQueue<Image>();
  protected volatile Queue<Relation> tempRelations = new ConcurrentLinkedQueue<Relation>();
  protected volatile Queue<Content> tempUpdateContents = new ConcurrentLinkedQueue<Content>();

  protected volatile Queue<String> tempDeletes = new ConcurrentLinkedQueue<String>();

  private String systemClean = null;

  protected Saver saver;
  private JdbcInfo jdbc;

  JdbcService()  {
    try {
      jdbc = new JdbcInfo();

      saver = new Saver();
    } catch (Exception e) {

      LogService.getInstance().setThrowable(e);
      System.exit(0);
    }

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Article Database";}

      public void execute() {
        execute = false;
        try {
          commit();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }

        jdbc.main.close();
      }
    });

    this.start();
  }

  public void run() {
    while(execute) {
      try {
        commit();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      
      jdbc.killTimeout();

      try {
        Thread.sleep(SLEEP);
      } catch (Exception e) {
      }

    }
  }

  void commit()  {
    startSleep = -1;

    try {
      jdbc.main.create();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      Application.addError(this);
      return;
    }

    try {
      jdbc.main.batch(saver.createDomainSQLs(tempDomains));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }

    try {
      jdbc.main.batch(saver.createMetaSQLs(tempMetas));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }

    try {
      saver.save(jdbc.main, tempContents);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }

    try {
      jdbc.main.batch(saver.createRelSQLs(tempRelations));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }

    while(!tempImages.isEmpty()) {
      try {
        saver.save(jdbc.main, tempImages.poll());
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    }

    try {
      jdbc.main.batch(saver.createUpdateContentSQLs(tempUpdateContents));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    try {
      //delete
      jdbc.main.batch(tempDeletes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    if(systemClean != null) {
      new SystemGC().deleteNoConstraintData(jdbc.main, systemClean);
    }

    jdbc.main.realse();

    startSleep = System.currentTimeMillis();
  }

  void save(Domain domain) { tempDomains.add(domain); }
  void save(Meta bean) { tempMetas.add(bean); }
  void save(Content bean) { tempContents.add(bean); }
  void updateStatus(Content bean) { tempUpdateContents.add(bean); }
  void save(Relation bean) { tempRelations.add(bean); }
  void save(Image bean) { tempImages.add(bean); }

  void delete(String sql) { tempDeletes.add(sql); }

  Domain loadTempDomain(final String id) throws Exception {
    return loadTemp(tempDomains, new Identify<Domain>(){
      public boolean is(Domain domain) {
        return id.equals(domain.getId());
      }
    });
  }

  Meta loadTempMeta(final String id) throws Exception {
    return loadTemp(tempMetas, new Identify<Meta>(){
      public boolean is(Meta bean) {
        return id.equals(bean.getId());
      }
    });
  }

  List<Meta> loadTempMetas(String domainId) throws Exception {
    List<Meta> metas = new ArrayList<Meta>();
    Iterator<Meta> iterator = tempMetas.iterator();
    while(iterator.hasNext()) {
      Meta bean = iterator.next();
      if(bean.getDomain().equals(domainId)) {
        metas.add(bean);
      }
    }
    return metas;
  }

  Content loadTempContent(final String id) throws Exception {
    return loadTemp(tempContents, new Identify<Content>(){
      public boolean is(Content bean) {
        return id.equals(bean.getMeta());
      }
    });
  }

  Image loadTempImage(final String id) throws Exception {
    return loadTemp(tempImages, new Identify<Image>(){
      public boolean is(Image bean) {
        return id.equals(bean.getId());
      }
    });
  }

  Relation loadTempRelation(final String id) throws Exception {
    return loadTemp(tempRelations, new Identify<Relation>(){
      public boolean is(Relation bean) {
        return id.equals(bean.getMeta());
      }
    });
  }

  List<Relation> loadTempRelations(String meta) throws Exception {
    List<Relation> list = new ArrayList<Relation>();
    Iterator<Relation> iterator = tempRelations.iterator();
    while(iterator.hasNext()) {
      Relation bean = iterator.next();
      if(bean.getMeta().equals(meta)) {
        list.add(bean);
      }
    }
    return list;
  }

  List<Image> loadTempImages(String meta) throws Exception {
    List<Image> list = new ArrayList<Image>();
    Iterator<Image> iterator = tempImages.iterator();
    while(iterator.hasNext()) {
      Image bean = iterator.next();
      if(bean.getMeta().equals(meta)) {
        list.add(bean);
      }
    }
    return list;
  }

  <T> T loadTemp(Queue<T> queue, Identify<T> identify) throws Exception {
    Iterator<T> iterator = queue.iterator();
    while(iterator.hasNext()) {
      T bean = iterator.next();
      if(identify.is(bean)) return bean;
    }
    return null;
  }

  static interface Identify <T> {
    public boolean is(T t);
  }

  int type() { return jdbc.type; }

  boolean isType(short t) { return jdbc.type == t; }

  Conn getConn() {
    if(jdbc.main.busy()) {
//      System.out.println("main conn is busy !!!");
      return jdbc.generateConn();
    }
    if(startSleep > 0 
        && (System.currentTimeMillis() - startSleep) < 10*1000l) {
//      System.out.println(" use main conn "+ jdbc.main.hashCode());
      return jdbc.main;
    }
//    System.out.println("main conn will be busy !!!");

    return jdbc.generateConn();
  }

  Queue<Domain> getTempDomains() { return tempDomains; }
  Queue<Meta> getTempMetas() { return tempMetas; }
  Queue<Content> getTempContents() { return tempContents; }
  Queue<Image> getTempImages() { return tempImages; }
  Queue<Relation> getTempRelations() { return tempRelations; }

  void setSystemClean(String min) { systemClean = min; }
}

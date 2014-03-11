/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.common;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;

import org.vietspider.common.io.LogService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 16, 2006
 */
public final class Application {

  public final static String CHARSET = "UTF-8" ;
  public final static String PROXY_ENABLE = "proxy.enable";
  public final static String PROXY_HOST = "proxy.host";
  public final static String PROXY_PORT = "proxy.port";
  public final static String PROXY_USER = "proxy.user";
  public final static String PROXY_PASSWORD = "proxy.password";

  public final static String AUTO_START_CRAWLER = "auto.start.crawler";
  public final static String TOTAL_EXECUTOR = "total.executor";
  public final static String TOTAL_WORKER_OF_EXECUTOR = "total.worker.of.executor";
  public final static String WEIGHT_PRIORITY_EXECUTOR = "weight.priority.executor";
  public final static String AUTO_SET_PRIORITY = "auto.set.priority";
//public final static String TEST_CONFIG_SOURCE = "test.config.source";
  public final static String DETECT_LANGUAGE = "detect.language";
  public final static String DETECT_WEBSITE = "detect.website";

  public final static String WORKER_TIMEOUT = "worker.timeout";
  public final static String HTTP_CLIENT_TIMEOUT = "http.client.timeout";

  public final static String LAST_DOWNLOAD_SOURCE = "last.download.source";

  public final static String DOWNLOAD_LIMIT_DATE = "download.limit.date";
  public final static String SAVE_IMAGE_TO_FILE = "save.image.to.file";

  public final static String EXPIRE_DATE = "expire.date";
  public final static String SCHEDULE_CLEAN_DATA = "schedule.clean.data";
  public final static String CLEAN_DATABASE = "clean.database";
  public final static String START_MINING_INDEX_SERVICE = "start.mining.index.service";
  public final static String REMOVE_DUPLICATE_CONTENT = "remove.duplicate.content";
  public final static String START_INDEX_DATA_SERVICE = "start.index.data.service";
  public final static String LOG_WEBSITE_ERROR = "log.website.error";
//public final static String POOL_TIMER = "pool.timer";
  public final static String QUEUE_SLEEP = "queue.sleep";

  public final static String HOST = "host";
  public final static String PORT = "port";
  public final static String WEB_PORT = "web.port";

  public final static String LANGUAGE = "language";
  public final static String LOCALE = "locale";

  public final static String CMS_LAYOUT = "cms.layout";

  public final static String BACKUP_FOLDER = "backup.folder";
  public final static String APPLICATION_NAME = "application.name";

  public static Properties CLIENT_PROPERTIES = null;
  public static Properties SERVER_PROPERTIES = null;

  public final static boolean DEMO = true;
  
  public static boolean PRINT = true;

//  public static Install LICENSE = Install.ENTERPRISE;
  public static Install LICENSE = Install.PROFESSIONAL;
//  public static Install LICENSE = Install.ENTERPRISE;
//  public static Install LICENSE = Install.PERSONAL;

//  public final static boolean TEST_MODE = true;

//  public static String [] GROUPS = {"ARTICLE", "DUSTBIN"};
//  public static String [] GROUPS = {/*"ARTICLE",*/ "XML", "DUSTBIN"};
  public static String [] GROUPS = {"XML", "DUSTBIN"};
//  public static String [] GROUPS = {"SEARCHTIONARY"};
//  public static String [] GROUPS = {"CLASSIFIED", "XML"};
//  public static String [] GROUPS = {"FORUM", "ARTICLE", "XML"};
// public static String [] GROUPS = {"ARTICLE", "CLASSIFIED", "XML", "DUSTBIN"};
//  public static String [] GROUPS = {"ARTICLE", "DOCUMENT"};
  //;{"ARTICLE","FORUM","BLOG", "DOCUMENT"};
  //"ARTICLE"
  //"ARTILE","FORUM","BLOG"
  
//  public static  volatile ClassLoader CLAZZ_LOADER;

  private static List<IShutdown> SHUTDOWNS = new Vector<IShutdown>();

  private static final Set<Object> ERRORS_SERVICE = new CopyOnWriteArraySet<Object>();

  public static final void addError(Object object) {
    ERRORS_SERVICE.add(object);
    LogService.getInstance().setMessage("CRAWLER", null, "Crawler is stop by "+ object.getClass() +"/"+object.hashCode());
  }
  
  public static boolean hasGroup(String group) {
    if(Application.LICENSE == Install.SEARCH_SYSTEM) return true;
    for(int i = 0; i < GROUPS.length; i++) {
      if(group.equals(GROUPS[i])) return true;
    }
    return false;
  }

  public static final void removeError(Object object) {
    ERRORS_SERVICE.remove(object);
    LogService.getInstance().setMessage("CRAWLER", null, "Crawler is continue by "+ object.getClass() +"/"+object.hashCode());
  }

  public static final boolean containsError(Object object) { return ERRORS_SERVICE.contains(object); }

  public static final boolean hasError() { return ERRORS_SERVICE.size() > 0; }

  public static synchronized void addShutdown(IShutdown thread){
    SHUTDOWNS.add(thread);
  }

  public static synchronized void removeShutdown(IShutdown thread){
    SHUTDOWNS.remove(thread);
  }

  public static abstract class IShutdown  {

    public int getPriority() { return 1; }

//  public boolean isNewThread() { return false; }

    public String getMessage() { return getClass().getName(); }

    public abstract void execute();
  }

  static {
    Runtime.getRuntime().addShutdownHook(new Thread(){
      public void run() {
        System.out.println("\n");
        for(int level = 0; level < 10; level++) {
          for(int i = 0; i < SHUTDOWNS.size(); i++) {
            IShutdown shutdown = SHUTDOWNS.get(i);
            if(shutdown.getPriority() != level) continue;
            executeShutdown(shutdown);
          }
        }
//      LogService.getInstance().setMessage(builder.toString());
      }
    });
  }

  private static void executeShutdown(IShutdown shutdown) {
    Date date_ = Calendar.getInstance().getTime();
    String msg = shutdown.getMessage();
    int prio = shutdown.getPriority();
    String message = "\n Prepare save "  + msg + " at level " + prio + " at "+ date_.toString() ;
    if(PRINT) System.out.println(message);

    try {
      shutdown.execute();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }


    date_ = Calendar.getInstance().getTime();
    message = msg + " at level " + prio + " at "+ date_.toString() ;
    if(PRINT) System.out.println(message);
  }

  public static boolean isSingleData() {
    return Application.LICENSE != Install.SEARCH_SYSTEM && Application.GROUPS.length == 1;
  }

}

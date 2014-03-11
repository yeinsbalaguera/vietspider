package org.vietspider.db;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;


public class CrawlerConfig {
  
  public final static short RDBMS_DB = 0;
  public final static short EMBEDED_DB = 1;

	public static int  LIMIT_DATE = 2*24*60*60*1000;
	public static int  EXPIRE_DATE = 7;
	public static boolean ENTERPRICE = false;
	
//	public static int GLOBAL_DEPTH = -1;
	
	public static boolean SAVE_IMAGE_TO_FILE = true;
	
	public static boolean UPDATE = false;
	public static int CLEAN_DATA_HOUR = -1;
	
	public static int  TOTAL_EXECUTOR = 2;
	public static int TOTAL_WORKER_OF_EXECUTOR = 2;
	
	public static int EXCUTOR_SIZE = 4;
	public static boolean INDEX_CONTENT = false;
	
	public static short DATABASE_TYPE = EMBEDED_DB;
	
	static {
	  loadConfig();
	}
	
	public static void loadConfig() {
	  SystemProperties properties = SystemProperties.getInstance();
	  try{
      ENTERPRICE = new Boolean(properties.getValue("enterprice"));
    }catch (Exception exp) {
      LogService.getInstance().setMessage(exp, null);
    }
	  
	  /*try{
	    LOAD_IMAGE = new Boolean(SystemProperties.get("loadImage"));
	  }catch (Exception exp) {
	    LogService.getInstance().setMessage(exp.toString());
	  }*/

//	  try{
//	    REMOVE_TITLE = new Boolean(SystemProperties.get("removeTitle"));
//	  }catch (Exception exp) {
//	    LogService.getInstance().setMessage(exp.toString());
//	  }
//
//	  try{
//	    REMOVE_DESC= new Boolean(SystemProperties.get("removeDes"));
//	  }catch (Exception exp) {
//	    LogService.getInstance().setMessage(exp.toString());
//	  }

	  try{
	    LIMIT_DATE = Integer.parseInt(properties.getValue(Application.DOWNLOAD_LIMIT_DATE));
	    if(LIMIT_DATE > 0) LIMIT_DATE = LIMIT_DATE*24*60*60*1000;
	  }catch (Exception exp) {
	    LogService.getInstance().setMessage(exp, null);
	  }

	  try{
	    SAVE_IMAGE_TO_FILE = new Boolean(properties.getValue(Application.SAVE_IMAGE_TO_FILE));
	  }catch (Exception exp) {
	    LogService.getInstance().setMessage(exp, null);
	  }
	  
	  try {
      String startIndexValue = properties.getValue(Application.START_INDEX_DATA_SERVICE);
      startIndexValue = startIndexValue != null ? startIndexValue.trim() : null;
      INDEX_CONTENT = "true".equals(startIndexValue);
    } catch (Exception e) {
      INDEX_CONTENT = false;
    }

	  /*try{
	    MANDATORY_LOAD_IMAGE = new Boolean(systemProperties.getValue("mandatoryLoadImage"));
	  }catch (Exception exp) {
	    LogService.getInstance().setMessage(exp, null());
	  }*/

	  try{
	    EXPIRE_DATE = Integer.parseInt(properties.getValue(Application.EXPIRE_DATE)); 
	    if(EXPIRE_DATE > 100) EXPIRE_DATE = 100;
	  }catch(Exception exp){      
	    LogService.getInstance().setMessage(exp, null);
	  }

//	  try{
//	    GLOBAL_DEPTH = Integer.parseInt(systemProperties.getValue("globalDepth")); 
//	  }catch(Exception exp){   
//	    GLOBAL_DEPTH = -1;
//	    LogService.getInstance().setMessage(exp, null);
//	  }

	  try{
	    CLEAN_DATA_HOUR = Integer.parseInt(properties.getValue(Application.SCHEDULE_CLEAN_DATA)); 
	  }catch(Exception exp){   
	    CLEAN_DATA_HOUR = -1;
	    LogService.getInstance().setMessage(exp, null);
	  }
	  
	  try{
      TOTAL_EXECUTOR = Integer.parseInt(properties.getValue(Application.TOTAL_EXECUTOR).trim());
    }catch (Exception exp) {
      LogService.getInstance().setMessage(exp, null);
      TOTAL_EXECUTOR = 1;
    }
    
    try {
      TOTAL_WORKER_OF_EXECUTOR = Integer.parseInt(properties.getValue(Application.TOTAL_WORKER_OF_EXECUTOR).trim());
    } catch (Exception exp) {
      LogService.getInstance().setMessage(exp, null);
      TOTAL_WORKER_OF_EXECUTOR = 1;
    }
    
    if(Application.LICENSE == Install.PERSONAL) {
//    DownloadedTracker.MAX_DATE = 15;
//    DownloadedTracker.FILE_SIZE = 128*1024;
//    DownloadedTracker.CACHE_SIZE = DownloadedTracker.FILE_SIZE/4;
    
    if(TOTAL_EXECUTOR > 2) TOTAL_EXECUTOR = 2;
    if(TOTAL_WORKER_OF_EXECUTOR > 2) TOTAL_WORKER_OF_EXECUTOR = 2;
  } if(Application.LICENSE == Install.PROFESSIONAL) {
//    DownloadedTracker.MAX_DATE = 30;
//    DownloadedTracker.FILE_SIZE = 256*1024;
//    DownloadedTracker.CACHE_SIZE = DownloadedTracker.FILE_SIZE/4;

    if(TOTAL_EXECUTOR > 10) TOTAL_EXECUTOR = 10;
    if(TOTAL_WORKER_OF_EXECUTOR > 3) TOTAL_WORKER_OF_EXECUTOR = 1;
  } if(Application.LICENSE == Install.ENTERPRISE) {
//    DownloadedTracker.MAX_DATE = 60;
//    DownloadedTracker.FILE_SIZE = 512*1024;
//    DownloadedTracker.CACHE_SIZE = DownloadedTracker.FILE_SIZE/4;
    
    if(TOTAL_EXECUTOR > 20) TOTAL_EXECUTOR = 20;
    if(TOTAL_WORKER_OF_EXECUTOR > 5) TOTAL_WORKER_OF_EXECUTOR = 1;
  } else {
//    DownloadedTracker.MAX_DATE = 100;
//    DownloadedTracker.FILE_SIZE = 1024*1024;
//    DownloadedTracker.CACHE_SIZE = DownloadedTracker.FILE_SIZE/4;
  }
    
    EXCUTOR_SIZE = TOTAL_EXECUTOR*TOTAL_WORKER_OF_EXECUTOR;
    
    String dbType = properties.getValue("dataSaver");
    if(dbType != null && dbType.startsWith("org.vietspider.db.content")) {
      DATABASE_TYPE = EMBEDED_DB;
    } else {
      DATABASE_TYPE = RDBMS_DB;
    }

	  /*try{
	    MINIMUM_PERCENT_RELATION = Integer.parseInt(systemProperties.getValue("minPercentRelation")); 
	  }catch(Exception exp){   
	    MINIMUM_PERCENT_RELATION = 10;
	    LogService.getInstance().setMessage(exp, null);
	  }*/
	}

}

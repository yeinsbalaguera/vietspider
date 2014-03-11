package org.vietspider.common.io;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Comparator;

import org.vietspider.common.text.CalendarUtils;

public class UtilFile {

  public static String FOLDER_DATA;
  
  static {  
    loadDataFolder();
  }
  
  private static void loadDataFolder() {
    if(FOLDER_DATA != null) return;
    FOLDER_DATA = System.getProperty("vietspider.data.path");
    if(FOLDER_DATA != null && !FOLDER_DATA.isEmpty() && new File(FOLDER_DATA).exists()) return;
    File file = new File("data");
    if(file.exists()) {
      FOLDER_DATA = file.getAbsolutePath();
      return ;
    }
    
    String appPath = System.getProperty("vietspider.path");
    file = new File(appPath+File.separator+"data");
    if(file.exists()) {
      FOLDER_DATA = file.getAbsolutePath();
      return ;
    }
    
//    try {
//      FOLDER_DATA = getFile(UtilFile.class.getClass(), "/data");  
//    }catch (Exception e) {
//      e.printStackTrace();
//      System.exit(-1);
//    }
//    FOLDER_DATA = new File(FOLDER_DATA).getAbsolutePath();
  }

  synchronized public static void deleteFolder(String path){
    deleteFolder(new File(path));    
  }

  synchronized public static void deleteFolder(File file){   
    deleteFolder(file, true);
  }

  synchronized public static String getFile(Class<?> clazz, String name) throws Exception {
    URL url = clazz.getResource(name);
    if(url == null) throw new Exception("Resource "+name+" not found in context "+ clazz.getName()); 
    return URLDecoder.decode(url.getFile(), "utf-8");
  }

  synchronized public static void deleteFolder(File file, boolean deleteParent){
    File[] list = file.listFiles();
    if( list == null || list.length < 1){
      if(deleteParent) file.delete();
      return;
    }
    for(File ele : list){
      if(ele.isDirectory()) deleteFolder(ele); else ele.delete();
    }
    if(deleteParent) file.delete();
  }
  
  synchronized public static File[] listModifiedFiles(String path, FileFilter filter){
    return listModifiedFiles(new File(path), filter);
  }
  
  synchronized public static File[] listModifiedFiles(File file, FileFilter filter){
    return listFiles(file, filter, new Comparator<File>() {
      public int compare(File a, File b) {
        if(a.lastModified() < b.lastModified()) return 1;
        if(a.lastModified() == b.lastModified()) return 0;
        return -1;
      }
    });
  }
  
  synchronized public static File[] listFiles(String path) {
    return listFiles(path, null);
  }
  
  synchronized public static File[] listFiles(File file) {
    return listFiles(file, null);
  }

  synchronized public static File[] listFiles(String path, FileFilter filter) {
    return listFiles(new File (path), filter);
  }
  
  synchronized public static File[] listFiles(File file, FileFilter filter) {
    return listFiles(file, filter, new Comparator<File>() {
      public int compare(File a, File b) {
        return b.compareTo(a);
      }
    });
  }

  synchronized public static File[] listFiles(File file, FileFilter filter, Comparator<File> comparator){
    File [] files = null;
    if(filter != null) {
      files = file.listFiles(filter);
    } else {
      files = file.listFiles();
    }
    if(files != null) Arrays.sort(files, comparator);
    return files;
  } 

  synchronized public static File getFile(File nFolder, String nFile) {
    File file = new File(nFolder, nFile);
    createFile(file, false);    
    return file;                    
  }

  synchronized public static File getFile(String nFolder, String nFile) { 
    File file = new File(getFolder(nFolder), nFile);
    createFile(file, false);    
    return file;                    
  }

  synchronized public static File getFolderNotCreate(String f){ 
    File file  = new File(FOLDER_DATA, f);
    if(!file.exists()) return null;
    return file;
  }

  synchronized public static File getFolder(String f) { 
    File file  = new File(FOLDER_DATA, f);
    if(file.isFile() && file.length() < 1) file.delete();
    createFile(file, true);        
    return file;
  }

  synchronized public static File createFile(File file, boolean isfolder) {
    try {
      if(file.exists()) return file;
      if(file.isDirectory() || isfolder) {
        file.mkdirs();
      }else { 
        if(file.getParentFile() != null) createFile(file.getParentFile(), true);
        file.createNewFile();
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(null, e);
      throw new RuntimeException(file.getAbsolutePath() +" : " + e.toString());
    }
    return file;
  }
  
  synchronized public static long length(File file) {
    if(file.isFile()) return file.length();
    File [] files = file.listFiles();
    if(files == null) return 0;
    long total  = 0;
    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) {
        total += length(files[i]);
      } else {
        total += files[i].length();
      }
    }
    return total;
  }
  
  public static boolean validate(String name) {
    if (name.length() < 10) return false;
    String date = name.substring(0, 10);
    try {
      CalendarUtils.getFolderFormat().parse(date);
      return true;
    } catch (Exception e) {
    }
    return false;
  }
  
}
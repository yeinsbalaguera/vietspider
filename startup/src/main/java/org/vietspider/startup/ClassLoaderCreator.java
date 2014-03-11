/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.startup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 28, 2007  
 */
public class ClassLoaderCreator {
  
  private String appPath = null;
  
  public String lookupPath() throws Exception {
    AppUnPacker appPacker = new AppUnPacker();
    String systemPath = System.getProperty("vietspider.path");
    File file = new File(systemPath + File.separator+"lib");
    if(file.exists()) {
      appPacker.unpack(file);
      return file.getPath(); 
    }
    
    file = new File("lib");
    if(file.exists()) {
      appPacker.unpack(file);
      return file.getPath(); 
    }
    
    URL lib = getClass().getResource("/lib");
    
    if(lib == null) {
      String urlPath = getClass().getResource("").toString();
      int index = urlPath.indexOf("startup.jar");
      if(index > 0) {
        urlPath =  urlPath.substring(0, index)+"lib";
        urlPath = urlPath.substring(4);
        lib = new URL(urlPath);
      }
    }
    if(lib == null)  return null;
    String path = lib.getFile();
    path = URLDecoder.decode(path, "utf-8");
    
    appPacker.unpack(new File(path));
    
    return path;
  }
  
  private List<URL> lookupJars(String path) throws Exception {
    List<URL> list = new LinkedList<URL>();
    
    File folder = new File(path);
    delete(new File(path, "delete")); 
    if(!folder.exists() || !folder.isDirectory()) {
      System.exit(-2);
    }
    
    File [] files = folder.listFiles();
    Arrays.sort(files, new Comparator<File>() {
      public int compare(File a, File b) {
        if(a.lastModified() < b.lastModified()) return 1;
        if(a.lastModified() == b.lastModified()) return 0;
        return -1;
      }
    });

    for(int i = 0; i < files.length; i++){
      if(files[i].getName().startsWith("data")){
        File file = new File(new File(path).getParentFile(), "data");
        String data = file.getPath();
        extractZip(files[i], data+"/");   
        files[i].delete();
        continue;
      }
      list.add(files[i].toURI().toURL());
    }
    
    return list;
  }
  
  private String lookupOSResources() {
    StringBuilder builder = new StringBuilder();
    String os_name = System.getProperty("os.name").toLowerCase();
    if(os_name.indexOf("windows") > -1 
        || os_name.indexOf("win") > -1) {
      os_name = "windows";
    } else if(os_name.indexOf("mac") > -1) {
      return "mac_os";
    } else if(os_name.indexOf("linux") > -1) {
      os_name = "linux";
    }
    builder.append(os_name);
//    String os_arch = System.getProperty("os.arch");
//    builder.append(os_arch.replace(' ', '_'));
    
    String model = System.getProperty("sun.arch.data.model");
    builder.append('_').append(model);
    System.out.println(builder.toString().toLowerCase());
    return builder.toString().toLowerCase();
  }
  
  public URLClassLoader createLoader(boolean containsParent) throws Exception {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    String path = lookupPath();
    if(!containsParent) classLoader = null;
    
    if(path == null) {
      return new URLClassLoader(new URL[0], classLoader);
    }
    
    this.appPath = path;
    
    List<URL> list = lookupJars(path);
   
    if(list.size() < 1) {
      return new URLClassLoader(new URL[0], classLoader);
    }
    
    try {
      String rpath = path + File.separator + lookupOSResources();
      File folder = new File(rpath);
      if(folder.exists()) {
        List<URL> rlist = lookupJars(rpath);
        if(rlist.size() > 0) list.addAll(rlist);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    File parent = new File(path).getParentFile();
    if(parent != null && parent.exists()) {
      System.setProperty("vietspider.path", parent.getAbsolutePath());
    }
    
    URL [] jars = new URL[list.size()];
    list.toArray(jars);
    return new URLClassLoader(jars, classLoader);
  }

//  public void kill() throws Exception {
//    Method method = instance.getClass().getDeclaredMethod("kill", new Class[]{});
//    method.invoke(instance, new Object[]{});
//  }

  private void delete(File file) throws Exception {
    if(!file.exists()) return;
    String text = new String(load(new FileInputStream(file)));
    text = text.trim();
    if(text.length() < 1) return ;
    String [] names = text.split("\n");
    for(String ele : names){
      File delete = new File(ele);
      delete.delete();
    }
    file.delete();
  }

  public void extractZip(File input, String output) throws Exception {
    BufferedOutputStream dest = null;
    FileInputStream fileInput = new FileInputStream(input);    
    ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(fileInput));
    ZipEntry entry;
    int count;    
    FileOutputStream fileOuput = null;
    int total =  0;   
    int BUFFER = 2048;    
    byte data[] = new byte[BUFFER];     
    while((entry = zipInput.getNextEntry()) != null) {      
      if(entry.isDirectory())
        createFile(new File(output+entry.getName()),true);
      else {       
        File f = null;
        if(output != null)
          f = new File(output+entry.getName());
        else
          f = new File(entry.getName());
        byte [] oldData = new byte[0];
        if(!f.exists())
          createFile(f, false);
        else{
          FileInputStream inputStream = new FileInputStream (f);
          oldData = new byte[(int)f.length()];
          inputStream.read(oldData);
        }
        fileOuput = new FileOutputStream(f);       
        dest = new BufferedOutputStream(fileOuput, BUFFER);      
        dest.write(oldData);
        while ((count = zipInput.read(data, 0, BUFFER)) != -1) {        
          dest.write(data, 0, count);
          total += count;
        }        
        dest.close();
      }
    }
    zipInput.close();
  }

  private File createFile(File file, boolean folder) throws Exception {
    if(file.getParentFile() != null) createFile( file.getParentFile(), true);
    if(file.exists()) return file;
    if(file.isDirectory() || folder) file.mkdir();
    else file.createNewFile();
    return file;
  }

  public synchronized byte[] load(FileInputStream input) throws Exception {
    FileChannel fchan = input.getChannel();
    long fsize = fchan.size();       
    ByteBuffer buff = ByteBuffer.allocate((int)fsize);        
    fchan.read(buff);
    buff.rewind();      
    byte[] data = buff.array();      
    buff.clear();      
    fchan.close();        
    input.close();       
    return data;
  }

  public String getAppPath() { return appPath; }
}

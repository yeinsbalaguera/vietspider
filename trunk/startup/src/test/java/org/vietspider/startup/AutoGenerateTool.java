/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.startup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.DataReader;
import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2011  
 */
public class AutoGenerateTool {

  private static class NetworkClassLoader extends ClassLoader {

    private byte[] b;

    public NetworkClassLoader(byte [] bytes) {
      this.b = bytes;
    }

    public Class<?> findClass(String name) {
      try {
        return defineClass(name, b, 0, b.length);
      } catch (Throwable e) {
        return null;
      }
    }

  }

  //D:\java\vietspider
  private static void collect(List<Class<?>> clazzes, File folder) throws Exception {
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) continue;
      String name = files[i].getName();
      if(name.endsWith("_MappingImpl.java")) {
        files[i].delete();
      }
      if(!name.endsWith(".class")) continue;

      String path = files[i].getAbsolutePath();
      int index = path.indexOf("classes");
      if(index < 0) continue;
      path = path.substring(index);

      index = path.indexOf(File.separatorChar);
      if(index < 0) continue;
      path = path.substring(index+1);

      index = path.indexOf(".class");
      if(index < 0) continue;
      path = path.substring(0, index);

      path = path.replace(File.separatorChar, '.');

      //      System.out.println(path);

      //      byte[] bytes = new DataReader().load(files[i]);
      //      NetworkClassLoader loader = new NetworkClassLoader(bytes);
      //      Class<?> clazz = loader.findClass(null);
      Class<?> clazz = null;
      try {
        clazz = Thread.currentThread().getContextClassLoader().loadClass(path);
      } catch (Throwable e) {
        System.err.println(path +  " : "+ e.toString());
      }
      if(clazz == null) continue;
      NodeMap nodeMap = clazz.getAnnotation(NodeMap.class);
      //      System.out.println(clazz.getName() +  " : "+ nodeMap);
      if(nodeMap != null) clazzes.add(clazz); 
      //GenerateCodeTool.generate(clazz);
    }

    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) collect(clazzes, files[i]);
    }
  }

  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\vietspider3\\src\\");
    //    File file = new File("D:\\java\\vietspider\\client-plugin\\");
    List<Class<?>> clazzes = new ArrayList<Class<?>>();
    collect(clazzes, new File(file, "client"));
    collect(clazzes, new File(file, "startup"));
    collect(clazzes, new File(file, "common"));        
    collect(clazzes, new File(file, "htmlparser"));   
    collect(clazzes, new File(file, "vietspiderdb")); 
    collect(clazzes, new File(file, "solr3"));
    collect(clazzes, new File(file, "solr3-searcher"));
    collect(clazzes, new File(file, "embededb"));
    collect(clazzes, new File(file, "model"));
    collect(clazzes, new File(file, "offices"));
    collect(clazzes, new File(file, "io"));
    collect(clazzes, new File(file, "crawler"));    
    collect(clazzes, new File(file, "httpserver2"));
    collect(clazzes, new File(file, "server-plugin"));

    collect(clazzes, new File(file, "client"));
    collect(clazzes, new File(file, "widget"));
    collect(clazzes, new File(file, "htmlexplorer"));
    collect(clazzes, new File(file, "gui"));
    collect(clazzes, new File(file, "client-plugin"));


    for(int i = 0; i < clazzes.size(); i++) {
      //      System.out.println(clazzes.get(i).isMemberClass());
      String name = clazzes.get(i).getName();
      if(name.indexOf('$') > -1) continue;
      //      System.out.println(name);
      //      name = name.replace('$', '.');
      //      Class clazz = Class.forName(name);
      //      System.out.println(clazz.getName());

      try {
        GenerateCodeTool.generateClazz(clazzes.get(i), false);
      } catch (Exception e) {
        System.err.println(name);
        e.printStackTrace();
      }
    }
  }
}

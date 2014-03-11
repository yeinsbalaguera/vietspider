/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.deploy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 24, 2008  
 */
public class DeployApp {
  
  private Packer packer;
  
  private List<String> ignoreJars = new ArrayList<String>();
  private List<String> ignoreFiles = new ArrayList<String>(); 
  
  public DeployApp( List<String> ignoreJars, List<String> ignoreFiles) {
    this();
    this.ignoreJars = ignoreJars;
    this.ignoreFiles = ignoreFiles;
  }
  
  public DeployApp() {
    packer = Pack200.newPacker();
    Map<String, String> p = packer.properties();
    // take more time choosing codings for better compression
    p.put(Packer.EFFORT, "7");  // default is "5"
    // use largest-possible archive segments (>10% better compression).
    p.put(Packer.SEGMENT_LIMIT, "-1");
    // reorder files for better compression.
    p.put(Packer.KEEP_FILE_ORDER, Packer.FALSE);
    // smear modification times to a single value.
    p.put(Packer.MODIFICATION_TIME, Packer.LATEST);
    // ignore all JAR deflation requests,
    // transmitting a single request to use "store" mode.
    p.put(Packer.DEFLATE_HINT, Packer.FALSE);
    // discard debug attributes
    p.put(Packer.CODE_ATTRIBUTE_PFX+"LineNumberTable", Packer.STRIP);
    // throw an error if an attribute is unrecognized
    p.put(Packer.UNKNOWN_ATTRIBUTE, Packer.ERROR);
    // pass one class file uncompressed:
    p.put(Packer.PASS_FILE_PFX+0, "mutants/Rogue.class");
  }
  
  public void deploy(List<File> list, File des, boolean pack) {
    for(int i = 0; i < list.size(); i++) {
      deploy(list.get(i), des, ignoreJars, pack);
    }
  }
  
  
  public void deploy(File src, File des, boolean pack) {
    deploy(src, des, ignoreJars, pack);
  }
  
  public void deploy(List<File> list, File des, List<String> ignores, boolean pack) {
    for(int i = 0; i < list.size(); i++) {
      deploy(list.get(i), des, ignores, pack);
    }
  }
  
  public void deploy(File src, File des, List<String> ignores, boolean pack) {
    for(int i = 0; i < ignores.size(); i++) {
      if(ignores.get(i).equals(src.getName())) return;
    }
    
    if(src.isDirectory()) {
      File [] files = src.listFiles();
      if(files == null) return;
      for(File file : files) {
        deploy(file, des, ignores, pack);
      }
      return;
    }
    if(!des.exists()) des.mkdirs();
    
//    String name = src.getName(); 
//    if(name.indexOf(".headvances.") < 0) return; 
    
    if(!pack(src, des, pack)) return;
    System.out.println("deploy " + src.getAbsolutePath());
  }
  
  private boolean pack(File file, File folder, boolean pack) {
    String name = file.getName(); 

    int index = name.lastIndexOf(".jar");
    if(index < 1) return false;
    
    if(!pack) {
      try {
        IOUtils.copy(file, new File(folder, name));
      } catch (Exception e) {
        e.printStackTrace();
      }
      return true;
    }
    
    name = name.substring(0, index);
    File newFile = new File(folder, name+".pack");
    
    try {
      FileOutputStream fos = new FileOutputStream(newFile);
      // Call the packer
      JarFile jarFile = new JarFile(file);
      packer.pack(jarFile, fos);
      jarFile.close();
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public void copy(File src, File des) {
    copy(src, des, ignoreFiles);
  }
  
  public void copy(File src, File des, List<String> ignores) {
    for(int i = 0; i < ignores.size(); i++) {
      if(ignores.get(i).equals(src.getName())) return;
    }
    
    System.out.println("copy " + src.getAbsolutePath());
    
    File newDes = new File(des, src.getName());
    if(src.isDirectory()) {
      if(!newDes.exists()) newDes.mkdirs();
      
      File [] files = src.listFiles();
      if(files == null) return;
      for(File file : files) {
        copy(file, newDes, ignores);
      }
      return;
    }
    
//    if(pack(src, des)) return;
      
    try {
      IOUtils.copy(src, newDes);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  public void setIgnoreJars(List<String> ignoreJars) { this.ignoreJars = ignoreJars; }

  public void setIgnoreFiles(List<String> ignoreFiles) { this.ignoreFiles = ignoreFiles; }
  
}

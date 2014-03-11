package org.vietspider.startup;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2008  
 */
public class AppUnPacker {
  
  private Packer packer;
  
  public AppUnPacker() {
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

  public void pack(File folder, boolean recursive) {
    File [] files  = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) {
        if(recursive) pack(files[i], recursive);
        continue;
      }
      String name  = files[i].getName();
      int index = name.lastIndexOf(".jar");
      if(index < 1) continue;
      name = name.substring(0, index);
      File newFile = new File(folder, name+".pack");
      
      try {
        FileOutputStream fos = new FileOutputStream(newFile);
        // Call the packer
        JarFile jarFile = new JarFile(files[i]);
        packer.pack(jarFile, fos);
        jarFile.close();
        fos.close();
        files[i].delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void unpack(File folder) {
    File [] files  = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) {
        unpack(files[i]);
        continue;
      }
      
      String name  = files[i].getName();
      int index = name.lastIndexOf(".pack");
      if(index < 1) continue;
      name = name.substring(0, index);
      File newFile = new File(folder, name+".jar");
      
      try {
        FileOutputStream fostream = new FileOutputStream(newFile);
        JarOutputStream jostream = new JarOutputStream(fostream);
        Unpacker unpacker = Pack200.newUnpacker();
        // Call the unpacker
        unpacker.unpack(files[i], jostream);
        // Must explicitly close the output.
        jostream.close();
        
        files[i].delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
}

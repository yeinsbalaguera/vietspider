/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.vietspider.common.Application;
import org.vietspider.common.Install;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 21, 2009  
 */
public class LicenseVerifier {
  
  public static File loadLicenseFile() {
    File licenseFile = new File(UtilFile.FOLDER_DATA+"/../lib/sync.vs.license");
    if(licenseFile.exists() && licenseFile.length() > 0) return licenseFile;
    licenseFile = new File(UtilFile.getFolder("system/"), "sync.vs.license");
    return licenseFile;
  }

  public static boolean verify(String plugin, File file) {
    if(Application.LICENSE == Install.PROFESSIONAL
        ||  Application.LICENSE == Install.ENTERPRISE) return true;
    if(!file.exists() || file.length() < 1) return false;
    ZipInputStream zipInputStream = null;
    try {
      byte [] data = null;
      byte[] encryptKey = null;
      byte[] signatureToVerify = null;

      FileInputStream fileInputStream = new FileInputStream(file);
      zipInputStream = new ZipInputStream(fileInputStream);

      for(int i = 0 ; i < 3; i++) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String name  = unzip(zipInputStream, byteArrayOutputStream);
        if(name == null) return false;
        if(name.equalsIgnoreCase("information")) {
          data = byteArrayOutputStream.toByteArray(); 
        } else if(name.equalsIgnoreCase("key")) {
          encryptKey  = byteArrayOutputStream.toByteArray();
        } else if(name.equalsIgnoreCase("license")) {
          signatureToVerify = byteArrayOutputStream.toByteArray();
        } 
      }

      return verify(plugin, data, encryptKey, signatureToVerify);
    } catch (Exception e) {
      try {
        if(zipInputStream != null) zipInputStream.close();
      } catch (Exception e1) {
        return false;
      }
    }

    return false;
  }

  private static String unzip(ZipInputStream zipInput, ByteArrayOutputStream outputStream) throws Exception {
    ZipEntry zipEntry = zipInput.getNextEntry(); 
    if(zipEntry == null) return null;      
    int read = -1;
    byte [] bytes = new byte[4*1024];  

    while ((read = zipInput.read(bytes, 0, bytes.length)) != -1) {        
      outputStream.write(bytes, 0, read);
    }        
    return zipEntry.getName();
  }



  public static boolean verify(String plugin, File keyFile, File userFile, File signatureFile) {
    try {
      byte[] encryptKey = RWData.getInstance().load(keyFile);

      byte [] data = RWData.getInstance().load(userFile);

      byte[] signatureToVerify = RWData.getInstance().load(signatureFile);

      return verify(plugin, data, encryptKey, signatureToVerify);
    } catch (Exception exception) { 
      return false;
    }
  }

  public static boolean verify(String plugin, byte[] data, byte[] encryptKey, byte[] signatureToVerify) {
    try {
//      System.out.println(data.length+ " / "+encryptKey.length+ "/"+ signatureToVerify.length);
      X509EncodedKeySpec pubKeySpecification = new X509EncodedKeySpec(encryptKey);
      KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
      PublicKey publicKey = keyFactory.generatePublic(pubKeySpecification);

      Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
      signature.initVerify(publicKey);

      signature.update(data, 0, data.length);

//      System.out.println("===== > "+signature.verify(signatureToVerify));
      if(!signature.verify(signatureToVerify)) return false;
      String value = new String(data, Application.CHARSET);
      String [] elements = value.split("\n");
      long start = System.currentTimeMillis();
      long expire = -1;
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim();
        if(elements[i].startsWith("date")) {
          elements[i] = elements[i].substring(6);
          start = Long.parseLong(elements[i].trim());
        } else if(elements[i].startsWith("expire")) {
          elements[i] = elements[i].substring(8);
          expire = Long.parseLong(elements[i].trim());
        } else if(elements[i].startsWith("plugin")) {
          elements[i] = elements[i].substring(8);
          if(elements[i].indexOf(plugin) < 0) return false;
        }
      }
      
//      System.out.println("===== > "+ (System.currentTimeMillis() - start)+ " / "+expire
//          + " / "+ (System.currentTimeMillis() - start < expire));
      return System.currentTimeMillis() - start < expire;
    } catch (Exception exception) { 
      return false;
    }
  }
}

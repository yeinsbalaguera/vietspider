/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 21, 2009  
 */
public class LicenseGenerator {
  
  public static File generate(File file) throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA", "SUN");

    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
    secureRandom.setSeed(0);
    keyPairGenerator.initialize(1024, secureRandom);

    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    PrivateKey privateKey = keyPair.getPrivate();
    PublicKey publicKey = keyPair.getPublic();

    Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
    signature.initSign(privateKey);

    byte [] data = RWData.getInstance().load(file);
    signature.update(data, 0, data.length);

    byte [] dataSignature = signature.sign();
    
    byte[] key = publicKey.getEncoded();
    
    File licenseFile = new File(file.getParentFile(), file.getName()+".vs.license");
    if(licenseFile.exists()) licenseFile.delete();
    FileOutputStream fileOutputStream = new FileOutputStream(licenseFile);
    ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
    
    ZipEntry entry = new ZipEntry("information");
    zipOutputStream.putNextEntry(entry);
    zipOutputStream.write(data);
    zipOutputStream.closeEntry();
    
    entry = new ZipEntry("license");
    zipOutputStream.putNextEntry(entry);
    zipOutputStream.write(dataSignature);
    zipOutputStream.closeEntry();
    
    entry = new ZipEntry("key");
    zipOutputStream.putNextEntry(entry);
    zipOutputStream.write(key);
    zipOutputStream.closeEntry();
    
    zipOutputStream.close();
    fileOutputStream.close();
    
    return licenseFile;
  }
}

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common;

import java.io.File;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LicenseGenerator;
import org.vietspider.common.io.LicenseVerifier;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 21, 2009  
 */
public class TestLicenseGenerator {
  public static void main(String[] args) throws Exception{
    StringBuilder builder = new StringBuilder();
    builder.append("plugin:").append(' ').append("joomla nukeviet").append('\n');
    builder.append("name:").append(' ').append("Nhu Dinh Thuan").append('\n');
    builder.append("email:").append(' ').append("nhudinhthuan@gmail.com").append('\n');
    builder.append("license:").append(' ').append("DEERT236DWSX#$5483GGWD").append('\n');
    
    long start = System.currentTimeMillis();
    builder.append("date:").append(' ').append(start).append('\n');
    
    long expire = 36*31*24*60*60*1000l;
    builder.append("expire:").append(' ').append(expire).append('\n');
    
    File file  = new File("D:\\Temp\\sync");
    if(file.exists()) file.delete();
    org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes("utf-8"));
    File licenseFile = LicenseGenerator.generate(file);
    System.out.println(LicenseVerifier.verify("joomla", licenseFile));
  }
}

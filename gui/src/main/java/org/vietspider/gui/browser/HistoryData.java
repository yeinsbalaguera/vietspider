/***************************************************************************
 * Copyright 2004-2006 The VietSpider Studio All rights reserved.  * 
 **************************************************************************/
package org.vietspider.gui.browser;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.ui.services.ClientLog;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * Jun 10, 2005
 */

public class HistoryData {

  private String name = "client/history/";

  public HistoryData(){
  }

  public synchronized String[] loadDate() throws Exception { 
    File [] files = UtilFile.listFiles(UtilFile.getFolder(name));  
    String [] names  = new String[Math.min(10, files.length)];   
    for( int i=0; i<names.length; i++) {
      names[i] = files[i].getName();      
    }        
    return names;
  }

  public synchronized void save( String text) throws Exception { 
    if( text == null || text.length() == 0) return ;
    text = text.trim();    
    if(!check(text)) return;
    String path = UtilFile.getFolder(name).getAbsolutePath(); 
    Calendar cal = Calendar.getInstance();
    path = path + File.separator + String.valueOf(cal.get(Calendar.YEAR))+"_"
    + String.valueOf(cal.get(Calendar.MONTH)+1)+"_"+ String.valueOf(cal.get(Calendar.DATE));   
    File file = new File( path); 
    if(this.find( text, file)) return;
    if( !file.exists()) file.createNewFile();   
    RWData.getInstance().append(new File(path), ("\n"+text).getBytes(Application.CHARSET));
  }

  private boolean check(String text){
    if(SWProtocol.isHttp(text)) return true;
    if(text.startsWith("ftp")) return true;
    return false;
  }

  public synchronized String[] loadDomain(String date) throws Exception {  
    return this.loadDomain(UtilFile.getFile(name,date));
  }

  public synchronized String[] loadDomain(File file) throws Exception {  
    String content = new String(RWData.getInstance().load( file));
    String [] split = content.split("\n");
    return split;
  }

  public synchronized List<String> loadRecentDate(int d) throws Exception {
    File [] files = UtilFile.listFiles(UtilFile.getFolder(name)); 
    List<String> list = new ArrayList<String>();
    String [] split ;
    for( int i=0; i< Math.min(files.length, d); i++){
      split = this.loadDomain( files[i]);
      for( int k=0; k<split.length; k++){
        if( !split[k].trim().equals("") && !list.contains( split[k]) ) list.add( split[k].trim());
      }
    }
    Collections.sort( list);
    return list;
  }
  public synchronized String[] loadRecentDateByArray(int d) throws Exception {
    List<String> list = this.loadRecentDate( d);
    String [] split = new String[ list.size()];
    for( int i=0; i<split.length; i++){
      split[i] = list.get( i);
    }
    return split;
  }


  public boolean find( String text, File file) throws Exception {    
    if( text == null || text.equals("")) return true;
    Charset charset = Charset.forName(Application.CHARSET);
    Pattern pattern = Pattern.compile( text, Pattern.CASE_INSENSITIVE); 
    if( !file.exists()) file.createNewFile();
    FileInputStream stream = null;
    FileChannel channel = null;
    boolean result = false;
    try {
      stream = new FileInputStream(file);
      channel = stream.getChannel();
      ByteBuffer bytes = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size( ));
      CharBuffer chars = charset.decode(bytes);   
      Matcher matcher = pattern.matcher(chars);
      result = matcher.find(); 
      chars.clear();
      bytes.clear();

      channel.close();
      stream.close();
    } finally {
      try {
        if(stream != null) stream.close();
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }

      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }
    return result;
  } 
}

package org.vietspider.common.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.vietspider.common.Application;

public class PropertiesFile {	  
  
  protected volatile boolean convert = true;
  
  public PropertiesFile(boolean convert) {
    this.convert = convert;
  }
  
  protected synchronized Properties load(String path ) throws Exception {	         
    return load( new File( path));
  }
  
  public synchronized Properties load(File f) throws Exception {    
    if(!f.exists()) f.createNewFile();
    
    return load(RWData.getInstance().load(f));
  }
  
  public synchronized Properties load(byte [] bytes)  throws Exception {
    Properties properties = new Properties();
    String data = new String(bytes, Application.CHARSET);
    String [] elements = data.split("\n");
    for(String element : elements) {
      int index = element.indexOf('=');
      if(index < 1) continue;
      String key  = element.substring(0, index).trim();
      String value  = element.substring(index + 1, element.length()).trim();
      if(convert) value = convert(value);
      properties.put(key, value);
    }
    return properties;
//    FileInputStream input = null;
//    try {
//      input = new FileInputStream(f);
//      Properties p = new Properties();    
//      p.load(input);
//      input.close();
//      return p;   
//    } finally {
//      try {
//        if(input != null) input.close();
//      } catch (Exception e) {
//        LogService.getInstance().setThrowable(e);
//      }
//    }
  }
  
  public synchronized void save(File file, Properties p) throws Exception {
    List<String> values = new ArrayList<String>();
    Iterator<Object> iterator = p.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next().toString();
      StringBuilder builder = new StringBuilder(key);
      builder.append('=').append(p.getProperty(key));
      values.add(builder.toString());
    }
    Collections.sort(values);
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < values.size(); i++) {
      builder.append(values.get(i)).append('\n');
    }
    RWData.getInstance().save(file, builder.toString().trim().getBytes());
  }

  protected synchronized void save(String path, Properties p) throws Exception {
    save(new File(path), p);
  }
  
  public synchronized void copy(String from,String dis) throws Exception {
    save(dis, load(from));	         
  }	
  
  public synchronized  void copy(File from,File dis) throws Exception {
    save(dis, load(from));           
  } 
  
  public synchronized void delete( String path) throws Exception {
    delete(new File(path));
  }
  
  public synchronized void delete(File f) throws Exception {    
    if(f.exists()) f.delete();	
  }
  
  public static String convert (String text) {
    if(text.indexOf("\\u") < -1) return text;
    
    char [] in = text.toCharArray();
    int off = 0;
    int len = in.length;
    
    char[] convtBuf = new char[1024];
    if (convtBuf.length < len) {
      int newLen = len * 2;
      if (newLen < 0) {
        newLen = Integer.MAX_VALUE;
      } 
      convtBuf = new char[newLen];
    }
    char aChar;
    char[] out = convtBuf; 
    int outLen = 0;
    int end = off + len;

    while (off < end) {
      aChar = in[off++];
      if (aChar == '\\') {
        aChar = in[off++];   
        if(aChar == 'u') {
          // Read the xxxx
          int value=0;
          for (int i=0; i<4; i++) {
            aChar = in[off++];  
            switch (aChar) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
              value = (value << 4) + aChar - '0';
              break;
            case 'a': case 'b': case 'c':
            case 'd': case 'e': case 'f':
              value = (value << 4) + 10 + aChar - 'a';
              break;
            case 'A': case 'B': case 'C':
            case 'D': case 'E': case 'F':
              value = (value << 4) + 10 + aChar - 'A';
              break;
            default:
              throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
            }
          }
          out[outLen++] = (char)value;
        } else {
          if (aChar == 't') aChar = '\t'; 
          else if (aChar == 'r') aChar = '\r';
          else if (aChar == 'n') aChar = '\n';
          else if (aChar == 'f') aChar = '\f'; 
          out[outLen++] = aChar;
        }
      } else {
        out[outLen++] = aChar;
      }
    }
    return new String (out, 0, outLen);
  }


  
}

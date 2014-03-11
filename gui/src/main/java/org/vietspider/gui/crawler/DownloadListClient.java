package org.vietspider.gui.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.ui.services.ClientLog;

public class DownloadListClient extends RandomAccess {

  static int SIZE = 100;
  private List<Long> listPages = new ArrayList<Long>(); 
  private int time;
  
  public DownloadListClient() {
    countPage((String)null);
  }

  public synchronized void update(String pattern, boolean update) throws Exception {
    if(pattern != null) pattern  = pattern.toLowerCase();
    File file = ClientConnector2.getCacheFolder("download");
    //UtilFile.getFolder("client/download");
    if(update) UtilFile.deleteFolder(file, false);

    File zipFile = ClientConnector2.getCacheFile("download", getTempFile());
    if(update || !zipFile.exists() || zipFile.length() < 1) {
      loadServerInputStream();//new CrawlerClientHandler().loadDownloadList();
    } 

    countPage(pattern);
  }

  public InputStream loadServerInputStream() throws Exception {
    time++;
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    HttpData httpData = null;
    FileOutputStream fileOutputStream = null;
    InputStream inputStream = null;
    File zipFile = ClientConnector2.getCacheFile("download", getTempFile());
    try {
      httpData = new CrawlerClientHandler().loadDownloadList();
      inputStream = httpData.getStream();

      if(zipFile.exists()) zipFile.delete();
      zipFile.createNewFile();

      fileOutputStream = new FileOutputStream(zipFile);
      
      new GZipIO().load(inputStream, fileOutputStream, null);
//      org.vietspider.common.io.RWData.getInstance().save(fileOutputStream, inputStream);
    } finally {
      connector.release(httpData);
      if(inputStream != null) inputStream.close();
      if(fileOutputStream != null) fileOutputStream.close();
    }

    FileInputStream fileInputStream = new FileInputStream(zipFile);
    return fileInputStream;
  }

  public int totalPage() {
    return listPages.size();
  }

  public List<String> loadPage(int page) {
    List<String> list = new LinkedList<String>();

    File file = new File(ClientConnector2.getCacheFolder("download"), getTempDataFile());
    if(!file.exists()) {
      file = ClientConnector2.getCacheFile("download", getTempFile());
    }
    RandomAccessFile stream = null;
    FileChannel channel = null;

    long start  = listPages.get(page);
    long size  = 0;
    if(page >= listPages.size() - 1) {
      size = file.length() - start;
    } else {
      size = listPages.get(page + 1) - start;
    }
    
    try {
      stream = new RandomAccessFile(file, "r");
      
      channel = stream.getChannel();
      ByteBuffer buff = ByteBuffer.allocate((int)size);      
      channel.read(buff, start);
      buff.rewind();      
      byte[] data = buff.array();
      
      String value = new String(data, Application.CHARSET);
      Collections.addAll(list, value.split("\n"));
      
      buff.clear();
      
      channel.close();
    } catch (Exception e) {
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
      }
      try {
        if(stream != null) stream.close();
      } catch (Exception e) {
      }
    }
  
    return list;
  }
  
  public boolean contains(String element) {
    File file = new File(ClientConnector2.getCacheFolder("download"), getTempFile());
    RandomAccessFile stream = null;
    FileChannel channel = null;
    
    Charset charset = Charset.forName("utf-8");
    Pattern pattern = Pattern.compile(element, Pattern.CASE_INSENSITIVE);
    
    List<Long> positions = countPage(file);
    
    try {
      int page  = 10;
      stream = new RandomAccessFile(file, "r");
      channel = stream.getChannel();
      
      while(page < positions.size()) {
        long start  = positions.get(page);
        long size  = 0;
        
        page += positions.size();
        if(page >= positions.size() - 1) {
          size = file.length() - start;
        } else {
          size = positions.get(page) - start;
        }
       
        ByteBuffer bytes = channel.map(FileChannel.MapMode.READ_ONLY, start, size);
        CharBuffer chars = charset.decode(bytes);
        Matcher matcher = pattern.matcher(chars);
        if (matcher.find()) return true;
//        System.out.println(" tai day " + page);
      }
    } catch (Exception e) {
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
      }
      try {
        if(stream != null) stream.close();
      } catch (Exception e) {
      }
    }
  
    return false;
  }
  
  private void countPage(String pattern) {
    filterFile(pattern);
    
    File file = new File(ClientConnector2.getCacheFolder("download"), getTempDataFile());
    if(!file.exists()) {
      file = ClientConnector2.getCacheFile("download", getTempFile());
    }
    listPages.clear();
    listPages = countPage(file);
  }

  List<Long> countPage(File file) {
    List<Long> values = new ArrayList<Long>();
    RandomAccessFile randomAccess = null;
    try {
      randomAccess = new RandomAccessFile(file, "r");
      values.add(0l);

      int counter = 0;
      String line = null;
      while((line  = readLine(randomAccess))!= null) {
        if(line.trim().isEmpty()) break;
        if(counter == 99) {
          values.add(randomAccess.getFilePointer());
          counter = 0;
        } else {
          counter++;
        }
      } 
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(randomAccess != null) randomAccess.close();
      } catch (Exception e) {
      }
    }
    return values;
  }
  
  private void filterFile(String pattern) {
    File dataFile = new File(
        ClientConnector2.getCacheFolder("download"), getTempDataFile());
    if(dataFile.exists()) dataFile.delete();
    
    if(pattern == null || pattern.isEmpty()) return;

    try {
      dataFile.createNewFile();
    } catch (Exception e) {
      return;
    }
    
    File zipFile = ClientConnector2.getCacheFile("download", getTempFile());
    
    FileInputStream inputStream = null;
    InputStreamReader inputReader = null;
    BufferedReader bufferedReader = null;
    
    FileOutputStream output = null;
    FileChannel channel = null;
    
    try {
      inputStream = new FileInputStream(zipFile);
      inputReader = new InputStreamReader(inputStream, Application.CHARSET);
      bufferedReader = new BufferedReader(inputReader);
      
      output = new FileOutputStream(dataFile, false);
      channel = output.getChannel();

      while(true) {
        String line = bufferedReader.readLine();
        if(line == null) break;
        if(line.trim().isEmpty() || line.equals("-1")) continue;

        if(line.toLowerCase().indexOf(pattern) < 0) continue;
        line += "\n";
//        System.out.println(line + " : "+ pattern);
        
        byte [] bytes = line.getBytes(Application.CHARSET);
        ByteBuffer buff = ByteBuffer.allocateDirect(bytes.length);
        buff.put(bytes);
        buff.rewind();
        if(channel.isOpen()) channel.write(buff);
        buff.clear(); 
      }

    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(output != null) output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(inputStream != null) inputStream.close();
      }catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }

      try {
        if(inputReader != null) inputReader.close();
      }catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }

      try {
        if(bufferedReader != null) bufferedReader.close();
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }
  }
  
  public File getFile() { 
    return ClientConnector2.getCacheFile("download", getTempFile());
  }
  
  public synchronized void append(String value) throws Exception {
    File file = ClientConnector2.getCacheFile("download", getTempFile());
    if(file.length() > 0) value = "\n" + value;
    RWData.getInstance().append(file, value.getBytes(Application.CHARSET));
  }
  
  private String getTempFile() {
    return "load"+String.valueOf(time)+".temp";
  }
  
  private String getTempDataFile() {
    return "load"+String.valueOf(time)+".data.temp";
  }

  public int getCurrentTime() { return time; }

}

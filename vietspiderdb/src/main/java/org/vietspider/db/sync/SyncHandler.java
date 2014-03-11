/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.sync;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.DataCollection;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.StorableTempQueue;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DBScripts;
import org.vietspider.net.client.DataClientService;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 22, 2009  
 */
public abstract class SyncHandler<T extends Serializable> extends StorableTempQueue<T> {

  protected String server_action;
  protected String server_address;

  protected DataClientService client;
  protected  String remote ;

  protected volatile boolean execute = true;
  protected volatile boolean connected = true;

  protected volatile long lastSync = -1;
  protected volatile int maxSync = 100;

  protected String username; 
  protected String handlerPath;

  protected DataCollection<T> dataCollection = null;

  public SyncHandler(File folder, String server, String action, String handlerPath) {
    super(folder, 100);
    this.server_address = server;
    this.server_action = action;
    this.handlerPath = handlerPath;

    initXMLFile();

    //    System.out.println(remote + " : "+ username);

    if(remote == null) {
      remote = SystemProperties.getInstance().getValue(server_address);
      username = SystemProperties.getInstance().getValue("sync.data.username");
    }

    if(remote == null || remote.trim().isEmpty()) return;
    LogService.getInstance().setMessage(null, "search engine sync " + folder.getName() + " to "+ remote);
    client = new DataClientService(remote);
  }

  private void initXMLFile() {
    File file = new File(UtilFile.getFolder("system"), "database.xml");
    if(!file.exists() || file.length() < 1) return;
    try {
      DBScripts initScripts = new DBScripts(file);
      String driver = initScripts.get("driver");
      if(driver != null && driver.trim().length() > 0) return;
      remote = initScripts.get("connection");
      username = initScripts.get("user");
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
  }

  public DataClientService getClient() { return client;  }

  public SyncHandler(String name, String server, String action, String handlerPath) {
    this(UtilFile.getFolder("content/sync/" + name), server, action, handlerPath);
  }

  public void add(T value) {
    if(client == null) return;
    client.closeTimeout();
    if(queue.size() >= sizeOfWorking) store1(queue);
    if(value != null) save(value);
  }

  protected boolean sync()  {
    if(client == null) return false;

    if(!connected) testConnection();
    if(!connected) return false;

    ArrayList<T> indexs = new ArrayList<T>();
    try {
      load(indexs);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    if(indexs.size() < 1) return false;

    lastSync = System.currentTimeMillis();

    return sync(indexs, 0);
    //    System.out.println("chuan bi sync toi "+ client.getUrl());
  }

  private boolean sync(ArrayList<T> indexs, int time)  {
    if(time  >= 3) {
//      CopyOnWriteArrayList<T> list = new CopyOnWriteArrayList<T>();
      ConcurrentLinkedQueue<T> list = new ConcurrentLinkedQueue<T>();
      list.addAll(indexs);
      store1(list);
      return true;
    }

    if(indexs.size() < 1) return true;

    try {
      Header header = new BasicHeader("action", server_action);
      Header header2 =new BasicHeader("username", username);
      
      LogService.getInstance().setMessage(null, "Post " + indexs.size() 
          + " " + indexs.get(0).getClass().getSimpleName() + " to " + client.getUrl());
      byte [] bytes = null;
//      System.out.println(" chay thu co "+ dataCollection);
      if(dataCollection != null) {
        dataCollection.set(indexs);
        bytes = client.postAsXML(handlerPath, dataCollection, header, header2);
//        bytes = client.postAsObject(handlerPath, dataCollection, header, header2);
      } else {
        bytes = client.postAsObject(handlerPath, indexs, header, header2);
//        bytes = client.postAsXML(handlerPath, indexs, header, header2);
      }

      //      bytes = new GZipIO().zip(bytes);
      String result = new String(bytes);
//      System.out.println(result);
      String [] elements  = result.trim().split("\n");
      for(String ele : elements) {
        removeSuccessfullValue(indexs, ele);
      }
      if(indexs.size() < 1) return true;
      return sync(indexs, time+1);
      //    } catch (ConnectionPoolTimeoutException e1) {
      //      connected = false;      
      //      String remote = SystemProperties.getInstance().getValue(server_address);
      //      if(remote == null || remote.trim().isEmpty()) return;
      //      LogService.getInstance().setMessage(null, "search engine sync " + folder.getName() + " to "+ remote);
      //      client = new DataClientService(remote);
    } catch (Throwable e) {
      connected = false;      
      LogService.getInstance().setThrowable(client.getUrl(), e);
//      CopyOnWriteArrayList<T> list = new CopyOnWriteArrayList<T>();
      ConcurrentLinkedQueue<T> list = new ConcurrentLinkedQueue<T>();
      list.addAll(indexs);
      store1(list);
      return false;
    }
  }

  public abstract void removeSuccessfullValue(ArrayList<T> list, String id) ;

  protected void save(T data) {
    //    System.out.println("luc save "+ data.getContentIndex());
    queue.add(data);
  }

 

  protected void testConnection() {
    if(remote == null || remote.isEmpty()) return;
    try {
      //      if(client != null) client.abort();
      //      client = new DataClientService(remote);
      Header  header = new BasicHeader("action", "test.connection");
      Header header2 =new BasicHeader("username", username);
      byte [] bytes = client.post(handlerPath, "hi".getBytes(), header, header2);
      if(bytes.length > 0) connected = true;
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
      connected = false;      
    }
  }

  public Object load(String id, String action, Header...headers) {
    try {
      List<Header> lheaders = new ArrayList<Header>();
      lheaders.add(new BasicHeader("action", action));
      lheaders.add(new BasicHeader("username", username));
      if(headers != null) Collections.addAll(lheaders, headers);         
      headers = lheaders.toArray(new Header[0]);
      byte [] bytes = client.post(handlerPath, id.getBytes(), headers);//URLPath.REMOTE_DATA_HANDLER,
      if(bytes.length < 10) return null;
      bytes = new GZipIO().unzip(bytes);
      ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
      ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
      return objectInputStream.readObject();
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, "Load " + id+ " error: "+e.toString());
      return null;
    } 
  }

  public <E> E load(String id, String action, Class<E> clazz, Header...headers) {
    return load(0, id, action, clazz, headers);
  }

  private <E> E load(int time, String id, String action, Class<E> clazz, Header...headers) {
    try {
      List<Header> lheaders = new ArrayList<Header>();
      lheaders.add(new BasicHeader("action", action));
      lheaders.add(new BasicHeader("username", username));
      if(headers != null) Collections.addAll(lheaders, headers);         
      headers = lheaders.toArray(new Header[0]);
      return client.<E>readFromXML(clazz, handlerPath, id.getBytes(), headers);
//      return client.<E>readAsObject(handlerPath, id.getBytes(), headers);//URLPath.REMOTE_DATA_HANDLER,
    } catch (Exception e) {
      if(time >= 2) {
        LogService.getInstance().setMessage(e, "Load " + id+ " error: "+e.toString());
        return null;
      }
      return load(time+1, id, action, clazz, headers);
    } 
  }
  
  public void load(java.util.Collection<T> working) throws Throwable {
    File [] files = listTempFiles();

    if(files == null || files.length < 1) {
      if(queue.size() >= maxSync || System.currentTimeMillis() - lastSync >= 5*60*1000l) {
        int idx = 0;
        Iterator<T> iterator = queue.iterator();
        //      if(temp.size() > 0) System.out.println(" luc truoc "+ temp.size());
        while(iterator.hasNext()) {
          T data  = iterator.next();
          working.add(data);
          queue.remove(data);
          idx++;
          if(idx >= sizeOfWorking) break;
        }
      }
      //      if(working.size() > 0) System.out.println(" luc sau "+ temp.size());
      return;
    }

    load(files, working);
  }


}

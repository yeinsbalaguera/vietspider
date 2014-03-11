/***************************************************************************
 * Copyright 2001-2003 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.pool;


/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 13, 2007
 */
public final class WorkerBak<V, E extends Status<V>> /*extends Container*/ {
 //implements Runnable {
 
 /* private int id;
  private volatile long start;
  
  private long timeout = 2*60*1000;
  
  private Reque<Task<E>> reque; 
  private volatile Task<E> task;
  
  private Executor<?, V, E> executor;
  
  private volatile E value = null;
  
  protected Map<String, Object> handlers = new HashMap<String, Object>();
  
  public WorkerBak(Executor<?, V, E> executor, int index, Reque<Task<E>> queue_, long timeout){
    this.executor = executor;
    this.reque = queue_;
    this.timeout = timeout;
    
    start = -1; 
    id = index;
    
//    resources = new HashMap<String, Object>();
    
    reque.toFirst();
    while(reque.hasNext()){
      reque.pop().setWorker(this);
    }
  }
  
  public void startProcess() { start = 0; }
  
  public void run() {
    reque.toFirst();
    value = executor.nextElement();
    if(value == null) {
      start = -1;
      return;
    }
    start = System.currentTimeMillis();
    while(reque.hasNext()) {
      task = reque.pop();
      try {
        if(task.execute(value) == null) break;
      } catch (OutOfMemoryError e) {
//        e.printStackTrace();
        LogService.getInstance().setMessage(executor.getValue(), null, e.toString());
        Runtime.getRuntime().gc();
        break;
      } catch(Throwable exp) {
//        exp.printStackTrace();
        LogService.getInstance().setThrowable(executor.getValue(), exp, exp.getMessage());
        break;
      }
    }   
//    if(task != null && value != null) task.end(value);
    task = null;
    value = null;
    start = -1;
  }

//**********************************************data***********************************************
  
  public String getStatus(int groupId) {
    return value != null ?  value.buildStatus(groupId) : null; 
  }
  
  public boolean isFree() { return start < 0; }
  
  public boolean isTimeout() {
    return start > 0 && System.currentTimeMillis() - start >= timeout;
  }
  
  public void abort() { 
    if(task != null) task.abort();
  }
  
  @SuppressWarnings("unchecked")
  public <X extends Executor<?, V, E>> X getExecutor() { return (X)executor; }
  
  public int getId() { return id; }
  
  public E getValue() { return value; }

  public Task<E> getTask() { return task; }

  public long getStart() { return start; }
  
  //***************************************** handlers **********************************************
  
  @SuppressWarnings("unchecked")
  public <R> R getHandler(Class<R> clazz) { return (R)handlers.get(clazz.getName()); }
  @SuppressWarnings("unchecked")
  public <R> R getHandler(String key) { return (R)handlers.get(key); }
  
  public void putHandler(Object resource) { handlers.put(resource.getClass().getName(), resource); }
  public void putHandler(String key, Object resource) { handlers.put(key, resource); }
  
  public void removeHandler(Class<?> clazz) { handlers.remove(clazz.getName()); }
  public void removeHandler(String key) { handlers.remove(key); }*/
  
}

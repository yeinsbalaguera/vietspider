package org.vietspider.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Author : Tuan Nguyen
 */
@SuppressWarnings("serial")
public class InmemoryCache<K, V>  

  extends LinkedHashMap<K, CacheEntry<V>> implements Cache<K, V> {
  
  private String name_ ;
  private String label_ ;
  
  private int maxSize_ ;
  private long liveTime_ = -1;
  private int cacheHit_ ;
  private int cacheMiss_ ;
  
  private List<RemovePlugin<V>> removePlugins;   
  
  public InmemoryCache(String name, int maxSize) {
    maxSize_  = maxSize ;
    name_     = name ;
    removePlugins = new ArrayList<RemovePlugin<V>>();
  }
  
  public String getName() { return name_ ; }
  public void   setName(String s) {  name_ = s ; } 
  
  public String getLabel() { 
    if(label_ !=  null)  return label_;
    if(name_.length() <= 30 ) return name_;
    String shortLabel = name_.substring(name_.lastIndexOf(".") + 1) ; 
    setLabel(shortLabel) ;
    return  shortLabel;
  }
  
  public void setLabel(String name) {  label_ = name ; }
  
  public int  getCacheSize()  { return size() ; }
  
  public int  getMaxSize() { return maxSize_ ; }
  public void setMaxSize(int max) { maxSize_ = max ; }
  
  public long  getLiveTime() { return liveTime_ ; }
  public void  setLiveTime(long period)  { liveTime_ = period * 1000;} 
  
  synchronized public V getCachedObject(K name) {
    CacheEntry<V> info = super.get(name) ;
    if(info ==  null)  {
      cacheMiss_++ ;
      return null ; 
    }
    
    if(isExpire(info)) {
      for(RemovePlugin<V> removePlugin : removePlugins) {
        removePlugin.handle(info.get());
      }
      super.remove(name) ;
      cacheMiss_++ ;
      return null ;
    }
    cacheHit_++ ;
    return info.get() ;
  }
  
  synchronized public V removeCachedObject(K name)  {
    CacheEntry<V> ref =  super.remove(name) ;
    for(RemovePlugin<V> removePlugin : removePlugins) {
      removePlugin.handle(ref.get());
    }
    if(ref == null) return null;
    
    return isExpire(ref) ? null : ref.get() ;
  }
    
  synchronized public void putCachedObject(K name, V obj) {
    if(liveTime_ == 0) return ;
    long expireTime = -1 ;
    if(liveTime_ > 0) expireTime = System.currentTimeMillis() + liveTime_ ;
    CacheEntry<V> ref = createCacheEntry(expireTime, obj) ;
//    System.out.println("chuan bi dat data "+size());
    super.put(name, ref) ;
  }
  
  synchronized public void clearCache() throws Exception {
    super.clear() ;
  }
  
  public int getCacheHit()  { return cacheHit_  ;}
  
  public int getCacheMiss() { return cacheMiss_ ; }
  
  protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
    if(size() <= maxSize_ ) return false;
    try {
      CacheEntry<V> entry = remove(eldest.getKey()) ;
      for(RemovePlugin<V> removePlugin : removePlugins) {
        removePlugin.handle(entry.get());
      }
    } catch (Exception ex) {
      throw  new RuntimeException(ex) ;
    }
    return  true ;
  }
  
  @SuppressWarnings("all")
  private boolean isExpire(CacheEntry info) {
    //-1 mean cache live for ever
//    System.out.println(" check is cached "+ this + "  / "+ liveTime_);
    if(liveTime_ < 0) return false ;
    if(System.currentTimeMillis() > info.getExpireTime()) return true ;
    return false ;
  }
  
  protected CacheEntry<V> createCacheEntry(long expTime, V objToCache) {
    return new CacheEntry<V>(expTime, objToCache) ;
  }
  
  public void addRemovePlugin(RemovePlugin<V> plugin) {
    removePlugins.add(plugin);
  }
  
  public void removeRemovePlugin(RemovePlugin<V> plugin) {
    removePlugins.remove(plugin);
  }
}
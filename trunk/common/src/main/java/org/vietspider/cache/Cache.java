package org.vietspider.cache;

/**
 * Author : Tuan Nguyen
 */
public interface Cache<K, V> {
  public String getName() ;
  public void   setName(String name) ;

  public String getLabel() ;
  public void   setLabel(String s) ;

  public V getCachedObject(K name) throws Exception ;
  public V removeCachedObject(K name) throws Exception ;
  public void   putCachedObject(K name, V obj) throws Exception ;
  public void   clearCache() throws Exception ;
  public int  getCacheSize() ;

  public int  getMaxSize() ;
  public void setMaxSize(int max) ;

  public long  getLiveTime() ;
  public void  setLiveTime(long period) ;

  public int getCacheHit() ;
  public int getCacheMiss() ;
}

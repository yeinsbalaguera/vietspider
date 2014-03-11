package org.vietspider.cache;

import java.io.Serializable;
import java.lang.ref.SoftReference;

/**
 * Author : Tuan Nguyen
 */
public class CacheEntry<V> extends SoftReference<V> implements Serializable {
  
  private long expireTime_ ;
  
  public CacheEntry(long expireTime, V o) {
    super(o) ;
    expireTime_ = expireTime ;
  }
  
  public long getExpireTime() { return expireTime_ ;  }
}

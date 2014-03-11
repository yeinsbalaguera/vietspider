package org.vietspider.nlp.tpt;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;

import org.vietspider.common.io.LogService;

//http://code.google.com/p/jdbm2/

class NlpTptStorage {

  private final static int MAX_RECORD = 100;


  private RecordManager recman;
  private PrimaryStoreMap<Long, NlpTptModel> main;

  private SecondaryTreeMap<String, Long, NlpTptModel> idIndex;
  private SecondaryTreeMap<String, Long, NlpTptModel> telephoneIndex;
  private SecondaryTreeMap<String, Long, NlpTptModel> mobileIndex;
  private SecondaryTreeMap<String, Long, NlpTptModel> emailIndex;
  private SecondaryTreeMap<String, Long, NlpTptModel> actionObjectIndex;
  private SecondaryTreeMap<String, Long, NlpTptModel> priceIndex;
  private SecondaryTreeMap<String, Long, NlpTptModel> areaIndex;
  private SecondaryTreeMap<String, Long, NlpTptModel> dateIndex;

  private long lastCommit = System.currentTimeMillis();
  private int counter = 0;
  //	
  private boolean defrag = false;

  public NlpTptStorage(File file) throws Exception {
    recman = RecordManagerFactory.createRecordManager(file.getAbsolutePath());
    //class jdbm.helper.PrimaryStoreMapImpl
    main = recman.storeMap("tpt");

    idIndex = main.secondaryTreeMap("idIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getId();
      }					
    });

    telephoneIndex = main.secondaryTreeMap("telephoneIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getTelephone();
      }					
    });

    mobileIndex = main.secondaryTreeMap("mobileIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getMobile();
      }         
    });

    emailIndex = main.secondaryTreeMap("emailIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getEmail();
      }					
    });

    actionObjectIndex = main.secondaryTreeMap("actionObjectIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getAction_object();
      }         
    });

    priceIndex = main.secondaryTreeMap("priceIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getPrice();
      }         
    });

    areaIndex = main.secondaryTreeMap("areaIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getArea();
      }         
    });

    dateIndex = main.secondaryTreeMap("dateIndex",
        new SecondaryKeyExtractor<String, Long, NlpTptModel>() {
      public String extractSecondaryKey(Long key, NlpTptModel value) {
        return value.getDate();
      }         
    });
  }

  public long size() { return main.size(); }

  public NlpTptModel getById(String id) {
    Iterable<Long> iterable = idIndex.get(id);
    if(iterable == null) return null;
    Iterator<Long> keyIterator = iterable.iterator();
    if(!keyIterator.hasNext()) return null;
    Long key = keyIterator.next();
    NlpTptModel model = main.get(key) ;
    model.setStorageId(key);
    return model;
  }

  public void deleteExpire(String date) {
    deleteExpire(date, 0);
  }

  private void deleteExpire(String date, int time) {
    if(time >= 3) return;
    Iterable<Long> iterable = dateIndex.get(date);
    if(iterable == null) return;
    Iterator<Long> keyIterator = iterable.iterator();
    try {
      while(keyIterator.hasNext()) {
        Long key = keyIterator.next();
        if(key == null) continue;
//        NlpTptModel model = main.get(key) ;
//        if(model != null) {
//          System.out.println(model.getId() + " : "+ key);
//        }
        try {
          main.remove(key);
        } catch (Exception e) {
//          LogService.getInstance().setThrowable(e);
//          LogService.getInstance().setMessage(e, e.toString());
        }
        keyIterator.remove();
      }
    } catch (ConcurrentModificationException e) {
      deleteExpire(date, time+1);
    }
  }

  public List<NlpTptModel> getByTelephone(String phone) {
    return this.<String>get(phone, telephoneIndex);
  }

  public List<NlpTptModel> getByMobile(String mobile) {
    return this.<String>get(mobile, mobileIndex);
  }

  public List<NlpTptModel> getByEmail(String email) {
    return this.<String>get(email, emailIndex);
  }

  public List<NlpTptModel> getByActionObject(String ao) {
    return this.<String>get(ao, actionObjectIndex);
  }

  public List<NlpTptModel> getByPrice(String price) {
    return this.<String>get(price, priceIndex);
  }

  public List<NlpTptModel> getByArea(String area) {
    return this.<String>get(area, areaIndex);
  }

  public List<NlpTptModel> getByDate(String date) {
    return this.<String>get(date, dateIndex);
  }

  private <T> List<NlpTptModel> get(String fieldValue, 
      SecondaryTreeMap<T, Long, NlpTptModel> index) {
    List<NlpTptModel> list = new ArrayList<NlpTptModel>();

    Iterable<Long> iterable = index.get(fieldValue);
    if(iterable == null) return list;

    Iterator<Long> keyIterator = iterable.iterator();
    while(keyIterator.hasNext()) {
      Long key = keyIterator.next();
      if(key == null) continue;
      NlpTptModel model = main.get(key);
      if(model == null) continue;
      model.setStorageId(key);
      list.add(model);
    }

    return list;
  }

  public void write(NlpTptModel model) throws Throwable {
    try {
      main.putValue(model);
    } catch (Error e) {
      defrag(model);
      throw e;
    }
    counter++;
  }

  public void remove(NlpTptModel model) throws Exception {
    if(model.getStorageId() == null) {
      throw new Exception ("Storage id not found!");
    }
    main.remove(model.getStorageId());
    counter++;
  }

  void commit() throws Throwable {
    //	  System.out.println("commit counter "+ counter);
    counter = 0; 
    recman.commit();
    lastCommit = System.currentTimeMillis();
  }

  void defrag(final NlpTptModel model) {
    new Thread() {
      public void run() {
        defrag = true;
        try {
          recman.defrag();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
        if(model != null)  main.putValue(model);
        defrag = false;
      }
    }.start();

  }

  boolean isCommit() {
    if(counter < 1 || defrag) return false;
    if(counter >= MAX_RECORD) return true;
    return System.currentTimeMillis() - lastCommit >= 15*60*1000l;
  }

  public void close() throws Exception {
    counter = 0; 
    try {
      recman.commit();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    recman.close();
  }

  public void test() {
    Collection<NlpTptModel> list = main.values();
    //      System.out.println(" === > "+list.size());
    int counter = 0;
    for(NlpTptModel model : list) {
      System.out.println("==========================");
      System.out.println("id: " +  model.getId());
      System.out.println("telephone: " + model.getTelephone());
      System.out.println("mobile: " + model.getMobile());
      System.out.println("address: " + model.getAddress());
      System.out.println("email: " + model.getEmail());
      System.out.println("price: " + model.getPrice());

      counter++;
      if(counter >= 100) break;
    }
  }

}

package sample;

import org.apache.jcs.JCS;

public class BookManager {
  
  private static BookManager instance;
//  private static int checkedOut = 0;
  private static JCS bookCache;

  private BookManager() {
    try {
      bookCache = JCS.getInstance("bookCache");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static BookManager getInstance() {
    synchronized (BookManager.class) {
      if (instance == null) {
        instance = new BookManager();
      }
    }

//    synchronized (instance) {
//      instance.checkedOut++;
//    }

    return instance;
  }

  public Book getBookVObj(int id) {
    System.out.println(hashCode() + ": BookVObj" + id);
    return (Book) bookCache.get("BookVObj" + id);
  }

  public int storeBookVObj(Book vObj) {
    try {
      if (vObj.bookId != 0) {
        bookCache.remove("BookVObj" + vObj.bookId);
      }
//      System.out.println(hashCode() + " put id " + "BookVObj" + vObj.bookId);
      bookCache.put("BookVObj" + vObj.bookId, vObj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return vObj.bookId;
  }

}

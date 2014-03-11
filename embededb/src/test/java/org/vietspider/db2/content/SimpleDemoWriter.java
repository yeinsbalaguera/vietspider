package org.vietspider.db2.content;

import org.xtreemfs.babudb.BabuDBFactory;
import org.xtreemfs.babudb.api.BabuDB;
import org.xtreemfs.babudb.api.DatabaseManager;
import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.api.database.DatabaseInsertGroup;
import org.xtreemfs.babudb.api.exception.BabuDBException;
import org.xtreemfs.babudb.config.BabuDBConfig;
import org.xtreemfs.babudb.log.DiskLogger.SyncMode;

public class SimpleDemoWriter {

  public static void main(String[] args) throws InterruptedException {
    BabuDB databaseSystem = null;
    try {           
      //start the database
      databaseSystem = BabuDBFactory.createBabuDB(
          new BabuDBConfig("D:\\Temp\\babudb\\myDatabase\\",
              "D:\\Temp\\babudb\\myDatabase/", 2, 
              1024 * 1024, 5 * 60, SyncMode.FDATASYNC, 
              0, 0, false, 16, 1024 * 1024 * 5));
      DatabaseManager dbm = databaseSystem.getDatabaseManager();

      //create a new database called myDB
                  dbm.createDatabase("myDB", 2);
      Database db = dbm.getDatabase("myDB");

      //create an insert group for atomic inserts
//      DatabaseInsertGroup group = db.createInsertGroup();

      //insert one key in each index
      long total = 1000000;
      long from = 1000000;
      for(long i = 0; i < total; i++) {
        String key = "Key" + String.valueOf(i);
        String value = "Value" + String.valueOf(i);
        db.singleInsert(1, key.getBytes(), value.getBytes(), null);
//        group.addInsert(1, key.getBytes(), value.getBytes());
//        //            group.addDelete(1, "Key34".getBytes());
//        //            group.addInsert(1, "Key2".getBytes(), "Value2".getBytes());
//
        if(i > 0 && i%100 == 0) {
          System.out.println(" ta thay co "+ i);
//          db.insert(group, null);
//          group = db.createInsertGroup();
        }
        
        if(i>0 && i%2000 == 0) {
          Thread.sleep(10*1000l);
        }
        //and execute group insert
      }
      

      //now do a lookup
      //            byte[] result = db.lookup(0, "Key1".getBytes(),null).get();

      //create a checkpoint for faster start-ups
     
    } catch (BabuDBException ex) {
      ex.printStackTrace();
    } finally {
      try {
        databaseSystem.getCheckpointer().checkpoint();
        databaseSystem.shutdown();
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
  }  
}
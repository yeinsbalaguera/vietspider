package org.vietspider.db2.content;

import org.xtreemfs.babudb.BabuDBFactory;
import org.xtreemfs.babudb.api.BabuDB;
import org.xtreemfs.babudb.api.DatabaseManager;
import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.api.database.DatabaseInsertGroup;
import org.xtreemfs.babudb.api.database.DatabaseRequestResult;
import org.xtreemfs.babudb.api.exception.BabuDBException;
import org.xtreemfs.babudb.config.BabuDBConfig;
import org.xtreemfs.babudb.log.DiskLogger.SyncMode;

public class SimpleDemoReader {

    public static void main(String[] args) throws InterruptedException {
      BabuDB databaseSystem = null;
        try {           
            //start the database
            databaseSystem = BabuDBFactory.createBabuDB(
                new BabuDBConfig("D:\\Temp\\babudb\\myDatabase\\",
                    "D:\\Temp\\babudb\\myDatabase/", 2, 
                    1024 * 1024 * 16, 5 * 60, SyncMode.SYNC_WRITE, 
                    0, 0, false, 16, 1024 * 1024 * 512));
            DatabaseManager dbm = databaseSystem.getDatabaseManager();
                        
            //create a new database called myDB
            Database db = dbm.getDatabase("myDB");
            
            DatabaseRequestResult<byte[]> result = null; 
            byte [] value;
//            = db.lookup(0, "Key1".getBytes(), null);
//            byte[] value = result.get();
//            System.out.println(new String(value));
            long start = System.currentTimeMillis();
            result = db.lookup(1, "Key799034".getBytes(), null);
            value = result.get();
            if(value != null) {
              System.out.println(new String(value));
            }
            long end = System.currentTimeMillis();
            System.out.println(" time " +  (end - start));
            
            
        } catch (BabuDBException ex) {
            ex.printStackTrace();
        } finally {
          try {
          //shutdown database
            databaseSystem.shutdown();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
    }  
}


import org.vietspider.startup.StartClient;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 28, 2007
 */
public class vietspiderclient {
  
  public static void main(String[] args) {    
    try{      
      new StartClient();
    }catch(Exception exp){
      exp.printStackTrace();
    }
    System.exit(0);  
  }
  
}

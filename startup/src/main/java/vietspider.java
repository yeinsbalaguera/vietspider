

import org.vietspider.startup.StartAll;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 28, 2007
 */
public class vietspider {
  
  public vietspider() {
    
    try {      
      new StartAll();
    }catch(Exception exp){
      exp.printStackTrace();
    }
    System.exit(0); 
  }
  
  public static void main(String[] args) {    
    new vietspider(); 
  }

}

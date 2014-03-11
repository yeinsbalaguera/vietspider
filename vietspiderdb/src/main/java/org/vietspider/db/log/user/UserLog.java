package org.vietspider.db.log.user;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.SetterMap;

@NodeMap("action-user")
public class UserLog {
  
  @NodeMap("user-name")
  private String userName;
  @NodeMap("action")
  private int action;
  @NodeMap("time")
  private long time = -1;
  
  public UserLog() {
  }

  public UserLog(String userName, int action) {
    this.userName = userName;
    this.action = action;
  }
  
  public String getUserName() {return userName;}
  public void setUserName(String userName) { this.userName = userName;}
  
  public int getAction() { return action; }
  public void setAction(int action) { this.action = action; }
  
  @Override
  public String toString() {
    return userName + " "+ action +" article " ;
  }

  @GetterMap("time")
  public long getTime() {
    if(time == -1) time = System.currentTimeMillis();
    return time; 
  }
  @SetterMap("time")
  public void setTime(long time) { this.time = time;  }

  
}

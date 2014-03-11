/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.tpt;

import java.io.Serializable;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 19, 2011  
 */
class NlpTptModel implements Serializable {
  
  private static final long serialVersionUID = 10L;

  private Long storageId;
  private String id;
  private String telephone;
  private String mobile;
  private String address;
  private String email;
  private String area;
  private String price;
  private String action_object;
  private String date;
  private String title;
  
  public NlpTptModel(String id) {
    this.id = id;
  }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public Long getStorageId() { return storageId; }
  public void setStorageId(Long storageId) { this.storageId = storageId; }

  public String getTelephone() { return telephone; }
  public void setTelephone(String phone) { this.telephone = phone; }
  
  public String getMobile() { return mobile; }
  public void setMobile(String mobile) { this.mobile = mobile; }
  
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  
  public String getArea() { return area; }
  public void setArea(String area) { this.area = area; }
  
  public String getPrice() { return price; }
  public void setPrice(String price) { this.price = price; }
  
  public String getAction_object() { return action_object; }
  public void setAction_object(String action_object) { this.action_object = action_object; }
  
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public boolean isEmpty() {
    return !(mobile != null 
    || email != null 
    || telephone != null
    || price != null
    || area != null
    || title != null
    || action_object != null);
  }
  
  public boolean isDuplicate(NlpTptModel model) {
    if(address != null 
        && !address.equals(model.address)) return false;
    
    if(area != null
        && price != null) {
      if(area.equals(model.area)
          && price.equals(model.price)) {
//        System.out.println(" master " + model.getId() + " : " + id);
        return true;
      }
    }
    if(!title.equalsIgnoreCase(model.title)) return false;
    return true;
    
  }
}

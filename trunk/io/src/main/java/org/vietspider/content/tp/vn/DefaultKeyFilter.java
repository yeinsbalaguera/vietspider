/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.tp.vn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.dict.WordIndex2;
import org.vietspider.index.analytics.PhraseData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 28, 2009  
 */
public class DefaultKeyFilter implements KeyFilter {

  /*private final static TreeSet<String> IGNORES = new TreeSet<String>(new Comparator<String>() {

    public int compare(String o1, String o2) {
      return o1.compareToIgnoreCase(o2);
    }
  });*/

  protected WordIndex2 wordIndex2;

  public DefaultKeyFilter() {
    loadIgnore();
  }

  public boolean isKey(PhraseData phrase) {
    String pattern  = phrase.getValue();
//    System.out.println(pattern + " / "+ wordIndex.contains(pattern));
    if(wordIndex2.contains(pattern.toLowerCase())) return false;
    int index = 0;
    int length = pattern.length();
    if(length  < 2) return false;

    if(!Character.isLetterOrDigit(pattern.charAt(index))) return false;

    while(index < length) {
      if(Character.isUpperCase(pattern.charAt(index))) return true;
      index++;
    }
    return false;
//        return phrase.isNoun();
  }

  private void loadIgnore() {
    File folder = UtilFile.getFolder("system/dictionary/vn/");
    File file = new File(folder, "default.ignore.key.tp");
    File txtFile = new File(folder, "default.ignore.word.txt");
//    System.out.println(file.lastModified() + "  : "+txtFile.lastModified());
    if(!file.exists() || file.length() < 1 
        || (txtFile.exists() && txtFile.lastModified() > file.lastModified())) {
      wordIndex2 = new WordIndex2(0);
      try {
        String text = new String(RWData.getInstance().load(txtFile), Application.CHARSET);
        String [] values = text.split(",");
        for(String value : values) {
          value = value.trim();
          if(value.isEmpty()) continue;
//          System.out.println(" === >"+ value);
          wordIndex2.add(value.toLowerCase());
        }
//        TreeSet<WordIndex2>  aaa = wordIndex2.getChildren();
//        Iterator<WordIndex2> iterator = aaa.iterator();
//        while(iterator.hasNext()) {
//          System.out.println(iterator.next().getCode());
//        }
        saveIndex();
      }catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
          /*"Vietnamnet","GMT","VN","TimeString", "E mail", "tp",
          "Việt Nam", "người việt", 
          "Ảnh","TT","Công","Giám","HCM","Hà Nội","TNHH","SG","VNN","VNExpress",
          "TP HCM","Trung Quốc","Mỹ","Nhật Bản","DN","TPHCM","TP.HCM","Nga","Anh","VCK",
          "CLB","CĐV", "CP", "NĐ", "ND", "TTg", "Quyết định", "Nghị quyết",
          "Hồ Chí Minh", "HCM", "tp hồ chí minh", "sài gòn",
          "HN","TP Hà Nội","UBND","UBND TP",
          "Đoàn","Đảng",
          "Ban","Chiều","Học","Hôm","Ngày","and","or", "nhà nước", "việt",
          "chính phủ", "trung ương", "thủ tướng", "đề án", "thành phố","thủ đô", "thường trực",
          "phó chủ tịch", "Quốc hội"
          , "Công ty", "Cổ phần", "cập nhật", "Chủ tịch"
          , "hoa kỳ", "liên hợp quốc", "washington", "tổng thống", "Mỹ", "USD" 
          , "lao động", "lđ", "cty", "Vietnam+"*/

          //        "An Giang","Bà Rịa-Vũng Tàu","Bạc Liêu","Bắc Cạn","Bắc Giang","Bắc Ninh",
          //       "Bến Tre","Bình Dương","Bình Định","Bình Phước","Bình Thuận","Cà Mau",
          //       "Cao Bằng","Cần Thơ","Đà Nẵng","Đắk Lắk","Đắk Nông","Điện Biên","Đồng Nai",
          //       "Đồng Tháp","Gia Lai","Hà Giang","Hà Nam","Hà Nội","Hà Tĩnh","Hải Dương",
          //       "Hải Phòng","Hậu Giang","Hòa Bình","Hồ Chí Minh","Hưng Yên","Khánh Hoà",
          //       "Kiên Giang","Kon Tum","Lai Châu","Lạng Sơn","Lào Cai","Lâm Đồng","Long An",
          //       "Nam Định","Nghệ An","Ninh Bình","Ninh Thuận","Phú Thọ","Phú Yên","Quảng Bình",
          //       "Quảng Nam","Quảng Ngãi","Quảng Ninh","Quảng Trị","Sóc Trăng","Sơn La","Tây Ninh",
          //       "Thái Bình","Thái Nguyên","Thanh Hoá","Thừa Thiên-Huế","Tiền Giang","Trà Vinh",
          //       "Tuyên Quang","Vĩnh Long","Vĩnh Phúc","Yên Bái",

          //       "thái lan","đài loan"
      return;
    }

    try {
      byte [] bytes = RWData.getInstance().load(file);
      ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(bytes));
      wordIndex2 = (WordIndex2) objectInput.readObject();
      objectInput.close();
      return;
    } catch (Exception e) {
      file.delete();
      LogService.getInstance().setThrowable(e);
    }
   
  }

  public void saveIndex() {
    File folder = UtilFile.getFolder("system/dictionary/vn/");
    saveIndex(new File(folder, "default.ignore.key.tp"));
  }
  
  protected void saveIndex(File file) {
    try {
      ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
      objectOutputStream.writeObject(wordIndex2);
      objectOutputStream.close();
      if(file.exists()) file.delete();
      RWData.getInstance().save(file, bytesOutput.toByteArray());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}

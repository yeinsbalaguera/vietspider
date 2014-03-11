/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 24, 2009  
 */
public class CityCodesForSearch {

  //http://192.168.1.32:4525/vietspider/DOMAIN/11/25.09.2009
  private final static CityCodesForSearch instance = new CityCodesForSearch();

  public final static CityCodesForSearch getInstance() { return instance; }

  private NameCode [] codes = new NameCode[0];

  private String [] names = new String[0];
  
  private Pattern pattern = Pattern.compile("ở|tại");

  public CityCodesForSearch() {
    List<String[]> cities = new ArrayList<String[]>();
    cities.add(new String[]{
        "thành phố hồ chí minh", "thanh pho ho chi minh", "hồ chí minh",
        "hochiminh", "ho chi minh", "tp.hcm", "tp hcm", "hcm", "tphcm", "hcmc",
        "sài gòn"
    });
    cities.add(new String[]{
        "hà nội", "ha noi", "hn", "hanoi"
    });
    cities.add(new String[]{
        "hải phòng", "hai phong", "haiphong"
    });

    cities.add(new String[]{
        "cần thơ", "can tho", "cantho"

    });
    cities.add(new String[]{
        "đà nẵng", "da nang", "danang"
    });
    cities.add(new String[]{
        "an giang", "angiang"
    });
    cities.add(new String[]{
        "bà rịa-vũng tàu", "ba ria-vung tau", 
        "br-vt", "bà rịa", "vũng tàu", "vung tau", "bà rịa vũng tàu", "bariavungtau"
    });
    cities.add(new String[]{
        "bạc liêu", "bac lieu", "baclieu"
    });
    cities.add(new String[]{
        "bắc kạn", "bac kan", "backan", "bắc cạn"
    });
    cities.add(new String[]{
        "bắc giang", "bac giang", "bacgiang"
    });
    cities.add(new String[]{
        "bắc ninh", "bac ninh", "bacninh"
    });
    cities.add(new String[]{
        "bến tre", "ben tre", "bentre"
    });

    cities.add(new String[]{
        "bình dương", "binh duong", "binhduong"
    });

    cities.add(new String[]{
        "bình định", "binh dinh", "binhdinh"
    });
    cities.add(new String[]{
        "bình phước", "binh phuoc", "binhphuoc"
    });
    cities.add(new String[]{ "bình thuận", "binh thuan", "binhthuan"});

    cities.add(new String[]{"cà mau", "ca mau", "camau" });
    cities.add(new String[]{"cao bằng", "cao bang", "caobang"});
    cities.add(new String[]{"đắc lắk", "dak lak", "daklak", "đắc lắc"});
    cities.add(new String[]{"đắc nông", "dak nong", "đắc nông", "daknong"});
    cities.add(new String[]{"điện biên", "dien bien", "dienbien"});
    cities.add(new String[]{"đồng nai", "dong nai", "dongnai"});
    cities.add(new String[]{"đồng tháp", "dong thap", "dongthap"});
    cities.add(new String[]{"gia lai", "gialai"});
    cities.add(new String[]{"hà giang", "ha giang", "hagiang"});
    cities.add(new String[]{"hà nam", "ha nam", "hanam"});
    cities.add(new String[]{"hà tĩnh", "ha tinh", "hatinh"});
    cities.add(new String[]{"hải dương", "hai duong", "haiphong"});
    cities.add(new String[]{"hậu giang", "hau giang", "haugiang"});
    cities.add(new String[]{"hòa bình", "hoa binh", "hoabinh"});
    cities.add(new String[]{"hưng yên", "hung yen", "hungyen"});
    cities.add(new String[]{"khánh hòa", "khanh hoa", "khanhhoa"});
    cities.add(new String[]{"kiên giang", "kien giang", "kiengiang"});
    cities.add(new String[]{"kon tum", "kontum"});
    cities.add(new String[]{"lai châu", "lai chau", "laichau"});
    cities.add(new String[]{"lâm đồng", "lam dong", "lamdong"});
    cities.add(new String[]{"lạng sơn", "lang son", "langson"});
    cities.add(new String[]{"lào cai", "lao cai", "laocai"});
    cities.add(new String[]{"long an", "longan"});
    cities.add(new String[]{"nam định", "nam dinh", "namdinh"});
    cities.add(new String[]{"nghệ an", "nghe an", "nghean"});
    cities.add(new String[]{"ninh bình", "ninh binh", "ninhbinh"});
    cities.add(new String[]{"ninh thuận", "ninh thuan", "ninhthuan"});
    cities.add(new String[]{"phú thọ", "phu tho", "phutho"});
    cities.add(new String[]{"phú yên", "phu yen", "phuyen"});
    cities.add(new String[]{"quảng bình", "quang binh", "quangbinh"});
    cities.add(new String[]{"quảng nam", "quang nam", "quangnam"});
    cities.add(new String[]{"quảng ngãi", "quang ngai", "quangngai"});
    cities.add(new String[]{"quảng ninh", "quang ninh", "hạ long", "cẩm phả"});
    cities.add(new String[]{"quảng trị", "quang tri", "quangtri"});
    cities.add(new String[]{"sóc trăng", "soc trang", "soctrang"});
    cities.add(new String[]{"sơn la", "son la", "sonla"});
    cities.add(new String[]{"tây ninh", "tay ninh", "tayninh"});
    cities.add(new String[]{"thái bình", "thai binh", "thaibinh"});
    cities.add(new String[]{"thái nguyên", "thai nguyen", "thainguyen"});
    cities.add(new String[]{"thanh hóa", "thanh hoa", "thanhhoa"});
    cities.add(new String[]{"thừa thiên-huế", "thua thien-hue", 
        "thừa thiên huế", "thua thien", "thuathienhue"});
    cities.add(new String[]{"tiền giang", "tien giang", "tiengiang"});
    cities.add(new String[]{"trà vinh", "tra vinh", "travinh"});
    cities.add(new String[]{"tuyên quang", "tuyen quang", "tuyenquang"});
    cities.add(new String[]{"vĩnh long", "vinh long", "vinhlong"});
    cities.add(new String[]{"vĩnh phúc", "vinh phuc", "vinhphuc"});
    cities.add(new String[]{"yên bái", "yen bai", "yenbai"});

    codes = new NameCode[cities.size()];
    names = new String[cities.size()];
    for(int i = 0; i < cities.size(); i++) {
      codes[i] = new NameCode(cities.get(i));
      names[i] = cities.get(i)[0];
    }

  }

  public String[] getNames() { return names; }
  
  public String[] match(String text) {
      int from  = 0;
      String name = null;
      char [] chars = text.toCharArray();
      for(int i = 0; i < codes.length; i++) {
        int[] index = codes[i].isName2(chars, from);
        if(index == null) continue;
        name = codes[i].getName(); 
        from = index[1];
        StringBuilder builder = new StringBuilder(text.substring(0, index[0]));
        if(index[1]+1 < text.length())  {
          builder.append(text.substring(index[1]+1, text.length()));
        }
        text = builder.toString();
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()) text = matcher.replaceAll("");
        break;
      }
      if(name  == null) return null;
      if(text == null || text.trim().length() < 1) return null;
      int idx = 0;
      while(idx < text.length()) {
        if(Character.isLetterOrDigit(text.charAt(idx)))  return new String[]{name, text};
        idx++;
      }
      return null;
  }

  public static void main(String[] args) {
    String [] values = CityCodesForSearch.getInstance().match("bán nhà hcm re");
    if(values == null) {
      System.out.println("not found");
    } else {
      System.out.println(values[0]);
      System.out.println(values[1]);
    }
  }

}

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 24, 2009  
 */
public class CityCodes {

  //http://192.168.1.32:4525/vietspider/DOMAIN/11/25.09.2009
  private final static CityCodes instance = new CityCodes();

  public final static CityCodes getInstance() { return instance; }

  private NameCode [] codes = new NameCode[0];

  private NameCode [] codes2 = new NameCode[0];

  private String [] names = new String[0];

  public CityCodes() {
    List<String[]> cities = new ArrayList<String[]>();
    List<String[]> cities2 = new ArrayList<String[]>();
    cities.add(new String[]{
        "thành phố hồ chí minh", "thanh pho ho chi minh", "hồ chí minh",
        "hochiminh", "ho chi minh", "tp.hcm", "tp hcm", "hcm", "tphcm", "hcmc",
        "sài gòn", 
        "quận 1", "q.1", "quan 1", "quan1", "q1",
        "quận 2", "q.2", "quan 2", "quan2", "q2",
        "quận 3", "q.3", "quan 3", "quan3", "q3",
        "quận 4", "q.4", "quan 4", "quan4", "q4",
        "quận 5", "q.5", "quan 5", "quan5", "q5",
        "quận 6", "q.6", "quan 6", "quan6", "q6",
        "quận 7", "q.7", "quan 7", "quan7", "q7",
        "quận 8", "q.8", "quan 8", "quan8", "q8",
        "quận 9", "q.9", "quan 9", "quan9", "q9",
        "quận 10", "q.10", "quan 10", "quan10", "q10",
        "quận 11", "q.11", "quan 11", "quan11", "q11",
        "quận 12", "q.12", "quan 12", "quan12", "q12",
        "gò vấp", "go vap", "tân bình", "tan binh", "tân phú", "tan phu",
        "bình thạnh", "binh thanh", "phú nhuận", "phu nhuan", "thủ đức", "thu duc",
        "bình tân", "binh tan", "củ chi", "cu chi", "hóc môn", "hoc mon", 
        "bình chánh", "binh chanh", "nhà bè", "nha be", "cần giờ", "can gio"
    });
    cities.add(new String[]{
        "hà nội", "ha noi", "hn", "hanoi",
        "hoàn kiếm", "hoan kiem", "cầu giấy", "cau giay", "ba đình", 
        "đống đa", "dong da", "hà đông",
        "hai bà trưng", "hoàng mai", "long biên", "tây hồ",  "thanh xuân", 
        "sơn tây", "ba vì", "chương mỹ", "đan phượng", "đông anh", "gia lâm", 
        "hoài đức", "mê linh", "mỹ đức", "sóc sơn", "phú xuyên", "phúc thọ",
        "quốc oai", "sóc sơn", "thạch thất", "thanh oai", "thanh trì", "thường tín", 
        "từ liêm", "ứng hòa"
    });
    cities2.add(new String[]{
        "hà nội", "biển 29", "biển 30", "hồ tây"
    });
    cities.add(new String[]{
        "hải phòng", "hai phong", "haiphong",
        "dương kinh", "hải an", "bạch long vĩ",
        "đồ sơn", "hồng bàng", "kiến an", "lê chân", "ngô quyền", "an dương", "an lão",
        "cát hải", "kiến thụy", "thủy nguyên", "tiên lãng", "vĩnh bảo"
    });

    cities.add(new String[]{
        "cần thơ", "can tho", "cantho", "tpct",
        "ninh kiều", "ninh kieu", 
        "bình thủy", "binh thu", "cái răng", "ô môn", "thốt nốt", "phong điền",
        "cờ đỏ", "thới lai", "vĩnh thạnh"

    });
    cities.add(new String[]{
        "đà nẵng", "da nang", "danang", 
        "hải châu", "thanh khê", "sơn trà", "ngũ hành sơn",
        "liên chiểu", "cẩm lệ", "hòa vang"
    });
    cities.add(new String[]{
        "an giang", "angiang", 
        "an phú", "châu phú", "chợ mới", "phú tân", "thoại sơn",
        "tịnh biên", "trí tôn"
    });
    cities.add(new String[]{
        "bà rịa-vũng tàu", "ba ria-vung tau", 
        "br-vt", "bà rịa", "vũng tàu", "vung tau", "bà rịa vũng tàu", "bariavungtau",
        "long điền", "đất đỏ", "tân thành", "châu đức", "xuyên mộc", "côn đảo"
    });
    cities.add(new String[]{
        "bạc liêu", "bac lieu", "baclieu",
        "phước long", "hồng dân", "vĩnh lợi", "giá rai", "đông hải"
    });
    cities.add(new String[]{
        "bắc kạn", "bac kan", "backan", "bắc cạn",
        "ba bể", "bạch thông", "chợ đồn", "chợ mới", "na rì", "yên lạc", "ngân sơn", "pác nặm"
    });
    cities.add(new String[]{
        "bắc giang", "bac giang", "bacgiang", 
        "yên thế", "tân yên", "lục ngạn", "hiệp hòa", "lạng giang", "sơn động",
        "lục nam", "việt yên", "yên dũng"
    });
    cities.add(new String[]{
        "bắc ninh", "bac ninh", "bacninh",
        "từ sơn", "gia bình", "thuận thành", "lương tài", "quế võ"
    });
    cities.add(new String[]{
        "bến tre", "ben tre", "bentre",
        "ba tri", "bình đại", "châu thành", "chợ lách", "giồng trôm", 
        "mỏ cày bắc", "mỏ cày nam", "thạch phú"
    });

    cities.add(new String[]{
        "bình dương", "binh duong", "binhduong",
        "thủ dầu một", "bến cát", "dầu tiếng", "tân uyên", "phú giáo", "thuận an", "dĩ an"
    });

    cities.add(new String[]{
        "bình định", "binh dinh", "binhdinh", 
        "qui nhơn", "an lão", "an nhơn", "hoài ân", "hoài nhơn", "phù cát",
        "phù mỹ", "tuy phước", "tây sơn", "vân canh", "vĩnh thạnh"
    });
    cities.add(new String[]{
        "bình phước", "binh phuoc", "binhphuoc",
        "đồng xoài", "phước long", "bình long",
        "bù đăng", "bù đốp", "bù gia mập", "chơn thành", "đồng phú", "hớn quản", "lộc ninh"
    });
    cities.add(new String[]{
        "bình thuận", "binh thuan", "binhthuan",
        "phan thiết", "la gi", "tuy phong", "bắc bình", "hàm thuận bắc",
        "hàm thuận nam", "tánh linh", "hàm tân", "đức linh", "phú quý",
    });

    cities.add(new String[]{
        "cà mau", "ca mau", "camau",
        "đầm dơi", "ngọc hiển", "cái nước", "trần văn thời", "u minh", "thới bình",
        "năm căn", "phú tân"
    });
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

    codes2 = new NameCode[cities2.size()];
    for(int i = 0; i < cities2.size(); i++) {
      codes2[i] = new NameCode(cities2.get(i));
    }
  }

  public String[] getNames() { return names; }
  
  public String matchQuery(String pattern) {
    if(pattern.indexOf("ở") > -1 || pattern.indexOf("tại") > -1) {
      int from  = 0;
      String name = null;
      char [] chars = pattern.toCharArray();
      for(int i = 0; i < codes.length; i++) {
        int index = codes[i].isName(chars, from);
        if(index < 0) continue;
        name = codes[i].getName(); 
        from = index;
      }

      return name;
    }
    return null;
  }

  public String match(char[] chars) {
    int from  = 0;
    String name = null;
    for(int i = 0; i < codes.length; i++) {
      int index = codes[i].isName(chars, from);
      if(index < 0) continue;
      name = codes[i].getName(); 
      from = index;
    }

    return name;
  }

  public String match2(char[] chars) {
    for(int i = 0; i < codes2.length; i++) {
      if(codes2[i].isName(chars, 0) > -1) return codes2[i].getName();
    }
    return null;
  }

  public static void main(String[] args) {
    String value = CityCodes.getInstance().match("bán nhà hcm".toCharArray());
    System.out.println(value);
    System.out.println();
  }

}

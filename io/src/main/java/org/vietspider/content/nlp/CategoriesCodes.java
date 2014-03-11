/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 1, 2009  
 */
public class CategoriesCodes {
  //http://192.168.1.32:4525/vietspider/DOMAIN/11/25.09.2009
  private final static CategoriesCodes  instance = new CategoriesCodes ();

  public final static  synchronized CategoriesCodes getInstance() { return instance; }

  private NameCode [] codes = new NameCode[0];

  public CategoriesCodes () {
    List<String[]> categories = new ArrayList<String[]>();
    categories.add(new String[]{
        "xây sửa nhà", "bể phốt","thông tắc","khử mùi hôi","thoát sàn","sửa toilét",
        "sửa wc","chống thấm","cống rãnh","điện nước","đục phá","vệ sinh","môi trường",
        "đô thị","hầm cầu","quét vôi","quét ve","quét sơn","đào móng","dỡ nhà","thiết kế",
        "xây dựng","tủ bếp","bồn tắm","công trình","thi công","phụ gia","bê tông","gốm",
        "đúc","hóa chất","tầng hầm","ván lót","sàn gỗ","bản lề","công trình ngầm","sơn dầu",
        "nạo vét","khử mùi","chống thấm dột","đường cống","thạch cao","trần","vách","lăn sơn",
        "cơi nới","nhà xưởng","sơn bả","khung sắt","khung nhôm","lát nền","lăn sơn","mái che",
        "phế liệu","chậu rửa","hóa chất","chất thải","hành lang","lan can","tủ bếp","nhà bếp",
        "thùng rác"
    });

    categories.add(new String[]{
        "bất động sản","đất nền", "đất thổ cư", "bán nhà","bán chung cư", "chung cư cao cấp","đất dự án",
        "nhà dự án","nhà mặt phố","cho thuê phòng","cho thuê nhà","cho thuê chung cư",
        "cho thuê cửa hàng","thuê cửa hàng","thuê nhà","bán đất", "thuê nhà", "thuê văn phòng",
        "thuê xưởng", "nhà mặt tiền", "cho thuê tầng", "thuê showroom","sang nhượng cửa hàng",
        "sang nhượng đất", "sang nhượng nhà", "chuyển nhượng đất", "biệt thự", "cho thuê biệt thự",
        "nhà mặt tiền", "nhà mặt phố", "thuê sàn", "căn hộ", "bán căn hộ", "bán nhà chia lô"
    });

    categories.add(new String[]{
        "sửa chữa dân dụng","nồi cơm điện","quạt điện", "quạt",
        "kỹ thuật bách khoa","kỹ sư","Điện Tử","sửa tivi","sửa LCD Plasma","máy lạnh","máy giặt",
        "điều hòa","sửa DVD","sửa KTS", "điện lạnh","điều khiển","quạt gió","tủ lạnh",
        "bình nước nóng","bình nóng lạnh","điện thoại","lò vi sóng","lò vi ba","quạt bàn",
        "lò viba","điện lạnh","bếp từ","máy nước nóng","dàn âm thanh","máy phát điện"
    });

    categories.add(new String[]{
        "kỹ thuật điện", "dây điện","ống luồn","bọc nhựa","steel","Kẹp ống","khớp nối",
        "Đai giữ ống","Đầu nối","Hộp nối ống","Máng cáp điện","Thang cáp điện",
        "Tháp giải nhiệt","điện gia dụng","điện công nghiệp","cảm biến",
        "chống giật","ống thép","nhà xưởng","sắt thép","rỉ","chống gỉ","ăn mòn","điện hóa"
    });

    categories.add(new String[]{
        "máy làm kem","máy ép nước mía", "máy làm vỏ ốc quế","máy vặt lông gà", 
        "tủ đá","tủ kem","máy đá viên","máy đá tinh khiết","máy điều hòa",
        "máy làm mát","máy hiệu ứng nhiệt","máy làm sữa chua","máy ép mía"
    });
    
    categories.add(new String[]{
        "máy hấp ủi","máy may","máy khâu", "máy vắt sổ","máy gấp","máy đột lỗ"
    });
    
    categories.add(new String[]{
        "máy tiết kiệm điện","máy năng lượng","hệ thống điện","máy phát điện","máy phát điện", 
        "máy biến áp","máy tĩnh điện","máy hàn", "máy phát hàn"
    });

    categories.add(new String[]{
        "máy đóng gói","máy hút trung tâm","máy hút","máy co màng","máy đóng đai",
        "máy niềng đai","máy dán", "máy đóng gói","máy chế biến","máy khía nút chai",
        "máy dán màng","máy tạo túi giấy"
    });

    categories.add(new String[]{
        "máy in lụa","máy khắc dấu","máy ép nhũ","máy chạm khắc","máy cắt","máy ép",
        "máy nghiền","máy sàng","máy ép trấu","máy ép mùn cưa", "máy nén hơi","máy xay",
        "máy tạo hạt","Máy cắt","máy cắt decal","máy điêu khắc","máy khắc cắt laser",
        "máy xay xát","máy ép bùn","máy cán màng", "máy khoan","máy cắt","máy ép ly",
        "máy đóng chiết"
    });

    categories.add(new String[]{
        "máy chà sàn","máy xây dựng","máy nghiền đá","máy nén khí",
        "xe bơm bê tông","máy nén","máy hút chân không","máy ly tâm"
    });

    categories.add(new String[]{
        "máy cơ khí","máy định vị","máy toàn đạc điện tử","máy dò hầm", "máy cân bằng ion",
        "máy trắc địa","máy phân tích", "máy kiểm tra","máy gia công","máy sản xuất", "máy móc"
    });

    categories.add(new String[]{
        "máy phun sương","máy phun rửa","máy thổi khí","máy phun sơn"
    });

    categories.add(new String[]{
        "máy đóng khoan","máy sấy","máy ép hình", "máy cán","máy hàn nhựa", 
        "máy bào","máy cưa","máy sàng rung","máy thông cống","máy hủy","máy ép gạch",
        "máy đột dập","máy hàn","máy tái chế", "máy ép vỉ","máy tiện","máy định hình bánh",
        "máy xẻ cuộn giấy"
    });

    categories.add(new String[]{
        "máy quét mã vạch","máy huỷ tài liệu","Máy Soi Tiền", "máy chấm công",
        "máy đánh chữ","máy đếm tiền","máy ép plastic","máy đóng gáy sách",
        "máy hủy tài liệu","màn chiếu","máy chiếu","máy hủy giấy","máy đóng sách","máy scan"
    });
    
    categories.add(new String[]{
        "máy hút bụi","máy cỏ","máy hơ tay","máy đánh giày","Máy sấy tóc",
        "máy làm vệ sinh","máy khử độc", "máy soi","máy tạo kiểu tóc", 
        "máy bơm nước","máy lau sàn","máy đóng chứng từ","máy khoan chứng từ",
        "máy photocopy","máy bán hàng"
    });

    categories.add(new String[]{
        "máy ảnh","máy chụp hình","máy ghi âm","máy bộ đàm"
    });

    categories.add(new String[]{
        "máy xịt phòng","máy đuổi muỗi","máy bắt muỗi", "máy diệt côn trùng",
        "máy nước nóng","máy vệ sinh","máy chà rửa sàn","máy đuổi chuột","máy diệt muỗi"
    });

    categories.add(new String[]{
        "máy trộn bột","máy đánh trứng","máy cán bột","máy thực phẩm","máy ozone",
        "máy xay sinh tố","máy xay thịt","máy làm sữa đậu nành", "máy đóng bột",
        "máy hâm sữa","máy tạo hương thơm","máy nướng","máy chiên thực phẩm"
    });

    categories.add(new String[]{
        "máy xét nghiệm","máy chiết dịch thể","máy massage","máy trợ thính","máy nâng ngực",
        "máy điều trị","máy tập đi bộ","máy xăm", "máy tập toàn thân","máy uốn","máy tập thể dục",
        "máy căng da mặt","máy ngâm chân","máy tập","máy mát sa","máy nội soi","máy chống ngủ gật"
    });

    categories.add(new String[]{
        "mạng viễn thông", "lắp mạng", "lắp internet",
        "internet","fpt","modem","adsl","VNPT","viettel","Mega Vnn",
        "cáp quang","fpttelecom","Viễn thông","hòa mạng","cáp","mạng",
        "MegaMe","Wifi","FiberPublic","FiberBronze","FiberSilver","FiverGold","Download"
        ,"quán nét","quán net","nạp","PAYPAL","MONEYBOOKER","Netnam","MegaHome","FTTH","fpt telecom"
    });

    categories.add(new String[]{
        "Website","quảng bá","quảng cáo","hosting","domain","banner","lô gô","đăng tin",
        "Web 2.0","Database","WYSIWYG","joomla","hosting","tên miền","máy chủ","AJAX","XHTML","RSS" 
    });

    categories.add(new String[]{
        "điện thoại","Bao đựng","bút chấm","Mobile", 
        "PDA phone","Phụ kiện","Pin điện thoại",
        "sạc điện thoại","Thẻ nhớ","Thẻ điện thoại",
        "bluetooth", "VOIP", "bộ đàm","Vỏ máy","Phần mềm",
        "Sim", "tứ quý","tài lộc","lộc phát","thần tài"
    });

    categories.add(new String[]{
        "nokia 1100/1101","nokia 1110/1110i","nokia 1011","nokia 1112","nokia 1200","nokia 1208",
        "nokia 1209","nokia 1600","nokia 1650","nokia 1680","nokia 2100","nokia 2110i",
        "nokia 2115i","nokia 2125i","nokia 2126i","nokia 2135","nokia 2300","nokia 2310",
        "nokia 2600","nokia 2600 classic","nokia 2610","nokia 2626","nokia 2630",
        "nokia 2650","nokia 2651","nokia 2652","nokia 2660","nokia 2680",
        "nokia 2760","nokia 2865i",               
    });

    categories.add(new String[]{
        "nokia E50","nokia E51","nokia E60","nokia E61/E61i","nokia E62","nokia E63","nokia E65",
        "nokia E66","nokia E70","nokia E71","nokia E75","nokia E90 Communicator",
    });

    categories.add(new String[]{
        "nokia N70",
        "nokia N71","nokia N72","nokia N73","nokia N75","nokia N76","nokia N77","nokia N78",
        "nokia N79","nokia N80","nokia N80 Internet Edition","nokia N81","nokia N81 8GB",
        "nokia N82","nokia N85","nokia N86 8MP","nokia N90","nokia N91","nokia N91 8GB",
        "nokia N92","nokia N93","nokia N93i","nokia N95","nokia N95 8GB","nokia N96","nokia N97"
    });

    categories.add(new String[]{
        "nokia X3","nokia X6","nokia D-211","nokia 610","nokia 770","nokia N-Gage","nokia N-Gage QD",
        "nokia N800","nokia N810 ","nokia N900"
    });

    categories.add(new String[]{
        "nokia 3109","nokia 3109 classic","nokia 3100/3100b/3105",
        "nokia 3110","nokia 3110 classic","nokia 3120","nokia 3120 classic","nokia 3155",
        "nokia 3200/3200b/3205","nokia 3210","nokia 3220","nokia 3230","nokia 3250",
        "nokia 3250 Xpress Music","nokia 3300","nokia 3310","nokia 3315","nokia 3330",
        "nokia 3410","nokia 3500 classic","nokia 3510/3590/3595","nokia 3510i","nokia 3555",
        "nokia 3600/3620/3650/3660","nokia 3600 slide"
    });

    categories.add(new String[]{
        "nokia 5000","nokia 5070","nokia 5100",
        "nokia 5110","nokia 5140","nokia 5140i","nokia 5200","nokia 5210","nokia 5220",
        "nokia 5300","nokia 5310","nokia 5320","nokia 5500 Sport","nokia 5510","nokia 5630",
        "nokia 5070","nokia 5700","nokia 5800"
    });

    categories.add(new String[]{
        "nokia 6010","nokia 6020","nokia 6021 ","nokia 6030",
        "nokia 6061","nokia 6070","nokia 6080","nokia 6085","nokia 6086","nokia 6100",
        "nokia 6101","nokia 6103","nokia 6110/6120","nokia 6110 Navigator","nokia 6111",
        "nokia 6120","nokia 6121","nokia 6124 classic","nokia 6125","nokia 6131",
        "nokia 6133","nokia 6136","nokia 6151",
        "nokia 6163","nokia 6165i","nokia 6170","nokia 6200","nokia 6210","nokia 6210 Navigator",
        "nokia 6212 classic","nokia 6215i","nokia 6220","nokia 6220 Classic","nokia 6230",
        "nokia 6233","nokia 6255i","nokia 6260 Slide","nokia 6263","nokia 6265","nokia 6267",
        "nokia 6270","nokia 6275i","nokia 6280","nokia 6288","nokia 6290","nokia 6300","nokia 6300i",
        "nokia 6301","nokia 6310i","nokia 6315i","nokia 6500 classic","nokia 6500 slide",
        "nokia 6510","nokia 6555","nokia 6600","nokia 6600 fold","nokia 6600 slide","nokia 6610",
        "nokia 6610i","nokia 6620","nokia 6630","nokia 6650","nokia 6650","nokia 6670",
        "nokia 6680","nokia 6681/6682","nokia 6800","nokia 6810","nokia 6820","nokia 6822"
    });

    categories.add(new String[]{
        "nokia 7070","nokia 7100 Supernova","nokia 7110","nokia 7160","nokia 7200","nokia 7210",
        "nokia 7210 Supernova","nokia 7250","nokia 7280","nokia 7310 Supernova","nokia 7360",
        "nokia 7380","nokia 7390","nokia 7500 Prism","nokia 7510 Supernova","nokia 7600",
        "nokia 7610","nokia 7610 Supernova","nokia 7650","nokia 7700","nokia 7710",
        "nokia 7900 Prism"
    });

    categories.add(new String[]{
        "nokia 8110","nokia 8210","nokia 8250","nokia 8310","nokia 8600",
        "nokia 8800","nokia 8810","nokia 8850","nokia 8890","nokia 8910","nokia 8910i",
        "nokia 9000", "nokia 9110", "nokia 9110i",
        "nokia 9210", "nokia 9290","nokia 9210i","nokia 9300",
        "nokia 9300i","nokia 9500"
    });

    categories.add(new String[]{
        "BlackBerry", "Sony Ericsson", "HTC Touch", "Palm", "Palm Treo", "iphone", 
        "htc", "o2 xda", "Dopod", "Vodafone", "Cingular", "Samsung SGH", "HP iPAQ", "T-Mobile MDA",
        "E-TEN Glofiish", "Mio Moov", "Dell Axim", "Toshiba", "Fujitsu-Siemens"
    });

    categories.add(new String[]{
        "samsung A127 ","samsung A137","samsung A227","samsung A237","samsung A437 ","samsung A460",
        "samsung A503","samsung A517 ","samsung A640","samsung A707","samsung A717","samsung A727",
        "samsung A737","samsung A747","samsung A800 ","samsung A827","samsung A837","samsung A867",
        "samsung A900","samsung A920","samsung A930","samsung A940","samsung A990"
    });

    categories.add(new String[]{
        "samsung C417","samsung D307","samsung D357 ","samsung D500","samsung D600",
        "samsung D807","samsung D900"
    });

    categories.add(new String[]{
        "samsung E250","samsung E590","samsung E710","samsung E715","samsung E720",
        "samsung E900","samsung E950","samsung F210 ","samsung F700 ","samsung F480",
        "samsung G600","samsung G800","samsung G810"
    });

    categories.add(new String[]{
        "samsung i300","samsung i500","samsung i550","samsung i600","samsung i607",
        "samsung i617","samsung i700","samsung i7110","samsung i730","samsung i760",
        "samsung i900(Omnia)","samsung i907(Epix)","samsung i8510(INNOV8) ",
        "samsung i8910(Omnia HD)","samsung J600","samsung J700","samsung M100",
        "samsung M510 ","samsung M520 ","samsung M620 (Upstage)","samsung M800 (Instinct)",
        "samsung Minikit"
    });

    categories.add(new String[]{
        "samsung N270","samsung P300","samsung P310","samsung P520","samsung T309",
        "samsung T319","samsung T409","samsung T509","samsung T519","samsung T619",
        "samsung T629","samsung T639","samsung T729 (Blast)","samsung T809","samsung T819",
        "samsung T919","samsung U100","samsung U300","samsung U470 (Juke)","samsung U520",
        "samsung U540","samsung U550","samsung U600","samsung U700","samsung U740 (Alias)",
        "samsung Soul (U900) ","samsung U940 (Glyde)"
    });

    categories.add(new String[]{
        "samsung W531","samsung X150","samsung X160","samsung X200","samsung X210",
        "samsung X300","samsung X400","samsung X450","samsung X460","samsung X480",
        "samsung X481","samsung X495","samsung X510","samsung X520","samsung X640",
        "samsung X660","samsung X680","samsung X700","samsung X820","samsung X830","samsung Z320",
        "samsung Z400","samsung 6.9 (X820)","samsung 9.9 (D830)","samsung 12.9 (D900)",
        "samsung 5.9 (U100)","samsung 9.6 (U300)","samsung 10.9 (U600)","samsung 12.1 (U700)",
        "samsung Ultra Music (F300)","samsung Ultra Video (F500)","samsung Ultra Smart (F700)"
    });
    
    
    categories.add(new String[]{
        "mực in","laser","HP","hp","caon","EPSON","LEXMARK","XEROX","SAMSUNG","hộp mực",
        "in phun","photo","máy fax","in ấn","ngành in","máy ép nhiệt","máy in","in ảnh",
        "in ấn","tờ rơi","hộp in","văn phòng phẩm","giấy in","photocopy"
    });

    categories.add(new String[]{
        "LAPTOP", "máy tính xách tay", "Loa máy tính", "máy tính cũ",
        "máy tính", "máy vi tính", "túi đựng laptop", "balo đựng laptop",
        "chuột máy tính", "bàn phím", "chuột quang", "đĩa cứng", "ổ cứng",
        "ổ dvd", "ổ cd-room", "màn hình", "card mạng", "ổ quang", "usb", "ổ flash", 
        "vệ sinh laptop", "làm sạch màn hình"
    });

    categories.add(new String[]{
        "sửa máy tính", "diệt virut", "nâng cấp phần mềm", "cài đặt phần mềm", "cứu dữ liệu",
        "cài đặt mạng"
    });

    categories.add(new String[]{
        "lao động phổ thông", "giúp việc nhà", "tuyển công nhân", "giúp việc", "chăm em bé",
        "trông quán net", "bán hàng", "ô sin", "lao công", "tạp vụ"
    });

    categories.add(new String[]{
        "lập trình viên", "kỹ sư xây dựng", "kế toán", "kỹ sư", "giám đốc", "điều hành",
        "nhân viên kinh doanh", "nhân viên văn phòng", "nhân viên", "trưởng phòng", "thư ký",
        "trợ ký"
    });

    /*categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });
    
    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });

    categories.add(new String[]{

    });*/

    codes = new NameCode[categories.size()];
    for(int i = 0; i < categories.size(); i++) {
      codes[i] = new NameCode(categories.get(i));
    }
  }

  public void match(Set<String> relations, char[] chars) {
    for(int i = 0; i < codes.length; i++) {
      int index = codes[i].isName(chars, 0);
      if(index < 0) continue;
      List<char[]> names = codes[i].getNames();
      for(int k = 0; k < names.size(); k++) {
        relations.add(new String(names.get(k)));
      }
    }
  }

}

/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpFilter;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 19, 2010  
 */
public class PhoneFilter implements INlpFilter {

  private final static String keys[] = new String[] {
    "liên hệ", "lien he", "liên lạc", "lh", "contact",
    "điện thoại", "tel", "hotline", "phone", "di động", 
    "mobile", "đt:", "đtdđ"
  };
  private final static int scores[] = new int[] {
    10, 10, 8, 2, 10,
    10, 6, 10, 10, 2,
    10, 6, 6
  };

  private final static int min = 8;
  private final static int max = 13;


  // 091/ 094/ 0123/ 0124/ 0125/ 0127/ 0129
  //090/ 093/ 0121/ 0122/ 0126/ 0128
  //http://vi.wikipedia.org/wiki/M%C3%A3_%C4%91i%E1%BB%87n_tho%E1%BA%A1i_Vi%E1%BB%87t_Nam
  @SuppressWarnings("unused")
  public void filter(String id, TextElement element) {
//    System.out.println(element.getValue());
    String text = element.getLower();
    if(text == null) return;
    
    for(int i = 0; i < keys.length; i++) {
      int idx = text.indexOf(keys[i]);
      if(idx < 0) continue;
      //      System.out.println(keys[i] +  " : "+ idx + " : " + index);
      //      if(index > 0 /*&& idx > index*/) continue;
      if(filter(element, text, idx + keys[i].length(), scores[i])) return;
    }

    filter(element, text, 0, 1);
  }

  private boolean filter(TextElement element, String text, int index, int score) {
    int start = -1;
    int counter = 0;
    String value = element.getValue();
    int end = text.indexOf("fax", index);
    if(end < index) end = value.length();

    //    System.out.println(index + " : " + end + " : "+ value.length()+ ", score : "+ score);
    //    System.out.println(value.substring(index));
    boolean _result = false;

    while(index < end) {
      char c = value.charAt(index);
      if(Character.isDigit(c) || c == 'o' || c == 'O') {
        if(start < 0) start = index;
        counter++;
        index++;
        continue;
      } 

//      if(counter > 0) {
//        System.out.println("mark method 0: " +score + " : "+ counter + " : " + element.getValue().substring(start));
//      }

      if(c == '(' || c == ')'
        || c == '.' 
          || Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }

      if( (c == '-' || c == ',') && start >= 0 
          && counter < 10 
          && (PhoneUtils.isMobile(value, start)
              || PhoneUtils.isCode(value, start) || score == 10)) {
        index++;
        continue;
      }

      if(counter > max) {
        counter = 0;
        int separator = -1;
        int i = start;
        while(i < index) {
          c = value.charAt(i);
          if(Character.isDigit(c)) {
            counter++;
            i++;
            continue;
          }

//          if(Character.isWhitespace(c) 
//              || Character.isSpaceChar(c)) c = ' ';

          if(counter >= min 
              && ( separator == -1 ||
                  PhoneUtils.checkPrefix(value, i+1) > 0)) break;

          if(separator == -1) {
            separator = c;
            i++;
            continue;
          }

          if(separator == c || separator == ')') {
            i++;
            continue;
          }
          break;
        }
        //        System.out.println(counter + " : " + " : " + value.substring(start, i));
        index = i;
      }

      boolean _rsult = mark(element, counter, score, start, index);
      if(!_rsult && counter > 0) {
        while(index < end) {
          c = value.charAt(index);
          if(!Character.isLetterOrDigit(c)) break;
          index++;
        }
      } else if(_result && !_result) {
        _result = _rsult;
      }
      counter = 0;
      start = -1;
      index++;
    }


    if(counter > max) {
      counter = 0;
      int separator = -1;
      int i = start;
      while(i < index) {
        char c = value.charAt(i);
        if(Character.isDigit(c)) {
          counter++;
          i++;
          continue;
        }

        if(counter >= min 
            && (separator == -1 ||
                PhoneUtils.checkPrefix(value, i+1) > 0)) break;

        if(separator == -1) {
          separator = c;
          i++;
          continue;
        }

        if(separator == c || separator == ')') {
          i++;
          continue;
        }

        break;
      }

      index = i;
      boolean _rsult = mark(element, counter, score, start, index);
      if(_result && !_result) _result = _rsult;

      start = -1;
      counter = 0;
      while(i < value.length()) {
        char c = value.charAt(i);
        if(Character.isDigit(c)) {
          counter++;
          if(start < 0) start = i;
        }
        i++;
      }

      //      System.out.println(counter + " : " + " :" + value.substring(start, i));
      _rsult = mark(element, counter, score, start, value.length());
      if(_result && !_result) _result = _rsult;
      return _result;
    } 

    return mark(element, counter, score, start, index);
  }

  private boolean mark(TextElement element,
      int counter, int score, int start, int index) {
//    if(counter >= 1) {
//      System.out.println("mark method 1: score:" + score + ", counter:" + counter + ", value: " + element.getValue().substring(start, index));
//    }
    //    if(counter >= 20) {
    //      System.out.println(element.getValue());
    //      new Exception().printStackTrace();
    //    }
    if(counter < min || counter > max)  return false;

    if(counter < 10 || counter > 11) score -= 2;

    if(score < 5) {
      int add = 0;
      TextElement pre = previous(element);
      if(pre != null) add = endWith(pre.getValue().toLowerCase());
      //      System.out.println(pre.getValue());
      if(add > 0) {
        score += add;
      } else if(pre != null) {
        pre = previous(pre);
        //        System.out.println(pre.getValue());
        if(pre != null) add = endWith(pre.getValue().toLowerCase());
        //        System.out.println(Math.round((float)add/2) + " : "+score);
        if(add > 0) score += Math.round((float)add/2);
        //        System.out.println(" ==  >"+ score);
      }
    }

    //    System.out.println(" ==  >1 "+ score);
    String value = element.getValue();
    if(score > -1 && score < 5) {
      score += PhoneUtils.checkPrefix(value.substring(start, index));
    }
    //    System.out.println(" ==  >2 "+ score);

    //    System.out.println("m.ark method 2: score:" + score + ", counter:" + counter + ", value: " + element.getValue().substring(start, index));
    if(score < 1) return false;

    element.putPoint(type(), score, start, index);

    //    int e = checkPrefix(value.substring(start, index));
    //    if(e == 3) return;

    //    if(score < 1) {
    //    if(score > 0 && score < 3) {
    //    if(score > 2 && score < 5) {
    //    if(score > 4 && score < 10) {
    //    if(score >= 10) {
    //      System.out.println(" ========================= ");
    //      System.out.println("total "+ counter + ", score " + score + " : " + checkPrefix(value.substring(start, index)));
    //      
    //      if(previous != null) {
    //        System.out.println("previous " + previous);
    //      }
    //      System.out.println(value);
    //      System.out.println(value.substring(start, index));
    //    }

    return true;
  }

  private TextElement previous(TextElement element) {
    TextElement pre = element.previous();
    if(pre == null) return null;
    if(pre.getValue().trim().isEmpty()) return previous(pre);
    return pre;
  }

  private int endWith(String text) {
    for(int i = 0; i < keys.length; i++) {
      int idx = text.indexOf(keys[i]);
      if(idx < 0) continue;
      idx += keys[i].length();
      while(idx < text.length()) {
        char c = text.charAt(idx);
        if(Character.isLetterOrDigit(c)) return 0;
        idx++;
      }
      return scores[i];//Math.round((float)scores[i]/2);
    }

    return 0;
  }

  public short type() { return NLPData.PHONE; }

  public static void main(String[] args) {
    PhoneFilter filter = new PhoneFilter();
    TextElement ele = new TextElement(
        //        " Căn hộ xanh Green Bulding Mã số tài sản: 290340 Cần bán căn hộ cao cấp dự án Green Building, Nam Hòa,  phường Phước Long A, Quận 9, TP.HCM. VND 13 triệu 400 ngàn/m2 USD SJC 1 2 2 - Diện tích khuôn viên: _ x _ Diện tích sử dụng: 66 m 2 Liên hệ Công ty TNHH Bất Động Sản Phát Hưng - Tiến Giáp 0919.882.386 136 Khánh Hội, P.6, Q.4 Cảm ơn vì đã xem tin. Gửi cho bạn bè Bản In Lưu Tin Quay Lại"
        //        " Bán nhà đường Thống Nhất, P.15, Q.Gò Vấp Mã số tài sản: 255143 Cần bán nhà phố, 474/3,  Thống Nhất , phường 15, Quận Gò Vấp, TP.HCM. VND 1 tỷ 500 triệu USD SJC 1 3 2 - Diện tích khuôn viên: 3.1 m x 14.45 m Diện tích sử dụng: 32.65 m 2 Liên hệ Viet An Land 0837406903 0908751089 Biệt thự Việt An, số 8, đường 27 - Trần Não, Phường Bình An, Quận 2, Tp.HCM Gửi cho bạn bè Bản In Lưu Tin Quay Lại"
        //        "Đất nền Phong phú 4, sổ đỏ, vị trí đẹp, giá hấp dẫn, thanh toán theo tiến độ, đầu tư tốt Mã số tài sản: 268581 Nhượng quyền sử dụng đất dự án-quy hoạch dự án Khu dân cư Phong Phú 4, Quốc Lộ 50 ,  phường Phong Phú, Huyện Bình Chánh, TP.HCM. VND 9 triệu 500 ngàn/m2 USD SJC - - - - Diện tích khuôn viên: 5 m x 20 m Diện tích sử dụng: 100 m 2 Liên hệ Huỳnh Anh Kiệt 35024883 0917564826 250 Nguyễn Thị Minh Khai, F.6, Q.3 Gửi cho bạn bè Bản In Lưu Tin Quay Lại"
        //"Cho người NN thuê căn hộ tầng 3, sau KS Fortuna, Láng Hạ, diện tích 50 m2, phòng khách, bếp, phòng ngủ, khép kín, tiện nghi hiện đại, người NN đang ở tầng 2. liên hệ: C.My, 38313811, 01223447328."
        //        " Hãy mua ngay căn hộ Nguyễn Quyền - Chỉ 720 triệu/ căn 62m2 (đã VAT) Mã số tài sản: 245664 Cần bán căn hộ cao cấp dự án Nguyễn Quyền Plaza, Phan Anh ,  Quận Bình Tân, TP.HCM. VND 720 triệu USD SJC - 2 1 - Diện tích khuôn viên: _ x _ Diện tích sử dụng: 62 m 2 Liên hệ Ngọc Tuyền 08 38650074 0908 354 236 411 - 413 Sư Vạn Hạnh (nối dài), P.12, Q.10, TP. HCM Gửi cho bạn bè Bản In Lưu Tin Quay Lại"
        //        "Bán nhà Đăng lúc: 16:20 , ngày 26/12/2010 - Hà Nội Đã xem: 2 . Mã Tin: 13300461 Người đăng: nguyen phuong E-mail: nhadat12196thanhcong@gmail.com Nội dung Bán nhà cấp 4 mặt đường Cầu Giấy. DT: 40m2 . Khu trung tâm, tiện kinh doanh buôn bán, điện nước an ninh tốt. Giá: 320 triệu/m (có thương lượng). LH: O987403076"
        //        " cho thuê phòng q11 24m2 máy lạnh, TH cáp, internet cáp quang cho thuê phòng q11 đường dương đình nghệ p8 quận 11, phòng 24m2 máy   lạnh,WC riêng trong phòng, TH cáp, internet cáp quang, có chổ để xe, cho   nấu ăn, có sân thượng, phòng thoáng mát sạch đẹp, giờ giấc tự do,cách   đầm sen 300m, siêu thị 200m, gần chợ, trường học, tt văn hóa quận 11.   phòng dành cho mọi đối tượng. giá 1tr8 lh: 49 dương đình nghệ p8 q11 0933032373 gặp thanh. cám ơn mọi người đã xem tin, đi ngang qua nhớ để lại dâu nhé, sẽ đáp trả lễ"
        //        " HCM-Củ Chi Cần Bán 1000m2 đất Cần bán 1000m2 đất trong đó + 300m 2 đất thổ cư + 700m2 đất vườn, trồng cây lâu năm, màu mỡ nền đất cao, đẹp, rộng rãi thoáng mát, thích hợp cho việc làm thảo điền hoặc trang trại, biệt thự, villar ... Giá bán : 1.9 T ỷ Thương Lượng-Chính Chủ-Miễn Trung Gian. Ngoài Ra Còn Có 1 Vài Mẫu Đất Với : Diện Tích :1.800m2 , 4300m2 , 5.300m2 10.000m2. Đất Công Nghiệp Liên hệ: 0938.802.699 A.Nguyên Tính Chất Pháp Lý: Sổ Đỏ Hợp Lệ Chính Chủ. Thủ Tục Rõ Ràng. Địa Thế: Mặt Tiền Đường Tỉnh Lộ 15 (Đường Lớn) Phía Sau Tiếp Giáp Với Sông Sài Gòn Khu Kinh Tế Tiềm Năng Đang Phát Triển Của TP.HCM. Mọi Thắc Mắc Quý Anh Chị Em Liên Hệ Trực Tiếp  Theo Điện Thoại Trên. thay đổi nội dung bởi: kuteboyZ ,  21-12-2010 lúc 08:52 PM ."
        //        " Bán căn hộ WATERMARK - Lạc Long Quân, bên bờ Hồ Tây (986) Giá : 2.500,0 USD / m2 Mã số : 1895310 Ngày đăng : 09/07/2010 - 12:57 Sửa chữa : 02/09/2010 - 08:35 Hết hạn : 31/12/2010 - 23:59 Nơi đăng : Hà Nội Hình thức : Cần bán Tình trạng : Hàng mới 100% Than phiền Lưu vào danh sách ưa thích Đăng bởi : timnhamoi Các tin rao vặt đã đăng Họ và tên : Việt Anh Địa chỉ : Điện thoại : 0989-34.08.70 Email : Y!M : Skype : Độ rộng bản đồ: 780x350 780x600 Ẩn bản đồ Thông tin cơ bản Thành Phố (Tỉnh) : Hà Nội Quận Cầu giấy Loại nhà : Nhà chung cư Căn hộ cao cấp Diện tích mặt sàn (m2) : - Kích thước mặt tiền (m) : - Diện tích sử dụng (m2) : - Thông tin chi tiết Nhượng một số xuất mua căn hộ Watermark diện tích 107m2 hướng Đông, 02 phòng ngủ và căn góc 131m2 hướng Đông Nam có 03 PN, căn góc tròn (rẻ quạt) 130m tầng cao, vào thẳng tên Hợp đồng với West Lake, giá gốc 2400-2600Usd/m2, chênh thấp (chỉ 1-3%) ================================================ Tọa lạc trên đường Lạc Long Quân, ngay bên bờ Tây Hồ Tây - Hà Nội, Tòa tháp WATERMARK cao 19 tầng hình cánh buồm vươn mình ra Hồ Tây do Công ty kiến trúc Aedas thiết kế . Chủ đầu tư: Công ty Refico (Anh) hợp tác với công ty CP Bất động sản Tây Hồ. Thiết kế: Watermark gồm 2 tầng hầm, từ tầng 1- tầng 4 dành cho trung tâm thương mại. 15 tầng còn lại dành cho 128 căn hộ cao cấp, hiện đại. Các căn hộ được phân thành 3 loại: căn hộ 1 phòng ngủ (diện tích khoảng 60,6m2); loại 2 phòng ngủ (93,4 - 109,8m2); căn hộ 3 phòng ngủ (125 - 131m2) và một số căn penthouse 1 tầng và penthouse 2 tầng. Tòa tháp Watermark, do Công ty kiến trúc Aedas thiết kế, nhìn từ xa giống như một cánh buồm vươn mình ra Hồ Tây. Chủ các căn hộ sẽ được tận hưởng trọn vẹn phong cảnh Hồ Tây thơ mộng nhờ hệ thống tường kính rộng xung quanh được làm bằng kính chịu lực. Tình trạng: Đã giải phóng mặt bằng xong, quây tôn, đặt biển dự án trên diện tích gần 2000 m2. Dự án sẽ được khởi công vào tháng 9 năm 2010, giới thiệu nhà mẫu vào đầu Quý IV/2010. Công ty Refico cũng đồng thời là chủ đầu tư của City Garden- 59 Ngô Tất Tố, Quận Bình Thạnh (TP.HCM); Centre Point- 106 Nguyễn Văn Trỗi , Quận Phú Nhuận (TP.HCM); Sanctury Ho Tram Resort (Vũng Tàu) ... Tiến độ thanh toán: Góp vốn trả dần làm 6 giai đọan. 4 lần đầu mỗi lần 15%, khỏi công sau lần 2, hoan thành móng sau lần 3. lần thứ 5 sau khi hoàn thành tầng mái 10%. Lần thứ 6 sau khi khách nhận nhà (Quý 1 năm 2013) nộp nốt 30%. Mức giá trung bình 2300-2500 Usd/m2 . Hiện đang góp vốn giai đoạn 1. WATERMARK là địa điểm dành cho những ai yêu mến những điều tuyệt vời nhất trong cuộc sống. Điểm gây ấn tượng nhất của WaterMark chính là thiết kế kiến trúc nội và ngoại thất độc đáo tạo nên phong cách sống phong phú cho bạn. Thiết kế ấn tượng của Watermark không những tạo ra một diện mạo hoàn mỹ và mới lạ cho khu cao ốc mà còn giúp bạn bao quát toàn diện quang cảnh Hồ Tây – đây chính là nét đặc trưng của Watermark. Dự án sẽ được khởi công vào năm 2010. Xin liên hệ để được cung cấp thông tin chi tiết theo: 0989-340.870 , Việt Anh ================================================== Hiện chúng tôi đang cần mua căn hộ Watermark, tầng cao, 3 phòng ngủ, căn góc 2 hướng Đông và Nam. Quý khách muốn chuyển nhượng xin liên hệ để có giá tốt nhất, thanh toán nhanh gọn."
        //        " !!! Cho thuê, nhà 2 căn 8x22 khu dân cư, gần Himlam, Conic !!! siêu siêu rẻ Cho thuê nhà 2 căn liền  kề, diện tích 4 x 22 cho mỗi căn, nhà mới xây  vào 10/2010, đã  có điện, nước, viễn thông. Số B14-15, Khu Dân Cư Ứng  Thành,  Ðường Trịnh Quang Nghị, P7, Q8 (gần Khu Dân Cư Phú Lợi, Him Lam,   Conic) + 1 trệt 2,5 lầu, sân thượng + Có sân trước, sân sau, mỗi  tầng đều có 1 WC riêng và 2 phòng + Khu dân cư yên tĩnh gần chợ,  trường học, ủy ban. + Thích hợp làm nhà ở, văn phòng, trường học,  ngân hàng...... Có thể thuê từng căn hoặc cả 2  (600$/căn/tháng) Mọi chi tiết xin vui lòng liên hệ: Chú Long (chính  chủ) 0903681992 - 0908041952"
        //        " Căn Hộ CC Thủ Đức - Giá Cực Shock - Đặt Mua Ngay Đăng lúc: 16:14 , ngày 26/12/2010 - TP HCM Đã xem: 53 . Mã Tin: 12997305 Người đăng: Nguyễn Kim Ngọc E-mail: loweet_simaraka@yahoo.com Nội dung CĂN HỘ  LAN PHƯƠNG MHBR TOWER Vị trí LAN PHƯƠNG MHBR TOWER 104 Hồ Văn Tư - Phường Trường Thọ - Quận Thủ Đức – TP.HCM Diện tích khuôn viên : 3.897m2 Diện tích xây dựng : 1.156m2 Diện tích sàn xây dựng : 23.172m2 Tiện ích CĂN HỘ LAN PHƯƠNG : Lan Phương MHBR Tower được thiết kế theo phong cách hiện đại với diện  tích xây dựng 1.156m2, mật độ xây dựng 33.69%, đặc biệt diện tích cây  xanh lớn. Sở hữu Căn hộ Lan Phương MHBR bạn sẽ tận hưởng một không gian  sống tuyệt vời với nhiều tiện ích cộng đồng như: Khoảng không gian xanh Môi trường sống hiện đại Khu vui chơi giải trí thiếu nhi Khu mua sắm Trung tâm thể thao – thẩm mỹ Chuỗi cửa hàng ăn uống, cafe bar… DIỆN TÍCH LAN PHƯƠNG : 6 LOẠI DIỆN TÍCH : TỪ 62m2 đến 141m2 GIÁ CĂN HỘ LAN PHƯƠNG : TỪ 14,3 triệu/m2 Được thi công va thiêt kế, giám sát bởi các nhà thầu nổi tiếng ở Việt Nam. ĐƠN VỊ THIẾT KẾ Công ty  TNHH tư vấn thiết kế DP ĐƠN VỊ THI CÔNG Công ty cổ phần xây dựng và kinh doanh địa ốc Hoà Bình ĐƠN VỊ GIÁM SÁT Công ty TNHH Công Trình Sư ĐƠN VỊ TÀI TRỢ VỐN Ngân hàng Phát triển đồng bằng sông Cửu Long (MHB) tài trợ thanh toán trả chậm đến 70% giá trị căn hộ LIÊN HỆ TƯ VẤN : Phùng Lụa : 0903 – 606 107 Công Ty CP BDS Dat Xanh Hoan Cau"
        //        " Cần bán nhà Vũng Tàu Bình Giã phường 8 giá 4.4tỉ Đăng lúc: 09:24 , ngày 26/12/2010 - Bà Rịa - Vũng Tàu Đã xem: 91 . Mã Tin: 12635390 Người đăng: can1773 E-mail: can1773@yahoo.com Nội dung Cần bán nhà Vũng Tàu số 306 Bình Giã phường 8 (hiện đang cho thuê làm văn phòng Công ty), gần trụ sở phường, chợ và trường học, khu quy hoạch buôn bán vật liệu xây dựng, lập xưởng và nhà nghỉ, khách sạn.. Đất 5mx44m=220m2 có 150m2 đất ở. Giá bán 4,4tỉ đồng (tức khoảng 20 triệu đồng/m2) thương lượng vì cần bán gấp. LH0913693668 hoặc email: can1773@yahoo.c om. Có sơ đồ vị trí kèm theo. Xin cảm ơn đã xem tin./."
        //        " Bán nhà & Đất Xã Tân Thới Nhì Huyện Hóc Môn Bán nhà & Đất Xã Tân Thới Nhì Huyện Hóc Môn , Diện tích 441,7m2 có nhà 70,2m2 . Xây tường bao quanh đất có cổng lớn , Từ quốc lộ 22 vô 50m , gần ngã tư hốc môn & ngã 3 lam sơn . Bán với giá 2,5 tỷ LH 098473962 ( A. Bạc ) DC : Hóc Môn thay đổi nội dung bởi: chuongkhin ,  10-12-2010 lúc 11:42 AM ."
        " Cần bán chcccc Đăng lúc: 14:20 , ngày 26/12/2010 - Hải Phòng Đã xem: 5 . Mã Tin: 13299628 Người đăng: Khách vãng lai Nội dung Cần bán gấp CHCCCC GRAND PANCEPIC ở Hồng Bàng -HP Toà TOWER 2 có 21 tầng Có 6 kiểu căn hộ với DT 96,35 - 153,49 m2. Mỗi căn có từ 2-4 PN. NT được TK hiện đại Giá từ 840 usd - 1090usd/m2. Ký trưc tiếp với chủ đầu tư ,đặt cọc trước 10 %. Hiện đang xây tầng 1 . Tháng 6-2012 giao nhà ( Giá áp dụng trước 10-1-2011)Ll L hệ Mr Tạo 092.608.29.29 ( quangtaonguyen0103@gmail.com)"
    );
    filter.filter("test", ele);
  }

}

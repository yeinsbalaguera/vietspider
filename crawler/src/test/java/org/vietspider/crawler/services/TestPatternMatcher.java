/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.services;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.chars.refs.RefsDecoder;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 13, 2007  
 */
public class TestPatternMatcher {
  
  private static void testSplit() {
    String subContent = "Tóm tắt nội dung : S ự thành bại của một con người không những phụ thuộc vào trí tuệ mà còn phụ thuộc vào suy nghĩ, tấm lòng và sự lao động của người đó. Tôma Êđixơn đã từng nói “Thiên tài được hình thành nhờ 1% trí thông mình còn 99% là do sự siêng năng cần cù”. Vì vậy, dù có thông minh đến đâu cũng cần phải có EQ cao. Người có EQ cao là người có tấm lòng rộng mở, quyết tâm học hỏi, đồng thời là người có suy nghĩ sâu sắc, có tính mạnh mẽ và kiên trì theo đuổi ước mơ, hoài bão tốt đẹp của mình. Chuyện kể về các danh nhân thế giới chính là nói về những người có chỉ số EQ cao. Truyện được trình bày hấp dẫn bằng hình ảnh sinh động, các tác giả sẽ giúp bạn có động lực để trở thành người có EQ cao, khác với IQ, EQ sẽ lớn lên trong chính sự quyết tâm của mỗi chúng ta. Napôlêông sinh ra ở đảo Coóc (thuộc địa của Pháp). Thân hình nhỏ bé, khuôn mặt trắng, xuất thân không có gì đặc biệt nhưng những câu chuyện về ông vẫn còn lưu truyền cho đến tận ngày nay. Ngay từ nhỏ Napôlêông đã có ước mơ đem lại độc lập cho Tổ quốc và lớn lên ông có hoài bão được trị vì cả thế giới rộng lớn này. Napôlêông là một hoàng đế đã tạo ra nhiều cuộc cải cách có ảnh hưởng lâu dài không chỉ ở Pháp mà còn ở rất nhiều nước châu Âu, đồng thời ông cũng là nhà quân sự đại tài trong lịch sử nhân loại. Điều đó được thể hiện ở ý chí bất khuất trong câu nói nổi tiếng của ông: “Không có gì là không thể”. Nhưng Napôlêông cũng có sai lầm lớn là trở thành một kẻ độc tài bởi ông chỉ mải mê theo đuổi những tham vọng vô cùng của mình như bị cuốn vào cơn lốc xoáy. Chính điều này đã tạo ra bi kịch cho những ngày cuối đời của ông. Hy v ọng rằng qua câu chuyện về Napôlêông các em sẽ học được ở ông những điểm đáng quý như: Cố gắng không ngừng, kiên trì chịu đựng khó khăn vất vả để nỗ lực vươn lên trong cuộc sống, biến ước mơ thành hiện thực. Sách được trình bày dưới hình thức tranh truyện . MỤC LỤC Cậu bé yêu nước vùng đảo Coóc Con đại bàng phi thường của nước Pháp Thần chiến tranh Không có gì là không thể Hoàng đế Napôlêông Không có anh hùng vĩnh viễn Ý kiến của bạn Họ và tên: Địa chỉ: Email: Điện thọai: Ý kiến: O ff T elex V NI Các bạn vui lòng sử dụng bộ font Unicode và gõ tiếng Việt có dấu. Nhà sách có thể biên tập lại ý kiến của bạn và không bảo đảm tất cả các ý kiến đều được đăng. Bạn nghĩ thế nào về website này ? Tuyệt vời Khá Tạm được Kém Quá tồi (94 Nguyễn Thị Minh Khai, P.6, Q.3, TP.HCM)  ";
    Pattern pattern = Pattern.compile("[.!?;,]");
    Matcher matcher = pattern.matcher(subContent);
//    System.out.println(subContent);
    int total = 0;
    int MIN_DES = 30;
    int start = 0;
    StringBuilder builder  = new StringBuilder();
    while(matcher.find(start)) {
      int end  = matcher.start()+1;
      StringBuilder newContent = new StringBuilder(subContent.subSequence(start, end));
      total += count(newContent);
      builder.append(' ').append(newContent);
      start = end+1;
      if(total > MIN_DES) break;
    }
    System.out.println("dem duoc "+total);
    
    System.out.println(builder);
  }
  
  private static int count(CharSequence charSeq){
    Pattern pattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");
    int start = 0;
    int counter = 0;
    Matcher matcher = pattern.matcher(charSeq);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      counter++;
    }
    return counter;
  }

  private static void testScanner() {
    RefsDecoder decoder = new RefsDecoder();
    String value = new String(decoder.decode("&nbsp;".toCharArray()));
    String input = "1 fish 2 fish red"+value+"fish blue fish";
    Scanner s = new Scanner(input).useDelimiter("\\s");
    while(s.hasNext()) {
      System.out.println(s.next());
    }
//    System.out.println(s.nextInt());
//    System.out.println(s.nextInt());
//    System.out.println(s.next());
    s.close(); 
  }
  
  private static void testWordCounter() {
    RefsDecoder decoder = new RefsDecoder();
    String value = new String(decoder.decode("&nbsp;".toCharArray()));
    System.out.println("value: |"+value+"|"+ Character.isWhitespace((int)value.charAt(0)));
    System.out.println(" so sanh bang "+ (value.charAt(0) == Character.PARAGRAPH_SEPARATOR));

    Pattern  pattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");
//  Pattern  pattern = Pattern.compile("\\b[U+00AB-U+00BB]", Pattern.UNICODE_CASE);

//  String txt = "toi thuan 3473:89 o day"+value+"thuan";
//  String txt = "Sau khi đoạn clip sex được cho là của nữ diễn viên chính sê- ri Nhật ký Vàng Anh phần 2 được phát tán trên mạng đã gây nên cú sốc lớn đối với cư dân mạng. Ngay lập tức nhiều câu hỏi của độc giả đặt ra là: Liệu Nhật ký Vàng Anh có phải tạm ngừng phát sóng? Vui cùng Hugo có tiếp tục ghi hình?..";
//    String txt = "© xaluan  ]  Diễn viên Nicole Kidman. Ảnh: HollywoodCeleb.  ";
    String txt ="Hiển thị 1 tới 6 (trong 6 sản phẩm)    Ảnh của sản phẩm    Tên sản phẩm-     Giá         NEC Versa E6201-F1624DRC    $885. 0   Thêm: CPU Intel Core 2 Duo T5200 - 2 x 1. Ghz/Cache 2MB/Memory 512MB/HDD 80GB/Video Card Up to 128Mb Share/Optical Disk /DVD Super Multi DL /Card Reader 4. .       NEC Versa E6210 - 2011DRC    $1,570. 0   Thêm: Core2Duo T7200 - 2 x 2. Ghz / Cache 4MB / Memory 512MB/HDD 100GB/Video Card Intel® GMA 128MB /Optical Disk DVD+RW/Card Reader 4 in1/Nic - Fax. .       NEC Versa M350-1704DW    $999. 0   Thêm: Intel Centrino 1. 3Ghz, 2MB L2 Cache, Ram 256MB, 60 HDD, DVD&CDRW, 15inch XGA TFT 1,024 x 768, Wireless 802. 1b,g (nhan dang van tay)       VERSA E6201 - F1713DRC (VERSA E6201 - F1713DRC)    $935. 0   Thêm: Core Duo T2080 - 2 x 1. 3Ghz/1024 Mb/160GB SATA/Up to 128Mb Share/DVD Super Multi DL/Webcam/Nic - Fax/802. 1b. - Bluetooth/14. \" SSV WXGA TFT/Vista. .       Versa S3200-F1700DRC    $1,050. 0   Thêm: Core Duo T2250 - 2 x 1. 3Ghz/Cache 2MB/Memory 512MB/HDD 80GB/Video Card Up to 128Mb Share/DVD Super Multi DL/Card Reader 3 in1/Nic - Fax 10/100 Mps -. .       Versa S3200-F1701DRC    $1,135. 0   Thêm: Core Duo T2250 - 2 x 1. 3Ghz/Cache 2MB/Memory 1024MB/HDD 80GB/Video Card Up to 128Mb Share/DVD Super Multi DL/Card Reader 3 in1/Nic - Fax 10/100 Mps. .";
//  String txt = "đến từ: RedFlag    Tiêu đề:  Thong bao quang cao ve domains moi cua thuongthuc";

    Matcher matcher = pattern.matcher(txt);
//  Matcher matcher = pattern.matcher("Sau khi đoạn");
    int start = 0;
    int counter = 0;
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      counter++;
    }
    System.out.println(counter);
  }
   
  public static void testURL() {
//    StringBuilder builder  = new StringBuilder(" http://thegioimevabe.com/Productsdetails.asp ?ID =150&");
//    String value = "http://www.chodientu.vn/?portal=auction&page=view_item&market_id=NMTI%3DTEzNjODQ%3DkzMTk4NA%3D%3D&category_id=165";
//    String value  = "http://www.3c.com.vn/Story/vn/thietbicntt/notebook/gateway/2007/7/19204.html";
    String value  = "http://vietsonic.com/Product_detail.asp?pID='UltraFlat'";
    
//    String txtPattern = "http://thegioimevabe.com/Productsdetails.asp?ID=*";
//    String txtPattern = "http://www.chodientu.vn/?portal=auction&page=view_item&market_id=*&category_id=*";
//    String txtPattern = "http://www.3c.com.vn/Story/vn/thietbicntt/*/*/*/*/*";
    
    String txtPattern  = "http://vietsonic.com/Product_detail.asp?pID=*";
    
    int i = 0;
    StringBuilder buildPattern = new StringBuilder();
    while(i < txtPattern.length()) {
      if(Character.isLetterOrDigit(txtPattern.charAt(i))) {
       buildPattern.append(txtPattern.charAt(i));
      } else if(txtPattern.charAt(i) == '*') {
        buildPattern.append("[^/\\?&]*");
      } else {
        buildPattern.append("\\s*[").append(txtPattern.charAt(i)).append(']');
      }
      i++;
    }
    System.out.println(buildPattern.toString());
    Pattern pattern  = Pattern.compile(buildPattern.toString(), Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(value);
    System.out.println(matcher.find());
    System.out.println(matcher.start());
    System.out.println(value.substring(matcher.start(), matcher.end()));
    System.out.println(matcher.end()+" : "+ value.length());
    System.out.println(value.indexOf("/", matcher.end()) < 0 && value.indexOf("&", matcher.end()) < 0 );
  }

  private static void testImagePattern() {
//  Pattern pattern = Pattern.compile("\\b[.!?;]");
//  String txt = "đến từ: Re!dFl.ag    Tiêu đề:  ";
//  Matcher matcher = pattern.matcher(txt);
//  System.out.println(matcher.find());
//  System.out.println(matcher.start());
//  System.out.println(txt.charAt(matcher.start()));

    StringBuilder builder  = new StringBuilder("Diễn viên Nicole Kidman. Ảnh: HollywoodCeleb ");
    Pattern pattern  = Pattern.compile("\\bảnh|\\bminh họa", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(builder.toString());
    System.out.println(matcher.find());
    System.out.println(matcher.start());
  }
  
  public static void testHashCodePattern(int key) {
    Pattern pattern = key < 0 ?
        Pattern.compile(String.valueOf(key)+"\\b") :
        Pattern.compile("\\b"+String.valueOf(key)+"\\b");
  }

  public static void main(String[] args) {
//  testWordCounter();
//    testScanner();
    testSplit();
//    testImagePattern();

//    testURL();

  }
}

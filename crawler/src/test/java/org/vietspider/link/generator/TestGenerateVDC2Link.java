/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerateVDC2Link {
  
  public static void main(String[] args) throws Exception {
//    String link  = "http://danhba.vdc.com.vn/tracuu/doanhnghiep/default.asp?MaNganhNghe=-1&MaThanhPho=-1&Page=";
    
//    http://www.thuvienkhoahoc.com/tusach/index.php?title=%C4%90%E1%BA%B7c+bi%E1%BB%87t%3AAllpages&from=C&namespace=0
//    String link  = "http://www.thuvienkhoahoc.com/tusach/index.php?title=%C4%90%E1%BA%B7c+bi%E1%BB%87t%3AAllpages&from=";
//    String link2 =  "&namespace=0";
    
//    String [] elements = {
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=2",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=3",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=4",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=5",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=6",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=11",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=7",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=71",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=46",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=9",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=82",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=12",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=55",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=13",
//        "http://www.tienphong.vn/Tianyon/Index.aspx?ChannelID=15"
//    } ;
    
    
    String link = "http://profile.live.com/results.aspx?form=people_search_header&q=";
    String  [] elements = {
        /*"anh" , /*"an",*/ 
        "bùi", "bôi",  "bền", "bông",
        "cương", "công", "chinh", "chuyên", /*"chi",*/ "cẩm", "cư",
        "dũng",  "dương", "dần",  "dĩnh", /*"danh",*/ 
        "đĩnh", "điều", "đạt", "đến", "đẳng", "điều",  "đại",
        "đình", /*"duy", */"dương", "dần", "đàm",
        /*"em", */
        "hương", "hùng", "hiếu", "hòa", "hội", "hà", "hân", /*"hoan", */
        /*"huy",*/ "hữu",  /*"hinh",    */    
        "kiều", "khánh", "khương",  "kỳ",
        "lương", /*"loan", "lan", "len",*/ "lơi", "lệ",
        "mạnh", /*"minh", "mai",*/
        "nơi", /*"nga",*/ "ngân", "như", "ngọc",
        "ôn", 
        "phương", "phùng",  "phú",
        "quyết", "quỳnh", /*"quang", */"quý",  "quân",
        "sơn", /*"san", "sang",*/
        "tấn", "tôn", "tuấn", "toán", "tương", "tình",  "tố", "tiêu",
        "thủy", "thuận", "thanh", "thông", "thôn", "thương", "trương", "thân",  "thoại", "thảo", 
        "trâm", "trân", "trác", /*"trinh", */"trường", "trung",
        "uông", "uyên",
        "vĩnh", "vân", "vĩnh", "vín", "vương", "việt",
        /*"xanh",*/ "xuân",
        /*"sen", *//*"san", */"sơn",
    
//        "nguyễn","trần","lê","huỳnh", "hoàng","phạm", /*"phan",*/ 
//        "vũ", "võ", "đặng", "bùi", "đỗ", "hồ", "ngô", 
//        "dương", "lý", "lưu", "vương", "trịnh", "trương", 
//        "đinh", "lâm", "giang", "quách",
        
        "An", "Bành", "Bùi", "Bạch ", "Cao ", "Chu ", "Chung ", "Chương ", "Cung ", "Diêu", "Diệp ",
        "Doãn", "Dư", "Dương ", "Giang ", "Giả ", "Hoa ", "Hoàng ", "Hà ", "Hùng ", "Hướng ", "Hạ ",
        "Hồ ", "Hồng ", "Hứa", "Khang" , "Khuất", "Khâu ", "Kim ", "Kiều ", "La ", "Lâm ", "Lê ",
        "Lý ", "Lư ", "Lưu", "Lương ", "Lại ", "Lục ", "Lữ ", "Mai", "Mạc ", "Mẫn ", "Nghiêm",
        "Nguyễn", "Ngô ", "Nhậm", "Ninh ", "Nông ", "Phan", "Phí ", "Phó ", "Phùng ", "Phương ", 
        "Phạm ", "Quách ", "Quản", "Sử ", "Thi ", "Thiệu", "Thái ", "Thôi", "Thạch ", "Tiêu ", 
        "Tiết ", "Trình ", "Trương ", "Trần", "Trịnh ", "Tô ", "Tôn ", "Tôn Thất", "Tăng ", "Tạ ",
        "Từ ", "Uông", "Vi ", "Văn ", "Vũ ", "Vương ", "Đan ", "Đinh ", "Đoàn ", "Đàm", "Đào ",
        "Đường ", "Đặng", "Đổng ", "Đỗ"
    }; 
    
    
    char [] chars = "0123456789aăâbcdđeêfghijklmnopqrstuưvwxyz".toCharArray();
    
//    for(int i = 0; i < elements.length; i++) {
//      System.out.println(link+ elements[i].trim());
//    }
    
    for(int i = 0; i < chars.length; i++) {
      System.out.println("http://yumesearch.timnhanh.com/member/"+ String.valueOf(chars[i]));
    }
  }
  
}

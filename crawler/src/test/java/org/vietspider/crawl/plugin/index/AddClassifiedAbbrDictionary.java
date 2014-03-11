/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 4, 2009  
 */
public class AddClassifiedAbbrDictionary {
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    String [][] data = {
        {"BVệ", "bảo vệ"},
        {"LĐ", "lao động"},
        {"pv", "phỏng vấn"},
        {"LĐPT", "lao động phổ thông"},
        {"lh", "liên hệ"},
        {"HS", "hồ sơ"},
        {"cty", "công ty"},
        {"kn", "kinh nghiệm"},
        {"kh", "khách hàng"},
        {"TN", "tốt nghiệp"},
        {"THPT", "trung học phổ thông"},
        {"NVKD", "nhân viên kinh doanh"},
        {"CB", "cơ bản"},
        {"ĐT", "điện thoại"},
        {"DS", "doanh số"},
        {"BĐS", "bất động sản"},
        {"ctv", "cộng tác viên"},
        {"TN", "thu nhập"},
        {"TG", "thời gian"},
        {"hc", "hành chính"},
        {"HH", "hoa hồng"},
        {"UT", "ưu tiên"},
        {"ƯT", "ưu tiên"},
        {"MT", "môi trường"},
        {"nv", "nhân viên"},
        {"LCB", "lương cơ bản"},
        {"AVGT", "anh văn giao tiếp"},
        {"VTVP", "vi tính văn phòng"},
        {"TT", "thỏa thuận"},
        {"TP", "thành phố"},
        {"VP", "văn phòng"},
        {"Pc", "phụ cấp"},
        {"CMND", "chứng minh nhân dân"},
        {"GVN", "giúp việc nhà"},
        {"GĐ", "gia đình"},
        {"CN", "công nhân,chủ nhật"},
        {"DV", "dịch vụ"},
        {"TVVP", "tạp vụ văn phòng"},
        {"TV", "tạp vụ"},
        {"VP", "văn phòng"},
        {"HĐ", "hợp đồng"},
        {"CĐ", "cao đẳng"},
        {"ĐH", "đại học"},
        {"SX", "sản xuất"},
        {"KD", "kinh doanh"},
        {"DS", "doanh số"},
        {"Y/C", "yêu cầu"},
        {"YC", "yêu cầu"},
        {"HS", "học sinh"},
        {"SV", "sinh viên"},
        {"VPP", "văn phòng phẩm"},
        {"TM", "thương mại"},
        {"XNK", "xuất nhập khẩu"},
        {"GVGĐ", "giúp việc gia đình"},
        {"TK", "thiết kế"},
        {"qc", "quảng cáo"},
        {"TNCĐ", "tốt nghiệp cao đẳng"},
        {"tnđh", "tốt nghiệp đại học"},
        {"QTKD", "quản trị kinh doanh"},
        {"TNTC", "tốt nghiệp trung cấp"},
        {"kt", "kỹ thuật"},
        {"KS", "kỹ sư"},
        {"CK", "chứng khoán"},
        {"ck", "chính khóa"},
        {"TNTCKT", "tốt nghiệp trung cấp kế toán"},
        {"TNTCKT", "tốt nghiệp tài chính kế toán"},
        {"BHXH", "bảo hiểm xã hội"},
        {"BHYT ", "bảo hiểm y tế"},
        {"TNTC", "tốt nghiệp trung cấp"},
        {"TNTC", "tốt nghiệp tài chính"},
        {"HN", "Hà Nội"},
        {"hcm", "Hồ Chí Minh"},
        {"AV", "anh văn"},
        {"VT", "vi tính"},
        {"NVVP", "nhân viên văn phòng"},
        {"CĐKT", "cao đẳng kế toán"},
        {"KTTH", "kế toán tổng hợp"},
        {"LKĐ", "lương khởi điểm"},
        {"KTế", "kinh tế"},
        {"KB", "khai báo"},
        {"KT", "kế toán"},
        {"TG", "trung gian"},
        {"XM", "xe máy"},
        {"SP", "sản phẩm"},
        {"ktt", "kế toán trưởng"},
        {"kt", "kế toán"},
        {"CH", "cửa hàng"},
        {"BCT", "báo cáo thuế"},
        {"BCTC", "báo cáo tài chính"},
        {"TH", "tổng hợp"},
        {"ĐHKT", "đại học kinh tế"},
        {"HV", "học viện"},
        {"GT", "giao tiếp"},
        {"DN", "doanh nghiệp"},
        {"BC", "báo cáo"},
        {"SS", "sổ sách"},
        {"TNĐHQG", "tốt nghiệp đại học quốc gia"},
        {"ktbh", "kế toán bán hàng"},
        {"NVBH", "nhân viên bán hàng"},
        {"SYLL", "sơ yếu lý lịch"},
        {"ĐK", "điều kiện"},
        {"BDS", "bất động sản"},
        {"KS", "khách sạn"},
        {"ĐK", "đăng ký"},
        {"ĐC", "địa chỉ"},
        {"TNCĐXD", "tốt nghiệp cao đẳng xây dựng"},
        {"KN", "kinh nghiệm"},
        {"TNPTTH", "tốt nghiệp phổ thông trung học"},
        {"LL", "lý lịch"},
        {"hkhn", "hộ khẩu hà nội"},
        {"CT", "chương trình"},
        {"CTV", "cộng tác viên"},
        {"GV", "giáo viên"},
        {"BH", "bán hàng"},
        {"BTG", "bán thời gian"},
        {"CQ", "cơ quan"},
        {"LTT", "lương thỏa thuận"},
        {"PV", "phục vụ"},
        {"NH", "nhà hàng"},
        {"th", "tháng"},
        {"GĐ", "giám đốc"},
        {"GD", "giao dịch"},
        {"CCCC", "chung cư cao cấp"},
        {"dt", "diện tích"},
        {"VT", "vị trí"},
        {"CMTND", "chứng minh thư nhân dân"},
        {"laptop", "máy tính xách tay"},
        {"hdd", "ổ cứng, đĩa cứng"},
        {"cccc", "chung cư cao cấp"},
        {"MP", "mặt phố"},
        {"CHCC", "căn hộ cao cấp"},
        {"GVMN", "giáo viên mầm non"},
        {"SĐT", "số điện thoại"},
        {"TGLV","thời gian làm việc"},
        {"MGTW","mẫu giáo trung ương"},
        {"NN","ngoại ngữ"},
        {"NN","nước ngoài"},
        {"TT","trung tâm"},
        {"TC","trung cấp"},
        {"CĐMG","cao đẳng mẫu giáo"},
        {"TĐ","trình độ"},
        {"TNĐHNN","tốt nghiệp đại học ngoại ngữ"},
        {"PTTH","phổ thông trung học"},
        {"KNLX","kinh nghiệm lái xe"},
        {"BHXH","bảo hiểm xã hội"},
        {"LĐ","Linh đàm"},
        {"ĐT","đối tác"},
        {"XK","xuất khẩu"},
        {"VLXD","vật liệu xây dựng"},
        {"TBVS","thiết bị vệ sinh"},
        {"NVBH","nhân viên bán hàng"},
        {"HKHN","hộ khẩu hà nội"},
        {"CH","cửa hàng"}
        ,{"GTSP","giới thiệu sản phẩm"}
      ,{"mTG","miễn trung gian"}
      ,{"YC","yêu cầu"}
      ,{"XD","xây dựng"}
      ,{"ĐTM","đô thị mới"}
      ,{"ĐL","đại lý"}
      ,{"GSBH","giám sát bán hàng"}
      ,{"KV","khu vực"}
      ,{"SS","sổ sách"}
      ,{"HC","hành chính"}
      ,{"MT","mặt tiền"}
      ,{"TT","tập thể"}
      ,{"HDV","hướng dẫn viên"}
      ,{"TK","tài khoản"}
      ,{"KSXD","kỹ sư xây dựng"}
      ,{"KTXD","kiến trúc xây dựng"}
      ,{"Developer","lập trình viên"}
//      ,{"",""}
//      ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
//    ,{"",""}
        
    };
//    for(int i = 0; i < data.length; i++)  {
//      AbbreviateDictionary2.getInstance().save(data[i][0], data[i][1]);
//    }
//    
////    String text = "Cty TNHH TK - QC Nhất Tâm, 315 NTThuật, Q.3, cccc cần tuyển NVKD, TNCĐ trở lên, VTVP, giao tiếp tốt, ưu tiên có kinh nghiệm trong lĩnh vực quảng cáo; kế toán tổng hợp, TNTC KT, lương thỏa thuận. Nộp hồ sơ: 361/39/3 Ng Đình Chiểu, P.5, Q.3, Tel. 39293463, 39293465";
////    text = "NV tiếp (VP) thị ngành in, LV cả ngày/bán TG, TN cao, cccc UT người chịu khó. LH: 597 Tân Kỳ Tân Quý, TP, Tel. 0913614498.";
//    String text = "Nhà cho thuê ng.căn khu vực Tân Sơn Nhì, Tân Phú, 4x17m, gồm 1 trệt, 2 lầu, sân thượng, có PK, PB, 3 PN, 3 WC, đường trước nhà 12m, khu hạ tầng mới, đẹp, an ninh, thoáng mát, 8,5tr/th. Tel. 0908593755, miễn TG";
//    String value = AbbreviateDictionary2.getInstance().compile(text);
//    System.out.println(value);
  }
  
}

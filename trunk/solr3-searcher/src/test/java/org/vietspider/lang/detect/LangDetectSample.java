/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.lang.detect;

import java.io.File;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class LangDetectSample {

  public String detect(String text) throws LangDetectException {
    Detector detector = DetectorFactory.getInstance().create();
    return detector.detect(text);
  }
  

  public static void main(String[] args) throws Exception {
    File file = new File("D:\\VietSpider Build 19\\data\\");
    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    LangDetectSample sample = new LangDetectSample();
    String text = "Rất nhiều chị em công sở thời hiện đại tự ti khi diện";
    System.out.println(text.length());
    System.out.println(sample.detect(text));

  }
}

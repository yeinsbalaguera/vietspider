package com.cybozu.labs.langdetect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;

import com.cybozu.labs.langdetect.util.LangProfile;

/**
 * Language Detector Factory Class
 * 
 * This class manages an initialization and constructions of {@link Detector}. 
 * 
 * Before using language detection library, 
 * load profiles with {@link DetectorFactory#loadProfile(String)} method
 * and set initialization parameters.
 * 
 * When the language detection,
 * construct Detector instance via {@link DetectorFactory#create()}.
 * See also {@link Detector}'s sample code.
 * 
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 * 
 * @see Detector
 * @author Nakatani Shuyo
 */
public class DetectorFactory {

  private static DetectorFactory INSTANCE = new DetectorFactory();

  public final static synchronized DetectorFactory getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new DetectorFactory();
    }
    return INSTANCE;
  }


  public HashMap<String, double[]> wordLangProbMap;
  public ArrayList<String> langlist;
  public Long seed = null;

  private DetectorFactory() {
    wordLangProbMap = new HashMap<String, double[]>();
    langlist = new ArrayList<String>();

    File file = UtilFile.getFolder("system/dictionary/langdetect/profiles/");
    try {
      loadProfile(file);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(0);
    }
  }

  /**
   * Load profiles from specified directory.
   * This method must be called once before language detection.
   *  
   * @param profileDirectory profile directory path
   * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FileLoadError})
   *                              or profile's format is wrong (error code = {@link ErrorCode#FormatError})
   */
  private void loadProfile(File profileDirectory) throws LangDetectException {
    File[] listFiles = profileDirectory.listFiles();
    
    //    System.out.println("directory "+ profileDirectory.getAbsolutePath());
    //    System.out.println(listFiles.length);
    if (listFiles == null || listFiles.length < 1) {
      URL url = DetectorFactory.class.getResource("profiles.zip");
      try {
        extract(url.openStream(), profileDirectory);
        listFiles = profileDirectory.listFiles();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    if (listFiles == null || listFiles.length < 1)
      throw new LangDetectException(ErrorCode.NeedLoadProfileError, "Not found profile: " + profileDirectory);

    int langsize = listFiles.length;
    int index = 0;
    for (File file: listFiles) {
      //      System.out.println("language profile " + file.getAbsolutePath());
      if (file.getName().startsWith(".") || !file.isFile()) continue;
      FileInputStream is = null;
      try {
        is = new FileInputStream(file);
        LangProfile profile = JSON.decode(is, LangProfile.class);
        addProfile(profile, index, langsize);
        ++index;
      } catch (JSONException e) {
        throw new LangDetectException(ErrorCode.FormatError, "profile format error in '" + file.getName() + "'");
      } catch (IOException e) {
        throw new LangDetectException(ErrorCode.FileLoadError, "can't open '" + file.getName() + "'");
      } finally {
        //        LogService.getInstance().setMessage(null, " load language detect profiles from "+ profileDirectory.getAbsolutePath());
        try {
          if (is!=null) is.close();
        } catch (IOException e) {}
      }
    }
  }

  private void extract(InputStream inputStream, File folder) throws Exception {
    ZipInputStream zipInput = null;
    try {
      zipInput = new ZipInputStream(inputStream);
      int read = -1;
      byte [] bytes = new byte[4*1024];  
      ZipEntry entry = null;
      while((entry = zipInput.getNextEntry()) != null) {
        String name = entry.getName();
        if(entry.isDirectory()) continue;
        int idx = name.lastIndexOf('/');
        if(idx > 0) name = name.substring(idx+1);
        File file = new File(folder, name);
        if(!file.exists() ) file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        try {
          while ((read = zipInput.read(bytes, 0, bytes.length)) != -1) {
            outputStream.write(bytes, 0, read);
          }    
        }  finally {
          try {
            outputStream.close();
          } catch (Exception e) {
          }  
        } 
      }
      zipInput.close();
    } finally {
      try {
        if(zipInput != null) zipInput.close();
      } catch (Exception e) {
      }

    }
  }

  /**
   * @param profile
   * @param langsize 
   * @param index 
   * @throws LangDetectException 
   */
  /* package scope */ void addProfile(LangProfile profile, int index, int langsize) throws LangDetectException {
    String lang = profile.name;
    if (langlist.contains(lang)) {
      throw new LangDetectException(ErrorCode.DuplicateLangError, "duplicate the same language profile");
    }
    langlist.add(lang);
    for (String word: profile.freq.keySet()) {
      if (!wordLangProbMap.containsKey(word)) {
        wordLangProbMap.put(word, new double[langsize]);
      }
      int length = word.length();
      if (length >= 1 && length <= 3) {
        double prob = profile.freq.get(word).doubleValue() / profile.n_words[length - 1];
        wordLangProbMap.get(word)[index] = prob;
      }
    }
  }

  /**
   * for only Unit Test
   */
  /* package scope */ void clear() {
    langlist.clear();
    wordLangProbMap.clear();
  }

  /**
   * Construct Detector instance
   * 
   * @return Detector instance
   * @throws LangDetectException 
   */
  public Detector create() throws LangDetectException {
    return createDetector();
  }

  /**
   * Construct Detector instance with smoothing parameter 
   * 
   * @param alpha smoothing parameter (default value = 0.5)
   * @return Detector instance
   * @throws LangDetectException 
   */
  public  Detector create(double alpha) throws LangDetectException {
    Detector detector = createDetector();
    detector.setAlpha(alpha);
    return detector;
  }

  private Detector createDetector() throws LangDetectException {
    if (langlist.size() == 0)
      throw new LangDetectException(ErrorCode.NeedLoadProfileError, "need to load profiles");
    Detector detector = new Detector(INSTANCE);
    return detector;
  }

  public void setSeed(long seed) { this.seed = seed; }

  public  final List<String> getLangList() {
    return Collections.unmodifiableList(langlist);
  }
}

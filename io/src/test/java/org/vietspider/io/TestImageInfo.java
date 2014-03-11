/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 11, 2007  
 */
public class TestImageInfo {
  /* public static void main(String[] args) {
  ImageInfo imageInfo = new ImageInfo();
  imageInfo.setDetermineImageNumber(true);
  boolean verbose = determineVerbosity(args);
  if (args.length == 0) {
    run(null, System.in, imageInfo, verbose);
  } else {
    int index = 0;
    while (index < args.length) {
      InputStream in = null;
      try {
        String name = args[index++];
        System.out.print(name + ";");
        if (name.startsWith("http://")) {
          in = new URL(name).openConnection().getInputStream();
        } else {
          in = new FileInputStream(name);
        }
        run(name, in, imageInfo, verbose);
        in.close();
      } catch (IOException e) {
        System.out.println(e);
        try {
          if (in != null) in.close();            
        } catch (IOException ee) {
        }
      }
    }
  }
}
*/
/*private static void print(String sourceName, ImageInfo ii, boolean verbose) {
  if (verbose) {
    printVerbose(sourceName, ii);
  } else {
    printCompact(sourceName, ii);
  }
}
*/
/*private static void printCompact(String sourceName, ImageInfo imageInfo) {
  final String SEP = "\t";
  System.out.println(
      sourceName + SEP + 
      imageInfo.getFormatName() + SEP +
      imageInfo.getMimeType() + SEP +
      imageInfo.getWidth() + SEP +
      imageInfo.getHeight() + SEP +
      imageInfo.getBitsPerPixel() + SEP +
      imageInfo.getNumberOfImages() + SEP +
      imageInfo.getPhysicalWidthDpi() + SEP +
      imageInfo.getPhysicalHeightDpi() + SEP +
      imageInfo.getPhysicalWidthInch() + SEP +
      imageInfo.getPhysicalHeightInch() + SEP +
      imageInfo.isProgressive()
  );
}

private static void printLine(int indentLevels, String text, float value, float minValidValue) {
  if (value < minValidValue) return;    
  printLine(indentLevels, text, Float.toString(value));
}

private static void printLine(int indentLevels, String text, int value, int minValidValue) {
  if (value >= minValidValue) printLine(indentLevels, text, Integer.toString(value));
}
*/
/* private static void printLine(int indentLevels, String text, String value) {
  if (value == null || value.length() == 0) return;
  while (indentLevels-- > 0) System.out.print("\t");
  if (text != null && text.length() > 0) {
    System.out.print(text);
    System.out.print(" ");
  }
  System.out.println(value);
}*/

/* private static void printVerbose(String sourceName, ImageInfo ii) {
  printLine(0, null, sourceName);
  printLine(1, "File format: ", ii.getFormatName());
  printLine(1, "MIME type: ", ii.getMimeType());
  printLine(1, "Width (pixels): ", ii.getWidth(), 1);
  printLine(1, "Height (pixels): ", ii.getHeight(), 1);
  printLine(1, "Bits per pixel: ", ii.getBitsPerPixel(), 1);
  printLine(1, "Progressive: ", ii.isProgressive() ? "yes" : "no");
  printLine(1, "Number of images: ", ii.getNumberOfImages(), 1);
  printLine(1, "Physical width (dpi): ", ii.getPhysicalWidthDpi(), 1);
  printLine(1, "Physical height (dpi): ", ii.getPhysicalHeightDpi(), 1);
  printLine(1, "Physical width (inches): ", ii.getPhysicalWidthInch(), 1.0f);
  printLine(1, "Physical height (inches): ", ii.getPhysicalHeightInch(), 1.0f);
  int numComments = ii.getNumberOfComments();
  printLine(1, "Number of textual comments: ", numComments, 1);
  if (numComments > 0) {
    for (int i = 0; i < numComments; i++) {
      printLine(2, null, ii.getComment(i));
    }
  }
}
*/
  
  /* private static void run(String sourceName, InputStream in, ImageInfo imageInfo, boolean verbose) {
  imageInfo.setInput(in);
  imageInfo.setDetermineImageNumber(true);
  imageInfo.setCollectComments(verbose);
  if (imageInfo.check()) print(sourceName, imageInfo, verbose);    
}*/
  
//  private static boolean determineVerbosity(String[] args) {
//    if (args != null && args.length > 0) {
//      for (int i = 0; i < args.length; i++) {
//        if ("-c".equals(args[i])) return false;
//      }
//    }
//    return true;
//  }
  
  public static void main(String[] args) throws Exception {
   /* URL url = new URL("http://danong.com/data/2008/10/24/14241001_52081066-dd00-4d1f-8097-a03.jpg?ID=10%2F24%2F2008%202%3A24%3A10%20PM");
    ImageInfo ii = new ImageInfo();
    ii.setInput(url.openStream()); // in can be InputStream or RandomAccessFile
    ii.setDetermineImageNumber(true); // default is false
    ii.setCollectComments(true); // default is false
    if (!ii.check()) {
      System.err.println("Not a supported image file format.");
      return;
    }
    System.out.println(ii.getFormatName() + ", " + ii.getMimeType() + 
      ", " + ii.getWidth() + " x " + ii.getHeight() + " pixels, " + 
      ii.getBitsPerPixel() + " bits per pixel, " + ii.getNumberOfImages() +
      " image(s), " + ii.getNumberOfComments() + " comment(s).");
     // there are other properties, check out the API documentation
*/  }
}

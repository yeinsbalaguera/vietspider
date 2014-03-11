package org.vietspider.ui.widget.images;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * A sample ImageRegistry that contains the images used in the CoolButton, CoolSlider & Progressindicator samples. 
 */
public class ToolBoxImageRegistry {
  
  private static final Logger logger = Logger.getLogger(ToolBoxImageRegistry.class.getPackage().getName());

  private static final Map<String, Image> IMAGE_MAP = new HashMap<String, Image>();
  private static final Map<String, Color> COLOR_MAP = new HashMap<String, Color>();

  public static final String IMG_INDICATOR_B_1 = "indicator_b_1.png";
  public static final String IMG_INDICATOR_B_2 = "indicator_b_2.png";
  public static final String IMG_INDICATOR_B_3 = "indicator_b_3.png";
  public static final String IMG_INDICATOR_B_4 = "indicator_b_4.png";
  public static final String IMG_INDICATOR_B_5 = "indicator_b_5.png";
  public static final String IMG_INDICATOR_B_6 = "indicator_b_6.png";
  public static final String IMG_INDICATOR_B_7 = "indicator_b_7.png";
  public static final String IMG_INDICATOR_B_8 = "indicator_b_8.png";
  
  public static final String IMG_BUTTON_CHECKBOX_NORMAL = "IMG_BUTTON_CHECKBOX_NORMAL";
  public static final String IMG_BUTTON_CHECKBOX_HOVER = "IMG_BUTTON_CHECKBOX_HOVER";
  public static final String IMG_BUTTON_CHECKBOX_PRESSED = "IMG_BUTTON_CHECKBOX_PRESSED";
  public static final String IMG_BUTTON_CHECKBOX_NORMAL_TOGGLED = "IMG_BUTTON_CHECKBOX_NORMAL_TOGGLED";
  public static final String IMG_BUTTON_CHECKBOX_HOVER_TOGGLED = "IMG_BUTTON_CHECKBOX_HOVER_TOGGLED";
  public static final String IMG_BUTTON_CHECKBOX_PRESSED_TOGGLED = "IMG_BUTTON_CHECKBOX_PRESSED_TOGGLED";
  
  public static final String IMG_BUTTON_CHECKBOX_HOT_SPOT = "IMG_BUTTON_CHECKBOX_HOT_SPOT";
  public static final String IMG_BUTTON_HOT_SPOT_TOGGLED = "IMG_BUTTON_CHECKBOX_HOT_SPOT_TOGGLED";

  static {

    IMAGE_MAP.put(IMG_INDICATOR_B_1, loadImage(IMG_INDICATOR_B_1));
    IMAGE_MAP.put(IMG_INDICATOR_B_2, loadImage(IMG_INDICATOR_B_2));
    IMAGE_MAP.put(IMG_INDICATOR_B_3, loadImage(IMG_INDICATOR_B_3));
    IMAGE_MAP.put(IMG_INDICATOR_B_4, loadImage(IMG_INDICATOR_B_4));
    IMAGE_MAP.put(IMG_INDICATOR_B_5, loadImage(IMG_INDICATOR_B_5));
    IMAGE_MAP.put(IMG_INDICATOR_B_6, loadImage(IMG_INDICATOR_B_6));
    IMAGE_MAP.put(IMG_INDICATOR_B_7, loadImage(IMG_INDICATOR_B_7));
    IMAGE_MAP.put(IMG_INDICATOR_B_8, loadImage(IMG_INDICATOR_B_8));
    
    IMAGE_MAP.put(IMG_BUTTON_CHECKBOX_NORMAL, loadImage("xcheckbox_normal.png"));
    IMAGE_MAP.put(IMG_BUTTON_CHECKBOX_HOVER, loadImage("xcheckbox_normal.png"));//hover
    IMAGE_MAP.put(IMG_BUTTON_CHECKBOX_PRESSED, loadImage("xcheckbox_pressed.png"));
    IMAGE_MAP.put(IMG_BUTTON_CHECKBOX_NORMAL_TOGGLED, loadImage("xcheckbox_normal_toggled.png"));
    IMAGE_MAP.put(IMG_BUTTON_CHECKBOX_HOVER_TOGGLED, loadImage("xcheckbox_normal_toggled.png"));
    IMAGE_MAP.put(IMG_BUTTON_CHECKBOX_PRESSED_TOGGLED, loadImage("xcheckbox_push_toggled.png"));
    IMAGE_MAP.put(IMG_BUTTON_CHECKBOX_HOT_SPOT, loadImage("checkbox_normal_hot_spot.png"));
    IMAGE_MAP.put(IMG_BUTTON_HOT_SPOT_TOGGLED, loadImage("checkbox_hot_spot_toggled.png"));

    Runtime.getRuntime().addShutdownHook(new Thread(){
      @Override
      public void run() {
        final Iterator<Image> iter = IMAGE_MAP.values().iterator();
        while(iter.hasNext()){
          final Image im = iter.next();
          if(im != null){
            im.dispose();
          }
        }
        final Iterator<Color> iterC = COLOR_MAP.values().iterator();
        while(iter.hasNext()){
          final Color im = iterC.next();
          if(im != null){
            im.dispose();
          }
        }
      }
    });
  }

  public static Image getImage(String key){
    return IMAGE_MAP.get(key);
  }

  public static Color getColor(String key){
    return COLOR_MAP.get(key);
  }

  public static Image loadImage (String imageFilename) {
    InputStream stream = ToolBoxImageRegistry.class.getResourceAsStream (imageFilename);
    if (stream == null){
      logger.log(Level.WARNING, "The image "+imageFilename+"no longer exists");
      return null;
    }
    Image image = null;
    try {
      image = new Image (Display.getDefault(), stream);
    } catch (SWTException ex) {
      logger.log(Level.SEVERE, "SWTException while trying to load image {0}", imageFilename);
    } finally {
      try {
        stream.close ();
      } catch (IOException ex) {}
    }
    return image;
  }

}

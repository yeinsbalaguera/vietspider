import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;


/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2008  
 */
public class TestPack {
  
  public static void main(String[] args) {
    // Create the Packer object
    Packer packer = Pack200.newPacker();

    // Initialize the state by setting the desired properties
    Map p = packer.properties();
    // take more time choosing codings for better compression
    p.put(Packer.EFFORT, "1");  // default is "5"
    // use largest-possible archive segments (>10% better compression).
    p.put(Packer.SEGMENT_LIMIT, "-1");
    // reorder files for better compression.
    p.put(Packer.KEEP_FILE_ORDER, Packer.FALSE);
    // smear modification times to a single value.
    p.put(Packer.MODIFICATION_TIME, Packer.LATEST);
    // ignore all JAR deflation requests,
    // transmitting a single request to use "store" mode.
    p.put(Packer.DEFLATE_HINT, Packer.FALSE);
    // discard debug attributes
    p.put(Packer.CODE_ATTRIBUTE_PFX+"LineNumberTable", Packer.STRIP);
    // throw an error if an attribute is unrecognized
    p.put(Packer.UNKNOWN_ATTRIBUTE, Packer.ERROR);
    // pass one class file uncompressed:
    p.put(Packer.PASS_FILE_PFX+0, "mutants/Rogue.class");
    try {
        JarFile jarFile = new JarFile("C:\\Temp\\gui.jar");
        FileOutputStream fos = new FileOutputStream("C:\\Temp\\gui.pack");
        // Call the packer
        packer.pack(jarFile, fos);
        jarFile.close();
        fos.close();
        
        File f = new File("C:\\Temp\\gui.pack");
        FileOutputStream fostream = new FileOutputStream("C:\\Temp\\gui2.jar");
        JarOutputStream jostream = new JarOutputStream(fostream);
        Unpacker unpacker = Pack200.newUnpacker();
        // Call the unpacker
        unpacker.unpack(f, jostream);
        // Must explicitly close the output.
        jostream.close();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }

  }
  
}

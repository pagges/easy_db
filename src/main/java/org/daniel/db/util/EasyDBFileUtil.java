package org.daniel.db.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import org.daniel.db.model.Entry;

public class EasyDBFileUtil {

  public static long writeFile(String filePath, long skip, byte[] content) {
    RandomAccessFile raf = null;
    long filePoint = 0;
    try {
      raf = new RandomAccessFile(filePath, "rw");
      raf.seek(skip);
      raf.write(content);
      filePoint = raf.getFilePointer();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        raf.close();
      } catch (Exception e) {
      }
    }
    return filePoint;
  }


  public static void readFile(String filePath, long skip, byte[] content) {
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(filePath, "rw");
      raf.seek(skip);
      raf.read(content);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        raf.close();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }
}

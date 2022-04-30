package org.daniel.db.util;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.daniel.db.config.SysConfig;

public class EasyDBFileUtil {


  /**
   * 初始化文件
   *
   * @param path
   */
  public static void initFile(String path) {
    try {
      File file = new File(path);
      file.createNewFile();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * random access write file
   *
   * @param filePath
   * @param skip
   * @param content
   * @return
   */
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


  /**
   * random access read file
   *
   * @param filePath
   * @param skip
   * @param content
   */
  public static void readFile(String filePath, long skip, byte[] content) {
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(filePath, "r");
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


  /**
   * @param filePath
   * @param jsonString
   */
  public static void appendWrite(String filePath, String jsonString) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(filePath, true);
      fos.write(jsonString.getBytes());
      fos.write("\r\n".getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fos != null) {
        try {
          fos.flush();
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


}

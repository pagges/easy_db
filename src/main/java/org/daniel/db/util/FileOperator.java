package org.daniel.db.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.daniel.db.model.Entry;

public class FileOperator {

  /**
   * write byte[]
   */
  public static void write(String path, Entry entry) {
    File file = new File(path);
//    if (!file.exists()) {
//      try {
//        file.createNewFile();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
    ObjectOutputStream objectOutputStream;
    FileOutputStream fileOutputStream;
    try {
      fileOutputStream = new FileOutputStream(file, true);
      if (file.length() < 1) {
        objectOutputStream = new ObjectOutputStream(fileOutputStream);
      } else {
        objectOutputStream = new EasyObjectOutputStream(fileOutputStream);
      }
      objectOutputStream.writeObject(entry);
      objectOutputStream.close();
      fileOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * read entry
   *
   * @param path
   * @return
   */
  public static Entry read(String path, byte[] key) {
    File file = new File(path);
    Entry entry = null;
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      try {
        while (true) {
          Entry object = (Entry) objectInputStream.readObject();
          if (Arrays.equals(object.getKey(), key)) {
            entry = object;
            return entry;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        objectInputStream.close();
        fileInputStream.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return entry;
  }


}

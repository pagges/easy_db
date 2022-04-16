package org.daniel.db.engine;


import java.io.File;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.Entry;
import org.daniel.db.util.FileOperator;


public class EasyDBEngine {

  private static SysConfig sysConfig;

  static {
    sysConfig = new SysConfig();
    initSystem();
  }


  /**
   * init system variable
   */
  public static void initSystem() {
    File file = new File(sysConfig.DATA_PATH);
    if (!file.exists()) {
      file.mkdir();
    }
  }


  public void put(byte[] key, byte[] value) {
    Entry entry = new Entry();
    entry.setKey(key);
    entry.setValue(value);
    FileOperator.write(sysConfig.ORIGINAL_FILE_PATH, entry);
  }

  public void delete(byte[] key) {
    Entry entry = new Entry();
    entry.setKey(key);
    entry.setMark((short) 0);
    FileOperator.write(sysConfig.ORIGINAL_FILE_PATH, entry);
  }

  public Entry get(byte[] key) {
    return FileOperator.read(sysConfig.ORIGINAL_FILE_PATH, key);
  }


}

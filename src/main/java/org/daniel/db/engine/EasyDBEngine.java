package org.daniel.db.engine;

import java.io.File;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.Entry;
import org.daniel.db.util.EasyDBFileUtil;


public class EasyDBEngine {

  private static FileEngine fileEngine;

  private static volatile EasyDBEngine easyDBEngine;

  private EasyDBEngine() {
  }


  public static EasyDBEngine getInstance() {
    if (null == easyDBEngine) {
      synchronized (EasyDBEngine.class) {
        if (null == easyDBEngine) {
          easyDBEngine = new EasyDBEngine();
        }
      }
    }
    return easyDBEngine;
  }


  static {
    initSystem();
    fileEngine = FileEngine.getInstance();
  }


  /**
   * init system variable
   */
  private static void initSystem() {
    File file = new File(SysConfig.DATA_PATH);
    if (!file.exists()) {
      file.mkdir();
    }
    EasyDBFileUtil.initFile(SysConfig.ORIGINAL_FILE_PATH);
    EasyDBFileUtil.initFile(SysConfig.BIN_LOG_FILE_PATH);
  }


  public void put(byte[] key, byte[] value) {
    Entry entry = new Entry();
    entry.setKey(key);
    entry.setValue(value);
    fileEngine.writeEntryIntoFile(entry);
  }

  public void delete(byte[] key) {
    Entry entry = new Entry();
    entry.setKey(key);
    entry.setMark((short) 0);
    fileEngine.writeEntryIntoFile(entry);
  }

  public Entry get(byte[] key) {
    return fileEngine.readEntryFormFile(key);
  }


}

package org.daniel.db.engine;

import java.io.File;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.Entry;


public class EasyDBEngine {

  private static FileEngine fileEngine;

  static {
    initSystem();
    fileEngine = new FileEngine(new MemoryIndexEngine());
  }


  /**
   * init system variable
   */
  private static void initSystem() {
    File file = new File(SysConfig.DATA_PATH);
    if (!file.exists()) {
      file.mkdir();
    }
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

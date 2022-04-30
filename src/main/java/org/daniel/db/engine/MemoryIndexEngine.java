package org.daniel.db.engine;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.BinlogModel;
import org.daniel.db.model.EntryIndex;
import org.daniel.db.util.SerializeUtil;

/**
 * maintain a in-memory map which save EntryIndex information
 */
@Slf4j
public class MemoryIndexEngine {


  private volatile static MemoryIndexEngine memoryIndexEngine;

  private MemoryIndexEngine() {
  }

  /**
   * in memory entryIndex map
   */
  private static Map<String, EntryIndex> entryIndexMap = new HashMap<>(64);


  public static MemoryIndexEngine getInstance() {
    if (null == memoryIndexEngine) {
      synchronized (MemoryIndexEngine.class) {
        if (memoryIndexEngine == null) {
          memoryIndexEngine = new MemoryIndexEngine();
        }
      }
    }
    return memoryIndexEngine;
  }


  static {
    buildIndex();
  }


  /**
   * put key-entryIndex into in-memory map
   *
   * @param keyBytes
   * @param entryIndex
   */
  public static void putEntryIndex(byte[] keyBytes, EntryIndex entryIndex) {
    entryIndexMap.put(getEntryKeyValue(keyBytes), entryIndex);
  }


  /**
   * get entryIndex from in-memory map
   *
   * @param keyBates
   * @return
   */
  public EntryIndex readEntryIndex(byte[] keyBates) {
    return entryIndexMap.getOrDefault(getEntryKeyValue(keyBates), null);
  }

  /**
   * @param keyBates
   * @return entryIndexMap key which key is String type
   */
  private static String getEntryKeyValue(byte[] keyBates) {
    return new String(keyBates);
  }

  /**
   * build index form binlog
   */
  private static void buildIndex() {
    long start = System.currentTimeMillis();
    try {
      RandomAccessFile rf = new RandomAccessFile(SysConfig.BIN_LOG_FILE_PATH, "r");
      String line = null;
      while ((line = rf.readLine()) != null) {
        BinlogModel binlogModel = (BinlogModel) SerializeUtil.jsonToObj(line, BinlogModel.class);
        EntryIndex entryIndex = binlogModel.toEntryIndex();
        putEntryIndex(binlogModel.getKey(), entryIndex);
      }
      long end = System.currentTimeMillis();
      log.info("in-memory index build success, size: {} , cost: {}ms",
          entryIndexMap.size(), (end - start));
    } catch (Exception e) {
      log.error("loadEntryIndexFromBinlog error,{}", e.getMessage());
      e.printStackTrace();
    }
  }

}

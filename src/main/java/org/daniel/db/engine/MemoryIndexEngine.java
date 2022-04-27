package org.daniel.db.engine;

import java.util.HashMap;
import java.util.Map;
import org.daniel.db.model.EntryIndex;

/**
 * maintain a in-memory map which save EntryIndex information
 */
public class MemoryIndexEngine {

  /**
   * in memory entryIndex map
   */
  private Map<String, EntryIndex> entryIndexMap = new HashMap<>(64);

  /**
   * put key-entryIndex into in-memory map
   *
   * @param keyBytes
   * @param entryIndex
   */
  public void putEntryIndex(byte[] keyBytes, EntryIndex entryIndex) {
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
  private String getEntryKeyValue(byte[] keyBates) {
    return new String(keyBates);
  }


}

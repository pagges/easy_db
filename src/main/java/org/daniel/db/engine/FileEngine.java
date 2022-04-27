package org.daniel.db.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.Entry;
import org.daniel.db.model.EntryIndex;
import org.daniel.db.util.EasyDBFileUtil;
import org.daniel.db.util.SerializeUtil;

/**
 * easy_db file engine : read or write entry object to file system and update in memory index map
 */
public class FileEngine {

  private MemoryIndexEngine memoryIndexEngine;

  private Map<String, Long> filePointerMap = new HashMap<>(1);


  public FileEngine(MemoryIndexEngine memoryIndexEngine) {
    this.memoryIndexEngine = memoryIndexEngine;
  }

  private void recordFilePointer(String path, Long pointer) {
    filePointerMap.put(path, pointer);
  }


  private long getFilePointer(String path) {
    return filePointerMap.getOrDefault(path, 0L);
  }


  /**
   * TODO: allocation file segment
   *
   * @return
   */
  private String allocationFile() {
    return SysConfig.ORIGINAL_FILE_PATH;
  }

  /**
   * write entry into file system
   *
   * @param entry
   */
  public void writeEntryIntoFile(Entry entry) {
    String filePath = allocationFile();
    Long filePointer = getFilePointer(SysConfig.ORIGINAL_FILE_PATH);
    byte[] bytes = SerializeUtil.objectToBytes(entry);
    long newFilePointer = EasyDBFileUtil
        .writeFile(SysConfig.ORIGINAL_FILE_PATH, filePointer, bytes);

    // write in-memory entryIndex map
    EntryIndex entryIndex = EntryIndex.builder()
        .filePath(filePath)
        .filePointer(filePointer)
        .bytesSize(bytes.length)
        .build();
    memoryIndexEngine.putEntryIndex(entry.getKey(), entryIndex);

    // record file pointer
    recordFilePointer(filePath, newFilePointer);
  }


  public Entry readEntryFormFile(byte[] keyBats) {
    EntryIndex entryIndex = memoryIndexEngine.readEntryIndex(keyBats);
    if (Objects.isNull(entryIndex)) {
      return null;
    }
    final String filePath = entryIndex.getFilePath();
    final Long skip = entryIndex.getFilePointer();
    final Integer bytesSize = entryIndex.getBytesSize();
    byte[] data = new byte[bytesSize];
    EasyDBFileUtil.readFile(filePath, skip, data);
    return (Entry) SerializeUtil.bytesToObject(data);
  }


}

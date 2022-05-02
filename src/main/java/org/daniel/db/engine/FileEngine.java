package org.daniel.db.engine;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.BinlogModel;
import org.daniel.db.model.Entry;
import org.daniel.db.model.EntryIndex;
import org.daniel.db.util.EasyDBFileUtil;
import org.daniel.db.util.SerializeUtil;

/**
 * easy_db file engine : read or write entry object to file system and update in memory index map
 */
public class FileEngine {

  private static MemoryIndexEngine memoryIndexEngine;

  private static BinlogEngine binlogEngine;

  ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);


  private volatile static FileEngine fileEngine;

  private FileEngine() {
  }

  public static FileEngine getInstance() {
    if (null == fileEngine) {
      synchronized (FileEngine.class) {
        if (null == fileEngine) {
          fileEngine = new FileEngine();
        }
      }
    }
    return fileEngine;
  }


  static {
    binlogEngine = BinlogEngine.getInstance();
    memoryIndexEngine = MemoryIndexEngine.getInstance();
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
    byte[] bytes = SerializeUtil.objectToBytes(entry);
    long offset = EasyDBFileUtil
        .writeFile(filePath, bytes);

    // write in-memory entryIndex map
    EntryIndex entryIndex = EntryIndex.builder()
        .filePath(filePath)
        .offset(offset)
        .bytesSize(bytes.length)
        .mark(entry.getMark())
        .build();
    memoryIndexEngine.putEntryIndex(entry.getKey(), entryIndex);

    BinlogModel binlog = BinlogModel.builder()
        .filePath(filePath)
        .offset(offset)
        .key(entry.getKey())
        .mark(entry.getMark())
        .bytesSize(bytes.length)
        .build();
    binlogEngine.writeBinlog(binlog);
  }


  /**
   * read entry from file system
   * @param keyBats
   * @return
   */
  public Entry readEntryFormFile(byte[] keyBats) {
    EntryIndex entryIndex = memoryIndexEngine.readEntryIndex(keyBats);
    if (Objects.isNull(entryIndex) || 0 == entryIndex.getMark()) {
      return null;
    }
    final String filePath = entryIndex.getFilePath();
    final Long skip = entryIndex.getOffset();
    final Integer bytesSize = entryIndex.getBytesSize();
    byte[] data = new byte[bytesSize];
    EasyDBFileUtil.readFile(filePath, skip, data);
    return (Entry) SerializeUtil.bytesToObject(data);
  }


}

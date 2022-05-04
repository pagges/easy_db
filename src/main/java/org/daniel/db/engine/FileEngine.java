package org.daniel.db.engine;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.daniel.db.config.SysConfig;
import org.daniel.db.constant.FileConstant;
import org.daniel.db.model.BinlogModel;
import org.daniel.db.model.Entry;
import org.daniel.db.model.EntryIndex;
import org.daniel.db.util.CmdUtil;
import org.daniel.db.util.EasyDBFileUtil;
import org.daniel.db.util.SerializeUtil;

/**
 * easy_db file engine : read or write entry object to file system and update in memory index map
 */
@Slf4j
public class FileEngine {

  private static MemoryIndexEngine memoryIndexEngine;

  private static BinlogEngine binlogEngine;


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
  private static String allocationFile(FileConstant fileConstant) {
    switch (fileConstant) {
      case MERGED:
        EasyDBFileUtil.initFile(SysConfig.MERGED_FILE_PATH);
        return SysConfig.MERGED_FILE_PATH;
      case ORIGINAL:
        EasyDBFileUtil.initFile(SysConfig.ORIGINAL_FILE_PATH);
        return SysConfig.ORIGINAL_FILE_PATH;
      default:
        EasyDBFileUtil.initFile(SysConfig.ORIGINAL_FILE_PATH);
        return SysConfig.ORIGINAL_FILE_PATH;
    }
  }

  /**
   * write entry into file system
   *
   * @param entry
   */
  public void writeEntryIntoFile(Entry entry) {
    String filePath = allocationFile(FileConstant.ORIGINAL);
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
   *
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


  public long getSize(){
    return memoryIndexEngine.getSize();
  }

  /**
   * merge deleted entry rows
   */
  public void mergeDeletedRows() {
    log.info("start merge deleted rows ...");
    long start = System.currentTimeMillis();
    String filePath = allocationFile(FileConstant.MERGED);
    Set<String> keySet = memoryIndexEngine.getKeySet();
    Set<Entry> entrySet = new HashSet<>();
    keySet.stream().forEach(key -> {
      Entry entry = readEntryFormFile(key.getBytes());
      if (Objects.nonNull(entry)) {
        entrySet.add(entry);
      }
    });
    try {
      entrySet.forEach(entry -> {
        byte[] bytes = SerializeUtil.objectToBytes(entry);
        long offset = EasyDBFileUtil
            .writeFile(filePath, bytes);

        // write in-memory entryIndex map
        EntryIndex entryIndex = EntryIndex.builder()
            .filePath(SysConfig.ORIGINAL_FILE_PATH)
            .offset(offset)
            .bytesSize(bytes.length)
            .mark(entry.getMark())
            .build();
        memoryIndexEngine.putEntryIndex(entry.getKey(), entryIndex);

        BinlogModel binlog = BinlogModel.builder()
            .filePath(SysConfig.ORIGINAL_FILE_PATH)
            .offset(offset)
            .key(entry.getKey())
            .mark(entry.getMark())
            .bytesSize(bytes.length)
            .build();
        binlogEngine.writeBinlog(binlog);
      });
      CmdUtil.rewriteFile(SysConfig.ORIGINAL_FILE_PATH, SysConfig.MERGED_FILE_PATH);
    } catch (Exception e) {
      log.error("merge file error...");
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    log.info("merge data success, key-size: {} , validate-key-size: {}, cost: {}ms", keySet.size(),
        entrySet.size(), (end - start));
  }

}

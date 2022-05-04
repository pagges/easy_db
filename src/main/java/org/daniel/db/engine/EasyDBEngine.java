package org.daniel.db.engine;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.Entry;
import org.daniel.db.util.EasyDBFileUtil;

@Slf4j
public class EasyDBEngine {

  private static FileEngine fileEngine;

  /**
   * merge thread
   */
  private static ScheduledExecutorService mergeThreadPool = Executors
      .newScheduledThreadPool(SysConfig.MERGE_THREAD_NUM);


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
    mergeTask();
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
    EasyDBFileUtil.initFile(SysConfig.MERGED_FILE_PATH);
  }


  public void put(byte[] key, byte[] value) {
    Entry entry = new Entry();
    entry.setKey(key);
    entry.setValue(value);
    entry.setMark((short) 1);
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

  /**
   * task:  merge deleted entry rows
   */
  private static void mergeTask() {
    log.info("EasyDB engine start mergeTask");
    mergeThreadPool
        .scheduleAtFixedRate(() -> {
              fileEngine.mergeDeletedRows();
            }, SysConfig.MERGE_THREAD_INIT_DELAY,
            SysConfig.MERGE_THREAD_PERIOD, TimeUnit.SECONDS);
  }

  public long size() {
    return fileEngine.getSize();
  }
}

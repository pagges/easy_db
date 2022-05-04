package org.daniel.db.config;

/**
 * easy_db system config
 */
public class SysConfig {

  /**
   * easy_db file path
   */
  public static String EASY_DB_SYS_FILE_PATH = "./";

  /**
   * data path
   */
  public static String DATA_PATH = EASY_DB_SYS_FILE_PATH + "data";

  /**
   * original file
   */
  public static String ORIGINAL_FILE_PATH = DATA_PATH + "/easy_db_original.data";
  /**
   * Merge deleted records
   */
  public static String MERGED_FILE_PATH = DATA_PATH + "/easy_db_merged.data";

  /**
   * merge thread number
   */
  public static int MERGE_THREAD_NUM = 1;

  /**
   * merge thread init delay (TimeUnit.SECONDS)
   */
  public static long MERGE_THREAD_INIT_DELAY = 3600;

  /**
   * merge thread period (TimeUnit.SECONDS)
   */
  public static long MERGE_THREAD_PERIOD = 3600 * 4;

  /**
   * essy db bin log
   */
  public static String BIN_LOG_FILE_PATH = DATA_PATH + "/easy_db_bin_log.data";
}

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
  public static String DATA_PATH = EASY_DB_SYS_FILE_PATH  + "/data";

  /**
   * original file
   */
  public static String ORIGINAL_FILE_PATH =  DATA_PATH + "/easy_db_original.data";
  /**
   * merged data
   */
  public static String MERGED_FILE_PATH = DATA_PATH + "/easy_db_merged.data";
}

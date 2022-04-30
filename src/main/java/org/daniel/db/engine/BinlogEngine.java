package org.daniel.db.engine;

import lombok.extern.slf4j.Slf4j;
import org.daniel.db.config.SysConfig;
import org.daniel.db.model.BinlogModel;
import org.daniel.db.util.EasyDBFileUtil;
import org.daniel.db.util.SerializeUtil;

/**
 * record easy db binlog record
 */
@Slf4j
public class BinlogEngine {

  private volatile static BinlogEngine binlogEngine;

  private BinlogEngine() {
  }


  public static BinlogEngine getInstance() {
    if (null == binlogEngine) {
      synchronized (BinlogEngine.class) {
        if (binlogEngine == null) {
          binlogEngine = new BinlogEngine();
        }
      }
    }
    return binlogEngine;
  }


  private static String allocationFile() {
    return SysConfig.BIN_LOG_FILE_PATH;
  }

  /**
   * record binlog into file system
   *
   * @param binlogModel
   */
  public void writeBinlog(BinlogModel binlogModel) {
    String filePath = allocationFile();
    String jsonStr = SerializeUtil.objToJSON(binlogModel);
    EasyDBFileUtil.appendWrite(filePath, jsonStr);
  }

}

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import org.daniel.db.config.SysConfig;
import org.daniel.db.engine.EasyDBEngine;
import org.daniel.db.model.BinlogModel;
import org.daniel.db.model.Entry;
import org.daniel.db.util.SerializeUtil;

public class DBTest {

  EasyDBEngine easyDBEngine = EasyDBEngine.getInstance();

  public void testPut() {
    for (int i = 0; i < 10000; i++) {
      String key = "key:" + i;
      String value = "value:" + i;
      easyDBEngine
          .put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
    }
  }

  public void readTest() {
    long start = System.currentTimeMillis();
    String key = "key:1000";
    Entry entry = easyDBEngine.get(key.getBytes(StandardCharsets.UTF_8));
    System.out
        .println(String.format("100000 records find %s cost: %d ms", key,
            (System.currentTimeMillis() - start)));
    System.out.println(entry);
  }


  public void readBinlogTest() {
    try {
      RandomAccessFile rf = new RandomAccessFile(SysConfig.BIN_LOG_FILE_PATH, "r");
      String line = null;
      while ((line = rf.readLine()) != null) {
        BinlogModel binlogModel = (BinlogModel) SerializeUtil.jsonToObj(line, BinlogModel.class);
        System.out.println(binlogModel.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void testDelete(){
    String key = "key:1000";
    easyDBEngine.delete(key.getBytes(StandardCharsets.UTF_8));
    System.out.println(easyDBEngine.get(key.getBytes(StandardCharsets.UTF_8)));
  }


  public static void main(String[] args) {
//    new DBTest().testPut();
    new DBTest().readTest();
//    new DBTest().testDelete();
  }

}

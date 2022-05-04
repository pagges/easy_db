import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import org.daniel.db.config.SysConfig;
import org.daniel.db.engine.EasyDBEngine;
import org.daniel.db.model.Entry;

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
    String key = "key:0";
    Entry entry = easyDBEngine.get(key.getBytes(StandardCharsets.UTF_8));
    System.out
        .println(String.format("100000 records find %s cost: %d ms", key,
            (System.currentTimeMillis() - start)));
    System.out.println(entry);
  }


  public void readDataFileTest() {
    try {
      RandomAccessFile rf = new RandomAccessFile(SysConfig.ORIGINAL_FILE_PATH, "r");
      final long offset = rf.length();
      rf.seek(offset);
      System.out.println(offset);
      System.out.println(rf.getFilePointer());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void testDelete() {
    String key = "key:999";
    easyDBEngine.delete(key.getBytes(StandardCharsets.UTF_8));
    System.out.println(easyDBEngine.get(key.getBytes(StandardCharsets.UTF_8)));
  }

  public void testSize() {
    System.out.println(easyDBEngine.size());
  }


  public static void main(String[] args) {
    DBTest dbTest = new DBTest();
    dbTest.testSize();
    dbTest.testDelete();
//    dbTest.testPut();
    dbTest.testSize();
  }

}

import java.nio.charset.StandardCharsets;
import org.daniel.db.engine.EasyDBEngine;
import org.daniel.db.model.Entry;

public class DBTest {

  EasyDBEngine easyDBEngine = new EasyDBEngine();

  public void testPut() {
    for (int i = 0; i < 100000; i++) {
      String key = "key:" + i;
      String value = "value:" + i;
      easyDBEngine
          .put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
    }
  }

  public void readTest() {
    long start = System.currentTimeMillis();
    String key = "key:3";
    Entry entry = easyDBEngine.get(key.getBytes(StandardCharsets.UTF_8));
    System.out
        .println(String.format("100000 records find %s cost: %d ms", key,
            (System.currentTimeMillis() - start)));
    System.out.println(entry);
  }

  public static void main(String[] args) {
//    new DBTest().testPut();
    new DBTest().readTest();
  }

}

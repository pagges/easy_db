package org.daniel.db.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class Entry implements Serializable {
  /**
   * key
   */
  private byte[] key;
  /**
   * value
   */
  private byte[] value;

  /**
   * 0: deleted 1: effective
   */
  private short mark;


  @Override
  public String toString() {
    return "Entry{" +
        "key=" + new String(key) +
        ", value=" + new String(value) +
        ", mark=" + mark +
        '}';
  }
}

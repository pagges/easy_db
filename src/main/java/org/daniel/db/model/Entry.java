package org.daniel.db.model;


import java.io.Serializable;
import java.util.Arrays;

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
   * 1： normal 0： deleted
   */
  private short mark;


  public byte[] getKey() {
    return key;
  }

  public void setKey(byte[] key) {
    this.key = key;
  }

  public byte[] getValue() {
    return value;
  }

  public void setValue(byte[] value) {
    this.value = value;
  }

  public short getMark() {
    return mark;
  }

  public void setMark(short mark) {
    this.mark = mark;
  }

  @Override
  public String toString() {
    return "Entry{" +
        "key=" + new String(key) +
        ", value=" + new String(value) +
        ", mark=" + mark +
        '}';
  }
}

package org.daniel.db.model;


import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BinlogModel implements Serializable {

  /**
   * The address of the file storing the key
   */
  private String filePath;

  /**
   * the offset of the file
   */
  private Long offset;

  /**
   * byte size of the entry
   */
  private Integer bytesSize;

  /**
   * key bytes
   */
  private byte[] key;

  /**
   * mark 0: deleted 1: effective
   */
  private short mark;

  public EntryIndex toEntryIndex() {
    return EntryIndex.builder()
        .filePath(this.filePath)
        .offset(this.offset)
        .bytesSize(this.bytesSize)
        .mark(this.mark)
        .build();
  }
}

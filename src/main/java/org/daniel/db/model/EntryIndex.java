package org.daniel.db.model;

import lombok.Builder;
import lombok.Getter;

/**
 * entry index in memory
 */

@Builder
@Getter
public class EntryIndex {

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
   * mark 0: deleted 1: effective
   */
  private short mark;

}

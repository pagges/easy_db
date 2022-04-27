package org.daniel.db.model;

import lombok.Builder;
import lombok.Getter;

/**
 * entry index in memory
 */

@Builder
@Getter
public class EntryIndex {

  private String filePath;

  private Long filePointer;

  private Integer bytesSize;

}

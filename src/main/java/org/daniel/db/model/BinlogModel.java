package org.daniel.db.model;


import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BinlogModel implements Serializable {

  private String filePath;

  private Long filePointer;

  private Integer bytesSize;

  private byte[] key;

  public EntryIndex toEntryIndex() {
    return EntryIndex.builder()
        .filePath(this.getFilePath())
        .filePointer(this.getFilePointer())
        .bytesSize(this.getBytesSize())
        .build();
  }
}

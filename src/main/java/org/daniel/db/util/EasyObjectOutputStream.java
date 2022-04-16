package org.daniel.db.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * 每个文件都有文件的头部和文件体两部分 在对象输出流（ObjectOutputStream） 中有 Protected 方法writeStreamHeader():void，这个方法是专门来写文件的头
 * 当我们追加写入序列化数据的时候，只有在第一次需要写文件头，以后都不需要
 */
public class EasyObjectOutputStream extends ObjectOutputStream {

  public EasyObjectOutputStream(OutputStream out) throws IOException {
    super(out);
  }

  protected EasyObjectOutputStream() throws IOException, SecurityException {
    super();
  }

  protected void writeStreamHeader() throws IOException {
  }
}

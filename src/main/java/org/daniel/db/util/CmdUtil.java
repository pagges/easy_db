package org.daniel.db.util;

public class CmdUtil {

  /**
   * use target file rewrite original file
   *
   * @param originalFile
   * @param targetFile
   */
  public static void rewriteFile(String originalFile, String targetFile) throws Exception {
    Runtime r = Runtime.getRuntime();
    String command = String.format("mv %s %s", targetFile, originalFile);
    Process p = r.exec(command);
    p.waitFor();
  }

}
